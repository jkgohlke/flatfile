package com.gohlke.flatfile.test;

import com.gohlke.flatfile.*;
import com.gohlke.flatfile.parser.FlatFileParser;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 7:22 PM</p>
 *
 * @author jgohlke
 */
public class ProcessingSampleTest
{
	FileFormatFactory fileFormatFactory;
	FileFormat fileFormat;

	@Before
	public void readFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( getClass().getResourceAsStream( "/mappings/ProcessingSample.xml" ) );
	}

	@Test
	public void verifyFileFormat()
	{
		assertNotNull( fileFormat );
		assertEquals( "\\r\\n", fileFormat.getLineSeparator() );
		assertEquals( Long.valueOf( 1L ), fileFormat.getSkipLines() );
		assertEquals( Charset.forName( "UTF-8" ), fileFormat.getCharset() );
		assertEquals( "Processing Sample File Format", fileFormat.getId() );

		Record entityRecord = fileFormat.getRecord( "Person" );

		assertNotNull( entityRecord );
		assertEquals( "Person", entityRecord.getId() );

		assertTrue( fileFormat.getRecordOrder().contains( entityRecord ) );
		assertEquals( entityRecord, fileFormat.getRecordOrder().toArray()[ 0 ] );

		assertEquals( MappingType.Delimited, entityRecord.getMappingType() );

		assertNull( entityRecord.getLineFilter() );

		assertTrue( entityRecord.getLines().size() == 1 );

		RecordLine line = entityRecord.getLines().get( 0 );

		assertEquals( Character.valueOf( ',' ), line.getDelimiter() );
		assertNull( line.getEscapeCharacter() );
		assertEquals( Character.valueOf( '"' ), line.getQuoteCharacter() );
		assertNull( line.getMinLength() );
		assertNull( line.getMaxLength() );

		assertEquals( entityRecord, line.getParentRecord() );

		assertNotNull( line.getMappings() );
	}

	@Test
	public void parseSampleFile() throws Exception
	{
		InputStream is = getClass().getResourceAsStream( "/files/Persons.csv" );
		if( is == null )
		{
			assertTrue( true );
			return;
		}

		/*assertNotNull( "Sample file missing!", is );*/

		FlatFileParser parser = new FlatFileParser( fileFormat, is );

		for( DataMap data : parser )
		{
			/*System.out.println( data );
			System.out.println( data.getRecordId() );
			TreeMap< String, String > dataMap = new TreeMap<String, String>( data.getBackingMap() );
			System.out.println( dataMap.toString() );
			System.out.println( data.getExtraMap() );*/
			assertEquals( "sp_Person", data.get( "spname" ) );
		}

		assertTrue( true );
	}
}
