package com.gohlke.flatfile.processors;

import org.apache.commons.lang.StringUtils;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 8:46 PM</p>
 *
 * @author jgohlke
 */
public class StripProcessor extends StringProcessor
{
	protected String pattern;

	public StripProcessor( String pattern )
	{
		this.pattern = pattern;
	}

	@Override
	public String process( String input, Object rawLine )
	{
		if( input == null )
			return null;

		return StringUtils.strip( input, pattern );
	}
}
