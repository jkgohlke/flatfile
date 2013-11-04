package com.gohlke.flatfile.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 8:59 PM</p>
 *
 * @author jgohlke
 */
public class ReplaceProcessor extends StringProcessor
{
	String regexString;
	Pattern regexPattern;

	String replacement;
	Scope scope;

	public ReplaceProcessor( String pattern, String replacement, Scope scope )
	{
		this.regexString = pattern;
		this.regexPattern = Pattern.compile( pattern );
		this.replacement = replacement;
		this.scope = scope;
	}

	@Override
	public String process( String input, Object rawLine )
	{
		if( input == null )
			return null;

		Matcher matcher = regexPattern.matcher( input );

		switch( scope )
		{
			case All:
				return matcher.replaceAll( replacement );
			case First:
				return matcher.replaceFirst( replacement );
			default:
				return null;
		}
	}

	public enum Scope
	{
		All,
		First
	}
}
