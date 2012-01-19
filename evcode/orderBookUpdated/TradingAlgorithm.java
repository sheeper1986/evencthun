package orderBookUpdated29_9;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class TradingAlgorithm {
	private Order order;
	private ArrayList<Order> pOList = new ArrayList<Order>();
	private ArrayList<Asset> assetList = new ArrayList<Asset>();
	private PriorityQueue<Order> oBList = new PriorityQueue<Order>();
	
	public TradingAlgorithm()
	{
		
	}
	
	public ArrayList<Order> matchedOrderSpread(ArrayList<Order> pOList, double price, double spread)
	{
		ArrayList<Order> temp = new ArrayList<Order>();
		int i = 0;
		while(i < pOList.size())
		{
			if(pOList.get(i).getType() == 2 && pOList.get(i).getStatus() == 0 && Math.abs((pOList.get(i).getPrice() - price)) > spread)
			{
					temp.add(pOList.get(i));
			}
				i++;
		}
		return temp;
	}
}
