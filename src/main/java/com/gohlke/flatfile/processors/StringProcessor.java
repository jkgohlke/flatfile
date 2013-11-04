package com.gohlke.flatfile.processors;

/**
 * <p>TODO: This class needs a description!</p>
 * <p>Date: 10/26/12</p>
 * <p>Time: 8:46 PM</p>
 *
 * @author jgohlke
 */
public abstract class StringProcessor
{
	public enum Order
	{
		Before,
		After
	}

	public enum Scope
	{
		Local,
		Global
	}

	protected Order myOrder = Order.Before;
	protected Scope myScope = Scope.Global;

	public Order getOrder()
	{
		return myOrder;
	}

	public void setOrder( Order o )
	{
		this.myOrder = o;
	}

	public Scope getScope()
	{
		return myScope;
	}

	public void setScope( Scope myScope )
	{
		this.myScope = myScope;
	}

	public abstract String process( String input, Object rawLine );
}
