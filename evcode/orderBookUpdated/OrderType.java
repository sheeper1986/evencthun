package orderBookUpdated12;

public enum OrderType {

	MARKET("1"),LIMIT("2");
	
	public final String value;
	
	OrderType(final String value)
	{
		this.value = value;
	}
	
	public static OrderType getOrderType(final Order newOrder)
	{
		if(Integer.parseInt(MARKET.value) == newOrder.getType())
		{
			return MARKET;
		}
		else
			return LIMIT;
	}
	
	public static OrderType transType(int i)
	{
		if(i == 1)
		{
			return MARKET;
		}
		else
			return LIMIT;
	}
}
