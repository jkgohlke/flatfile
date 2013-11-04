package com.gohlke.flatfile;

import com.gohlke.flatfile.parser.FlatFileParser;
import com.gohlke.flatfile.processors.transforms.BooleanTransform;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 11/19/12</p>
 * <p>Time: 10:17 PM</p>
 *
 * @author jgohlke
 */
public class FlatFileUtil
{
	protected FileFormatFactory fileFormatFactory;
	protected FileFormat fileFormat;
	protected String xmlFileName;
	protected String srcFileName;

	public FlatFileUtil( String xmlFileName, String srcFileName ) throws IOException, SAXException, ParserConfigurationException
	{
		this.xmlFileName = xmlFileName;
		this.srcFileName = srcFileName;
		this.fileFormatFactory = new FileFormatFactory();
		InputStream is = new FileInputStream( xmlFileName );
		this.fileFormat = fileFormatFactory.createFileFormat( is );
		IOUtils.closeQuietly( is );
	}

	private void addAllMappingIdsFrom( List<String> list, RecordLine line )
	{
		for( RecordMapping mapping : line.getMappings() )
		{
			list.add( mapping.getId() );
			for( RecordLine l : mapping.getLines() )
			{
				addAllMappingIdsFrom( list, l );
			}
		}
	}

	public Map< String, List<String> > recordIdMap()
	{
		Map< String, List<String> > recordIdMap = new LinkedHashMap<String, List<String>>( fileFormat.getRecordOrder().size() );

		for( Record r : fileFormat.getRecordOrder() )
		{
			List< String > list;
			if( ( list = recordIdMap.get( r.getId() ) ) == null )
			{
				recordIdMap.put( r.getId(), ( list = new ArrayList<String>() ) );
			}

			for( RecordLine l : r.getLines() )
			{
				addAllMappingIdsFrom( list, l );
			}
		}

		return recordIdMap;
	}

	public List<String> mappingIds( String id )
	{
		List<String> mappingIdsList = new ArrayList<String>();

		for( RecordLine l : fileFormat.getRecord( id ).getLines() )
		{
			addAllMappingIdsFrom( mappingIdsList, l );
		}

		return mappingIdsList;
	}

	public List< DataMap > readAll( long maximum, long skip ) throws FileNotFoundException
	{
		List< DataMap > ret = new LinkedList<DataMap>();

		InputStream is = new FileInputStream( srcFileName );

		FlatFileParser parser = new FlatFileParser( fileFormat, is );

		long count = 0;
		while( parser.hasNext() )
		{
			count++;
			if( count < skip )
			{
				continue;
			}
			if( count >= maximum + skip )
			{
				break;
			}

			try
			{
				ret.add( parser.getNext() );
			}
			catch( Throwable t )
			{
				Map<String, String> exceptionMap = new HashMap<String, String>();

				exceptionMap.put( "stacktrace", ExceptionUtils.getFullStackTrace( t ) );
				exceptionMap.put( "count", String.valueOf( count ) );
				exceptionMap.put( "line", parser.getPreviousFullLine() );

				DataMap dataMap = new DataMap( parser.getPreviousFullLine(), "Exception", exceptionMap, null );
				ret.add( dataMap );
			}
		}

		IOUtils.closeQuietly( is );

		return ret;
	}

	public String getStoredProcedureName( String recordId )
	{
		if( fileFormat == null )
			return null;
		Record r = fileFormat.getRecord( recordId );
		if( r == null )
			return null;
		Map<String, String> additionalAttribs = r.getAdditionalAttributes();
		if( additionalAttribs == null )
			return null;

		return additionalAttribs.get( "spname" );
	}

	public String resolveStoredProcedureName( String env, String storedProcName )
	{
		if( env == null || storedProcName == null )
			return null;

		Pattern dboPattern = Pattern.compile( "^(\\x5B?([^\\x5D\\.]+)\\x5D?\\.)?(\\x5B?([^\\x5D\\.]+)\\x5D?\\.)?(\\x5B?(dbo)\\x5D?\\.)?(\\x5B?([^\\x5D]+)\\x5D?)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE );

		String servername = "";
		String dbname = "";

		String spname = null;
		Matcher matcher = dboPattern.matcher( storedProcName );
		if( matcher.find() )
		{
			spname = matcher.group( 8 );
		}
		else
		{
			spname = storedProcName;
		}

		String fullSpName = env + "." + spname;
		matcher = dboPattern.matcher( fullSpName );
		if( matcher.find() )
		{
			servername = matcher.group( 2 );
			dbname = matcher.group( 4 );
		}

		if( servername.length() > 0 )
			servername = "[" + servername + "].";
		if( dbname.length() > 0 )
			dbname = "[" + dbname + "].";

		return servername + dbname + "[dbo].[" + spname + "]";
	}

	public String getStoredProcedureInfoQuery( String storedProcName ) throws SQLException
	{
		if( storedProcName == null )
			return null;

		Pattern dboPattern = Pattern.compile( "^(\\x5B?([^\\x5D\\.]+)\\x5D?\\.)?(\\x5B?([^\\x5D\\.]+)\\x5D?\\.)?(\\x5B?(dbo)\\x5D?\\.)?(\\x5B?([^\\x5D]+)\\x5D?)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE );

		String servername = "";
		String dbname = "";
		String spname = storedProcName;
		Matcher matcher = dboPattern.matcher( storedProcName );
		if( matcher.find() )
		{
			servername = matcher.group( 2 );
			dbname = matcher.group( 4 );
			spname = matcher.group( 8 );
		}

		if( servername.length() > 0 )
			servername = "[" + servername + "].";
		if( dbname.length() > 0 )
			dbname = "[" + dbname + "].";

		return "SELECT * FROM " + servername + dbname + "[INFORMATION_SCHEMA].[PARAMETERS] WHERE SPECIFIC_NAME = '" + spname + "' AND PARAMETER_MODE = 'IN' ORDER BY ORDINAL_POSITION ASC";
	}

	public Map<String, String> getStoredProcedureVariableNameToData( DataMap dataMap )
	{
		if( dataMap == null )
			return null;

		Map<String, String> retMap = new LinkedHashMap<String, String>();

		for( String id : mappingIds( dataMap.getRecordId() ) )
		{
			Map<String, String> extra = dataMap.getExtraMap().get( id );
			if( extra != null && extra.containsKey( "spvar" ) )
				retMap.put( extra.get( "spvar" ), dataMap.get( id ) );
		}

		retMap.put( "@lineNumber", String.valueOf( dataMap.getLineNumber() ) );
		retMap.put( "@recordId", String.valueOf( dataMap.getRecordId() ) );

		return retMap;
	}

	public List<String> getStoredProcedureVariableNames( DataMap dataMap )
	{
		if( dataMap == null )
			return null;

		List<String> retList = new ArrayList<String>();

		for( String id : mappingIds( dataMap.getRecordId() ) )
		{
			Map<String, String> extra = dataMap.getExtraMap().get( id );
			if( extra != null && extra.containsKey( "spvar" ) )
				retList.add( extra.get( "spvar" ) );
		}

		retList.add( "@lineNumber" );
		retList.add( "@recordId" );

		return retList;
	}

	public String getStoredProcedureExecuteQuery( String storedProcName, List<String> variableNames, List<String> variableTypes, Map<String, String> data ) throws SQLException
	{
		if( storedProcName == null )
			return null;
		if( variableNames == null )
			return null;
		if( variableTypes == null )
			return null;
		if( data == null )
			return null;

		StringBuilder strLine = new StringBuilder();

		for( int i = 0, variableNamesSize = variableNames.size(); i < variableNamesSize; i++ )
		{
			String parameterName = variableNames.get( i );
			String value = data.containsKey( parameterName ) ? data.get( parameterName ) : null;

			if( value != null && value.length() > 0 )
			{
				String dataType = variableTypes.get( i );
				if( dataType == null )
				{
					strLine.append( "NULL, " );
				}
				/*else if( dataType.equals( "date" ) ||
						   dataType.equals( "time" ) ||
						   dataType.equals( "datetime" ) ||
						   dataType.equals( "smalldate" ) ||
						   dataType.equals( "smalldatetime" ) ||
						   dataType.equals( "datetime2" ) )
				   {
					   stmt.setString( index, value );
				   }
				   else if( dataType.equals( "varchar" ) ||
						   dataType.equals( "nvarchar" ) ||
						   dataType.equals( "char" ) ||
						   dataType.equals( "nchar" ) ||
						   dataType.equals( "text" ) ||
						   dataType.equals( "ntext" ) )
				   {
					   stmt.setString( index, value );
				   }*/
				else if( dataType.equals( "int" ) ||
						dataType.equals( "bigint" ) ||
						dataType.equals( "tinyint" ) ||
						dataType.equals( "smallint" ) )
				{
					strLine.append( Long.parseLong( value ) ).append( ", " );
				}
				else if( dataType.equals( "bit" ) )
				{
					BooleanTransform booleanTransform = new BooleanTransform( BooleanTransform.Format.Any );
					Boolean parsed = booleanTransform.parseAny( value );
					if( parsed != null )
					{
						strLine.append( parsed ).append( ", " );
					}
					else
					{
						strLine.append( "'" ).append( value.replaceAll( "[']", "''" ) ).append( "'" ).append( ", " );
					}
				}
				else if( dataType.startsWith( "decimal" ) )
				{
					strLine.append( Double.parseDouble( value ) ).append( ", " );
				}
				else
				{
					strLine.append( "'" ).append( value.replaceAll( "[']", "''" ) ).append( "'" ).append( ", " );
				}
			}
			else
			{
				strLine.append( "NULL, " );
			}
		}
		return ("EXECUTE " + storedProcName + " " + StringUtils.strip( strLine.toString(), ", " ) + ";");
	}
}
