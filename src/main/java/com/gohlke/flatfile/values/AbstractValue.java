package com.gohlke.flatfile.values;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 7:02 PM</p>
 *
 * @author jgohlke
 */
public abstract class AbstractValue
{
	public abstract Object resolve();

	public abstract Object resolve( Object context );

	public abstract Object resolve( Class<?> resolveToClass, Object context );

	public abstract Object resolve( Class<?> resolveToClass );
}
