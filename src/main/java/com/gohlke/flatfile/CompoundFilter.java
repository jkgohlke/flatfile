package com.gohlke.flatfile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 2/6/13</p>
 * <p>Time: 2:14 PM</p>
 *
 * @author jgohlke
 */
public abstract class CompoundFilter extends AbstractLineFilter
{
	protected List<AbstractLineFilter> filters;

	protected CompoundFilter()
	{
		this.filters = new LinkedList<AbstractLineFilter>();
	}

	public List<AbstractLineFilter> getFilters()
	{
		return Collections.unmodifiableList( this.filters );
	}

	public void setFilters( List<AbstractLineFilter> filters )
	{
		this.filters = new LinkedList<AbstractLineFilter>( filters );
	}

	public void addFilter( AbstractLineFilter filter )
	{
		this.filters.add( filter );
	}

	@Override
	public Record getParentRecord()
	{
		return super.getParentRecord();
	}

	@Override
	public void setParentRecord( Record parentRecord )
	{
		super.setParentRecord( parentRecord );
		for( AbstractLineFilter filter : this.filters )
			filter.setParentRecord( parentRecord );
	}

	@Override
	public abstract boolean acceptLine( Object parsedLine );
}
