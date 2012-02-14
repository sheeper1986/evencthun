package orderBookUpdated52_5;

public enum OrderType 
{
	MARKET("1"), LIMIT("2");
	
	public final String value;
	
	OrderType(final String value)
	{
		this.value = value;
	}
	
	public static OrderType getOrderType(final Order newOrder)
	{
		if(newOrder.isMarketOrder())
		{
			return MARKET;
		}
		else
		{
			return LIMIT;
		}
	}
}
