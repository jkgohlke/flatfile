package com.gohlke.flatfile.processors.transforms;


import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/27/12</p>
 * <p>Time: 1:01 AM</p>
 *
 * @author jgohlke
 */
public class NumberTransform implements TypeTransform
{
	protected DecimalFormat decimalFormat;
	protected String pattern;

	public NumberTransform( String pattern )
	{
		this.decimalFormat = new DecimalFormat( pattern );
	}

	public DecimalFormat getDecimalFormat()
	{
		return decimalFormat;
	}

	public String getPattern()
	{
		return pattern;
	}

	@Override
	public Object parse( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		if( input instanceof String )
		{
			try
			{
				if( ( (String)input ).length() == 0 )
					return null;
				return decimalFormat.parse( (String)input );
			}
			catch( ParseException e )
			{
				return null;
			}
		}
		else if( input instanceof String[] )
		{
			String[] arr = (String[])input;
			Object[] retArr = new Object[ arr.length ];
			for( int i = 0, input1Length = ( arr ).length; i < input1Length; i++ )
			{
				retArr[ i ] = parse( arr[ i ] );
			}
			return retArr;
		}
		else if( input instanceof Number )
		{
			return input;
		}
		else if( input instanceof Number[] )
		{
			return input;
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

		if( input instanceof String )
		{
			return print( parse( input ) );
		}
		else if( input instanceof Number )
		{
			return decimalFormat.format( input );
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
}
