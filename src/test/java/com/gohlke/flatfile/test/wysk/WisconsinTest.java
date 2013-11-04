package com.gohlke.flatfile.test.wysk;

import com.gohlke.flatfile.*;
import com.gohlke.flatfile.parser.FlatFileParser;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 7:22 PM</p>
 *
 * @author jgohlke
 */
public class WisconsinTest
{
	private static final transient Logger LOG = LoggerFactory.getLogger( WisconsinTest.class );
	
	FileFormatFactory fileFormatFactory;
	FileFormat fileFormat;

	@Before
	public void readFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( new FileInputStream( "D:\\JavaProgramming\\com.wysk\\ref\\WI_SoS_01_Master.xml" ) );

		assertTrue( true );
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

	@Test
	public void parseSampleFile() throws Exception
	{
		File inputFile = new File( "G:\\Programming\\com.wysk\\ref\\WI Data - DnB 07-08-2013.txt" );
		InputStream is = new FileInputStream( inputFile );

		LOG.info( "Input File: " + inputFile.getAbsolutePath() );

		FlatFileParser parser = new FlatFileParser( fileFormat, is, FlatFileParser.DEFAULT_READ_BUFFER_SIZE, false );

		Map< String, List<String> > recordIdMap = new HashMap<String, List<String>>( fileFormat.getRecordOrder().size() );

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

		for( DataMap data : parser )
		{
			if( data == null )
			{
				LOG.info( "~Null Data~" );
				continue;
			}

			LOG.info( String.valueOf( data.getLineNumber() ) );

			if( data.getRecordId().equals( "Page Break" ) )
			{
				LOG.info( "~Page Break~" );
				continue;
			}
			//LOG.info( data.getRawString() );
			
			LOG.info( data.getRecordId() );
			List<String> idList = recordIdMap.get( data.getRecordId() );
			for( String id : idList )
			{
				//LOG.info( StringUtils.rightPad( String.valueOf( id ), 50, " " ) + ": " + StringUtils.rightPad( String.valueOf( data.get( id ) ), 70, " " ) + ": " + String.valueOf( data.getExtraMap().get( id ) == null ? null : data.getExtraMap().get( id ).get( "spvar" ) ) );
				LOG.info( String.valueOf( id ) );
				LOG.info( String.valueOf( data.get( id ) ) );
				LOG.info( data.getExtraMap().toString() );
				LOG.info( String.valueOf( data.getExtraMap().get( id ) ) );
				/*if( data.getExtraMap().get( id ) == null )
					LOG.info( "UNMAPPED!!!! ( " + id + " )" );
				else
					LOG.info( String.valueOf( data.getExtraMap().get( id ).get( "spvar" ) ) );*/
			}
			//LOG.info( "" );

			if( data.get( "Business Name" ) == null || data.get( "Business Name" ).equals( "" ) )
			{
				LOG.info( "NULL name!" );
				break;
			}

			System.gc();
		}

		parser.close();

		assertTrue( true );
	}
}
