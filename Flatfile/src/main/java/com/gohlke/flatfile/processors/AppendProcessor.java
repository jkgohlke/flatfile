package com.gohlke.flatfile.processors;

import com.gohlke.flatfile.values.AbstractValue;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 10:16 PM</p>
 *
 * @author jgohlke
 */
public class AppendProcessor extends StringProcessor
{
	protected AbstractValue value;

	public AppendProcessor( AbstractValue value )
	{
		this.value = value;
	}

	@Override
	public String process( String input, Object rawLine )
	{
		if( input == null )
			input = "";

		StringProcessor stringProcessor = (StringProcessor)value.resolve( StringProcessor.class );
		if( stringProcessor == null )
		{
			String s = (String)value.resolve( String.class );
			if( s != null )
				return input.concat( s );
		}
		else
		{
			String s = stringProcessor.process( input, rawLine );
			if( s != null )
				return input.concat( s );
		}
		return null;
	}
}
