package orderBookUpdated50_7;

public enum OrderSide {
	
	UNKNOWN_SIDE("0"), BUY("1"), SELL("2");
	
	public final String value;
	
	OrderSide(final String value)
	{
		this.value = value;
	}
	
	public static OrderSide getSide(final Order newOrder)
	{
		if(newOrder.getSide() == Integer.parseInt(BUY.value))
		{
			return BUY;
		}
		else if(newOrder.getSide() == Integer.parseInt(SELL.value))
		{
			return SELL;
		}
		else
			return UNKNOWN_SIDE;	
	}
}