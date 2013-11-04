package com.gohlke.flatfile.test;

import com.gohlke.flatfile.processors.StateProcessor;
import com.gohlke.flatfile.processors.transforms.ExtractTransform;
import com.gohlke.flatfile.processors.transforms.TypeTransform;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 1/9/13</p>
 * <p>Time: 1:10 PM</p>
 *
 * @author jgohlke
 */
public class ProcessorTest
{
	@Test
	public void doProcessors()
	{
		StateProcessor processor = new StateProcessor( StateProcessor.Type.Full );
		assertEquals( "Texas", processor.process( "TX", "TX" ) );
		assertEquals( "Texas", processor.process( "tx", "tx" ) );
		processor = new StateProcessor( StateProcessor.Type.Short );
		assertEquals( "TX", processor.process( "Texas", "Texas" ) );
		assertEquals( "TX", processor.process( "texas", "texas" ) );
		assertEquals( "TX", processor.process( "TEXAS", "TEXAS" ) );
		assertEquals( "NC", processor.process( "North Carolina", "North Carolina" ) );
		assertEquals( "NC", processor.process( "north carolina", "north carolina" ) );
		assertEquals( "NC", processor.process( "NORTH CAROLINA", "NORTH CAROLINA" ) );

		ExtractTransform extractTransform = new ExtractTransform( "^(([0-9]*)-)?([0-9]+)$", "\\2-\\3" );
		assertEquals( "-1234", doTransform( extractTransform, "1234" ) );
		assertEquals( "01-2345", doTransform( extractTransform, "01-2345" ) );
		assertEquals( null, doTransform( extractTransform, "" ) );
	}

	private String doTransform( TypeTransform t, String s )
	{
		return t.print( t.parse( s ) );
	}
}
