package com.gohlke.flatfile.filters;

import com.gohlke.flatfile.AbstractLineFilter;
import com.gohlke.flatfile.CompoundFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 6:34 PM</p>
 *
 * @author jgohlke
 */
public class NotFilter extends CompoundFilter
{
	private static final transient Logger LOG = LoggerFactory.getLogger( NotFilter.class );

	public NotFilter()
	{
		super();
	}

	public NotFilter( AbstractLineFilter filter )
	{
		super();
		filters.add( filter );
	}

	public void setFilter( AbstractLineFilter filter )
	{
		filters.clear();
		filters.add( filter );
	}

	@Override
	public List<AbstractLineFilter> getFilters()
	{
		return super.getFilters();
	}

	@Override
	public void setFilters( List<AbstractLineFilter> filters )
	{
		if( filters.size() > 0 )
			setFilter( filters.get( 0 ) );
	}

	@Override
	public void addFilter( AbstractLineFilter filter )
	{
		setFilter( filter );
	}

	@Override
	public boolean acceptLine( Object parsedLine )
	{
		return ( filters != null && filters.size() > 0 ) && !filters.get( 0 ).acceptLine( parsedLine );
	}
}
