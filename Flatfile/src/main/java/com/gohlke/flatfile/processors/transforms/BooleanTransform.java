package com.gohlke.flatfile.processors.transforms;

import org.apache.commons.lang.StringUtils;



/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/27/12</p>
 * <p>Time: 1:32 AM</p>
 *
 * @author jgohlke
 */
public class BooleanTransform implements TypeTransform
{
	public enum Format
	{
		Any,
		TrueFalse,
		TF,
		YN,
		YesNo,
		Bit
	}

	protected Format format;

	public BooleanTransform( Format format )
	{
		this.format = format;
	}

	public String printTrueFalse( Boolean input )
	{
		return input ? "true" : "false";
	}

	public String printTF( Boolean input )
	{
		return input ? "T" : "F";
	}

	public String printYN( Boolean input )
	{
		return input ? "Y" : "N";
	}

	public String printYesNo( Boolean input )
	{
		return input ? "Yes" : "No";
	}

	public String printBit( Boolean input )
	{
		return input ? "1" : "0";
	}

	public Boolean parseTrueFalse( String input )
	{
		input = StringUtils.trim( input );

		if( input.equalsIgnoreCase( "true" ) )
		{
			return true;
		}
		else if( input.equalsIgnoreCase( "false" ) )
		{
			return false;
		}
		else
		{
			return null;
		}
	}

	public Boolean parseTF( String input )
	{
		input = StringUtils.trim( input );

		if( input.equalsIgnoreCase( "T" ) )
		{
			return true;
		}
		else if( input.equalsIgnoreCase( "F" ) )
		{
			return false;
		}
		else
		{
			return null;
		}
	}

	public Boolean parseYN( String input )
	{
		input = StringUtils.trim( input );

		if( input.equalsIgnoreCase( "Y" ) )
		{
			return true;
		}
		else if( input.equalsIgnoreCase( "N" ) )
		{
			return false;
		}
		else
		{
			return null;
		}
	}

	public Boolean parseYesNo( String input )
	{
		input = StringUtils.trim( input );

		if( input.equalsIgnoreCase( "Yes" ) )
		{
			return true;
		}
		else if( input.equalsIgnoreCase( "No" ) )
		{
			return false;
		}
		else
		{
			return null;
		}
	}

	public Boolean parseBit( String input )
	{
		input = StringUtils.trim( input );

		if( input.equalsIgnoreCase( "1" ) )
		{
			return true;
		}
		else if( input.equalsIgnoreCase( "0" ) )
		{
			return false;
		}
		else
		{
			return null;
		}
	}

	public Boolean parseAny( String input )
	{
		Boolean parsed;

		parsed = parseTrueFalse( input );
		if( parsed != null )
			return parsed;

		parsed = parseYesNo( input );
		if( parsed != null )
			return parsed;

		parsed = parseYN( input );
		if( parsed != null )
			return parsed;

		parsed = parseTF( input );
		if( parsed != null )
			return parsed;

		parsed = parseBit( input );
		if( parsed != null )
			return parsed;

		return null;
	}

	@Override
	public Boolean parse( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		if( input instanceof String )
		{
			String s = (String)input;
			switch( format )
			{
				case Any:
					return parseAny( s );
				case TrueFalse:
					return parseTrueFalse( s );
				case TF:
					return parseTF( s );
				case YN:
					return parseYN( s );
				case YesNo:
					return parseYesNo( s );
				case Bit:
					return parseBit( s );
				default:
					throw new UnsupportedOperationException();
			}
		}
		else if( input instanceof Number )
		{
			Number number = (Number)input;
			if( number.intValue() == 0 )
				return parse( "0" );
			else if( number.intValue() > 0 )
				return parse( "1" );
			else
				return null;
		}
		else if( input instanceof Boolean )
		{
			return (Boolean)input;
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String print( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		Boolean parsed = parse( input );
		if( parsed == null )
			return null;

		switch( format )
		{
			case Any:
				throw new UnsupportedOperationException();
			case TrueFalse:
				return printTrueFalse( parsed );
			case YN:
				return printYN( parsed );
			case TF:
				return printTF( parsed );
			case YesNo:
				return printYesNo( parsed );
			case Bit:
				return printBit( parsed );
			default:
				throw new UnsupportedOperationException();
		}
	}
}
