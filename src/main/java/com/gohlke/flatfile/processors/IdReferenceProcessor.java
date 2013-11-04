package com.gohlke.flatfile.processors;

import com.gohlke.flatfile.RecordMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 11/2/12</p>
 * <p>Time: 9:31 PM</p>
 *
 * @author jgohlke
 */
public class IdReferenceProcessor extends StringProcessor
{
	private static final transient Logger LOG = LoggerFactory.getLogger( IdReferenceProcessor.class );

	protected Object reference;

	public IdReferenceProcessor( Object reference )
	{
		this.reference = reference;
	}

	@Override
	public String process( String input, Object rawLine )
	{
		//if( input != null && rawLine == null )
		//	rawLine = input;

		if( rawLine == null )
			throw new UnsupportedOperationException( "No line was specified!" );

		if( reference instanceof RecordMapping )
		{
			if( rawLine instanceof String[] ||
				rawLine instanceof String )
			{
				RecordMapping mapping = (RecordMapping)reference;
				return mapping.parseAndProcessRecursively( rawLine );
			}
		}
		return null;
	}
}
