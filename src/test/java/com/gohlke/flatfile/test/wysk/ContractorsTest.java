package com.gohlke.flatfile.test.wysk;

import com.gohlke.flatfile.*;
import com.gohlke.flatfile.parser.FlatFileParser;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 7:22 PM</p>
 *
 * @author jgohlke
 */
public class ContractorsTest
{
	FileFormatFactory fileFormatFactory;
	FileFormat fileFormat;

	@Before
	public void readFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( getClass().getResourceAsStream( "/mappings/Example5.xml" ) );

		assertTrue( true );
	}

	private void addAllMappingIdsFrom( List<String> list, RecordLine line )
	{
		for( RecordMapping mapping : line.getMappings() )
		{
			list.add( mapping.getId() );
			for( RecordLine l : mapping.getLines() )
				addAllMappingIdsFrom( list, l );
		}
	}

	@Test
	public void parseSampleFile() throws Exception
	{
		InputStream is = getClass().getResourceAsStream( "/files/Contractors.csv" );
		if( is == null )
		{
			assertTrue( true );
			return;
		}

		FlatFileParser parser = new FlatFileParser( fileFormat, is );

		long count = 0;

		Map< String, List<String> > recordIdMap = new HashMap<String, List<String>>( fileFormat.getRecordOrder().size() );

		for( Record r : fileFormat.getRecordOrder() )
		{
			List< String > list;
			if( ( list = recordIdMap.get( r.getId() ) ) == null )
				recordIdMap.put( r.getId(), ( list = new ArrayList<String>() ) );

			for( RecordLine l : r.getLines() )
			{
				addAllMappingIdsFrom( list, l );
			}
		}

		for( DataMap data : parser )
		{
			count++;
			if( count > 15 )
				break;

			//System.out.println( data );
			//System.out.println();
			//System.out.println( count + ": " + data.getRecordId() );
			List< String > recordIds = recordIdMap.get( data.getRecordId() );
			for( String id : recordIds )
			{
				String value = data.get( id );
				//System.out.println( id );
				//System.out.println( value );
				//System.out.println( "    VALUE:   " + value );
				//System.out.println( "    LENGTH:  " + ( value == null ? 0 : value.length() ) );
				//System.out.println( "    ATTRIBS: " + data.getExtraMap().get( id ) );
			}
		}

		assertTrue( true );
	}
}
