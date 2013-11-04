package com.gohlke.flatfile.processors;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 9:14 PM</p>
 *
 * @author jgohlke
 */
public class LowercaseProcessor extends StringProcessor
{
	@Override
	public String process( String input, Object rawLine )
	{
		if( input == null )
			return null;
		return input.toLowerCase();
	}
}
