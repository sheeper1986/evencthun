package orderBookUpdated50;

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
	
	public boolean isInAssetList(ArrayList<Asset> assetList)
	{
		for(int i = 0; i < assetList.size(); i++)
		{
			if(this.getAssetSymbol().equals(assetList.get(i).getAssetSymbol()))
			{
				return true;
			}
		}
		return false;
	}

	public void updateAssetList(ArrayList<Asset> assetList)
	{
		if(this.isInAssetList(assetList))
		{
			int i = 0;
			while(i < assetList.size())
			{
				if(this.getAssetSymbol().equals(assetList.get(i).getAssetSymbol()))
				{
					assetList.set(i, this);
				}
				i++;
			}
		}
		else
		{
			assetList.add(this);
		}
	}
	
	public String toString()
	{
		return "StockName: " + assetSymbol + " Volume " + assetVolume + " cost " + assetCost + " Profit " + assetInterest;
	}
}
