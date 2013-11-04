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
public class CSVSampleTest
{
	FileFormatFactory fileFormatFactory;
	FileFormat fileFormat;

	@Before
	public void readFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( getClass().getResourceAsStream( "/mappings/CSVSample.xml" ) );
	}

	@Test
	public void verifyFileFormat()
	{
		assertNotNull( fileFormat );
		assertEquals( "\\r\\n", fileFormat.getLineSeparator() );
		assertEquals( Long.valueOf( 1L ), fileFormat.getSkipLines() );
		assertEquals( Charset.forName( "ISO-8859-1" ), fileFormat.getCharset() );
		assertEquals( "CSV Sample File Format", fileFormat.getId() );

		Record entityRecord = fileFormat.getRecord( "Entity" );

		assertNotNull( entityRecord );
		assertEquals( "Entity", entityRecord.getId() );

		assertTrue( fileFormat.getRecordOrder().contains( entityRecord ) );
		assertEquals( entityRecord, fileFormat.getRecordOrder().toArray()[ 0 ] );

		assertEquals( MappingType.Delimited, entityRecord.getMappingType() );

		assertNull( entityRecord.getLineFilter() );
		assertEquals( entityRecord.getRank(), Long.valueOf( 100L ) );

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

		assertEquals( 3, mappings.size() );

		RecordMapping firstNameMapping = mappings.get( 0 );
		assertEquals( "First Name", firstNameMapping.getId() );
		assertNull( firstNameMapping.getSpan() );
		assertEquals( Integer.valueOf( 0 ), firstNameMapping.getIndex() );
		assertEquals( MappingType.Delimited, firstNameMapping.getMappingType() );
		assertEquals( line, firstNameMapping.getParentLine() );

		RecordMapping middleNameMapping = mappings.get( 1 );
		assertEquals( "Middle Name", middleNameMapping.getId() );
		assertNull( middleNameMapping.getSpan() );
		assertEquals( Integer.valueOf( 1 ), middleNameMapping.getIndex() );
		assertEquals( MappingType.Delimited, middleNameMapping.getMappingType() );
		assertEquals( line, middleNameMapping.getParentLine() );

		RecordMapping lastNameMapping = mappings.get( 2 );
		assertEquals( "Last Name", lastNameMapping.getId() );
		assertNull( lastNameMapping.getSpan() );
		assertEquals( Integer.valueOf( 2 ), lastNameMapping.getIndex() );
		assertEquals( MappingType.Delimited, lastNameMapping.getMappingType() );
		assertEquals( line, lastNameMapping.getParentLine() );
	}

	@Test
	public void parseSampleFile() throws Exception
	{
		InputStream is = getClass().getResourceAsStream( "/files/Names.csv" );
		assertNotNull( "Sample file missing!", is );

		FlatFileParser parser = new FlatFileParser( fileFormat, is );

		DataMap data = parser.getNext();
		assertNotNull( data );

		assertEquals( "Entity", data.getRecordId() );
		assertEquals( "Jennifer", data.get( "First Name" ) );
		assertEquals( "Kaye", data.get( "Middle Name" ) );
		assertEquals( "Gohlke", data.get( "Last Name" ) );

		data = parser.getNext();
		assertNotNull( data );

		assertEquals( "Entity", data.getRecordId() );
		assertEquals( "Marcus", data.get( "First Name" ) );
		assertEquals( "Ben", data.get( "Middle Name" ) );
		assertEquals( "Gohlke", data.get( "Last Name" ) );

		data = parser.getNext();
		assertNotNull( data );

		assertEquals( "Entity", data.getRecordId() );
		assertEquals( "Kristen", data.get( "First Name" ) );
		assertEquals( "Giselle-May", data.get( "Middle Name" ) );
		assertEquals( "Gohlke", data.get( "Last Name" ) );

		data = parser.getNext();
		assertNull( data );
	}
}
