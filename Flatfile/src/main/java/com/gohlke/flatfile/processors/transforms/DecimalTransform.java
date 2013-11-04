package com.gohlke.flatfile.processors.transforms;


import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/29/12</p>
 * <p>Time: 9:25 PM</p>
 *
 * @author jgohlke
 */
public class DecimalTransform implements TypeTransform
{
	public DecimalTransform()
	{

	}

	@Override
	public Object parse( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		if( input instanceof String )
		{
			return new BigDecimal( (String)input );
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
		else if( input instanceof BigInteger )
		{
			return new BigDecimal( (BigInteger)input );
		}
		else if( input instanceof BigInteger[] )
		{
			BigInteger[] arr = (BigInteger[])input;
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
			throw new UnsupportedOperationException();
	}

	@Override
	public String print( Object input ) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
}
