package com.gohlke.flatfile.processors;

import org.apache.commons.lang.WordUtils;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/29/12</p>
 * <p>Time: 10:09 PM</p>
 *
 * @author jgohlke
 */
public class CapitalizeProcessor extends StringProcessor
{
	@Override
	public String process( String input, Object rawLine )
	{
		if( input == null )
			return null;
		return WordUtils.capitalizeFully( input );
	}
}
