package orderBookUpdated52_5;

import java.math.BigDecimal;
import java.util.Random;

public class RandomGenerator 
{
	Random random;
	
	public RandomGenerator()
	{
		 this.random = new Random(); 
	}
	
	public int randomType(int probOfMarket)
	{
		int num = random.nextInt(100) + 1;//1-100
        int result;
        
        if(num <= probOfMarket)
        {
            result = 1;
        }
        else
        {
        	 result = 2;
        }
        return result;
	}
	
	public int randomSide(int probOfBuy)
	{
		int num = random.nextInt(100) + 1;//1-100
        int result;
        
        if(num <= probOfBuy)
        {
            result = 1;
        }
        else
        {
        	 result = 2;
        }
        return result;
	}
	
	public int randomVolume(int i, int j)
	{
		int num = random.nextInt(j) + i;
		return num;
	}
	
	public double randomBidPrice(double aimedBidPrice)
	{
		int num = random.nextInt(100) + 1;//1-100
        double bidPrice;
        
        if(num <= 5)
        {
        	bidPrice =  new Format().priceFormat(aimedBidPrice*0.985);
        }
        else if(num > 5 && num <= 15)
        {
        	bidPrice =  new Format().priceFormat(aimedBidPrice*0.9875);
        }
        else if(num > 15 && num <= 25)
        {
        	bidPrice = new Format().priceFormat(aimedBidPrice*0.99);
        }
        else if(num > 25 && num <= 45)
        {
        	bidPrice = new Format().priceFormat(aimedBidPrice*0.9925);
        }
        else if(num > 45 && num <= 70)
        {
        	bidPrice =  new Format().priceFormat(aimedBidPrice*0.995);
        }
        else
        {
        	bidPrice = new Format().priceFormat(aimedBidPrice*0.9975);
        }
        return bidPrice;
	}
	
	public double randomAskPrice(double aimedAskPrice)
	{
		int num = random.nextInt(100) + 1;//1-100
        double askPrice;
        
        if(num <= 5)
        {
        	askPrice = new Format().priceFormat(aimedAskPrice*1.015);
        }
        else if(num > 5 && num <= 15)
        {
        	askPrice =  new Format().priceFormat(aimedAskPrice*1.0125);
        }
        else if(num > 15 && num <= 25)
        {
        	askPrice =  new Format().priceFormat(aimedAskPrice*1.01);
        }
        else if(num > 25 && num <= 45)
        {
        	askPrice =  new Format().priceFormat(aimedAskPrice*1.0075);
        }
        else if(num > 45 && num <= 70)
        {
        	askPrice =  new Format().priceFormat(aimedAskPrice*1.005);
        }
        else
        {
        	askPrice = new Format().priceFormat(aimedAskPrice*1.0025);
        }
        return askPrice;
	}
	
	public double randomInitBuyPrice(double Price, double spread)
	{
		double randomBuyPrice = new Format().priceFormat(Price - Math.random()*spread);		
		return randomBuyPrice;
	}
	
	public double randomInitSellPrice(double Price, double spread)
	{
		double randomSellPrice = new Format().priceFormat(Price + Math.random()*spread);
		return randomSellPrice;
	}
}
