package com.gohlke.flatfile.processors;

import com.gohlke.flatfile.values.AbstractValue;
import org.apache.commons.lang.StringUtils;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 10:16 PM</p>
 *
 * @author jgohlke
 */
public class InsertProcessor extends StringProcessor
{
	protected Integer index;
	protected AbstractValue value;

	public InsertProcessor( Integer index, AbstractValue value )
	{
		this.index = index;
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
				return StringUtils.substring( input, 0, index ) + s + StringUtils.substring( input, index, input.length() );
		}
		else
		{
			String s = stringProcessor.process( input, rawLine );
			if( s != null )
				return StringUtils.substring( input, 0, index ) + s + StringUtils.substring( input, index, input.length() );
		}

		return null;
	}
}
