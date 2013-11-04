package com.gohlke.flatfile.processors;

import org.apache.commons.lang.StringUtils;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/27/12</p>
 * <p>Time: 4:20 AM</p>
 *
 * @author jgohlke
 */
public class PadLeftProcessor extends StringProcessor
{
	protected Integer minLength;
	protected String padWith;

	public PadLeftProcessor( Integer minLength, String padWith )
	{
		this.minLength = minLength;
		this.padWith = padWith;
	}

	public Integer getMinLength()
	{
		return minLength;
	}

	public String getPadWith()
	{
		return padWith;
	}

	@Override
	public String process( String input, Object rawLine )
	{
		return StringUtils.leftPad( input, getMinLength(), getPadWith() );
	}
}
