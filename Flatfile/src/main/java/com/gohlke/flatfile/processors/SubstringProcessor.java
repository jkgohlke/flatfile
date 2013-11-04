package com.gohlke.flatfile.processors;

import com.gohlke.flatfile.Span;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 8:53 PM</p>
 *
 * @author jgohlke
 */
public class SubstringProcessor extends StringProcessor
{
	protected Span span = null;

	public SubstringProcessor( Span span )
	{
		this.span = span.resolveCoordinatesFrom( 0 );
	}

	@Override
	public String process( String input, Object rawLine )
	{
		if( input == null )
			return null;

		return span.parse( input );
	}
}
