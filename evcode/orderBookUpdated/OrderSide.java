package orderBookUpdated52_2;

public enum OrderSide {
	
	BUY("1"), SELL("2");
	
	public final String value;
	
	OrderSide(final String value)
	{
		this.value = value;
	}
	
	public static OrderSide getSide(final Order newOrder)
	{
		if(newOrder.isBuySide())
		{
			return BUY;
		}
		else
		{
			return SELL;
		}
	}
}