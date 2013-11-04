package com.gohlke.flatfile.parser;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import com.gohlke.flatfile.*;
import org.clapper.util.misc.FileHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 1:30 PM</p>
 *
 * @author jgohlke
 */
public class FlatFileParser implements Closeable, Iterable<DataMap>
{
	private static final transient Logger LOG = LoggerFactory.getLogger( FlatFileParser.class );

	public static final int DEFAULT_READ_BUFFER_SIZE = 65536;

	protected FileFormat fileFormat;
	protected InputStream is;
	//protected Scanner scanner;
	protected BufferedReader bufferedInput;
	protected String markedLine;
	protected String nextLine;

	protected List< String > cachedLines;

	//protected Reader reader;

	protected Long markedLineNumber;
	protected Long cachedLineNumberStart;
	protected volatile long lineNumber = 0L;
	protected boolean hasNextLine;
	protected boolean forceOrder;
	protected boolean enabled = true;
	protected boolean onlyRaw = false;

	protected List< ExceptionListener > exceptionListeners;
	protected HashMap< String, Boolean > parsingRecords;
	protected HashMap< String, FileHashMap< Long, DataMap > > recordCache;
	protected FileHashMap<Long, DataMap> currentCache;
	protected Iterator<DataMap> currentCacheIterator;
	private int readBufferSize;
	private Integer csvReadAheadLinesLimit;

	public int getReadBufferSize()
	{
		return readBufferSize;
	}

	public Integer getCsvReadAheadLinesLimit()
	{
		return csvReadAheadLinesLimit;
	}

	public FlatFileParser( FileFormat fileFormat, InputStream is )
	{
		this( fileFormat, is, DEFAULT_READ_BUFFER_SIZE, false );
	}

	public FlatFileParser( FileFormat fileFormat, InputStream is, boolean forceOrder )
	{
		this( fileFormat, is, DEFAULT_READ_BUFFER_SIZE, forceOrder );
	}

	public FlatFileParser( FileFormat fileFormat, InputStream is, int readBufferSize, boolean forceOrder )
	{
		this( fileFormat, is, DEFAULT_READ_BUFFER_SIZE, forceOrder, null );
	}

	public FlatFileParser( FileFormat fileFormat, InputStream is, int readBufferSize, boolean forceOrder, Integer csvReadAheadLinesLimit )
	{
		this.forceOrder = forceOrder;
		this.fileFormat = fileFormat;
		this.is = is;
		this.readBufferSize = readBufferSize;
		if( csvReadAheadLinesLimit == null )
			this.csvReadAheadLinesLimit = fileFormat.getCsvReadAheadLinesLimit();
		else
			this.csvReadAheadLinesLimit = csvReadAheadLinesLimit;
		this.bufferedInput = new BufferedReader( new InputStreamReader( new BufferedInputStream( is, readBufferSize ), fileFormat.getCharset() ), (int)Math.round( readBufferSize / 2D ) );
		//this.reader = this.bufferedInput;
		/*this.scanner = new Scanner( this.reader );
		this.scanner.useDelimiter( this.fileFormat.getLineSeparator() );*/
		//this.hasNextLine = this.scanner.hasNextLine();
		try
		{
			this.cachedLines = new ArrayList<String>();
			this.markedLine = null;
			this.markedLineNumber = null;
			this.cachedLineNumberStart = null;

			this.nextLine = this.bufferedInput.readLine();
			this.hasNextLine = ( this.nextLine != null );
		}
		catch( IOException e )
		{
			this.hasNextLine = false;
		}
		this.exceptionListeners = new LinkedList<ExceptionListener>();

		this.parsingRecords = new HashMap<String, Boolean>();
		if( this.forceOrder )
		{
			this.recordCache = new HashMap<String, FileHashMap<Long, DataMap>>();
			if( this.fileFormat.getRecordOrder().size() > 0 )
			{
				String firstId = ( (Record)this.fileFormat.getRecordOrder().toArray()[ 0 ] ).getId();
				this.parsingRecords.put( firstId, true );
				for( Record record : fileFormat.getRecordOrder() )
				{
					try
					{
						this.recordCache.put( record.getId(), new FileHashMap<Long, DataMap>( System.getProperty( "java.io.tmpdir" ) + UUID.randomUUID(), FileHashMap.TRANSIENT ) );
					}
					catch( Throwable t )
					{
						throw new RuntimeException( t );
					}
				}
				this.currentCache = this.recordCache.get( firstId );
				this.currentCacheIterator = currentCache.values().iterator();
			}
		}
		else
		{
			this.recordCache = null;
			this.currentCache = null;
			this.currentCacheIterator = null;
			for( Record record : fileFormat.getRecordOrder() )
			{
				this.parsingRecords.put( record.getId(), true );
			}
		}

		if( this.fileFormat.getSkipLines() != null )
		{
			for( int i = 0; i < this.fileFormat.getSkipLines(); i++ )
				getNextLine();
		}
	}

	public void setOnlyRaw( boolean onlyRaw )
	{
		this.onlyRaw = onlyRaw;
	}

	public boolean isOnlyRaw()
	{
		return onlyRaw;
	}

	protected CSVParser getCsvParser( RecordLine currentLine )
	{
		return currentLine.getCsvParser();
	}

	protected CSVWriter getCsvWriter( RecordLine currentLine, Writer writer )
	{
		return currentLine.getCsvWriter( writer );
	}

	public synchronized DataMap getNext() throws Exception
	{
		if( !hasNext() )
			return null;

		DataMap retDataMap = null;
		Map<String, String> retMap = new HashMap<String, String>();
		Map<String, Map<String, String>> extraMap = new HashMap<String, Map<String, String>>();

		Set< Record > recordOrder = fileFormat.getRecordOrder();
		if( hasNextLine() )
		{
			boolean stopLoop = false;
			while( !stopLoop && enabled )
			{
				for( Iterator<Record> recordIterator = recordOrder.iterator(); recordIterator.hasNext(); )
				{
					retDataMap = null;
					retMap = new HashMap<String, String>();
					extraMap = new HashMap<String, Map<String, String>>();

					Record record = recordIterator.next();

					//String matchedLine = null;
					//RecordLine matchedRecordLine = null;
					boolean noFilter = record.getLineFilter() == null;
					boolean matched = noFilter;

					mark();

					if( !matched )
					{
						for( Iterator<RecordLine> recordLineIterator = record.getLines().iterator(); recordLineIterator.hasNext(); )
						{
							RecordLine recordLine = recordLineIterator.next();

							DataMap dataMap = iterateRecordLine( null, recordLine, retMap, extraMap, false, true );

							if( isOnlyRaw() )
							{
								//LOG.info( "raw string: " + dataMap.getRawString() );
								if( dataMap != null && dataMap.getRawString() != null && dataMap.getRawString().length() > 0 )
								{
									matched = true;
									//matchedLine = dataMap.getRawString();
									//matchedRecordLine = recordLine;
									reset();
									mark();
									break;
								}
							}
							else
							{
								//LOG.info( "dataMap: " + dataMap );
								//if( dataMap != null )
								//LOG.info( "raw string: " + dataMap.getRawString() );

								if( dataMap != null )
								{
									matched = true;
									//matchedLine = dataMap.getRawString();
									//matchedRecordLine = recordLine;
									reset();
									mark();
									break;
								}
							}
						}
					}

					/*LOG.info( String.valueOf( noFilter ) );
					LOG.info( String.valueOf( matched ) );
					LOG.info( String.valueOf( markedLine ) );
					LOG.info( String.valueOf( previousLine ) );
					LOG.info( String.valueOf( previousFullLine ) );
					LOG.info( String.valueOf( nextLine ) );*/

					if( matched )
					{
						for( RecordLine recordLine : record.getLines() )
						{
							DataMap dataMap;
							//if( matchedRecordLine == recordLine )
							//	dataMap = iterateRecordLine( matchedLine, recordLine, retMap, extraMap, true, false );
							//else
							//	dataMap = iterateRecordLine( noFilter ? null : previousFullLine, recordLine, retMap, extraMap, true, false );
							//	dataMap = iterateRecordLine( !hasNextLine() ? previousLine : null, recordLine, retMap, extraMap, true, false );
								dataMap = iterateRecordLine( null, recordLine, retMap, extraMap, true, false );

							//HACK: mark/reset won't work if the underlying stream has hit an EOF.
							//if( !noFilter )
							//	getNextLine();

							//LOG.info( "full data map: " + String.valueOf( dataMap ) );

							if( isOnlyRaw() )
							{
								if( dataMap != null && dataMap.getRawString() != null && dataMap.getRawString().length() > 0 )
								{
									mark();
									if( retDataMap == null )
										retDataMap = dataMap;
									else
										retDataMap.merge( dataMap );
								}
								else if( dataMap != null && !dataMap.getExtraMap().isEmpty() )
								{
									mark();
									if( retDataMap == null )
										retDataMap = dataMap;
									else
										retDataMap.merge( dataMap );
								}
								else if( recordLine.getMappings().size() == 0 )
								{
									mark();
								}
							}
							else
							{
								if( dataMap != null && !dataMap.isEmpty() )
								{
									mark();
									if( retDataMap == null )
										retDataMap = dataMap;
									else
										retDataMap.merge( dataMap );
								}
								else if( dataMap != null && !dataMap.getExtraMap().isEmpty() )
								{
									mark();
									if( retDataMap == null )
										retDataMap = dataMap;
									else
										retDataMap.merge( dataMap );
								}
								else if( recordLine.getMappings().size() == 0 )
								{
									mark();
								}
							}
						}
					}

					if( isOnlyRaw() )
					{
						if( retDataMap != null && retDataMap.getRawString() != null && retDataMap.getRawString().length() > 0 )
						{
							//mark();
							//reset();
						}
						else
						{
							if( recordIterator.hasNext() )
								reset();
						}
					}
					else
					{
						if( retDataMap != null && !retDataMap.isEmpty() )
						{
							//mark();
							//reset();
						}
						else
						{
							if( recordIterator.hasNext() )
								reset();
						}
					}

					/*if( isOnlyRaw() )
					{
						if( retDataMap == null || ( retDataMap.getRawString() == null || retDataMap.getRawString().length() == 0 ) )
							retDataMap = null;
					}
					else
					{
						if( retDataMap == null || retDataMap.isEmpty() )
							retDataMap = null;
					}*/

					if( retDataMap != null )
					{
						retDataMap.setLineNumber( lineNumber );
						break;
					}
				}

				if( retDataMap != null )
				{
					if( forceOrder )
					{
						if( parsingRecords.get( retDataMap.getRecordId() ) != null )
						{
							if( isOnlyRaw() )
							{
								stopLoop = retDataMap.getRawString() != null && retDataMap.getRawString().length() > 0;
							}
							else
							{
								stopLoop = !retDataMap.isEmpty();
							}
						}
						else
						{
							recordCache.get( retDataMap.getRecordId() ).put( retDataMap.getLineNumber(), retDataMap );
							retDataMap = null;
							stopLoop = false;
						}
					}
					else
					{
						if( isOnlyRaw() )
						{
							stopLoop = retDataMap.getRawString() != null && retDataMap.getRawString().length() > 0;
						}
						else
						{
							stopLoop = !retDataMap.isEmpty();
						}
					}
				}
				else
				{
					stopLoop = false;
				}

				if( !stopLoop )
					stopLoop = !hasNextLine();
			}
		}

		if( !hasNextLine() && retDataMap == null && forceOrder && enabled )
		{
			while( enabled && retDataMap == null )
			{
				if( this.currentCache == null || this.currentCacheIterator == null )
				{
					break;
				}

				if( this.currentCacheIterator.hasNext() )
				{
					retDataMap = this.currentCacheIterator.next();
				}
				else
				{
					retDataMap = null;
				}

				while( enabled && this.currentCache != null && ( this.currentCache.size() == 0 || !this.currentCacheIterator.hasNext() ) )
				{
					//Get from cache
					if( this.parsingRecords.size() > 0 && recordOrder.size() > this.parsingRecords.size() )
					{
						String nextId = ( (Record)recordOrder.toArray()[ this.parsingRecords.size() ] ).getId();
						this.parsingRecords.put( nextId, true );
						this.currentCache = this.recordCache.get( nextId );
						this.currentCacheIterator = currentCache.values().iterator();
					}
					else
					{
						this.currentCache = null;
						this.currentCacheIterator = null;
					}
				}
			}
		}

		return retDataMap;
	}

	public String getPreviousFullLine()
	{
		return previousFullLine;
	}

	private Object previousFields = null;
	private String previousFullLine = null;
	private String rawString = null;
	protected DataMap iterateRecordLine( String nextLine, RecordLine recordLine, Map<String, String> retMap, Map<String, Map<String, String>> extraMap, boolean ignoreFilter, boolean ignoreFields ) throws Exception
	{
		Record parentRecord = recordLine.getParentRecord();
		RecordMapping parentMapping = recordLine.getParentRecordMapping();

		/*LOG.info( "***" );
		if( recordLine.getParentRecord() != null )
			LOG.info( "parentRecord: " + recordLine.getParentRecord().getId() );
		LOG.info( "recordLine: " + recordLine.getId() );
		LOG.info( "ignoreFilter: " + String.valueOf( ignoreFilter ) );
		LOG.info( "ignoreFields: " + String.valueOf( ignoreFields ) );*/

		try
		{
			rawString = null;
			Object fields = null;
			if( recordLine.getMappings().size() == 0 )
			{
				String line;
				if( nextLine == null )
				{
					line = getNextLine();
				}
				else
					line = nextLine;

				previousFullLine = line;
				previousFields = line;

				return null;
			}
			else if( recordLine.getMappingType() == MappingType.Delimited )
			{
				String[] csvFields;
				if( nextLine == null )
				{
					csvFields = parseCsvFields( recordLine );

					if( csvFields == null || previousCsvLine == null )
						return null;

					/*StringWriter stringWriter = new StringWriter();
					CSVWriter csvWriter = getCsvWriter( recordLine, stringWriter );
					csvWriter.writeNext( csvFields );
					rawString = stringWriter.toString();*/
					rawString = previousCsvLine;
					previousFullLine = previousCsvLine;
					previousFields = csvFields;
				}
				else
				{
					csvFields = getCsvParser( recordLine ).parseLine( nextLine );
					rawString = nextLine;
				}

				fields = csvFields;
			}
			else if( recordLine.getMappingType() == MappingType.FixedWidth )
			{
				String line;
				if( nextLine == null )
				{
					line = recordLine.preprocessLine( getNextLine() );
					previousFullLine = line;
					previousFields = line;
				}
				else
					line = nextLine;

				if( line == null )
					return null;

				if( recordLine.getMaxLength() != null &&
					line.length() > recordLine.getMaxLength() )
				{
					return null;
				}

				if( recordLine.getMinLength() != null &&
					line.length() < recordLine.getMinLength() )
				{
					return null;
				}

				fields = line;
				rawString = line;
			}
			else
			{
				//throw new RuntimeException( "Unsupported: Mixed Delimited w/ Fixed Width." );
				fields = previousFields;
			}

			//LOG.info( "previousFullLine: " + previousFullLine );

			String recordId = null;
			boolean doMapping = false;
			if( parentMapping != null && parentRecord == null )
			{
				doMapping = true;
			}
			else if( parentMapping == null && parentRecord != null )
			{
				recordId = parentRecord.getId();
				doMapping = ignoreFilter || parentRecord.getLineFilter() == null || parentRecord.getLineFilter().acceptLine( fields );
			}

			//LOG.info( "ignoreFilter: " + ignoreFilter );
			//LOG.info( "doMapping: " + doMapping );
			//LOG.info( "previousFullLine: " + previousFullLine );
			//LOG.info( "fields: " + fields );

			if( doMapping )
			{
				if( !isOnlyRaw() && !ignoreFields )
				{
					for( RecordMapping mapping : recordLine.getMappings() )
					{
						if( !enabled )
							break;
						iterateRecordMapping( fields, mapping, retMap, extraMap );
					}

					if( parentRecord != null )
						retMap.putAll( parentRecord.getAdditionalAttributes() );
				}
				return new DataMap( previousFullLine, recordId, retMap, extraMap );
			}
		}
		catch( Exception e )
		{
			fireException( e );
		}

		return null;
	}

	protected void iterateRecordMapping( Object fields, RecordMapping mapping, Map<String, String> retMap, Map<String, Map<String, String>> extraMap ) throws Exception
	{
		boolean saveResults = ( mapping.getId() != null && mapping.getId().length() > 0 );
		if( saveResults )
			extraMap.put( mapping.getId(), mapping.getAdditionalAttributes() );

		String parsed = mapping.parse( fields );
		parsed = mapping.processBefore( parsed, previousFields );

		for( RecordLine nestLine : mapping.getLines() )
		{
			if( !enabled )
				break;

			DataMap dataMap = iterateRecordLine( parsed == null ? "" : parsed, nestLine, retMap, extraMap, true, false );

			if( dataMap != null )
			{
				retMap.putAll( dataMap.getBackingMap() );
				extraMap.putAll( dataMap.getExtraMap() );
				break;
			}
		}

		if( saveResults )
		{
			parsed = mapping.processAfter( parsed, previousFields );
			retMap.put( mapping.getId(), parsed );
		}
	}

	private String previousCsvLine = null;
	protected String[] parseCsvFields( RecordLine recordLine ) throws IOException
	{
		CSVParser parser = getCsvParser( recordLine );

		previousCsvLine = null;

		String[] result = null;

		long startLine = getLineNumber();

		do
		{
			if( !hasNextLine() )
			{
				if( parser.isPending() )
					throw new IOException( "Unexpected end-of-file in the middle of a delimited field." );
				else
					return result;
			}
			String nextLine = getNextLine();

			if( nextLine == null )
			{
				if( parser.isPending() )
					throw new IOException( "Unexpected end-of-file in the middle of a delimited field." );
				else
					return result;
			}

			nextLine = recordLine.preprocessLine( nextLine );

			//if( recordLine.getQuoteCharacter() != null && recordLine.getEscapeCharacter() != null )
			//{
				/*String delimiter = String.valueOf( recordLine.getDelimiter() );
				String quote = String.valueOf( recordLine.getQuoteCharacter() );
				String escape = String.valueOf( recordLine.getEscapeCharacter() );

				String escapedDelimiter = Pattern.quote( delimiter );
				String escapedQuote = Pattern.quote( quote );
				String escapedEscape = Pattern.quote( escape );*/

				//String oldLine = nextLine;
				//Replace all delimiters that are inside quotes.
				//(?<=")([^"]*)(?<!^|\\),(?!"|$)([^"]*)(?=")
				//$1\,$2
				//nextLine = nextLine.replaceAll( "(?<=" + escapedQuote + ")([^" + escapedQuote + "]*)(?<!^|" + escapedEscape + "),(?!" + escapedQuote + "|$)([^" + escapedQuote + "]*)(?=" + escapedQuote + ")", "$1" + Matcher.quoteReplacement( String.valueOf( recordLine.getEscapeCharacter() ).concat( String.valueOf( recordLine.getDelimiter() ) ) ) + "$2" );

				//Replace double quotes
				//(?<!^|,|\\)""(?!,|$)
				//\"
				//nextLine = nextLine.replaceAll( "(?<!^|" + escapedDelimiter + "|" + escapedEscape + ")" + escapedQuote + escapedQuote + "(?!" + escapedDelimiter + "|$)", Matcher.quoteReplacement( String.valueOf( recordLine.getEscapeCharacter() ).concat( String.valueOf( recordLine.getQuoteCharacter() ) ) ) );

				//Replace phrases within phrases
				//(?<!^|,|\\)(?!",|$)"([^\\"]+)"(?!,")(?=[^"]*(",?)+)
				//\"$1\$2
				//nextLine = nextLine.replaceAll( "(?<!^|" + escapedDelimiter + "|" + escapedEscape + ")(?!" + escapedQuote + escapedDelimiter + "|$)" + escapedQuote + "([^" + escapedQuote + escapedEscape + "]+)" + escapedQuote + "(?!" + escapedDelimiter + escapedQuote + ")(?=[^" + escapedQuote + "]*(" + escapedQuote + escapedDelimiter + "?)+)", Matcher.quoteReplacement( String.valueOf( recordLine.getEscapeCharacter() ).concat( String.valueOf( recordLine.getQuoteCharacter() ) ) ) + "$1" + Matcher.quoteReplacement( "\\" ) + "$2" );

				//Replace accidentally delimited quotes at the end of a field...
				//(?<!\\)\\(?=(",)|("$))
				//\\
				//nextLine = nextLine.replaceAll( "(?<!" + escapedEscape + ")" + escapedEscape + "(?=(" + escapedQuote + escapedDelimiter + ")|(" + escapedQuote + "$))", Matcher.quoteReplacement( String.valueOf( recordLine.getEscapeCharacter() ).concat( String.valueOf( recordLine.getEscapeCharacter() ) ) ) );

				//Replace stray undelimited quotes
				//(?<!^|,|\\)"(?!,|$)
				//\"
				//nextLine = nextLine.replaceAll( "(?<!^|" + escapedDelimiter + "|" + escapedEscape + ")" + escapedQuote + "(?!" + escapedDelimiter + "|$)", Matcher.quoteReplacement( String.valueOf( recordLine.getEscapeCharacter() ).concat( String.valueOf( recordLine.getQuoteCharacter() ) ) ) );

				/*if( !nextLine.equals( oldLine ) )
				{
					System.out.println( "OLD: " + oldLine );
					System.out.println( "NEW: " + nextLine );
					System.out.println();
				}*/
			//}

			//System.out.println( parser.isPending() + ", " + nextLine );
			/*if( parser.isPending() )
			{
				System.out.println( nextLine );
			}*/

			if( previousCsvLine == null )
				previousCsvLine = nextLine;
			else
			{
				previousCsvLine += nextLine;
				LOG.info( "ADDITIONAL LINE:" + nextLine );
			}

			String[] r = parser.parseLineMulti( nextLine );

			//System.out.println( Arrays.toString( r ) );

			if( r.length > 0 )
			{
				if( result == null )
				{
					result = r;
				}
				else
				{
					String[] t = new String[ result.length + r.length ];
					System.arraycopy( result, 0, t, 0, result.length );
					System.arraycopy( r, 0, t, result.length, r.length );
					result = t;
				}
			}
		}
		while( parser.isPending() && ( csvReadAheadLinesLimit == null || ( getLineNumber() - startLine < csvReadAheadLinesLimit ) ) );

		if( parser.isPending() && csvReadAheadLinesLimit != null && ( getLineNumber() - startLine < csvReadAheadLinesLimit ) )
		{
			throw new IOException( "Parser pending while CSV read-ahead limit reached!" );
		}

		return result;
	}

	public boolean hasNext()
	{
		return hasNextRecord() || hasNextLine();
	}

	protected boolean hasNextRecord()
	{
		return enabled && forceOrder && ( ( this.currentCache != null && this.currentCacheIterator != null && this.currentCacheIterator.hasNext() ) );
	}

	protected boolean hasNextLine()
	{
		return enabled && ( this.parsingRecords.size() > 0 ) && hasNextLine;
	}

	private void mark() throws IOException
	{
		//LOG.info( ">>> mark()" );

		Long oldMarkedLineNumber = this.markedLineNumber;

		this.markedLineNumber = this.lineNumber;
		this.markedLine = this.nextLine;

		Long difference = ( oldMarkedLineNumber == null ? 0 : this.markedLineNumber - oldMarkedLineNumber );

		//LOG.info( "difference: " + String.valueOf( difference ) );
		if( this.cachedLineNumberStart == null ||
			difference > 0 )
		{
			this.cachedLines.clear();
			this.cachedLines.add( this.markedLine );
			this.cachedLineNumberStart = this.markedLineNumber;
		}

		this.hasNextLine = ( this.nextLine != null );
		//bufferedInput.mark( readBufferSize );
	}

	private void reset() throws IOException
	{
		//LOG.info( "<<< reset()" );
		//bufferedInput.reset();
		if( this.markedLine != null )
		{
			this.lineNumber = this.markedLineNumber;
			this.markedLineNumber = null;
			this.nextLine = this.markedLine;
			this.markedLine = null;
			this.hasNextLine = ( this.nextLine != null );
		}
	}

	protected String getNextLine()
	{
		//LOG.info( "" );
		//LOG.info( ">>> getNextLine()" );
		try
		{
			String nextLine = null;
			if( hasNextLine )
			{
				nextLine = this.nextLine;
				incLineNumber();

				/*LOG.info( "Next Line:" + nextLine );
				LOG.info( "Line Number:" + this.lineNumber );
				LOG.info( "Marked Line Number:" + this.markedLineNumber );
				LOG.info( "cachedLines.size: " + this.cachedLines.size() );
				LOG.info( "this.cachedLineNumberStart: " + this.cachedLineNumberStart );
				LOG.info( "cachedLocation:" + String.valueOf( this.cachedLineNumberStart == null ? null : ( this.lineNumber - this.cachedLineNumberStart ) ) );*/

				if( this.cachedLineNumberStart != null && this.cachedLines.size() > 0 )
				{
					if( this.markedLineNumber != null )
					{
						if( this.cachedLines.size() > ( this.lineNumber - this.cachedLineNumberStart ) )
						{
							this.nextLine = this.cachedLines.get( (int)( this.lineNumber - this.cachedLineNumberStart ) );
						}
						else
						{
							this.nextLine = bufferedInput.readLine();
							this.cachedLines.add( this.nextLine );
						}
					}
					else
					{
						if( this.cachedLines.size() > ( this.lineNumber - this.cachedLineNumberStart ) )
						{
							this.nextLine = this.cachedLines.get( (int)( this.lineNumber - this.cachedLineNumberStart ) );
						}
						else
						{
							this.cachedLines.clear();
							this.cachedLineNumberStart = null;
							this.nextLine = bufferedInput.readLine();
						}
					}
				}
				else
				{
					this.nextLine = bufferedInput.readLine();
				}
				hasNextLine = ( this.nextLine != null );

				//LOG.info( String.valueOf( nextLine ) );
				//LOG.info( "<<< getNextLine()" );
				return nextLine;
			}
			else
			{
				return null;
			}
		}
		catch( IOException e )
		{
			hasNextLine = false;
			return null;
		}
	}

	@Override
	public void close() throws IOException
	{
		if( recordCache != null )
		{
			for( FileHashMap<Long, DataMap> fileCache : recordCache.values() )
			{
				fileCache.delete();
			}
			recordCache.clear();
		}

		this.enabled = false;
		//scanner.close();
		bufferedInput.close();
		//reader.close();
	}

	protected void fireException( Exception e ) throws Exception
	{
		if( exceptionListeners.size() == 0 )
			throw e;
		else
		{
			for( ExceptionListener listener : exceptionListeners )
			{
				listener.onException( e );
			}
		}
	}

	public void addExceptionListener( ExceptionListener listener )
	{
		exceptionListeners.add( listener );
	}

	public void removeExceptionListener( ExceptionListener listener )
	{
		exceptionListeners.remove( listener );
	}

	@Override
	public Iterator<DataMap> iterator()
	{
		return new FlatFileParserIterator();
	}

	public synchronized void incLineNumber()
	{
		lineNumber++;
	}

	public synchronized long getLineNumber()
	{
		return lineNumber;
	}

	protected class FlatFileParserIterator implements Iterator<DataMap>
	{
		@Override
		public boolean hasNext()
		{
			return hasNextRecord() || hasNextLine();
		}

		@Override
		public DataMap next()
		{
			if( !hasNext() )
				throw new NoSuchElementException();

			try
			{
				return getNext();
			}
			catch( Exception e )
			{
				throw new RuntimeException( e );
			}
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}
