package orderBookUpdated17;

public enum OrderSide {
	
	UNKNOWN_SIDE("0"), BUY("1"), SELL("2"), CANCEL("3");
	
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
		else if(newOrder.getSide() == Integer.parseInt(CANCEL.value)) 
		{
			return CANCEL;
		}
		else
			return UNKNOWN_SIDE;	
	}
}

/*public static OrderSide transSide(int i)
{
	if(i == 1)
	{
		return BUY;
	}
	else if(i == 2)
	{
		return SELL;
	}
	else
		return CANCEL;
}*/
