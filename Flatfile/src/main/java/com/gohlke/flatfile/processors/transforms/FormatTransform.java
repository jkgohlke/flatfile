package com.gohlke.flatfile.processors.transforms;

import com.gohlke.flatfile.processors.StringProcessor;



/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 9:22 PM</p>
 *
 * @author jgohlke
 */
public class FormatTransform extends StringProcessor implements TypeTransform
{
	String pattern;

	public FormatTransform( String pattern )
	{
		this.pattern = pattern;
	}

	@Override
	public String parse( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		return String.format( pattern, input );
	}

	@Override
	public String print( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		return String.format( pattern, input );
	}

	@Override
	public String process( String input, Object rawLine )
	{
		return String.format( pattern, input );
	}
}
