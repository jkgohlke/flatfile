package com.gohlke.flatfile.values;

import java.util.regex.Pattern;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 11:39 AM</p>
 *
 * @author jgohlke
 */
public class RegexValue extends AbstractValue
{
	Pattern regexPattern;

	public RegexValue( String regexString )
	{
		this.regexPattern = Pattern.compile( regexString );
	}

	@Override
	public Pattern resolve()
	{
		return regexPattern;
	}

	@Override
	public Object resolve( Object context )
	{
		return resolve();
	}

	@Override
	public Object resolve( Class<?> resolveToClass, Object context )
	{
		return resolve( resolveToClass );
	}

	@Override
	public Object resolve( Class<?> resolveToClass )
	{
		if( resolveToClass == null )
			return null;

		Object returnValue;

		Pattern resolvedObject = resolve();
		if( resolvedObject == null )
			return null;

		if( resolveToClass.equals( String.class ) )
		{
			returnValue = resolveToClass.cast( resolvedObject.pattern() );
		}
		else if( resolveToClass.equals( Pattern.class ) )
		{
			returnValue = resolveToClass.cast( resolvedObject );
		}
		else
		{
			try {
				returnValue = resolveToClass.cast( resolvedObject );
			} catch( ClassCastException e ) {
				returnValue = null;
			}
		}

		return returnValue;
	}
}
