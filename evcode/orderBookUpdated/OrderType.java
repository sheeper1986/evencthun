package orderBookUpdated50_6;

public enum OrderType 
{
	UNKNOWN_TYPE("0"), MARKET("1"), LIMIT("2");
	
	public final String value;
	
	OrderType(final String value)
	{
		this.value = value;
	}
	
	public static OrderType getOrderType(final Order newOrder)
	{
		if(newOrder.getOrderType() == Integer.parseInt(MARKET.value))
		{
			return MARKET;
		}
		else if(newOrder.getOrderType() == Integer.parseInt(LIMIT.value)) 
		{
			return LIMIT;
		}
		else
			return UNKNOWN_TYPE;	
	}
}
