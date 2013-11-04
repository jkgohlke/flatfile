package com.gohlke.flatfile;

import au.com.bytecode.opencsv.CSVWriter;
import com.gohlke.flatfile.processors.StringProcessor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 3:42 PM</p>
 *
 * @author jgohlke
 */
public class RecordMapping
{
	protected RecordLine parentLine;
	protected String id;
	protected MappingType mappingType;
	protected Span span;
	protected Integer index;
	protected List< StringProcessor > processors;
	protected List< RecordLine > lines;

	protected Map< String, String > additionalAttributes;

	protected RecordMapping()
	{
		processors = new LinkedList<StringProcessor>();
		additionalAttributes = new HashMap<String, String>();
		lines = new LinkedList<RecordLine>();
	}

	public RecordLine getParentLine()
	{
		return parentLine;
	}

	protected void setParentLine( RecordLine parentLine )
	{
		this.parentLine = parentLine;
	}

	public String getId()
	{
		return id;
	}

	protected void setId( String id )
	{
		this.id = id;
	}

	public Span getSpan()
	{
		return span;
	}

	protected void setSpan( Span span )
	{
		this.span = span;
	}

	public Integer getIndex()
	{
		return index;
	}

	protected void setIndex( Integer index )
	{
		this.index = index;
	}

	public List<StringProcessor> getProcessors()
	{
		return Collections.unmodifiableList( processors );
	}

	protected void setProcessors( List<StringProcessor> processors )
	{
		this.processors.clear();
		this.processors.addAll( processors );
	}

	protected void addProcessor( StringProcessor processor )
	{
		processors.add( processor );
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

	public void addLine( RecordLine line )
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

	public MappingType getMappingType()
	{
		if( getSpan() != null )
		{
			return MappingType.FixedWidth;
		}
		else if( getIndex() != null )
		{
			return MappingType.Delimited;
		}
		else
		{
			return MappingType.Compound;
		}
	}

	protected String processGlobalBefore( String parsedField, Object rawLine, RecordMapping mapping, RecordMapping previousMapping )
	{
		if( mapping.getProcessors() != null )
		{
			for( StringProcessor processor : mapping.getProcessors() )
			{
				if( processor.getOrder() == StringProcessor.Order.Before &&
					processor.getScope() == StringProcessor.Scope.Global )
				{
					parsedField = processor.process( parsedField, rawLine );
				}
			}
		}

		if( mapping.getParentLine().getProcessors() != null )
		{
			for( StringProcessor processor : mapping.getParentLine().getProcessors() )
			{
				if( processor.getOrder() == StringProcessor.Order.Before &&
					processor.getScope() == StringProcessor.Scope.Global )
				{
					parsedField = processor.process( parsedField, rawLine );
				}
			}
		}

		if( mapping.getParentLine().getParentRecord() == null &&
			mapping.getParentLine().getParentRecordMapping() != null )
		{
			RecordMapping nextMapping = mapping.getParentLine().getParentRecordMapping();
			parsedField = nextMapping.processGlobalBefore( parsedField, rawLine, nextMapping, mapping );
		}

		return parsedField;
	}

	protected String processGlobalAfter( String parsedField, Object rawLine, RecordMapping mapping, RecordMapping previousMapping )
	{
		if( mapping.getParentLine().getParentRecord() == null &&
			mapping.getParentLine().getParentRecordMapping() != null )
		{
			RecordMapping nextMapping = mapping.getParentLine().getParentRecordMapping();
			//System.out.println( ">>> " + parsedField );
			parsedField = nextMapping.processGlobalAfter( parsedField, rawLine, nextMapping, mapping );
			//System.out.println( "<<< " + parsedField );
		}

		if( mapping.getParentLine().getProcessors() != null )
		{
			for( StringProcessor processor : mapping.getParentLine().getProcessors() )
			{
				if( processor.getOrder() == StringProcessor.Order.After &&
					processor.getScope() == StringProcessor.Scope.Global )
				{
					//System.out.println( "&&& " + processor.getClass().getCanonicalName() );
					parsedField = processor.process( parsedField, rawLine );
				}
			}
		}

		if( mapping.getProcessors() != null )
		{
			for( StringProcessor processor : mapping.getProcessors() )
			{
				if( processor.getOrder() == StringProcessor.Order.After &&
					processor.getScope() == StringProcessor.Scope.Global )
				{
					//System.out.println( "*** " + processor.getClass().getCanonicalName() );
					parsedField = processor.process( parsedField, rawLine );
				}
			}
		}

		parsedField = mapping.processLocalAfter( parsedField, rawLine );
		//System.out.println( "||| " + parsedField );
		return parsedField;
	}

	public String processGlobalBefore( String parsedField, Object rawLine )
	{
		return processGlobalBefore( parsedField, rawLine, this, null );
	}

	public String processGlobalAfter( String parsedField, Object rawLine )
	{
		return processGlobalAfter( parsedField, rawLine, this, null );
	}

	public String processLocalBefore( String parsedField, Object rawLine )
	{
		if( parentLine.getProcessors() != null )
		{
			for( StringProcessor processor : parentLine.getProcessors() )
			{
				if( processor.getOrder() == StringProcessor.Order.Before &&
					processor.getScope() == StringProcessor.Scope.Local )
				{
					parsedField = processor.process( parsedField, rawLine );
				}
			}
		}

		if( getProcessors() != null )
		{
			for( StringProcessor processor : getProcessors() )
			{
				if( processor.getOrder() == StringProcessor.Order.Before &&
					processor.getScope() == StringProcessor.Scope.Local )
				{
					parsedField = processor.process( parsedField, rawLine );
				}
			}
		}

		return parsedField;
	}

	public String processLocalAfter( String parsedField, Object rawLine )
	{
		if( getProcessors() != null )
		{
			for( StringProcessor processor : getProcessors() )
			{
				if( processor.getOrder() == StringProcessor.Order.After &&
					processor.getScope() == StringProcessor.Scope.Local )
				{
					parsedField = processor.process( parsedField, rawLine );
				}
			}
		}

		if( parentLine.getProcessors() != null )
		{
			for( StringProcessor processor : parentLine.getProcessors() )
			{
				if( processor.getOrder() == StringProcessor.Order.After &&
					processor.getScope() == StringProcessor.Scope.Local )
				{
					parsedField = processor.process( parsedField, rawLine );
				}
			}
		}

		return parsedField;
	}

	public String processBefore( String parsedField, Object rawLine )
	{
		parsedField = processGlobalBefore( parsedField, rawLine, this, null );
		return processLocalBefore( parsedField, rawLine );
	}

	public String processAfter( String parsedField, Object rawLine )
	{
		parsedField = processLocalAfter( parsedField, rawLine );
		return processGlobalAfter( parsedField, rawLine, this, null );
	}

	public String parse( Object parsedLine )
	{
		if( parsedLine instanceof String && getMappingType().equals( MappingType.FixedWidth ) )
		{
			return span.parse( (String)parsedLine );
		}
		else if( parsedLine instanceof String[] && getMappingType().equals( MappingType.Delimited ) )
		{
			String[] fields = (String[])parsedLine;

			if( fields.length <= getIndex() )
			{
				return null;
			}
			else
			{
				return fields[ getIndex() ];
			}
		}
		else
		{
			parsedLine = getRawFields( parsedLine, this );
			if( parsedLine != null )
				return parse( parsedLine );
		}

		return null;
	}

	protected Object getRawFields( Object rawLine, RecordMapping mapping )
	{
		try {
			if( mapping.getMappingType() == MappingType.Delimited &&
				rawLine instanceof String )
			{
				return mapping.getParentLine().getCsvParser().parseLine( (String)rawLine );
			}
			else if( mapping.getMappingType() == MappingType.FixedWidth &&
					 rawLine instanceof String[] )
			{
				StringWriter writer = new StringWriter();
				CSVWriter csvWriter = mapping.getParentLine().getCsvWriter( writer );
				csvWriter.writeNext( (String[])rawLine );
				String raw = writer.toString();
				try{
					writer.close();
					csvWriter.close();
				} catch( Exception e ) {
					//Nothing...
				}
				return raw;
			}
			else
			{
				return rawLine;
			}
		} catch( IOException e ) {
			return rawLine;
		}
	}

	public String parseAndProcessRecursively( Object rawLine )
	{
		return parseAndProcessRecursively( rawLine, rawLine, this, null );
	}

	protected String parseAndProcessRecursively( Object parsedLine, Object rawLine, RecordMapping mapping, RecordMapping previousMapping )
	{
		if( mapping.getParentLine().getParentRecord() == null &&
			mapping.getParentLine().getParentRecordMapping() != null )
		{
			// We're dealing with an internal record...

			RecordMapping nextMapping = mapping.getParentLine().getParentRecordMapping();

			String parent = mapping.parseAndProcessRecursively( parsedLine, rawLine, nextMapping, mapping );
			parent = mapping.parse( parent );
			parent = mapping.processBefore( parent, rawLine );
			parent = mapping.processAfter( parent, rawLine );

			return parent;
		}
		else if( mapping.getParentLine().getParentRecord() != null &&
				 mapping.getParentLine().getParentRecordMapping() == null )
		{
			// We're on the bottom floor, so we can just parse it no problem.

			parsedLine = getRawFields( parsedLine, mapping );

			String value = mapping.parse( parsedLine );
			value = mapping.processBefore( value, rawLine );
			value = mapping.processAfter( value, rawLine );

			return value;
		}
		else
		{
			System.out.println( "Error, parent line does not have parent record or parent mapping!!" );
			return null;
		}
	}

	@Override
	public String toString()
	{
		return "RecordMapping{" +
				"id='" + id + '\'' +
				", mappingType=" + getMappingType() +
				", span=" + span +
				", index=" + index +
				'}';
	}
}
