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
import java.util.List;

import static org.junit.Assert.*;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 7:22 PM</p>
 *
 * @author jgohlke
 */
public class TimezoneConvertSampleTest
{
	FileFormatFactory fileFormatFactory;
	FileFormat fileFormat;

	@Before
	public void readFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( getClass().getResourceAsStream( "/mappings/TimezoneConvertSample.xml" ) );
	}

	@Test
	public void verifyFileFormat()
	{
		assertNotNull( fileFormat );
		assertEquals( "\\r\\n", fileFormat.getLineSeparator() );
		assertEquals( Long.valueOf( 0L ), fileFormat.getSkipLines() );
		assertEquals( Charset.forName( "UTF-8" ), fileFormat.getCharset() );
		assertEquals( "Timezone Convert CSV Sample File Format", fileFormat.getId() );

		Record entityRecord = fileFormat.getRecord( "DateTime Record" );

		assertNotNull( entityRecord );
		assertEquals( "DateTime Record", entityRecord.getId() );

		assertTrue( fileFormat.getRecordOrder().contains( entityRecord ) );
		assertEquals( entityRecord, fileFormat.getRecordOrder().toArray()[ 0 ] );

		assertEquals( MappingType.Delimited, entityRecord.getMappingType() );

		assertNull( entityRecord.getLineFilter() );

		assertTrue( entityRecord.getLines().size() == 1 );

		RecordLine line = entityRecord.getLines().get( 0 );

		assertEquals( Character.valueOf( ',' ), line.getDelimiter() );
		assertEquals( Character.valueOf( '\\' ), line.getEscapeCharacter() );
		assertEquals( Character.valueOf( '"' ), line.getQuoteCharacter() );
		assertNull( line.getMinLength() );
		assertNull( line.getMaxLength() );

		assertEquals( entityRecord, line.getParentRecord() );

		assertNotNull( line.getMappings() );

		List<RecordMapping> mappings = line.getMappings();

		assertEquals( 1, mappings.size() );

		RecordMapping firstNameMapping = mappings.get( 0 );
		assertEquals( "Date Time", firstNameMapping.getId() );
		assertNull( firstNameMapping.getSpan() );
		assertEquals( Integer.valueOf( 0 ), firstNameMapping.getIndex() );
		assertEquals( MappingType.Delimited, firstNameMapping.getMappingType() );
		assertEquals( line, firstNameMapping.getParentLine() );


	}

	@Test
	public void parseSampleFile() throws Exception
	{
		InputStream is = getClass().getResourceAsStream( "/files/DateTimes.csv" );
		assertNotNull( "Sample file missing!", is );

		FlatFileParser parser = new FlatFileParser( fileFormat, is );

		DataMap data = parser.getNext();
		assertNotNull( data );

		assertEquals( "DateTime Record", data.getRecordId() );
		assertEquals( "18451229 120000+0000", data.get( "Date Time" ) );

		data = parser.getNext();
		assertNotNull( data );

		assertEquals( "DateTime Record", data.getRecordId() );
		assertEquals( "17891121 220500+0000", data.get( "Date Time" ) );

		data = parser.getNext();
		assertNotNull( data );

		assertEquals( "DateTime Record", data.getRecordId() );
		assertEquals( "19590103 000000+0000", data.get( "Date Time" ) );

		data = parser.getNext();
		assertNotNull( data );

		assertEquals( "DateTime Record", data.getRecordId() );
		assertEquals( "20121027 092600+0000", data.get( "Date Time" ) );

		data = parser.getNext();
		assertNull( data );
	}
}
