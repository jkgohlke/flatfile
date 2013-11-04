package com.gohlke.flatfile;

import com.gohlke.flatfile.parser.FlatFileParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 11/28/12</p>
 * <p>Time: 10:26 PM</p>
 *
 * @author jgohlke
 */
public class Main
{
	private static FileFormatFactory fileFormatFactory;
	private static FileFormat fileFormat;

	public static void main( String[] args ) throws Exception, SAXException, ParserConfigurationException
	{
		InputStream is = new FileInputStream( "D:\\JavaProgramming\\com.wysk\\Flat File Parser\\src\\test\\resources\\mappings\\real\\Texas - Secretary of State.xml" );
		fileFormatFactory = new FileFormatFactory();
		fileFormat = fileFormatFactory.createFileFormat( is );
		parseSampleFile();
	}

	private static void addAllMappingIdsFrom( List<String> list, RecordLine line )
	{
		for( RecordMapping mapping : line.getMappings() )
		{
			list.add( mapping.getId() );
			for( RecordLine l : mapping.getLines() )
				addAllMappingIdsFrom( list, l );
		}
	}

	public static void parseSampleFile() throws Exception
	{
		for( int i = 0; i < 1000; i++ )
		{
			InputStream is = new FileInputStream( "D:\\JavaProgramming\\com.wysk\\Flat File Parser\\src\\test\\resources\\files\\real\\20121101.txt" );

			FlatFileParser parser = new FlatFileParser( fileFormat, is, 8192, false );
			parser.setOnlyRaw( true );

			long count = 0;

			Map<String, List<String>> recordIdMap = new HashMap<String, List<String>>( fileFormat.getRecordOrder().size() );

			for( Record r : fileFormat.getRecordOrder() )
			{
				List<String> list;
				if( ( list = recordIdMap.get( r.getId() ) ) == null )
					recordIdMap.put( r.getId(), ( list = new ArrayList<String>() ) );

				for( RecordLine l : r.getLines() )
				{
					addAllMappingIdsFrom( list, l );
				}
			}

			long startTime = System.currentTimeMillis();

			for( DataMap data : parser )
			{
				count++;
			}

			System.out.println( count / ( (double)( System.currentTimeMillis() - startTime ) / 1000 ) + " records/second" );

		}
	}
}
