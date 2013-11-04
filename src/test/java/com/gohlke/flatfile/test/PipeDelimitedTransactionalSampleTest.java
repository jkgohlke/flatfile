package com.gohlke.flatfile.test;

import com.gohlke.flatfile.*;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 4:46 PM</p>
 *
 * @author jgohlke
 */
public class PipeDelimitedTransactionalSampleTest
{
	FileFormatFactory fileFormatFactory;
	FileFormat fileFormat;

	@Before
	public void readFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( getClass().getResourceAsStream( "/mappings/PipeDelimitedTransactionalSample.xml" ) );
	}

	@Test
	public void verifyFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		assertNotNull( fileFormat );
		assertEquals( "\\r\\n", fileFormat.getLineSeparator() );
		assertEquals( Long.valueOf( 0L ), fileFormat.getSkipLines() );
		assertEquals( Charset.forName( "ISO-8859-1" ), fileFormat.getCharset() );
		assertEquals( "Pipe Delimited Transactional Sample File Format", fileFormat.getId() );

		Record furnitureRecord = fileFormat.getRecord( "Furniture" );

		assertNotNull( furnitureRecord );
		assertEquals( "Furniture", furnitureRecord.getId() );

		assertTrue( fileFormat.getRecordOrder().contains( furnitureRecord ) );
		assertEquals( furnitureRecord, fileFormat.getRecordOrder().toArray()[ 0 ] );

		assertEquals( MappingType.Delimited, furnitureRecord.getMappingType() );

		assertNotNull( furnitureRecord.getLineFilter() );
		assertTrue( furnitureRecord.getLineFilter().acceptLine( new String[]{ "Furniture","Display Shelf","10'","2'","7'6\""} ) );

		assertTrue( furnitureRecord.getLines().size() == 1 );

		RecordLine furnitureLine = furnitureRecord.getLines().get( 0 );

		assertEquals( Character.valueOf( '|' ), furnitureLine.getDelimiter() );
		assertNull( furnitureLine.getEscapeCharacter() );
		assertNull( furnitureLine.getQuoteCharacter() );
		assertNull( furnitureLine.getMinLength() );
		assertNull( furnitureLine.getMaxLength() );

		assertEquals( furnitureLine.getParentRecord(), furnitureRecord );

		assertNotNull( furnitureLine.getMappings() );

		List<RecordMapping> furnitureMappings = furnitureLine.getMappings();

		assertEquals( furnitureMappings.size(), 5 );

		RecordMapping furnitureItemTypeMapping = furnitureMappings.get( 0 );
		assertEquals( "Item Type", furnitureItemTypeMapping.getId() );
		assertEquals( Integer.valueOf( 0 ), furnitureItemTypeMapping.getIndex() );
		assertEquals( MappingType.Delimited, furnitureItemTypeMapping.getMappingType() );
		assertEquals( furnitureLine, furnitureItemTypeMapping.getParentLine() );
		assertNull( furnitureItemTypeMapping.getSpan() );

		RecordMapping furnitureDesc = furnitureMappings.get( 1 );
		assertEquals( "Description", furnitureDesc.getId() );
		assertEquals( Integer.valueOf( 1 ), furnitureDesc.getIndex() );
		assertEquals( MappingType.Delimited, furnitureDesc.getMappingType() );
		assertEquals( furnitureLine, furnitureDesc.getParentLine() );
		assertNull( furnitureDesc.getSpan() );

		RecordMapping furnitureLength = furnitureMappings.get( 2 );
		assertEquals( "Length", furnitureLength.getId() );
		assertEquals( Integer.valueOf( 2 ), furnitureLength.getIndex() );
		assertEquals( MappingType.Delimited, furnitureLength.getMappingType() );
		assertEquals( furnitureLine, furnitureLength.getParentLine() );
		assertNull( furnitureLength.getSpan() );

		RecordMapping furnitureWidth = furnitureMappings.get( 3 );
		assertEquals( "Width", furnitureWidth.getId() );
		assertEquals( Integer.valueOf( 3 ), furnitureWidth.getIndex() );
		assertEquals( MappingType.Delimited, furnitureWidth.getMappingType() );
		assertEquals( furnitureLine, furnitureWidth.getParentLine() );
		assertNull( furnitureWidth.getSpan() );

		RecordMapping furnitureHeight = furnitureMappings.get( 4 );
		assertEquals( "Height", furnitureHeight.getId() );
		assertEquals( Integer.valueOf( 4 ), furnitureHeight.getIndex() );
		assertEquals( MappingType.Delimited, furnitureHeight.getMappingType() );
		assertEquals( furnitureLine, furnitureHeight.getParentLine() );
		assertNull( furnitureHeight.getSpan() );


		Record fixtureRecord = fileFormat.getRecord( "Fixture" );

		assertNotNull( fixtureRecord );
		assertEquals( "Fixture", fixtureRecord.getId() );

		assertTrue( fileFormat.getRecordOrder().contains( fixtureRecord ) );
		assertEquals( fixtureRecord, fileFormat.getRecordOrder().toArray()[ 1 ] );

		assertEquals( MappingType.Delimited, fixtureRecord.getMappingType() );

		assertNotNull( fixtureRecord.getLineFilter() );
		assertTrue( fixtureRecord.getLineFilter().acceptLine( new String[]{ "Fixture", "Neon Sign", "Front Door" } ) );

		assertTrue( fixtureRecord.getLines().size() == 1 );

		RecordLine fixtureLine = fixtureRecord.getLines().get( 0 );

		assertEquals( Character.valueOf( '|' ), fixtureLine.getDelimiter() );
		assertNull( fixtureLine.getEscapeCharacter() );
		assertNull( fixtureLine.getQuoteCharacter() );
		assertNull( fixtureLine.getMinLength() );
		assertNull( fixtureLine.getMaxLength() );

		assertEquals( fixtureLine.getParentRecord(), fixtureRecord );

		assertNotNull( fixtureLine.getMappings() );

		List<RecordMapping> fixtureMappings = fixtureLine.getMappings();

		assertEquals( fixtureMappings.size(), 3 );

		RecordMapping fixtureItemTypeMapping = fixtureMappings.get( 0 );
		assertEquals( "Item Type", fixtureItemTypeMapping.getId() );
		assertEquals( Integer.valueOf( 0 ), fixtureItemTypeMapping.getIndex() );
		assertEquals( MappingType.Delimited, fixtureItemTypeMapping.getMappingType() );
		assertEquals( fixtureLine, fixtureItemTypeMapping.getParentLine() );
		assertNull( fixtureItemTypeMapping.getSpan() );

		RecordMapping fixtureDesc = fixtureMappings.get( 1 );
		assertEquals( "Description", fixtureDesc.getId() );
		assertEquals( Integer.valueOf( 1 ), fixtureDesc.getIndex() );
		assertEquals( MappingType.Delimited, fixtureDesc.getMappingType() );
		assertEquals( fixtureLine, fixtureDesc.getParentLine() );
		assertNull( fixtureDesc.getSpan() );

		RecordMapping fixtureLength = fixtureMappings.get( 2 );
		assertEquals( "Location", fixtureLength.getId() );
		assertEquals( Integer.valueOf( 2 ), fixtureLength.getIndex() );
		assertEquals( MappingType.Delimited, fixtureLength.getMappingType() );
		assertEquals( fixtureLine, fixtureLength.getParentLine() );
		assertNull( fixtureLength.getSpan() );
	}
}
