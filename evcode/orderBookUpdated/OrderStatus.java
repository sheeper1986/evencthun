package orderBookUpdated50_6;

public enum OrderStatus {

	NEW_ORDER("0"), FILLED("1"), PART_FILLED("2"), REJECTED("3"), CANCELED("4");
	
	public final String value;
	
	OrderStatus(final String value)
	{
		this.value = value;
	}
	
	public static OrderStatus getOrderStatus(final Order newOrder)
	{
		if(newOrder.getStatus() == Integer.parseInt(FILLED.value))
		{
			return FILLED;
		}
		else if(newOrder.getStatus() == Integer.parseInt(PART_FILLED.value))
		{
			return PART_FILLED;
		}
		else if(newOrder.getStatus() == Integer.parseInt(REJECTED.value))
		{
			return REJECTED;
		}
		else if(newOrder.getStatus() == Integer.parseInt(CANCELED.value))
		{
			return CANCELED;
		}
		else
			return NEW_ORDER;
	}
}