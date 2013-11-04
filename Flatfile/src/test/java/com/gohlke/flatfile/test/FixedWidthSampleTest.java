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
 * <p>Time: 7:23 PM</p>
 *
 * @author jgohlke
 */
public class FixedWidthSampleTest
{
	FileFormatFactory fileFormatFactory;
	FileFormat fileFormat;

	@Before
	public void readFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( getClass().getResourceAsStream( "/mappings/FixedWidthSample.xml" ) );
	}

	@Test
	public void verifyFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		assertNotNull( fileFormat );
		assertEquals( "\\r\\n", fileFormat.getLineSeparator() );
		assertEquals( Long.valueOf( 0L ), fileFormat.getSkipLines() );
		assertEquals( Charset.forName( "ISO-8859-1" ), fileFormat.getCharset() );
		assertEquals( "Fixed Width Sample File Format", fileFormat.getId() );

		Record stateEntryRecord = fileFormat.getRecord( "State Entry" );

		assertNotNull( stateEntryRecord );
		assertEquals( "State Entry", stateEntryRecord.getId() );

		assertTrue( fileFormat.getRecordOrder().contains( stateEntryRecord ) );
		assertEquals( stateEntryRecord, fileFormat.getRecordOrder().toArray()[ 0 ] );

		assertEquals( MappingType.FixedWidth, stateEntryRecord.getMappingType() );

		assertNull( stateEntryRecord.getLineFilter() );

		assertTrue( stateEntryRecord.getLines().size() == 1 );

		RecordLine line = stateEntryRecord.getLines().get( 0 );

		assertNull( line.getDelimiter() );
		assertNull( line.getEscapeCharacter() );
		assertNull( line.getQuoteCharacter() );
		assertEquals( Long.valueOf( 38L ), line.getMinLength() );
		assertEquals( Long.valueOf( 38L ), line.getMaxLength() );

		assertEquals( line.getParentRecord(), stateEntryRecord );

		assertNotNull( line.getMappings() );

		List<RecordMapping> mappings = line.getMappings();

		assertEquals( mappings.size(), 3 );


		RecordMapping stateIdMapping = mappings.get( 0 );
		assertEquals( "State ID", stateIdMapping.getId() );
		assertEquals( Integer.valueOf( 0 ), stateIdMapping.getIndex() );
		assertEquals( MappingType.FixedWidth, stateIdMapping.getMappingType() );
		assertEquals( line, stateIdMapping.getParentLine() );

		assertNotNull( stateIdMapping.getSpan() );
		Span span = stateIdMapping.getSpan();
		assertNotNull( span.getStartIndex() );
		assertNotNull( span.getEndIndex() );
		assertEquals( Integer.valueOf( 0 ), span.getStartIndex() );
		assertEquals( Integer.valueOf( 4 ), span.getEndIndex() );
		assertEquals( 4, span.length() );


		RecordMapping stateNameMapping = mappings.get( 1 );
		assertEquals( "State Name", stateNameMapping.getId() );
		assertEquals( Integer.valueOf( 1 ), stateNameMapping.getIndex() );
		assertEquals( MappingType.FixedWidth, stateNameMapping.getMappingType() );
		assertEquals( line, stateNameMapping.getParentLine() );

		assertNotNull( stateNameMapping.getSpan() );
		span = stateNameMapping.getSpan();
		assertNotNull( span.getStartIndex() );
		assertNotNull( span.getEndIndex() );
		assertEquals( Integer.valueOf( 4 ), span.getStartIndex() );
		assertEquals( Integer.valueOf( 30 ), span.getEndIndex() );
		assertEquals( 26, span.length() );


		RecordMapping admissionDateMapping = mappings.get( 2 );
		assertEquals( "Admission Date", admissionDateMapping.getId() );
		assertEquals( Integer.valueOf( 2 ), admissionDateMapping.getIndex() );
		assertEquals( MappingType.FixedWidth, admissionDateMapping.getMappingType() );
		assertEquals( line, admissionDateMapping.getParentLine() );

		assertNotNull( admissionDateMapping.getSpan() );
		span = admissionDateMapping.getSpan();
		assertNotNull( span.getStartIndex() );
		assertNotNull( span.getEndIndex() );
		assertEquals( Integer.valueOf( 30 ), span.getStartIndex() );
		assertEquals( Integer.valueOf( 38 ), span.getEndIndex() );
		assertEquals( 8, span.length() );

		assertTrue( admissionDateMapping.getProcessors().size() == 2 );
	}

	@Test
	public void parseSampleFile() throws Exception
	{
		InputStream is = getClass().getResourceAsStream( "/files/States.dat" );
		assertNotNull( "Sample file missing!", is );

		FlatFileParser parser = new FlatFileParser( fileFormat, is );

		DataMap data = parser.getNext();
		assertNotNull( data );
		assertEquals( "State Entry", data.getRecordId() );
		assertEquals( "TX", data.get( "State ID" ) );
		assertEquals( "Texas", data.get( "State Name" ) );
		assertEquals( "1845-12-29 00:00:00.0000000 UTC", data.get( "Admission Date" ) );

		data = parser.getNext();
		assertNotNull( data );
		assertEquals( "State Entry", data.getRecordId() );
		assertEquals( "NC", data.get( "State ID" ) );
		assertEquals( "North Carolina", data.get( "State Name" ) );
		assertEquals( "1789-11-21 00:00:00.0000000 UTC", data.get( "Admission Date" ) );

		data = parser.getNext();
		assertNotNull( data );
		assertEquals( "State Entry", data.getRecordId() );
		assertEquals( "AK", data.get( "State ID" ) );
		assertEquals( "Alaska", data.get( "State Name" ) );
		assertEquals( "1959-01-03 00:00:00.0000000 UTC", data.get( "Admission Date" ) );

		data = parser.getNext();
		assertNotNull( data );
		assertEquals( "State Entry", data.getRecordId() );
		assertEquals( "FED", data.get( "State ID" ) );
		assertEquals( "United States", data.get( "State Name" ) );
		assertEquals( null, data.get( "Admission Date" ) );

		data = parser.getNext();
		assertNotNull( data );
		assertEquals( "State Entry", data.getRecordId() );
		assertEquals( "UNKN", data.get( "State ID" ) );
		assertEquals( "Unknown", data.get( "State Name" ) );
		assertEquals( null, data.get( "Admission Date" ) );

		data = parser.getNext();
		assertNull( data );
	}
}
