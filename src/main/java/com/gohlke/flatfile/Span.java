package com.gohlke.flatfile;

import org.apache.commons.lang.StringUtils;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 4:28 PM</p>
 *
 * @author jgohlke
 */
public class Span
{
	public Integer startIndex = null;
	public Integer endIndex = null;
	public Integer length = null;

	public Span() {

	}

	public Integer getStartIndex()
	{
		return startIndex;
	}

	public void setStartIndex( Integer startIndex )
	{
		this.startIndex = startIndex;
	}

	public Integer getEndIndex()
	{
		return endIndex;
	}

	protected void setEndIndex( Integer endIndex )
	{
		this.endIndex = endIndex;
	}

	public int length()
	{
		return ( this.endIndex - this.startIndex );
	}

	protected Integer getLength()
	{
		return length;
	}

	protected void setLength( Integer length )
	{
		this.length = length;
	}

	public Span resolveCoordinatesFrom( Integer position )
	{
		Span newSpan = new Span();
		newSpan.setStartIndex( getStartIndex() );
		newSpan.setEndIndex( getEndIndex() );
		newSpan.setLength( getLength() );

		if( newSpan.getLength() != null && newSpan.getStartIndex() == null || newSpan.getEndIndex() == null )
		{
			if( newSpan.getStartIndex() != null )
			{
				newSpan.setEndIndex( newSpan.getStartIndex() + newSpan.getLength() );
				newSpan.setLength( null );
			}
			else
			{
				newSpan.setStartIndex( position );
				newSpan.setEndIndex( position + length );
				newSpan.setLength( null );
			}
		}

		return newSpan;
	}

	public String parse( String src )
	{
		if( src == null )
			return null;

		if( startIndex != null )
		{
			if( endIndex != null )
			{
				return StringUtils.substring( src, startIndex, endIndex );
			}
			else
			{
				return StringUtils.substring( src, startIndex );
			}
		}
		else
		{
			throw new RuntimeException( "Invalid span coordinates for " + toString() );
		}
	}

	@Override
	public String toString()
	{
		return "Span{" +
				"startIndex=" + startIndex +
				", endIndex=" + endIndex +
				", length=" + length +
				'}';
	}
}
