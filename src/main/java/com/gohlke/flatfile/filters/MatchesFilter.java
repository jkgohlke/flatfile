package com.gohlke.flatfile.filters;

import com.gohlke.flatfile.AbstractLineFilter;
import com.gohlke.flatfile.RecordMapping;
import com.gohlke.flatfile.processors.transforms.TypeTransform;
import com.gohlke.flatfile.values.AbstractValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/25/12</p>
 * <p>Time: 6:34 PM</p>
 *
 * @author jgohlke
 */
public class MatchesFilter extends AbstractLineFilter
{
	private static final transient Logger LOG = LoggerFactory.getLogger( MatchesFilter.class );

	protected AbstractValue left;
	protected AbstractValue right;

	public MatchesFilter()
	{
		this.left = null;
		this.right = null;
	}

	public MatchesFilter( AbstractValue left, AbstractValue right )
	{
		this.left = left;
		this.right = right;
	}

	public void setLeft( AbstractValue left )
	{
		this.left = left;
	}

	public void setRight( AbstractValue right )
	{
		this.right = right;
	}

	public boolean matches( Object rawRecord, RecordMapping leftMapping, Object rightValue )
	{
		////LOG.info( ( rawRecord instanceof Object[] ) ? Arrays.toString( (Object[])rawRecord ) : String.valueOf( rawRecord ) );
		String leftString = leftMapping.parseAndProcessRecursively( rawRecord );
		//LOG.info( leftString );
		//LOG.info( String.valueOf( rightValue ) );

		if( leftString == null )
			return false;
		else if( rightValue == null )
			return false;
		else if( rightValue instanceof RecordMapping )
		{
			RecordMapping rightMapping = (RecordMapping)rightValue;

			String rightString = rightMapping.parseAndProcessRecursively( rawRecord );

			//LOG.info( rightString );
			//LOG.info( String.valueOf( leftString.equals( rightString ) ) );

			return rightString != null && leftString.equals( rightString );
		}
		else if( rightValue instanceof Pattern )
		{
			Pattern rightPattern = (Pattern)rightValue;

			Matcher rightMatcher = rightPattern.matcher( leftString );

			//LOG.info( rightPattern.pattern() );
			//LOG.info( String.valueOf( rightMatcher.matches() ) );

			return rightMatcher.matches();
		}
		else if( rightValue instanceof String )
		{
			String rightString = (String)rightValue;

			//LOG.info( rightString );
			//LOG.info( String.valueOf( leftString.equals( rightString ) ) );

			return leftString.equals( rightString );
		}
		else if( rightValue instanceof TypeTransform )
		{
			TypeTransform rightTransform = (TypeTransform)rightValue;

			String rightString = rightTransform.print( rawRecord );

			//LOG.info( rightString );
			//LOG.info( String.valueOf( leftString.equals( rightString ) ) );

			return rightString != null && leftString.equals( rightString );
		}

		return false;
	}

	public boolean matches( Object rawRecord, Pattern leftPattern, Object rightValue )
	{
		if( rightValue == null )
			return false;
		else if( rightValue instanceof RecordMapping )
		{
			RecordMapping rightMapping = (RecordMapping)rightValue;

			String rightString = rightMapping.parseAndProcessRecursively( rawRecord );

			if( rightString == null )
				return false;

			Matcher leftMatcher = leftPattern.matcher( rightString );

			return leftMatcher.matches();
		}
		else if( rightValue instanceof Pattern )
		{
			//throw new RuntimeException( "Cannot match a regex against a regex!" );
			return false;
		}
		else if( rightValue instanceof String )
		{
			String rightString = (String)rightValue;

			Matcher leftMatcher = leftPattern.matcher( rightString );

			return leftMatcher.matches();
		}
		else if( rightValue instanceof TypeTransform )
		{
			TypeTransform rightTransform = (TypeTransform)rightValue;

			String rightString = rightTransform.print( rawRecord );

			if( rightString == null )
				return false;

			Matcher leftMatcher = leftPattern.matcher( rightString );

			return leftMatcher.matches();
		}

		return false;
	}

	public boolean matches( Object rawRecord, String leftString, Object rightValue )
	{
		if( rightValue == null )
			return false;
		else if( rightValue instanceof RecordMapping )
		{
			RecordMapping rightMapping = (RecordMapping)rightValue;

			String rightString = rightMapping.parseAndProcessRecursively( rawRecord );

			return rightString != null && leftString.equals( rightString );
		}
		else if( rightValue instanceof Pattern )
		{
			Pattern rightPattern = (Pattern)rightValue;

			Matcher rightMatcher = rightPattern.matcher( leftString );

			return rightMatcher.matches();
		}
		else if( rightValue instanceof String )
		{
			String rightString = (String)rightValue;
			return leftString.equals( rightString );
		}
		else if( rightValue instanceof TypeTransform )
		{
			TypeTransform rightTransform = (TypeTransform)rightValue;

			String rightString = rightTransform.print( rawRecord );

			return rightString != null && leftString.equals( rightString );
		}

		return false;
	}

	public boolean matches( Object rawRecord, TypeTransform leftTransform, Object rightValue )
	{
		String leftString = leftTransform.print( rawRecord );

		if( leftString == null )
			return false;
		else if( rightValue == null )
			return false;
		else if( rightValue instanceof RecordMapping )
		{
			RecordMapping rightMapping = (RecordMapping)rightValue;

			String rightString = rightMapping.parseAndProcessRecursively( rawRecord );

			return rightString != null && leftString.equals( rightString );
		}
		else if( rightValue instanceof Pattern )
		{
			Pattern rightPattern = (Pattern)rightValue;

			Matcher rightMatcher = rightPattern.matcher( leftString );

			return rightMatcher.matches();
		}
		else if( rightValue instanceof String )
		{
			String rightString = (String)rightValue;
			return leftString.equals( rightString );
		}
		else if( rightValue instanceof TypeTransform )
		{
			TypeTransform rightTransform = (TypeTransform)rightValue;

			String rightString = rightTransform.print( rawRecord );

			return rightString != null && leftString.equals( rightString );
		}

		return false;
	}

	@Override
	public boolean acceptLine( Object rawRecord )
	{
		if( left == null || right == null )
			return false;

		Object leftValue = left.resolve( getParentRecord() );
		Object rightValue = right.resolve( getParentRecord() );

		boolean retVal = false;

		if( leftValue != null )
		{
			if( leftValue instanceof RecordMapping )
			{
				retVal = matches( rawRecord, (RecordMapping)leftValue, rightValue );
			}
			else if( leftValue instanceof Pattern )
			{
				retVal = matches( rawRecord, (Pattern)leftValue, rightValue );
			}
			else if( leftValue instanceof String )
			{
				retVal = matches( rawRecord, (String)leftValue, rightValue );
			}
			else if( leftValue instanceof TypeTransform )
			{
				retVal = matches( rawRecord, (TypeTransform)leftValue, rightValue );
			}
		}

		return retVal;
	}
}
