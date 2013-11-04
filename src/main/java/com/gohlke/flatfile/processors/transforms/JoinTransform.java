package com.gohlke.flatfile.processors.transforms;

import org.apache.commons.lang.StringUtils;


import java.util.Collection;
import java.util.Iterator;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/27/12</p>
 * <p>Time: 1:18 AM</p>
 *
 * @author jgohlke
 */
public class JoinTransform implements TypeTransform
{
	protected String separator;

	public JoinTransform()
	{
		this( null );
	}

	public JoinTransform( String separator )
	{
		this.separator = separator == null ? "" : separator;
	}

	public String getSeparator()
	{
		return separator;
	}

	@Override
	public String parse( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		if( input instanceof Object[] )
		{
			return StringUtils.join( (Object[])input, separator );
		}
		else if( input instanceof Iterator )
		{
			return StringUtils.join( (Iterator)input, separator );
		}
		else if( input instanceof Collection )
		{
			return StringUtils.join( (Collection)input, separator );
		}
		else if( input instanceof String )
		{
			return (String)input;
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String print( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		return parse( input );
	}
}
