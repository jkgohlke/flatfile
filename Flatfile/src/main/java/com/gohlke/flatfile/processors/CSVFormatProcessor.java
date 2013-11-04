package com.gohlke.flatfile.processors;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.Quote;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 2/25/13</p>
 * <p>Time: 11:54 AM</p>
 *
 * @author jgohlke
 */
public class CSVFormatProcessor extends StringProcessor
{
	private static final transient Logger LOG = LoggerFactory.getLogger( CSVFormatProcessor.class );

	CSVFormat format;

	public CSVFormatProcessor(
			char delimiter,
			Character quote,
			String quotePolicy,
			Character escape,
			boolean ignoreSurroundingSpaces,
			boolean ignoreEmptyLines )
	{
		format = CSVFormat.newFormat( delimiter );

		if( quote != null )
		{
			format = format.withQuoteChar( quote );
		}

		if( quotePolicy != null )
		{
			Quote quotePolicyObj;

			if( quotePolicy.equalsIgnoreCase( "all" ) )
				quotePolicyObj = Quote.ALL;
			else if( quotePolicy.equalsIgnoreCase( "minimal" ) || quotePolicy.equalsIgnoreCase( "min" ) )
				quotePolicyObj = Quote.MINIMAL;
			else if( quotePolicy.equalsIgnoreCase( "alpha" ) || quotePolicy.equalsIgnoreCase( "nonnumeric" ) )
				quotePolicyObj = Quote.NON_NUMERIC;
			else if( quotePolicy.equalsIgnoreCase( "none" ) )
				quotePolicyObj = Quote.NONE;
			else
				quotePolicyObj = Quote.NONE;

			format = format.withQuotePolicy( quotePolicyObj );
		}

		if( escape != null )
		{
			format = format.withEscape( escape );
		}

		format = format.withIgnoreSurroundingSpaces( ignoreSurroundingSpaces );
		format = format.withIgnoreEmptyLines( ignoreEmptyLines );

		//LOG.info( "CSV Format: " + format.toString() );
	}

	@Override
	public String process( String input, Object rawLine )
	{
		//LOG.info( "rawLine: " + rawLine );
		//LOG.info( "INPUT: " + input );
		String output = input;

		try {
			CSVParser parser = format.parse( new StringReader( input ) );

			List<CSVRecord> records = parser.getRecords();

			if( records.size() > 0 )
			{
				StringWriter stringWriter = new StringWriter();
				CSVWriter writer = new CSVWriter( stringWriter, ',', '"', '\\', "\n" );

				CSVRecord record = records.get( 0 );
				String[] values = new String[ record.size() ];
				for( int i = 0; i < record.size(); i++ )
				{
					values[ i ] = record.get( i );// .replaceAll( "\\\"", "'" );
				}

				writer.writeNext( values );
				output = StringUtils.removeEnd( stringWriter.toString(), "\n" );
			}

		} catch( IOException e ) {
			throw new RuntimeException( e );
		}

		//LOG.info( "OUTPUT: " + output );

		return output;
	}
}
