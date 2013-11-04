package com.gohlke.flatfile.test.wysk;

import com.gohlke.flatfile.FileFormat;
import com.gohlke.flatfile.FileFormatFactory;
import org.junit.Before;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 7:22 PM</p>
 *
 * @author jgohlke
 */
public class BPCleanupWorkersTest
{
	FileFormatFactory fileFormatFactory;
	FileFormat fileFormat;

	@Before
	public void readFileFormat() throws IOException, SAXException, ParserConfigurationException
	{
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( getClass().getResourceAsStream( "/mappings/real/BP Cleanup Workers.xml" ) );

		assertTrue( true );
	}

	/*@Test
	public void parseSampleFile() throws Exception
	{
		InputStream is = getClass().getResourceAsStream( "/files/real/bpcleanupworkers.csv" );
		assertNotNull( "File missing!", is );

		FlatFileParser parser = new FlatFileParser( fileFormat, is );
		CSVWriter csvWriter = new CSVWriter( new FileWriter( "D:/BP Cleanup Workers.csv" ), ',', '"', CSVWriter.NO_ESCAPE_CHARACTER );

		long count = 0;

		String[] values = new String[ 15 ];
		values[ 0 ] = "Index";
		values[ 1 ] = "Full Name";
		values[ 2 ] = "First Name";
		values[ 3 ] = "Middle Name";
		values[ 4 ] = "Last Name";
		values[ 5 ] = "Suffix";
		values[ 6 ] = "Full Address";
		values[ 7 ] = "Street Address";
		values[ 8 ] = "City";
		values[ 9 ] = "State";
		values[ 10 ] = "Postal Code";
		values[ 11 ] = "Postal Code Extension";
		values[ 12 ] = "Cell Phone Number";
		values[ 13 ] = "Best Date, First";
		values[ 14 ] = "Best Date, Last";

		csvWriter.writeNext( values );

		for( DataMap data : parser )
		{
			count++;

			values = new String[ 15 ];
			values[ 0 ] = String.valueOf( count );
			values[ 1 ] = data.get( "Full Name" );
			values[ 2 ] = data.get( "First Name" );
			values[ 3 ] = data.get( "Middle Name" );
			values[ 4 ] = data.get( "Last Name" );
			values[ 5 ] = data.get( "Suffix" );
			values[ 6 ] = data.get( "Full Address" );
			values[ 7 ] = data.get( "Street Address" );
			values[ 8 ] = data.get( "City" );
			values[ 9 ] = data.get( "State" );
			values[ 10 ] = data.get( "Postal Code" );
			values[ 11 ] = data.get( "Postal Code Extension" );
			values[ 12 ] = data.get( "Cell Phone Number" );
			values[ 13 ] = data.get( "Best Date, First" );
			values[ 14 ] = data.get( "Best Date, Last" );

			csvWriter.writeNext( values );

			System.out.println( data.getBackingMap() );
		}
		System.out.println( count );

		assertTrue( true );
	}*/
}
