package com.gohlke.flatfile.filters;

import com.gohlke.flatfile.AbstractLineFilter;
import com.gohlke.flatfile.CompoundFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 2/6/13</p>
 * <p>Time: 2:10 PM</p>
 *
 * @author jgohlke
 */
public class OneOfFilter extends CompoundFilter
{
	private static final transient Logger LOG = LoggerFactory.getLogger( OneOfFilter.class );

	public OneOfFilter()
	{
		super();
	}

	@Override
	public boolean acceptLine( Object parsedLine )
	{
		boolean accept = false;

		for( AbstractLineFilter filter : this.filters )
		{
			accept = filter.acceptLine( parsedLine );

			if( accept )
				break;
		}

		return accept;
	}
}
