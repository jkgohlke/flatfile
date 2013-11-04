package com.gohlke.flatfile;

import com.gohlke.flatfile.Record;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 6:37 PM</p>
 *
 * @author jgohlke
 */
public abstract class AbstractLineFilter
{
	protected Record parentRecord;

	public Record getParentRecord()
	{
		return parentRecord;
	}

	protected void setParentRecord( Record parentRecord )
	{
		this.parentRecord = parentRecord;
	}

	public abstract boolean acceptLine( Object parsedLine );
}
