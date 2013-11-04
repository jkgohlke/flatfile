package com.gohlke.flatfile.processors;

import com.gohlke.flatfile.values.AbstractValue;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 11/2/12</p>
 * <p>Time: 8:44 PM</p>
 *
 * @author jgohlke
 */
public class SetProcessor extends StringProcessor
{
	protected boolean onlyIfEmpty;
	protected AbstractValue value;

	public SetProcessor( AbstractValue value, boolean onlyIfEmpty )
	{
		this.value = value;
		this.onlyIfEmpty = onlyIfEmpty;
	}

	@Override
	public String process( String input, Object rawLine )
	{
		if( input != null &&
			input.length() > 0 &&
			onlyIfEmpty )
			return input;

		StringProcessor stringProcessor = (StringProcessor)value.resolve( StringProcessor.class );
		if( stringProcessor == null )
		{
			return (String)value.resolve( String.class );
		}
		else
		{
			return stringProcessor.process( input, rawLine );
		}
	}
}
