package com.gohlke.flatfile.processors.transforms;



/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/27/12</p>
 * <p>Time: 12:20 AM</p>
 *
 * @author jgohlke
 */
public interface TypeTransform
{
	public Object parse( Object input ) throws UnsupportedOperationException;
	public String print( Object input ) throws UnsupportedOperationException;
}
