package com.gohlke.flatfile;

import com.gohlke.flatfile.filters.*;
import com.gohlke.flatfile.processors.*;
import com.gohlke.flatfile.processors.transforms.*;
import com.gohlke.flatfile.values.AbstractValue;
import com.gohlke.flatfile.values.ConstantValue;
import com.gohlke.flatfile.values.IdReferenceValue;
import com.gohlke.flatfile.values.RegexValue;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 5:29 PM</p>
 *
 * @author jgohlke
 */
public class FileFormatFactory
{
	private static final transient Logger LOG = LoggerFactory.getLogger( FileFormatFactory.class );

	private String[] valueTypes = new String[]{ "reference", "constant", "regex" };
	private String[] operationTypes = new String[]{ "matches", "not", "allof", "oneof" };
	private String[] compoundOperationTypes = new String[]{ "allof", "oneof" };

	public FileFormat createFileFormat( InputStream in ) throws ParserConfigurationException, IOException, SAXException
	{
		DocumentBuilder parser;
		Document document;
		NodeList children;

		fileFormat = null;
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		parser = fact.newDocumentBuilder();
		document = parser.parse( ( new org.xml.sax.InputSource( in ) ) );
		children = document.getChildNodes();
		for( int i = 0; i < children.getLength(); i++ )
		{
			Node child = children.item( i );
			if( ( "file-format".equals( child.getNodeName() ) ) && ( child.getNodeType() == Node.ELEMENT_NODE ) )
			{
				return (FileFormat)traverse( child );
			}
		}
		return null;
	}

	private List<Object> getChildNodes( Node node )
	{
		List<Object> nodes = new ArrayList<Object>();
		NodeList children = node.getChildNodes();
		if( children != null )
		{
			for( int i = 0; i < children.getLength(); i++ )
			{
				Node child = children.item( i );
				Object o = traverse( child );
				if( o != null )
				{
					nodes.add( o );
				}
			}
		}
		return nodes;
	}

	private boolean isElementNodeOfType( String type, Node node )
	{
		return type.equals( node.getNodeName() ) && node.getNodeType() == 1;
	}

	private Node getChildElementNodeOfType( String type, Node node )
	{
		NodeList children = node.getChildNodes();
		if( children != null )
		{
			for( int i = 0; i < children.getLength(); i++ )
			{
				Node child = children.item( i );
				if( type.equals( child.getNodeName() ) && child.getNodeType() == 1 )
				{
					return child;
				}
			}
		}
		return null;
	}

	private Node getChildElementNodeOfType( String[] types, Node node )
	{
		NodeList children = node.getChildNodes();
		if( children != null )
		{
			for( int i = 0; i < children.getLength(); i++ )
			{
				Node child = children.item( i );
				for( String type : types )
				{
					if( type.equals( child.getNodeName() ) && child.getNodeType() == 1 )
					{
						return child;
					}
				}
			}
		}
		return null;
	}

	private List<Node> getChildElementNodesOfType( String type, Node node )
	{
		List<Node> nodes = new ArrayList<Node>();
		NodeList children = node.getChildNodes();
		if( children != null )
		{
			for( int i = 0; i < children.getLength(); i++ )
			{
				Node child = children.item( i );
				if( type.equals( child.getNodeName() ) && child.getNodeType() == 1 )
				{
					nodes.add( child );
				}
			}
		}
		return nodes;
	}

	private String getChildTextNodeValue( Node node )
	{
		NodeList children = node.getChildNodes();
		if( children != null )
		{
			for( int i = 0; i < children.getLength(); i++ )
			{
				Node child = children.item( i );
				if( child.getNodeType() == 3 )
				{
					return child.getNodeValue();
				}
			}
		}
		return null;
	}

	private boolean hasAttributeValueNamed( Node node, String name )
	{
		return node.getAttributes().getNamedItem( name ) != null;
	}

	private Map<String, String> getAttributes( Node node )
	{
		HashMap< String, String > retMap = new HashMap<String, String>();
		NamedNodeMap namedNodeMap = node.getAttributes();
		for( int i = 0; i < namedNodeMap.getLength(); i++ )
		{
			Node n = namedNodeMap.item( i );
			retMap.put( n.getNodeName(), n.getNodeValue() );
		}

		return retMap;
	}

	private String getAttributeValueNamed( Node node, String name )
	{
		return hasAttributeValueNamed( node, name ) ? node.getAttributes().getNamedItem( name ).getNodeValue() : null;
	}

	private String optAttributeValueNamed( Node node, String name, String def )
	{
		return hasAttributeValueNamed( node, name ) ? node.getAttributes().getNamedItem( name ).getNodeValue() : def;
	}

	private Node getAttributeNamed( Node node, String name )
	{
		NamedNodeMap map = node.getAttributes();
		return map.getNamedItem( name );
	}

	private AbstractLineFilter parseIfNode( Node node )
	{
		AbstractValue leftValue = (AbstractValue)traverse( getChildElementNodeOfType( valueTypes, node ) );

		if( leftValue != null )
		{
			AbstractLineFilter filter = (AbstractLineFilter)traverse( getChildElementNodeOfType( operationTypes, node ) );

			if( filter instanceof MatchesFilter )
			{
				MatchesFilter matchesFilter = (MatchesFilter)filter;
				matchesFilter.setLeft( leftValue );
			}

			return filter;
		}

		return null;
	}

	private AbstractValue leftTmp;
	private AbstractValue rightTmp;
	private FileFormat fileFormat;
	private Object traverse( Node node )
	{
		if( node == null )
			return null;

		int type = node.getNodeType();
		if( type == Node.ELEMENT_NODE )
		{
			String nodeName = node.getNodeName();
			if( nodeName.equals( "file-format" ) )
			{
				fileFormat = new FileFormat();

				if( hasAttributeValueNamed( node, "id" ) )
				{
					fileFormat.setId( getAttributeValueNamed( node, "id" ) );
				}

				fileFormat.setCsvReadAheadLinesLimit( getAttributeValueNamed( node, "csvReadAheadLimit" ) != null ? Integer.parseInt( getAttributeValueNamed( node, "csvReadAheadLimit" ) ) : null );
				fileFormat.setCharset( Charset.forName( optAttributeValueNamed( node, "encoding", "ISO-8859-1" ) ) );
				fileFormat.setSkipLines( Long.parseLong( optAttributeValueNamed( node, "skipLines", "0" ) ) );
				fileFormat.setLineSeparator( optAttributeValueNamed( node, "lineSeparator", "\\r\\n" ) );

				List<Record> records = new ArrayList<Record>();

				List<Object> children = getChildNodes( node );
				for( Object child : children )
				{
					if( child.getClass().equals( Record.class ) )
					{
						records.add( (Record)child );
					}
				}

				for( Record nextRecord : records )
				{
					if( nextRecord.getRank() == null )
					{
						Long highestRank = null;

						for( Record record : fileFormat.getRecordOrder() )
						{
							if( highestRank == null )
							{
								highestRank = record.getRank();
							}
							else if( highestRank < record.getRank() )
							{
								highestRank = record.getRank();
							}
						}

						if( highestRank == null )
						{
							nextRecord.setRank( (long)records.size() );
						}
						else
						{
							nextRecord.setRank( highestRank - fileFormat.getRecordOrder().size() );
						}
					}
					fileFormat.addRecord( nextRecord );
				}
				FileFormat f = fileFormat;
				fileFormat = null;
				return f;
			}
			else if( nodeName.equals( "record" ) )
			{
				Record r = new Record();
				r.setId( getAttributeValueNamed( node, "id" ) );
				if( hasAttributeValueNamed( node, "rank" ) )
				{
					r.setRank( Long.parseLong( getAttributeValueNamed( node, "rank" ) ) );
				}

				Map< String, String > additionalAttr = getAttributes( node );
				additionalAttr.remove( "id" );
				additionalAttr.remove( "rank" );
				r.setAdditionalAttributes( additionalAttr );

				Node filterNode = getChildElementNodeOfType( "filter", node );
				if( filterNode != null )
				{
					AbstractLineFilter lineFilter = (AbstractLineFilter)traverse( filterNode );
					lineFilter.setParentRecord( r );
					r.setLineFilter( lineFilter );
				}
				List<Object> children = getChildNodes( node );
				for( Object o : children )
				{
					if( o.getClass().equals( RecordLine.class ) )
					{
						RecordLine line = (RecordLine)o;
						line.setParentRecord( r );
						r.addLine( line );
					}
				}
				return r;
			}
			else if( nodeName.equals( "line" ) )
			{
				RecordLine li = new RecordLine();
				if( hasAttributeValueNamed( node, "id" ) )
					li.setId( getAttributeValueNamed( node, "id" ) );

				Node delimit = getAttributeNamed( node, "delimiter" );
				if( delimit != null )
				{
					if( hasAttributeValueNamed( node, "delimiter" ) )
					{
						String delim = getAttributeValueNamed( node, "delimiter" );
						if( delim != null )
						{
							li.setDelimiter( StringEscapeUtils.unescapeJava( delim ).charAt( 0 ) );
						}
					}

					if( hasAttributeValueNamed( node, "escape" ) )
					{
						String escape = getAttributeValueNamed( node, "escape" );
						if( escape != null )
						{
							li.setEscapeCharacter( StringEscapeUtils.unescapeJava( escape ).charAt( 0 ) );
						}
					}

					if( hasAttributeValueNamed( node, "quote" ) )
					{
						String quote = getAttributeValueNamed( node, "quote" );
						if( quote != null )
						{
							li.setQuoteCharacter( StringEscapeUtils.unescapeJava( quote ).charAt( 0 ) );
						}
					}
				}

				Node length = getAttributeNamed( node, "length" );
				if( length != null )
				{
					Long lineLength = Long.parseLong( getAttributeValueNamed( node, "length" ) );
					li.setMaxLength( lineLength );
					li.setMinLength( lineLength );
				}
				else
				{
					Node minLength = getAttributeNamed( node, "minLength" );
					if( minLength != null )
					{
						li.setMinLength( Long.parseLong( getAttributeValueNamed( node, "minLength" ) ) );
					}
					Node maxLength = getAttributeNamed( node, "maxLength" );
					if( maxLength != null )
					{
						li.setMaxLength( Long.parseLong( getAttributeValueNamed( node, "maxLength" ) ) );
					}
				}

				List<Object> v = getChildNodes( node );
				for( Object o : v )
				{
					if( o instanceof RecordMapping )
					{
						RecordMapping mapping = (RecordMapping)o;
						mapping.setParentLine( li );

						if( !mapping.getAdditionalAttributes().containsKey( "compound" ) )
						{
							if( mapping.getIndex() != null )
							{
								if( mapping.getIndex() > 0 || li.getMappings().size() == 0 )
									mapping.setIndex( li.getMappings().size() );
								else
								{
									Integer lastIndex = li.getMappings().get( li.getMappings().size() - 1 ).getIndex();
									mapping.setIndex( lastIndex );
								}
							}
							else
							{
								if( li.getMappings().size() == 0 )
									mapping.setIndex( li.getMappings().size() );
								else
								{
									Integer lastIndex = li.getMappings().get( li.getMappings().size() - 1 ).getIndex();
									mapping.setIndex( lastIndex + 1 );
								}
							}

							if( mapping.getSpan() != null )
							{
								if( li.getMappings().size() > 0 )
								{
									RecordMapping prevMapping = li.getMappings().get( li.getMappings().size() - 1 );
									if( prevMapping.getSpan() == null )
										mapping.setSpan( mapping.getSpan().resolveCoordinatesFrom( 0 ) );
									else
										mapping.setSpan( mapping.getSpan().resolveCoordinatesFrom( prevMapping.getSpan().getEndIndex() ) );
								}
								else
								{
									mapping.setSpan( mapping.getSpan().resolveCoordinatesFrom( 0 ) );
								}
							}
							else
							{
								if( li.getMappings().size() > 0 )
								{
									RecordMapping prevMapping = li.getMappings().get( li.getMappings().size() - 1 );
									if( prevMapping.getSpan() != null )
										mapping.setSpan( prevMapping.getSpan() );
								}
							}

							//System.out.println( mapping.getId() );
						}
						li.addRecordMapping( mapping );
					}
				}

				List<StringProcessor> processors = (List<StringProcessor>)traverse( getChildElementNodeOfType( "process", node ) );
				if( processors != null )
					li.setProcessors( processors );

				List<StringProcessor> preprocessors = (List<StringProcessor>)traverse( getChildElementNodeOfType( "preprocess", node ) );
				if( preprocessors != null )
					li.setPreprocessors( preprocessors );

				return li;
			}

			else if( nodeName.equals( "filter" ) )
			{
				CompoundFilter filter = (CompoundFilter)traverse( getChildElementNodeOfType( compoundOperationTypes, node ) );

				if( filter == null )
				{
					AbstractLineFilter innerFilter = parseIfNode( node );
					if( innerFilter != null )
					{
						filter = new OneOfFilter();
						filter.addFilter( innerFilter );
					}
				}

				return filter;
			}
			else if( nodeName.equals( "if" ) )
			{
				return parseIfNode( node );
			}

			else if( nodeName.equals( "allof" ) )
			{
				AllOfFilter filter = new AllOfFilter();

				List<Object> children = getChildNodes( node );
				for( Object o : children )
				{
					if( o instanceof AbstractLineFilter )
						filter.addFilter( (AbstractLineFilter)o );
				}

				return filter;
			}
			else if( nodeName.equals( "oneof" ) )
			{
				OneOfFilter filter = new OneOfFilter();

				List<Object> children = getChildNodes( node );
				for( Object o : children )
				{
					if( o instanceof AbstractLineFilter )
						filter.addFilter( (AbstractLineFilter)o );
				}

				return filter;
			}
			else if( nodeName.equals( "matches" ) )
			{
				MatchesFilter filter = new MatchesFilter();
				AbstractValue rightValue = (AbstractValue)traverse( getChildElementNodeOfType( valueTypes, node ) );
				filter.setRight( rightValue );
				return filter;
			}
			else if( nodeName.equals( "not" ) )
			{
				NotFilter filter = new NotFilter();
				AbstractLineFilter innerFilter = (AbstractLineFilter)traverse( getChildElementNodeOfType( operationTypes, node ) );
				filter.setFilter( innerFilter );
				return filter;
			}

			else if( nodeName.equals( "reference" ) )
			{
				return new IdReferenceValue( fileFormat, getChildTextNodeValue( node ) );
			}
			else if( nodeName.equals( "constant" ) )
			{
				return new ConstantValue( getChildTextNodeValue( node ) );
			}
			else if( nodeName.equals( "regex" ) )
			{
				return new RegexValue( getChildTextNodeValue( node ) );
			}

			else if( nodeName.equals( "boolean" ) )
			{
				String format = optAttributeValueNamed( node, "format", "any" );

				BooleanTransform.Format boolFormat = null;
				if( format.equalsIgnoreCase( "any" ) )
				{
					boolFormat = BooleanTransform.Format.Any;
				}
				else if( format.equalsIgnoreCase( "truefalse" ) )
				{
					boolFormat = BooleanTransform.Format.TrueFalse;
				}
				else if( format.equalsIgnoreCase( "yn" ) )
				{
					boolFormat = BooleanTransform.Format.YN;
				}
				else if( format.equalsIgnoreCase( "yesno" ) )
				{
					boolFormat = BooleanTransform.Format.YesNo;
				}
				else if( format.equalsIgnoreCase( "bit" ) )
				{
					boolFormat = BooleanTransform.Format.Bit;
				}

				if( boolFormat != null )
				{
					return new BooleanTransform( boolFormat );
				}
			}
			else if( nodeName.equals( "csvformat" ) )
			{
				String delimiter = StringEscapeUtils.unescapeJava( getAttributeValueNamed( node, "delimiter" ) );
				String escape = StringEscapeUtils.unescapeJava( getAttributeValueNamed( node, "escape" ) );
				String quote = StringEscapeUtils.unescapeJava( getAttributeValueNamed( node, "quote" ) );

				String quotePolicy = getAttributeValueNamed( node, "quotePolicy" );
				boolean ignoreSurroundingSpaces = ( getAttributeValueNamed( node, "surroundingSpaces" ) != null && getAttributeValueNamed( node, "surroundingSpaces" ).equalsIgnoreCase( "ignore" ) );
				boolean ignoreEmptyLines = ( getAttributeValueNamed( node, "emptyLines" ) != null && getAttributeValueNamed( node, "emptyLines" ).equalsIgnoreCase( "ignore" ) );

				return new CSVFormatProcessor(
						delimiter != null ? delimiter.charAt( 0 ) : null,
						quote != null ? quote.charAt( 0 ) : null,
						quotePolicy,
						escape != null ? escape.charAt( 0 ) : null,
						ignoreSurroundingSpaces,
						ignoreEmptyLines );
			}
			else if( nodeName.equals( "datetime" ) )
			{
				String pattern = getAttributeValueNamed( node, "pattern" );
				String timezone = getAttributeValueNamed( node, "timezone" );
				String chronology = getAttributeValueNamed( node, "chronology" );
				return new DateTimeTransform( pattern, timezone, chronology );
			}
			else if( nodeName.equals( "decimal" ) )
			{
				return new DecimalTransform();
			}
			else if( nodeName.equals( "extract" ) )
			{
				String pattern = getAttributeValueNamed( node, "pattern" );
				String outputPattern = getAttributeValueNamed( node, "output" );
				return new ExtractTransform( pattern, outputPattern );
			}
			else if( nodeName.equals( "format" ) )
			{
				String pattern = getAttributeValueNamed( node, "pattern" );
				return new FormatTransform( pattern );
			}
			else if( nodeName.equals( "integer" ) )
			{
				String radix = getAttributeValueNamed( node, "radix" );
				return new IntegerTransform( ( radix == null ) ? 10 : Integer.parseInt( radix ) );
			}
			else if( nodeName.equals( "join" ) )
			{
				String separator = getAttributeValueNamed( node, "separator" );
				return new JoinTransform( separator );
			}
			else if( nodeName.equals( "number" ) )
			{
				String pattern = getAttributeValueNamed( node, "pattern" );
				return new NumberTransform( pattern );
			}
			else if( nodeName.equals( "pad" ) )
			{
				Integer minLength = Integer.parseInt( getAttributeValueNamed( node, "length" ) );
				String padWith = getAttributeValueNamed( node, "with" );
				if( optAttributeValueNamed( node, "type", "left" ).equals( "left" ) )
					return new PadLeftProcessor( minLength, padWith );
				else
					return new PadRightProcessor( minLength, padWith );
			}
			else if( nodeName.equals( "split" ) )
			{
				String separator = getAttributeValueNamed( node, "separator" );
				return new SplitTransform( separator );
			}

			else if( nodeName.equals( "state" ) )
			{
				String stateType = getAttributeValueNamed( node, "type" );
				return new StateProcessor( ( stateType != null && stateType.equals( "short" ) ) ? StateProcessor.Type.Short : StateProcessor.Type.Full );
			}
			else if( nodeName.equals( "address" ) )
			{
				String addressType = getAttributeValueNamed( node, "type" );
				return new AddressProcessor( ( addressType != null && addressType.equals( "short" ) ) ? AddressProcessor.Type.Short : AddressProcessor.Type.Full );
			}
			else if( nodeName.equals( "append" ) )
			{
				String text = getAttributeValueNamed( node, "text" );
				AbstractValue value = (AbstractValue)traverse( getChildElementNodeOfType( valueTypes, node ) );

				if( value != null && text != null )
					throw new RuntimeException( "Invalid combination of a child value and an attribute value." );
				else if( text != null )
					return new AppendProcessor( new ConstantValue( text ) );
				else if( value != null )
					return new AppendProcessor( value );
			}
			else if( nodeName.equals( "capitalize" ) )
			{
				return new CapitalizeProcessor();
			}
			else if( nodeName.equals( "compress" ) )
			{
				return new CompressProcessor();
			}
			else if( nodeName.equals( "insert" ) )
			{
				String text = getAttributeValueNamed( node, "text" );
				AbstractValue value = (AbstractValue)traverse( getChildElementNodeOfType( valueTypes, node ) );

				Integer index = Integer.parseInt( getAttributeValueNamed( node, "index" ) );

				if( value != null && text != null )
					throw new RuntimeException( "Invalid combination of a child value and an attribute value." );
				else if( text != null )
					return new InsertProcessor( index, new ConstantValue( text ) );
				else if( value != null )
					return new InsertProcessor( index, value );
			}
			else if( nodeName.equals( "left" ) )
			{
				Integer length = Integer.parseInt( getAttributeValueNamed( node, "length" ) );
				return new LeftProcessor( length );
			}
			else if( nodeName.equals( "lowercase" ) )
			{
				return new LowercaseProcessor();
			}
			else if( nodeName.equals( "prepend" ) )
			{
				String text = getAttributeValueNamed( node, "text" );
				AbstractValue value = (AbstractValue)traverse( getChildElementNodeOfType( valueTypes, node ) );

				if( value != null && text != null )
					throw new RuntimeException( "Invalid combination of a child value and an attribute value." );
				else if( text != null )
					return new PrependProcessor( new ConstantValue( text ) );
				else if( value != null )
					return new PrependProcessor( value );
			}
			else if( nodeName.equals( "replace" ) )
			{
				String pattern = getAttributeValueNamed( node, "pattern" );
				String replacement = getAttributeValueNamed( node, "replacement" );
				String scope = optAttributeValueNamed( node, "scope", "all" );
				return new ReplaceProcessor( pattern, replacement, scope.equals( "first" ) ? ReplaceProcessor.Scope.First : ReplaceProcessor.Scope.All );
			}
			else if( nodeName.equals( "right" ) )
			{
				Integer length = Integer.parseInt( getAttributeValueNamed( node, "length" ) );
				return new RightProcessor( length );
			}
			else if( nodeName.equals( "set" ) )
			{
				boolean onlyIfEmpty = false;
				String logicString = optAttributeValueNamed( node, "if", "always" );
				if( logicString.equals( "always" ) )
				{
					onlyIfEmpty = false;
				}
				else if( logicString.equals( "empty" ) )
				{
					onlyIfEmpty = true;
				}
				else
				{
					throw new RuntimeException( "Invalid value for the 'if' attribute, must be one of 'always' or 'empty'." );
				}

				String text = getAttributeValueNamed( node, "text" );
				AbstractValue value = (AbstractValue)traverse( getChildElementNodeOfType( valueTypes, node ) );

				if( value != null && text != null )
					throw new RuntimeException( "Invalid combination of a child value and an attribute value." );
				else if( text != null )
					return new SetProcessor( new ConstantValue( text ), onlyIfEmpty );
				else if( value != null )
					return new SetProcessor( value, onlyIfEmpty );
			}
			else if( nodeName.equals( "strip" ) )
			{
				String pattern = getAttributeValueNamed( node, "pattern" );
				return new StripProcessor( pattern );
			}
			else if( nodeName.equals( "substring" ) )
			{
				Span span = new Span();
				if( hasAttributeValueNamed( node, "length" ) )
				{
					span.setLength( Integer.parseInt( getAttributeValueNamed( node, "length" ) ) );
				}
				if( hasAttributeValueNamed( node, "start" ) )
				{
					span.setStartIndex( Integer.parseInt( getAttributeValueNamed( node, "start" ) ) );
				}
				if( hasAttributeValueNamed( node, "end" ) )
				{
					span.setEndIndex( Integer.parseInt( getAttributeValueNamed( node, "end" ) ) );
				}

				return new SubstringProcessor( span );
			}
			else if( nodeName.equals( "transform" ) )
			{
				List< TypeTransform >  transforms = new ArrayList<TypeTransform>();

				List< Object > children = getChildNodes( node );
				for( Object o : children )
				{
					if( o instanceof TypeTransform )
					{
						transforms.add( (TypeTransform)o );
					}
					else if( o instanceof StringProcessor )
					{
						transforms.add( new StringProcessorTransform( (StringProcessor)o ) );
					}
				}

				return new TransformProcessor( transforms );
			}
			else if( nodeName.equals( "trim" ) )
			{
				return new TrimProcessor();
			}
			else if( nodeName.equals( "uppercase" ) )
			{
				return new UppercaseProcessor();
			}
			else if( nodeName.equals( "process" ) || nodeName.equals( "preprocess" ) )
			{
				String orderString = optAttributeValueNamed( node, "order", "before" );
				String scopeString = optAttributeValueNamed( node, "scope", "local" );
				StringProcessor.Order processOrder = orderString.equalsIgnoreCase( "after" ) ? StringProcessor.Order.After : StringProcessor.Order.Before;
				StringProcessor.Scope processScope = scopeString.equalsIgnoreCase( "global" ) ? StringProcessor.Scope.Global : StringProcessor.Scope.Local;

				List< StringProcessor > processors = new LinkedList<StringProcessor>();

				List<Object> children = getChildNodes( node );
				for( Object o : children )
				{
					if( o instanceof StringProcessor )
					{
						StringProcessor processor = (StringProcessor)o;
						processor.setOrder( processOrder );
						processor.setScope( processScope );
						processors.add( processor );
					}
				}

				return processors;
			}
			else if( nodeName.equals( "span" ) )
			{
				Span span = new Span();
				if( hasAttributeValueNamed( node, "length" ) )
				{
					span.setLength( Integer.parseInt( getAttributeValueNamed( node, "length" ) ) );
				}
				if( hasAttributeValueNamed( node, "start" ) )
				{
					span.setStartIndex( Integer.parseInt( getAttributeValueNamed( node, "start" ) ) );
				}
				if( hasAttributeValueNamed( node, "end" ) )
				{
					span.setEndIndex( Integer.parseInt( getAttributeValueNamed( node, "end" ) ) );
				}
				return span;
			}
			else if( nodeName.equals( "mapping" ) )
			{
				RecordMapping mapping = new RecordMapping();
				if( hasAttributeValueNamed( node, "id" ) )
					mapping.setId( getAttributeValueNamed( node, "id" ) );
				if( hasAttributeValueNamed( node, "index" ) )
				{
					String indexStr = getAttributeValueNamed( node, "index" );
					if( indexStr.equals( "previous" ) )
						mapping.setIndex( -1 );
					else
					{
						int index = Integer.parseInt( indexStr );
						if( index < 0 )
							index = 0;
						mapping.setIndex( index );
					}
				}

				Map< String, String > additionalAttr = getAttributes( node );
				additionalAttr.remove( "id" );
				mapping.setAdditionalAttributes( additionalAttr );

				Span span = (Span)traverse( getChildElementNodeOfType( "span", node ) );
				mapping.setSpan( span );

				List<StringProcessor> processors = (List<StringProcessor>)traverse( getChildElementNodeOfType( "process", node ) );
				if( processors != null )
					mapping.setProcessors( processors );

				RecordLine line = (RecordLine)traverse( getChildElementNodeOfType( "line", node ) );
				if( line != null )
				{
					line.setParentRecordMapping( mapping );
					mapping.addLine( line );
				}

				return mapping;
			}
		}
		return null;
	}
}
