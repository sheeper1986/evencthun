package orderBookUpdated12;

public enum OrderSide {
	
	BUY("1"),SELL("2");
	
	public final String value;
	
	OrderSide(final String value)
	{
		this.value = value;
	}
	
	public static OrderSide getSide(final Order newOrder)
	{
		if(Integer.parseInt(BUY.value) == newOrder.getSide())
		{
			return BUY;
		}
		else
			return SELL;
	}
	
	public static OrderSide transSide(int i)
	{
		if(i == 1)
		{
			return BUY;
		}
		else
			return SELL;
	}

}
