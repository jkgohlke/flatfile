package com.gohlke.flatfile.processors.transforms;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;



/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/27/12</p>
 * <p>Time: 12:19 AM</p>
 *
 * @author jgohlke
 */
public class DateTimeTransform implements TypeTransform
{
	protected DateTimeFormatter formatter;
	protected Chronology chronology;
	protected DateTimeZone timezone;
	protected String pattern;

	public DateTimeTransform( String pattern, String timezoneId, String chronologyId )
	{
		this.pattern = pattern;
		timezone = DateTimeZone.UTC;
		if( timezoneId != null && timezoneId.length() > 0 )
			timezone = DateTimeZone.forID( timezoneId );

		if( chronologyId == null ||
			chronologyId.equalsIgnoreCase( "ISO" ) ||
			chronologyId.equalsIgnoreCase( "ISO8601" ) )
		{
			chronology = ISOChronology.getInstance( timezone );
		}
		else if( chronologyId.equalsIgnoreCase( "GregorianJulian" ) )
		{
			chronology = GJChronology.getInstance( timezone );
		}
		else if( chronologyId.equalsIgnoreCase( "Gregorian" ) )
		{
			chronology = GregorianChronology.getInstance( timezone );
		}
		else if( chronologyId.equalsIgnoreCase( "Julian" ) )
		{
			chronology = JulianChronology.getInstance( timezone );
		}
		else if( chronologyId.equalsIgnoreCase( "Coptic" ) )
		{
			chronology = CopticChronology.getInstance( timezone );
		}
		else if( chronologyId.equalsIgnoreCase( "Buddhist" ) )
		{
			chronology = BuddhistChronology.getInstance( timezone );
		}
		else if( chronologyId.equalsIgnoreCase( "Ethiopic" ) )
		{
			chronology = EthiopicChronology.getInstance( timezone );
		}

		formatter = DateTimeFormat.forPattern( pattern ).withChronology( chronology ).withZone( timezone );
	}

	public DateTimeFormatter getFormatter()
	{
		return formatter;
	}

	public Chronology getChronology()
	{
		return chronology;
	}

	@Override
	public DateTime parse( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		if( input instanceof String )
		{
			if( ( (String)input ).length() == 0 )
				return null;
			return formatter.parseDateTime( (String)input );
		}
		else if( input instanceof Number )
		{
			Number number = (Number)input;
			return ( new DateTime( number.longValue(), chronology ) ).withZone( timezone );
		}
		else if( input instanceof DateTime )
		{
			DateTime dateTime = (DateTime)input;
			return ( new DateTime( dateTime, chronology ) ).withZone( timezone );
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String print( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		return formatter.print( parse( input ) );
	}
}
