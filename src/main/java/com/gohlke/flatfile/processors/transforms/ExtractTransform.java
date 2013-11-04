package com.gohlke.flatfile.processors.transforms;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/27/12</p>
 * <p>Time: 1:05 AM</p>
 *
 * @author jgohlke
 */
public class ExtractTransform implements TypeTransform
{
	private static final transient Logger LOG = LoggerFactory.getLogger( ExtractTransform.class );

	String regexString;
	Pattern regexPattern;

	String outputPattern;

	public ExtractTransform( String pattern, String outputPattern )
	{
		this.regexString = pattern;
		this.regexPattern = Pattern.compile( pattern );

		this.outputPattern = outputPattern;
	}

	protected String parseOutputPattern( Matcher matcher )
	{
		String s = outputPattern;
		//LOG.info( Arrays.toString( getMatcherGroups( matcher ) ) );
		for( int i = 0; i <= matcher.groupCount() && i <= 9; i++ )
		{
			if( matcher.group( i ) == null )
				s = StringUtils.replace( s, "\\" + i, "" );
			else
				s = StringUtils.replace( s, "\\" + i, StringEscapeUtils.escapeJava( matcher.group( i ) ) );
		}
		for( int i = matcher.groupCount(); i <= 9; i++ )
		{
			s = StringUtils.replace( s, "\\" + i, "" );
		}
		return StringEscapeUtils.unescapeJava( s );
	}

	protected String[] getMatcherGroups( Matcher matcher )
	{
		String[] arr = new String[ matcher.groupCount() ];
		for( int i = 1; i <= matcher.groupCount(); i++ )
		{
			arr[ i - 1 ] = matcher.group( i );
		}
		return arr;
	}

	public String getRegexString()
	{
		return regexString;
	}

	public Pattern getRegexPattern()
	{
		return regexPattern;
	}

	@Override
	public Object parse( Object input ) throws UnsupportedOperationException
	{
		if( input == null )
			return null;

		if( input instanceof String )
		{
			Matcher matcher = getRegexPattern().matcher( (String)input );
			if( matcher.find() )
			{
				if( outputPattern != null )
					return parseOutputPattern( matcher );
				else
					return getMatcherGroups( matcher );
			}
			else
				return null;
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

		if( outputPattern != null )
			return (String)parse( input );
		else
			throw new UnsupportedOperationException();
	}
}
