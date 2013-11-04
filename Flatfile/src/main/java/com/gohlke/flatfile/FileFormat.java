package com.gohlke.flatfile;

import java.nio.charset.Charset;
import java.util.*;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 3:31 PM</p>
 *
 * @author jgohlke
 */
public class FileFormat
{
	protected String id = null;
	protected Map< String, Record > records = null;
	protected Set< Record > recordOrder = null;
	protected Charset charset = null;
	protected Long skipLines = null;
	protected String lineSeparator = null;
	protected Integer csvReadAheadLinesLimit = null;

	public FileFormat()
	{
		records = new HashMap<String, Record>();
		recordOrder = new TreeSet<Record>();
	}

	public Map<String, Record> getRecords()
	{
		return Collections.unmodifiableMap( records );
	}

	protected void setRecords( Map<String, Record> records )
	{
		this.records.clear();
		this.records.putAll( records );
		recordOrder.clear();
		recordOrder.addAll( this.records.values() );
	}

	protected void addRecord( Record r )
	{
		String recordId = r.getId();
		if( recordId != null )
		{
			records.put( recordId, r );
		}
		recordOrder.add( r );
	}

	public Record getRecord( String name )
	{
		return records.get( name );
	}

	public Set<Record> getRecordOrder()
	{
		return Collections.unmodifiableSet( recordOrder );
	}

	public Charset getCharset()
	{
		return charset;
	}

	protected void setCharset( Charset charset )
	{
		this.charset = charset;
	}

	public Integer getCsvReadAheadLinesLimit()
	{
		return csvReadAheadLinesLimit;
	}

	protected void setCsvReadAheadLinesLimit( Integer csvReadAheadLinesLimit )
	{
		this.csvReadAheadLinesLimit = csvReadAheadLinesLimit;
	}

	public Long getSkipLines()
	{
		return skipLines;
	}

	protected void setSkipLines( Long skipLines )
	{
		this.skipLines = skipLines;
	}

	public String getLineSeparator()
	{
		return lineSeparator;
	}

	protected void setLineSeparator( String lineSeparator )
	{
		this.lineSeparator = lineSeparator;
	}

	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	@Override
	public String toString()
	{
		return "FileFormat{" +
				"records=" + records +
				", recordOrder=" + recordOrder +
				", charset=" + charset +
				", skipLines=" + skipLines +
				", lineSeparator='" + lineSeparator + '\'' +
				'}';
	}
}
