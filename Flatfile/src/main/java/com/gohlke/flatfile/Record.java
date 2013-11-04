package com.gohlke.flatfile;

import java.util.*;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 3:32 PM</p>
 *
 * @author jgohlke
 */
public class Record implements Comparable< Record >
{
	protected String id = null;
	protected Long rank = null;

	protected AbstractLineFilter lineFilter = null;
	protected List<RecordLine> lines;

	protected Map< String, String > additionalAttributes;

	protected Record()
	{
		lines = new LinkedList<RecordLine>();
		additionalAttributes = new HashMap<String, String>();
	}

	public String getId()
	{
		return id;
	}

	protected void setId( String id )
	{
		this.id = id;
	}

	public Long getRank()
	{
		return rank;
	}

	protected void setRank( Long rank )
	{
		this.rank = rank;
	}

	public AbstractLineFilter getLineFilter()
	{
		return lineFilter;
	}

	protected void setLineFilter( AbstractLineFilter lineFilter )
	{
		this.lineFilter = lineFilter;
		this.lineFilter.setParentRecord( this );
	}

	public List<RecordLine> getLines()
	{
		return Collections.unmodifiableList( lines );
	}

	protected void setLines( List<RecordLine> lines )
	{
		this.lines.clear();
		this.lines.addAll( lines );
	}

	protected void addLine( RecordLine line )
	{
		lines.add( line );
	}

	public Map<String, String> getAdditionalAttributes()
	{
		return Collections.unmodifiableMap( additionalAttributes );
	}

	public void setAdditionalAttributes( Map<String, String> additionalAttributes )
	{
		this.additionalAttributes.clear();
		this.additionalAttributes.putAll( additionalAttributes );
	}

	@Override
	public int compareTo( Record o )
	{
		return o.getRank().compareTo( getRank() );
	}

	public MappingType getMappingType()
	{
		for( RecordLine line : lines )
		{
			for( RecordMapping mapping : line.getMappings() )
			{
				if( mapping.getMappingType() != MappingType.Compound &&
					mapping.getMappingType() != null )
					return mapping.getMappingType();
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		return "Record{" +
				"id='" + id + '\'' +
				", rank=" + rank +
				", lineFilter=" + lineFilter +
				", lines=" + lines +
				'}';
	}
}
