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
public class IntegerTransform implements TypeTransform
{
	protected int radix;

	public IntegerTransform( Integer radix )
	{
		this.radix = ( radix == null ) ? 10 : radix;
	}

	@Override
	public Object parse( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		if( input instanceof String )
		{
			return new BigInteger( (String)input, radix );
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
		else if( input instanceof BigDecimal )
		{
			return ( (BigDecimal)input ).toBigInteger();
		}
		else if( input instanceof BigDecimal[] )
		{
			BigDecimal[] arr = (BigDecimal[])input;
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
		input = parse( input );
		if( input instanceof BigInteger )
		{
			return ( (BigInteger)input ).toString( radix );
		}
		else if( input instanceof Number )
		{
			return ( BigInteger.valueOf( ( (Number)input ).longValue() ) ).toString( radix );
		}
		else
			throw new UnsupportedOperationException();
	}
}
