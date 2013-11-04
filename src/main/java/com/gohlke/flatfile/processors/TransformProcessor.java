package com.gohlke.flatfile.processors;

import com.gohlke.flatfile.processors.transforms.TypeTransform;

import java.util.Collections;
import java.util.List;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 10:10 PM</p>
 *
 * @author jgohlke
 */
public class TransformProcessor extends StringProcessor
{
	protected List< TypeTransform > typeTransforms;

	public TransformProcessor( List< TypeTransform > typeTransforms )
	{
		this.typeTransforms = Collections.unmodifiableList( typeTransforms );
	}

	@Override
	public String process( String input, Object rawLine )
	{
		if( input == null || input.length() == 0 )
			return null;
		try
		{
			Object currentObject = input;
			for( int i = 0, typeTransformsSize = typeTransforms.size(); i < typeTransformsSize; i++ )
			{
				TypeTransform transform = typeTransforms.get( i );

				if( i < typeTransformsSize - 1 )
					currentObject = transform.parse( currentObject );
				else
					currentObject = transform.print( currentObject );
			}

			if( currentObject instanceof String )
				return String.valueOf( currentObject );
			else if( currentObject == null )
				return null;
			else
				throw new UnsupportedOperationException();
		}
		catch( UnsupportedOperationException e )
		{
			throw new RuntimeException( e );
		}
	}
}
