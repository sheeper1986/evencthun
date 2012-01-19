package orderBookUpdated29_9;

import java.util.ArrayList;

public class Asset 
{
	private String assetSymbol;
	private int assetVolume;
	private double assetCost;
	private double assetInterest;
	
	public Asset()
	{
		
	}
	
	public void setAssetSymbol(String symbol)
	{
		this.assetSymbol = symbol;
	}
	public String getAssetSymbol()
	{
		return assetSymbol;
	}
	
	public void setAssetVolume(int totalVolume)
	{
		this.assetVolume = totalVolume;
	}
	public int getAssetVolume()
	{
		return assetVolume;
	}
	
	public void setAssetCost(double assetCost)
	{
		this.assetCost = assetCost;
	}
	public double getAssetCost()
	{
		return assetCost;
	}
	
	public void setAssetInterest(double assetInterest)
	{
		this.assetInterest = assetInterest;
	}
	public double getAssetInterest()
	{
		return assetInterest;
	}
	
	public void updateAsset(Order order)
	{
		if(order.getSide() == 1)
		{
			this.setAssetSymbol(order.getSymbol());
			this.setAssetCost((this.getAssetCost()*this.getAssetVolume() + order.getDealingPrice()*order.getVolume())/(this.getAssetVolume() + order.getVolume()));
			this.setAssetVolume(this.getAssetVolume() + order.getVolume());
			//this.setProfit(this.getTotalVolume()*this.getCost() - this.getTotalVolume()*currentPrice);
		}
		else
			this.setAssetSymbol(order.getSymbol());
		    this.setAssetCost((this.getAssetCost()*this.getAssetVolume() - order.getPrice()*order.getVolume())/(this.getAssetVolume() - order.getVolume()));
		    this.setAssetVolume(this.getAssetVolume() - order.getVolume());
	}

	public void updateAssetList(ArrayList<Asset> assetList, Order order)
	{
		if(assetList != null)
		{
			int i = 0;
			while(i < assetList.size())
			{
				if(this.getAssetSymbol().equals(assetList.get(i).getAssetSymbol()))
				{
					assetList.get(i).updateAsset(order);
				}
				i++;
			}
		}
		else
			assetList.add(this);
	}
	
	public String toString()
	{
		return "StockName: " + assetSymbol + " Volume " + assetVolume + " cost " + assetCost + " Profit " + assetInterest;
	}
}
