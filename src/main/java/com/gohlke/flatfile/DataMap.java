package com.gohlke.flatfile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 7:47 PM</p>
 *
 * @author jgohlke
 */
public class DataMap implements Map< String, String >, Serializable
{
	private static final long serialVersionUID = 2526475295622776167L;

	protected Long rawLineNumber;
	protected String rawString;
	protected String recordId;
	protected Map< String, String > backingMap;
	protected Map< String, Map< String, String > > extraMap;

	public DataMap( String rawString, String recordId, Map<String, String> backingMap, Map<String, Map<String, String>> extraMap )
	{
		this.rawString = rawString;
		this.recordId = recordId;
		this.backingMap = backingMap;
		this.extraMap = extraMap;
		this.rawLineNumber = null;
	}

	public void merge( DataMap dataMap )
	{
		merge( dataMap, "\r\n" );
	}

	public void merge( DataMap dataMap, String separator )
	{
		//System.out.println( backingMap );
		//System.out.println( dataMap.getBackingMap() );

		Map<String, String> prevBackingMap = backingMap;
		backingMap = new HashMap<String, String>( prevBackingMap );
		for( String key : dataMap.getBackingMap().keySet() )
			backingMap.put( key, dataMap.getBackingMap().get( key ) );

		//System.out.println( backingMap );
		//System.out.println();

		Map< String, Map< String, String > > prevExtraMap = extraMap;
		extraMap = new HashMap<String, Map<String, String>>( prevExtraMap );
		for( String key : dataMap.getExtraMap().keySet() )
			extraMap.put( key, dataMap.getExtraMap().get( key ) );

		rawString += separator + dataMap.getRawString();
	}

	private void readObject( ObjectInputStream in ) throws ClassNotFoundException, IOException
	{
		//always perform the default de-serialization first
		in.defaultReadObject();

		/*backingMap = Collections.unmodifiableMap( readMap( in ) );

		extraMap = new HashMap<String, Map<String, String>>(  );
		int len = in.readInt();
		for( int i = 0; i < len; i++ )
		{
			String key = in.readUTF();
			Map< String, String > value = readMap( in );
			extraMap.put( key, value );
		}
		extraMap = Collections.unmodifiableMap( extraMap );*/
	}

	private void writeObject( ObjectOutputStream out ) throws IOException
	{
		//perform the default serialization for all non-transient, non-static fields
		out.defaultWriteObject();

		/*writeMap( out, backingMap );

		out.writeInt( extraMap.keySet().size() );
		for( String key : extraMap.keySet() )
		{
			out.writeUTF( key );
			writeMap( out, extraMap.get( key ) );
		}*/
	}

	/*private Map<String, String> readMap( ObjectInputStream in ) throws IOException
	{
		Map<String, String> newMap = new HashMap< String, String >();
		int len = in.readInt();
		for( int i = 0; i < len; i++ )
		{
			String key = in.readUTF();
			String value = in.readUTF();
			newMap.put( key, value );
		}
		return newMap;
	}

	private void writeMap( ObjectOutputStream out, Map<String, String> map ) throws IOException
	{
		out.writeInt( map.keySet().size() );
		for( String key : map.keySet() )
		{
			if( key != null && map.get( key ) != null )
			{
				out.writeUTF( key );
				out.writeUTF( map.get( key ) );
			}
		}
	}*/

	public Long getLineNumber()
	{
		return rawLineNumber;
	}

	public void setLineNumber( Long rawLineNumber )
	{
		this.rawLineNumber = rawLineNumber;
	}

	public String getRawString()
	{
		return rawString;
	}

	public void setRawString( String rawString )
	{
		this.rawString = rawString;
	}

	public String getRecordId()
	{
		return recordId;
	}

	protected void setRecordId( String recordId )
	{
		this.recordId = recordId;
	}

	public Map<String, String> getBackingMap()
	{
		return backingMap;
	}

	protected void setBackingMap( Map<String, String> map )
	{
		this.backingMap = Collections.unmodifiableMap( map );
	}

	public Map<String, Map<String, String>> getExtraMap()
	{
		return extraMap;
	}

	protected void setExtraMap( Map<String, Map<String, String>> map )
	{
		this.extraMap = Collections.unmodifiableMap( map );
	}


	@Override
	public int size()
	{
		return backingMap.size();
	}

	@Override
	public boolean isEmpty()
	{
		return backingMap.isEmpty();
	}

	@Override
	public boolean containsKey( Object key )
	{
		return backingMap.containsKey( key );
	}

	@Override
	public boolean containsValue( Object value )
	{
		return backingMap.containsValue( value );
	}

	@Override
	public String get( Object key )
	{
		return backingMap.get( key );
	}

	@Override
	public String put( String key, String value )
	{
		return backingMap.put( key, value );
	}

	@Override
	public String remove( Object key )
	{
		return backingMap.remove( key );
	}

	@Override
	public void putAll( Map<? extends String, ? extends String> m )
	{
		backingMap.putAll( m );
	}

	@Override
	public void clear()
	{
		backingMap.clear();
	}

	@Override
	public Set<String> keySet()
	{
		return backingMap.keySet();
	}

	@Override
	public Collection<String> values()
	{
		return backingMap.values();
	}

	@Override
	public Set<Entry<String, String>> entrySet()
	{
		return backingMap.entrySet();
	}

	@Override
	public String toString()
	{
		return "DataMap{" +
				"recordId='" + recordId + '\'' +
				", backingMap=" + backingMap +
				", extraMap=" + extraMap +
				'}';
	}
}
