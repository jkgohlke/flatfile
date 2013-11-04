package com.gohlke.flatfile.values;

import com.gohlke.flatfile.FileFormat;
import com.gohlke.flatfile.Record;
import com.gohlke.flatfile.RecordLine;
import com.gohlke.flatfile.RecordMapping;
import com.gohlke.flatfile.processors.StringProcessor;
import com.gohlke.flatfile.processors.IdReferenceProcessor;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 7:05 PM</p>
 *
 * @author jgohlke
 */
public class IdReferenceValue extends AbstractValue
{
	protected FileFormat fileFormat;
	protected String referenceId;

	public IdReferenceValue( FileFormat fileFormat, String referenceId )
	{
		this.fileFormat = fileFormat;
		this.referenceId = referenceId;
	}

	protected Object resolveObject( RecordMapping mapping )
	{
		Object retObj;

		for( RecordLine line : mapping.getLines() )
		{
			if( ( retObj = resolveObject( line ) ) != null )
				return retObj;
		}

		if( mapping.getId() != null && mapping.getId().equals( referenceId ) )
			return mapping;

		return null;
	}

	protected Object resolveObject( RecordLine line )
	{
		Object retObj;

		for( RecordMapping mapping : line.getMappings() )
		{
			if( ( retObj = resolveObject( mapping ) ) != null )
				return retObj;
		}

		if( line.getId() != null && line.getId().equals( referenceId ) )
			return line;

		return null;
	}

	protected Object resolveObject( Record record )
	{
		Object retObj;
		for( RecordLine line : record.getLines() )
		{
			if( ( retObj = resolveObject( line ) ) != null )
				return retObj;
		}

		if( record.getId() != null && record.getId().equals( referenceId ) )
			return record;

		return null;
	}

	protected Object resolveObject( FileFormat fileFormat )
	{
		Object retObj;
		for( Record record : fileFormat.getRecordOrder() )
		{
			if( ( retObj = resolveObject( record ) ) != null )
				return retObj;
		}

		if( fileFormat.getId() != null && fileFormat.getId().equals( referenceId ) )
			return fileFormat;

		return null;
	}

	protected Object resolveObject( Object context )
	{
		if( context instanceof FileFormat )
		{
			return resolveObject( (FileFormat)context );
		}
		else if( context instanceof Record )
		{
			return resolveObject( (Record)context );
		}
		else if( context instanceof RecordLine )
		{
			return resolveObject( (RecordLine)context );
		}
		else if( context instanceof RecordMapping )
		{
			return resolveObject( (RecordMapping)context );
		}

		return null;
	}

	@Override
	public Object resolve()
	{
		return resolve( fileFormat );
	}

	@Override
	public Object resolve( Object context )
	{
		Object value = resolveObject( context );
		if( value == null )
			throw new RuntimeException( "Could not resolve element with id '" + referenceId + "'." );
		return value;
	}

	@Override
	public Object resolve( Class<?> resolveToClass, Object context )
	{
		if( resolveToClass == null )
			return null;

		Object returnValue;

		Object resolvedObject = resolve( context );
		if( resolvedObject == null )
			return null;

		if( resolveToClass.equals( StringProcessor.class ) )
		{
			StringProcessor stringProcessor = new IdReferenceProcessor( resolvedObject );
			returnValue = resolveToClass.cast( stringProcessor );
		}
		else
		{
			try {
				returnValue = resolveToClass.cast( resolvedObject );
			} catch( ClassCastException e ) {
				e.printStackTrace();
				returnValue = null;
			}
		}

		return returnValue;
	}

	@Override
	public Object resolve( Class<?> resolveToClass )
	{
		return resolve( resolveToClass, fileFormat );
	}
}
