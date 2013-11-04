package com.gohlke.flatfile.processors;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/29/12</p>
 * <p>Time: 8:54 PM</p>
 *
 * @author jgohlke
 */
public class StateProcessor extends StringProcessor
{
	public enum Type
	{
		Full,
		Short
	}

	protected Type type;
	protected Map< String, List<Pattern>> patterns;

	public StateProcessor( Type type )
	{
		this.type = type;
		this.patterns = new HashMap<String, List<Pattern>>();
		init();
	}

	private void init()
	{
		putPattern( "AK", "Alaska" );
		putPattern( "AL", "Alabama" );
		putPattern( "AR", "Arkansas" );
		putPattern( "AZ", "Arizona" );
		putPattern( "CA", "California" );
		putPattern( "CO", "Colorado" );
		putPattern( "CT", "Connecticut" );
		putPattern( "DC", "District of Columbia" );
		putPattern( "DE", "Delaware" );
		putPattern( "FL", "Florida" );
		putPattern( "GA", "Georgia" );
		putPattern( "HI", "Hawaii" );
		putPattern( "IA", "Iowa" );
		putPattern( "ID", "Idaho" );
		putPattern( "IL", "Illinois" );
		putPattern( "IN", "Indiana" );
		putPattern( "KS", "Kansas" );
		putPattern( "KY", "Kentucky" );
		putPattern( "LA", "Louisiana" );
		putPattern( "MA", "Massachusetts" );
		putPattern( "MD", "Maryland" );
		putPattern( "ME", "Maine" );
		putPattern( "MI", "Michigan" );
		putPattern( "MN", "Minnesota" );
		putPattern( "MO", "Missouri" );
		putPattern( "MS", "Mississippi" );
		putPattern( "MT", "Montana" );
		putPattern( "NC", "North Carolina" );
		putPattern( "ND", "North Dakota" );
		putPattern( "NE", "Nebraska" );
		putPattern( "NH", "New Hampshire" );
		putPattern( "NJ", "New Jersey" );
		putPattern( "NM", "New Mexico" );
		putPattern( "NV", "Nevada" );
		putPattern( "NY", "New York" );
		putPattern( "OH", "Ohio" );
		putPattern( "OK", "Oklahoma" );
		putPattern( "OR", "Oregon" );
		putPattern( "PA", "Pennsylvania" );
		putPattern( "RI", "Rhode Island" );
		putPattern( "SC", "South Carolina" );
		putPattern( "SD", "South Dakota" );
		putPattern( "TN", "Tennessee" );
		putPattern( "TX", "Texas" );
		putPattern( "UT", "Utah" );
		putPattern( "VA", "Virginia" );
		putPattern( "VT", "Vermont" );
		putPattern( "WA", "Washington" );
		putPattern( "WI", "Wisconsin" );
		putPattern( "WV", "West Virginia" );
		putPattern( "WY", "Wyoming" );
	}

	private void putPattern( String shortSyn, String fullSyn )
	{
		int flags = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE;

		LinkedList<Pattern> list = new LinkedList<Pattern>();
		list.add( Pattern.compile( "^(" + fullSyn + ")$", flags ) );
		list.add( Pattern.compile( "^(" + shortSyn + ")$", flags ) );
		list.add( Pattern.compile( "^(" + shortSyn + "\\.)$", flags ) );

		if( type == Type.Full )
			patterns.put( fullSyn, list );
		else
			patterns.put( shortSyn, list );
	}

	private String preProcess( String input )
	{
		return input;
	}

	private String processToken( String token )
	{
		for( String replacement : patterns.keySet() )
		{
			for( Pattern p : patterns.get( replacement ) )
			{
				if( p.matcher( token ).matches() )
					return replacement;
			}
		}
		return token;
	}

	@Override
	public String process( String input, Object rawLine )
	{
		if( input == null )
			return null;

		input = StringUtils.trim( input ).replaceAll( "[ ]{2,}", " " ).toLowerCase();

		input = preProcess( input ).replaceAll( "[ ]{2,}", " " );

		return StringUtils.trim( processToken( input ) ).replaceAll( "[ ]{2,}", " " );
	}
}
