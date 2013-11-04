package com.gohlke.flatfile.processors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

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
public class AddressProcessor extends StringProcessor
{
	public enum Type
	{
		Full,
		Short
	}

	protected Type type;
	protected Map< String, List<Pattern>> patterns;

	public AddressProcessor( Type type )
	{
		this.type = type;
		this.patterns = new HashMap<String, List<Pattern>>();
		init();
	}

	private void init()
	{
		boolean full = ( type == Type.Full );
		int flags = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE;
		
		List<Pattern> list = new LinkedList<Pattern>();
		list.add( Pattern.compile( "^n\\.w\\.$", flags ) );
		list.add( Pattern.compile( "^nw$", flags ) );
		list.add( Pattern.compile( "^north-west$", flags ) );
		list.add( Pattern.compile( "^northwest$", flags ) );

		if( full )
			patterns.put( "Northwest", list );
		else
			patterns.put( "NW", list );

		list = new LinkedList<Pattern>();
		list.add( Pattern.compile( "^s\\.w\\.$", flags ) );
		list.add( Pattern.compile( "^sw$", flags ) );
		list.add( Pattern.compile( "^south-west$", flags ) );
		list.add( Pattern.compile( "^southwest$", flags ) );

		if( full )
			patterns.put( "Southwest", list );
		else
			patterns.put( "SW", list );

		list = new LinkedList<Pattern>();
		list.add( Pattern.compile( "^n\\.e\\.$", flags ) );
		list.add( Pattern.compile( "^ne$", flags ) );
		list.add( Pattern.compile( "^north-east$", flags ) );
		list.add( Pattern.compile( "^northeast$", flags ) );

		if( full )
			patterns.put( "Northeast", list );
		else
			patterns.put( "NE", list );

		list = new LinkedList<Pattern>();
		list.add( Pattern.compile( "^s\\.e\\.$", flags ) );
		list.add( Pattern.compile( "^se$", flags ) );
		list.add( Pattern.compile( "^south-east$", flags ) );
		list.add( Pattern.compile( "^southeast$", flags ) );

		if( full )
			patterns.put( "Southeast", list );
		else
			patterns.put( "SE", list );

		putPattern( "n", "north" );
		putPattern( "s", "south" );
		putPattern( "e", "east" );
		putPattern( "w", "west" );

		putPattern( "aly", "alley" );
		putPattern( "anx", "annex" );
		putPattern( "apt", "apartment" );
		putPattern( "arc", "arcade" );
		putPattern( "ave", "avenue" );
		putPattern( "bsmt", "basement" );
		putPattern( "byu", "bayou" );
		putPattern( "bch", "beach" );
		putPattern( "bnd", "bend" );
		putPattern( "blf", "bluff" );
		putPattern( "btm", "bottom" );
		putPattern( "blvd", "boulevard" );
		putPattern( "br", "branch" );
		putPattern( "brg", "bridge" );
		putPattern( "brk", "brook" );
		putPattern( "bldg", "building" );
		putPattern( "bg", "burg" );
		putPattern( "byp", "bypass" );
		putPattern( "cp", "camp" );
		putPattern( "cyn", "canyon" );
		putPattern( "cpe", "cape" );
		putPattern( "cswy", "causeway" );
		putPattern( "ctr", "center" );
		putPattern( "cir", "circle" );
		putPattern( "clfs", "cliff" );
		putPattern( "clfs", "cliffs" );
		putPattern( "clb", "club" );
		putPattern( "cor", "corner" );
		putPattern( "cors", "corners" );
		putPattern( "crse", "course" );
		putPattern( "ct", "court" );
		putPattern( "cts", "courts" );
		putPattern( "cv", "cove" );
		putPattern( "crk", "creek" );
		putPattern( "cres", "crescent" );
		putPattern( "xing", "crossing" );
		putPattern( "dl", "dale" );
		putPattern( "dm", "dam" );
		putPattern( "dept", "department" );
		putPattern( "dv", "divide" );
		putPattern( "dr", "drive" );
		putPattern( "est", "estate" );
		putPattern( "expy", "expressway" );
		putPattern( "ext", "extension" );
		putPattern( "fls", "falls" );
		putPattern( "fry", "ferry" );
		putPattern( "fld", "field" );
		putPattern( "flds", "fields" );
		putPattern( "flt", "flat" );
		putPattern( "fl", "floor" );
		putPattern( "frd", "ford" );
		putPattern( "frst", "forest" );
		putPattern( "frg", "forge" );
		putPattern( "frk", "fork" );
		putPattern( "frks", "forks" );
		putPattern( "ft", "fort" );
		putPattern( "fwy", "freeway" );
		putPattern( "frnt", "front" );
		putPattern( "gdns", "garden" );
		putPattern( "gdns", "gardens" );
		putPattern( "gtwy", "gateway" );
		putPattern( "gln", "glen" );
		putPattern( "grn", "green" );
		putPattern( "grv", "grove" );
		putPattern( "hngr", "hanger" );
		putPattern( "hbr", "harbor" );
		putPattern( "hvn", "haven" );
		putPattern( "hts", "heights" );
		putPattern( "hwy", "highway" );
		putPattern( "hl", "hill" );
		putPattern( "hls", "hills" );
		putPattern( "holw", "hollow" );
		putPattern( "inlt", "inlet" );
		putPattern( "is", "island" );
		putPattern( "iss", "islands" );
		putPattern( "jct", "junction" );
		putPattern( "ky", "key" );
		putPattern( "knls", "knoll" );
		putPattern( "knls", "knolls" );
		putPattern( "lk", "lake" );
		putPattern( "lks", "lakes" );
		putPattern( "lndg", "landing" );
		putPattern( "ln", "lane" );
		putPattern( "lgt", "light" );
		putPattern( "lf", "loaf" );
		putPattern( "lbby", "lobby" );
		putPattern( "lcks", "lock" );
		putPattern( "lcks", "locks" );
		putPattern( "ldg", "lodge" );
		putPattern( "lowr", "lower" );
		putPattern( "mnr", "manor" );
		putPattern( "mdws", "meadow" );
		putPattern( "mdws", "meadows" );
		putPattern( "ml", "mill" );
		putPattern( "mls", "mills" );
		putPattern( "msn", "mission" );
		putPattern( "mt", "mount" );
		putPattern( "mtn", "mountain" );
		putPattern( "nck", "neck" );
		putPattern( "ofc", "office" );
		putPattern( "orch", "orchard" );
		putPattern( "pkwy", "parkway" );
		putPattern( "ph", "penthouse" );
		putPattern( "pnes", "pine" );
		putPattern( "pnes", "pines" );
		putPattern( "pl", "place" );
		putPattern( "pln", "plain" );
		putPattern( "plns", "plains" );
		putPattern( "plz", "plaza" );
		putPattern( "pt", "point" );
		putPattern( "prt", "port" );
		putPattern( "pr", "prairie" );
		putPattern( "radl", "radial" );
		putPattern( "rnch", "ranch" );
		putPattern( "rpds", "rapid" );
		putPattern( "rpds", "rapids" );
		putPattern( "rst", "rest" );
		putPattern( "rdg", "ridge" );
		putPattern( "riv", "river" );
		putPattern( "rd", "road" );
		putPattern( "rm", "room" );
		putPattern( "shl", "shoal" );
		putPattern( "shls", "shoals" );
		putPattern( "shr", "shore" );
		putPattern( "shrs", "shores" );
		putPattern( "spc", "space" );
		putPattern( "spg", "spring" );
		putPattern( "spgs", "springs" );
		putPattern( "sq", "square" );
		putPattern( "sta", "station" );
		putPattern( "stra", "stravenue" );
		putPattern( "strm", "stream" );
		putPattern( "st", "street" );
		putPattern( "ste", "suite" );
		putPattern( "smt", "summit" );
		putPattern( "ter", "terrace" );
		putPattern( "trce", "trace" );
		putPattern( "trak", "track" );
		putPattern( "trfy", "trafficway" );
		putPattern( "trl", "trail" );
		putPattern( "trlr", "trailer" );
		putPattern( "tunl", "tunnel" );
		putPattern( "tpke", "turnpike" );
		putPattern( "un", "union" );
		putPattern( "uppr", "upper" );
		putPattern( "vly", "valley" );
		putPattern( "via", "viaduct" );
		putPattern( "vw", "view" );
		putPattern( "vlg", "village" );
		putPattern( "vl", "ville" );
		putPattern( "vis", "vista" );
		putPattern( "way", "way" );
		putPattern( "wls", "well" );
		putPattern( "wls", "wells" );
	}

	private void putPattern( String shortSyn, String fullSyn )
	{
		int flags = Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE;

		LinkedList<Pattern> list = new LinkedList<Pattern>();
		list.add( Pattern.compile( "^" + fullSyn + "$", flags ) );
		list.add( Pattern.compile( "^" + shortSyn + "$", flags ) );
		list.add( Pattern.compile( "^" + shortSyn + "\\.$", flags ) );

		if( type == Type.Full )
			patterns.put( fullSyn, list );
		else
			patterns.put( shortSyn, list );
	}

	private String preProcess( String input )
	{
		input = input.replaceAll( " po box ", " P.O. Box " );
		input = input.replaceAll( " po box #", " P.O. Box " );
		input = input.replaceAll( " pobox ", " P.O. Box " );
		input = input.replaceAll( " pobox #", " P.O. Box " );
		input = input.replaceAll( " p\\.o\\.box ", " P.O. Box " );
		input = input.replaceAll( " p\\.o\\.box #", " P.O. Box " );
		input = input.replaceAll( " p\\. o\\.box ", " P.O. Box " );
		input = input.replaceAll( " p\\.o\\. box #", " P.O. Box " );
		input = input.replaceAll( " north west ", " northwest " );
		input = input.replaceAll( " south west ", " southwest " );
		input = input.replaceAll( " north east ", " northeast " );
		input = input.replaceAll( " south east ", " southeast " );
		input = input.replaceAll( " n w ", " nw " );
		input = input.replaceAll( " s w ", " sw " );
		input = input.replaceAll( " n e ", " ne " );
		input = input.replaceAll( " s e ", " se " );
		input = input.replaceAll( " n w\\. ", " nw " );
		input = input.replaceAll( " s w\\. ", " sw " );
		input = input.replaceAll( " n e\\. ", " ne " );
		input = input.replaceAll( " s e\\. ", " se " );
		input = input.replaceAll( " n\\. w\\. ", " nw " );
		input = input.replaceAll( " s\\. w\\. ", " sw " );
		input = input.replaceAll( " n\\. e\\. ", " ne " );
		input = input.replaceAll( " s\\. e\\. ", " se " );
		input = input.replaceAll( "#", " #" );
		input = input.replaceAll( "# ([\\d+])", " #$1 " );
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

		StringBuilder stringBuilder = new StringBuilder();
		String[] tokens = input.split( "[ ]" );
		for( String token : tokens )
		{
			stringBuilder.append( processToken( token ) );
			stringBuilder.append( " " );
		}

		return WordUtils.capitalize( StringUtils.trim( stringBuilder.toString() ).replaceAll( "[ ]{2,}", " " ) );
	}
}
