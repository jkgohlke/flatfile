package com.gohlke.flatfile.values;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 7:05 PM</p>
 *
 * @author jgohlke
 */
public class ConstantValue extends AbstractValue
{
	String value;

	public ConstantValue( String value )
	{
		this.value = value;
	}

	@Override
	public String resolve()
	{
		return value;
	}

	@Override
	public String resolve( Object context )
	{
		return resolve();
	}

	public Object resolve( Class<?> resolveToClass, Object context )
	{
		if( resolveToClass == null )
			return null;

		Object returnValue = null;

		Object resolvedObject = resolve( context );
		if( resolvedObject == null )
			return null;

		if( resolveToClass.equals( String.class ) )
		{
			if( resolvedObject instanceof String )
				returnValue = resolveToClass.cast( resolvedObject.toString() );
			else if( resolveToClass.isPrimitive() )
				returnValue = resolveToClass.cast( String.valueOf( resolvedObject ) );
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

	public Object resolve( Class<?> resolveToClass )
	{
		return resolve( resolveToClass, null );
	}
}
