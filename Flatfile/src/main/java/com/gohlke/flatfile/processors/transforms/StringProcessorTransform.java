package com.gohlke.flatfile.processors.transforms;

import com.gohlke.flatfile.processors.StringProcessor;



/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/29/12</p>
 * <p>Time: 8:28 PM</p>
 *
 * @author jgohlke
 */
public class StringProcessorTransform implements TypeTransform
{
	protected StringProcessor stringProcessor;

	public StringProcessorTransform( StringProcessor stringProcessor )
	{
		this.stringProcessor = stringProcessor;
	}

	@Override
	public Object parse( Object input ) throws UnsupportedOperationException
	{
		if( input instanceof String[] )
		{
			String[] arr = (String[])input;
			for( int i = 0, arrLength = arr.length; i < arrLength; i++ )
			{
				arr[ i ] = (String)parse( arr[ i ] );
			}
			return arr;
		}
		else if( input instanceof String || input == null )
		{
			return stringProcessor.process( (String)input, null );
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
			return stringProcessor.process( (String)input, null );
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
}
