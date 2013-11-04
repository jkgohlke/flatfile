package com.gohlke.flatfile.test.wysk;

import com.gohlke.flatfile.*;
import com.gohlke.flatfile.parser.FlatFileParser;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 7:22 PM</p>
 *
 * @author jgohlke
 */
public class RealTest
{
	private static final transient Logger LOG = LoggerFactory.getLogger( RealTest.class );
	
	FileFormatFactory fileFormatFactory;
	FileFormat fileFormat;

	//@Before
	public void readFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( new FileInputStream( "D:\\Dropbox\\Dropbox\\Wysk - Research\\Databases\\Bankruptcy\\XML\\Bankruptcy Daily.xml" ) );

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

	//@Test
	public void parseSampleFile() throws Exception
	{
		InputStream is = new FileInputStream( "D:\\Dropbox\\Dropbox\\Wysk - Research\\Databases\\Bankruptcy\\Sample daily.txt" );

		//assertTrue( fileFormat.getRecord( "Corporation" ).getLineFilter().acceptLine( new String[]{ "000242184", "Corporation For-Profit", "Test" } ) );
		//assertTrue( fileFormat.getRecord( "Corporation" ).getLineFilter().acceptLine( new String[]{ "000242184", "Corporation For-Profit", "" } ) );
		//assertTrue( fileFormat.getRecord( "Corporation" ).getLineFilter().acceptLine( new String[]{ "000242184", "Corporation For-Profit", null } ) );
		//assertFalse( fileFormat.getRecord( "Corporation" ).getLineFilter().acceptLine( new String[]{ "000242184", "Corporation For-Profit", "Bank" } ) );

		FlatFileParser parser = new FlatFileParser( fileFormat, is, 8192, true );

		assertTrue( fileFormat.getCsvReadAheadLinesLimit().equals( parser.getCsvReadAheadLinesLimit() ) );

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

		Long lastLineNumber = null;
		for( DataMap data : parser )
		{
			if( data == null )
				continue;

			if( data.getRecordId().equals( "Page Break" ) )
				continue;

			//if( data.getLineNumber() > 2000 )
			//	break;

			LOG.info( String.valueOf( data.getLineNumber() ) );
			//LOG.info( "Line: " + String.valueOf( data.getLineNumber() ) );

			if( lastLineNumber == null )
				lastLineNumber = data.getLineNumber();
			else
			{
				//LOG.info( "Expected Next: " + String.valueOf( lastLineNumber + 1L ) );
				assertEquals( lastLineNumber + 1L, data.getLineNumber().longValue() );
				lastLineNumber = data.getLineNumber();
			}

			LOG.info( data.getRawString() );
			//LOG.info( data.getRecordId() );
			List<String> idList = recordIdMap.get( data.getRecordId() );
			for( String id : idList )
			{
				//LOG.info( StringUtils.rightPad( id, 50, " " ) + ": " + data.get( id ) );
			}
			//LOG.info( "" );

			data = null;

			System.gc();
			Thread.yield();
		}

		parser.close();

		assertTrue( true );
	}
}
