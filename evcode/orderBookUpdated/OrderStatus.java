package orderBookUpdated17;

public enum OrderStatus {

	NEW("0"), FILLED("1"), PART_FILLED("2"), REJECTED("3"), CANCELED("4");
	
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
			return NEW;
	}
}

/*public static OrderStatus transStatus(int i)
{
	if(i == 1)
	{
		return FILLED;
	}
	else if(i == 2)
	{
		return PART_FILLED;
	}
	else if(i == 3)
	{
		return REJECTED;
	}
	else if(i == 4)
	{
		return CANCELED;
	}
	else
		return NEW;	
}*/

