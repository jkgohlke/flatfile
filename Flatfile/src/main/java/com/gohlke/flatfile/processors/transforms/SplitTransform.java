package com.gohlke.flatfile.processors.transforms;

import org.apache.commons.lang.StringUtils;


import java.util.ArrayList;
import java.util.Collections;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/27/12</p>
 * <p>Time: 1:11 AM</p>
 *
 * @author jgohlke
 */
public class SplitTransform implements TypeTransform
{
	protected String separator = null;

	public SplitTransform()
	{
		this( null );
	}

	public SplitTransform( String separator )
	{
		this.separator = separator;
	}

	public String getSeparator()
	{
		return separator;
	}

	public String[] split( String input, String separator )
	{
		if( separator == null || separator.length() == 0 )
		{
			String[] retArr = new String[ input.length() ];
			char[] charArray = input.toCharArray();
			for( int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++ )
			{
				Character c = charArray[ i ];
				retArr[ i ] = String.valueOf( c );
			}
			return retArr;
		}
		else
			return StringUtils.split( input, separator );
	}

	@Override
	public String[] parse( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		if( input instanceof String )
		{
			return StringUtils.split( (String)input, separator );
		}
		else if( input instanceof String[] )
		{
			ArrayList< String > outputList = new ArrayList<String>();
			String[] inputArray = (String[])input;
			for( String intStr : inputArray )
			{
				if( intStr == null )
					continue;
				String[] arr = parse( intStr );
				if( arr == null )
					continue;

				Collections.addAll( outputList, arr );
			}
			return (String[])outputList.toArray();
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String print( Object input ) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
}
