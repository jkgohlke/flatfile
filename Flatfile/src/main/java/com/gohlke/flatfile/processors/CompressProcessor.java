package com.gohlke.flatfile.processors;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/29/12</p>
 * <p>Time: 9:05 PM</p>
 *
 * @author jgohlke
 */
public class CompressProcessor extends StringProcessor
{
	@Override
	public String process( String input, Object rawLine )
	{
		if( input == null )
			return null;

		return input.replaceAll( "[ ]{2,}", " " );
	}
}
