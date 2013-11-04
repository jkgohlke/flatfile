package com.gohlke.flatfile;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import com.gohlke.flatfile.processors.StringProcessor;

import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 5:56 PM</p>
 *
 * @author jgohlke
 */
public class RecordLine
{
	private CSVParser csvParser;
	private CSVWriter csvWriter;

	protected List<RecordMapping> mappings;

	private Record parentRecord = null;
	private RecordMapping parentRecordMapping = null;

	protected Character delimiter = null;
	protected Character quoteCharacter = null;
	protected Character escapeCharacter = null;

	protected Long maxLength = null;
	protected Long minLength = null;

	protected List<StringProcessor> processors;
	protected List<StringProcessor> preprocessors;

	protected String id;

	protected RecordLine()
	{
		mappings = new LinkedList<RecordMapping>();
		processors = new LinkedList<StringProcessor>();
		preprocessors = new LinkedList<StringProcessor>();
	}

	public String getId()
	{
		return id;
	}

	protected void setId( String id )
	{
		this.id = id;
	}

	public List<RecordMapping> getMappings()
	{
		return Collections.unmodifiableList( mappings );
	}

	protected void setRecordMappings( List<RecordMapping> mappings )
	{
		this.mappings.clear();
		this.mappings.addAll( mappings );
	}

	protected void addRecordMapping( RecordMapping mapping )
	{
		mappings.add( mapping );
	}

	public Character getDelimiter()
	{
		return delimiter;
	}

	protected void setDelimiter( Character delimiter )
	{
		this.delimiter = delimiter;
	}

	public Character getQuoteCharacter()
	{
		return quoteCharacter;
	}

	protected void setQuoteCharacter( Character quoteCharacter )
	{
		this.quoteCharacter = quoteCharacter;
	}

	public Character getEscapeCharacter()
	{
		return escapeCharacter;
	}

	protected void setEscapeCharacter( Character escapeCharacter )
	{
		this.escapeCharacter = escapeCharacter;
	}

	public Long getMaxLength()
	{
		return maxLength;
	}

	protected void setMaxLength( Long maxLength )
	{
		this.maxLength = maxLength;
	}

	public Long getMinLength()
	{
		return minLength;
	}

	protected void setMinLength( Long minLength )
	{
		this.minLength = minLength;
	}

	public Record getParentRecord()
	{
		return parentRecord;
	}

	public void setParentRecord( Record parentRecord )
	{
		this.parentRecord = parentRecord;
	}

	public RecordMapping getParentRecordMapping()
	{
		return parentRecordMapping;
	}

	public void setParentRecordMapping( RecordMapping parentRecordMapping )
	{
		this.parentRecordMapping = parentRecordMapping;
	}

	public List<StringProcessor> getProcessors()
	{
		return Collections.unmodifiableList( processors );
	}

	protected void setProcessors( List<StringProcessor> processors )
	{
		this.processors.clear();
		this.processors.addAll( processors );
	}

	protected void addProcessor( StringProcessor processor )
	{
		processors.add( processor );
	}

	public List<StringProcessor> getPreprocessors()
	{
		return Collections.unmodifiableList( preprocessors );
	}

	protected void setPreprocessors( List<StringProcessor> processors )
	{
		this.preprocessors.clear();
		this.preprocessors.addAll( processors );
	}

	protected void addPreprocessor( StringProcessor processor )
	{
		preprocessors.add( processor );
	}

	public String preprocessLine( String rawLine )
	{
		if( rawLine != null && preprocessors != null )
		{
			for( StringProcessor processor : preprocessors )
			{
				rawLine = processor.process( rawLine, rawLine );
			}
		}
		return rawLine;
	}

	public MappingType getMappingType()
	{
		for( RecordMapping mapping : getMappings() )
		{
			if( mapping.getMappingType() != MappingType.Compound &&
				mapping.getMappingType() != null )
				return mapping.getMappingType();
		}
		return null;
	}

	public CSVParser getCsvParser()
	{
		if( csvParser == null )
		{
			Character delimiter = CSVParser.DEFAULT_SEPARATOR;
			Character escapeChar = CSVParser.NULL_CHARACTER;
			Character quoteChar = CSVParser.NULL_CHARACTER;

			if( getDelimiter() != null )
			{
				delimiter = getDelimiter();
			}
			if( getEscapeCharacter() != null )
			{
				escapeChar = getEscapeCharacter();
			}
			if( getQuoteCharacter() != null )
			{
				quoteChar = getQuoteCharacter();
			}

			csvParser = new CSVParser( delimiter, quoteChar, escapeChar );
		}
		return csvParser;
	}

	public CSVWriter getCsvWriter( Writer writer )
	{
		if( csvWriter == null )
		{
			Character delimiter = CSVWriter.DEFAULT_SEPARATOR;
			Character escapeChar = CSVWriter.NO_ESCAPE_CHARACTER;
			Character quoteChar = CSVWriter.NO_QUOTE_CHARACTER;

			if( getDelimiter() != null )
			{
				delimiter = getDelimiter();
			}
			if( getEscapeCharacter() != null )
			{
				escapeChar = getEscapeCharacter();
			}
			if( getQuoteCharacter() != null )
			{
				quoteChar = getQuoteCharacter();
			}

			csvWriter = new CSVWriter( writer, delimiter, quoteChar, escapeChar );

		}
		return csvWriter;
	}

	@Override
	public boolean equals( Object o )
	{
		if( this == o ) return true;
		if( o == null || getClass() != o.getClass() ) return false;

		RecordLine line = (RecordLine)o;

		if( delimiter != null ? !delimiter.equals( line.delimiter ) : line.delimiter != null ) return false;
		if( escapeCharacter != null ? !escapeCharacter.equals( line.escapeCharacter ) : line.escapeCharacter != null )
		{
			return false;
		}
		if( mappings != null ? !mappings.equals( line.mappings ) : line.mappings != null ) return false;
		if( maxLength != null ? !maxLength.equals( line.maxLength ) : line.maxLength != null ) return false;
		if( minLength != null ? !minLength.equals( line.minLength ) : line.minLength != null ) return false;
		if( parentRecord != null ? !parentRecord.equals( line.parentRecord ) : line.parentRecord != null ) return false;
		if( parentRecordMapping != null ? !parentRecordMapping.equals( line.parentRecordMapping ) : line.parentRecordMapping != null )
		{
			return false;
		}
		if( processors != null ? !processors.equals( line.processors ) : line.processors != null ) return false;
		if( quoteCharacter != null ? !quoteCharacter.equals( line.quoteCharacter ) : line.quoteCharacter != null )
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = mappings != null ? mappings.hashCode() : 0;
		result = 31 * result + ( parentRecord != null ? parentRecord.hashCode() : 0 );
		result = 31 * result + ( parentRecordMapping != null ? parentRecordMapping.hashCode() : 0 );
		result = 31 * result + ( delimiter != null ? delimiter.hashCode() : 0 );
		result = 31 * result + ( quoteCharacter != null ? quoteCharacter.hashCode() : 0 );
		result = 31 * result + ( escapeCharacter != null ? escapeCharacter.hashCode() : 0 );
		result = 31 * result + ( maxLength != null ? maxLength.hashCode() : 0 );
		result = 31 * result + ( minLength != null ? minLength.hashCode() : 0 );
		result = 31 * result + ( processors != null ? processors.hashCode() : 0 );
		return result;
	}

	@Override
	public String toString()
	{
		return "RecordLine{" +
				"mappings=" + mappings +
				", delimiter=" + delimiter +
				", quoteCharacter=" + quoteCharacter +
				", escapeCharacter=" + escapeCharacter +
				", maxLength=" + maxLength +
				", minLength=" + minLength +
				", processors=" + processors +
				'}';
	}
}
