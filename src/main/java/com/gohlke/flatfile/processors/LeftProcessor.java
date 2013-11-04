package com.gohlke.flatfile.processors;

import org.apache.commons.lang.StringUtils;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 9:15 PM</p>
 *
 * @author jgohlke
 */
public class LeftProcessor extends StringProcessor
{
	protected Integer length;

	public LeftProcessor( Integer length )
	{
		this.length = length;
	}

	@Override
	public String process( String input, Object rawLine )
	{
		return StringUtils.left( input, length );
	}
}
