package orderBookUpdated52_2;

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
	
	public double randomBidPrice(double bestBidPrice)
	{
		int num = random.nextInt(100) + 1;//1-100
        double bidPrice;
        if(num <= 5)
        {
        	bidPrice =  new BigDecimal(bestBidPrice*0.985).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
        else if(num > 5 && num <= 15)
        {
        	bidPrice =  new BigDecimal(bestBidPrice*0.9875).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
        else if(num > 15 && num <= 25)
        {
        	bidPrice = new BigDecimal(bestBidPrice*0.99).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
        else if(num > 25 && num <= 45)
        {
        	bidPrice = new BigDecimal(bestBidPrice*0.9925).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
        else if(num > 45 && num <= 70)
        {
        	bidPrice =  new BigDecimal(bestBidPrice*0.995).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
        else
        {
        	bidPrice = new BigDecimal(bestBidPrice*0.9975).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
        return bidPrice;
	}
	
	public double randomAskPrice(double bestAskPrice)
	{
		int num = random.nextInt(100) + 1;//1-100
        double askPrice;
        
        if(num <= 5)
        {
        	askPrice = new BigDecimal(bestAskPrice*1.015).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        else if(num > 5 && num <= 15)
        {
        	askPrice =  new BigDecimal(bestAskPrice*1.0125).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        else if(num > 15 && num <= 25)
        {
        	askPrice =  new BigDecimal(bestAskPrice*1.01).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        else if(num > 25 && num <= 45)
        {
        	askPrice =  new BigDecimal(bestAskPrice*1.0075).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        else if(num > 45 && num <= 70)
        {
        	askPrice =  new BigDecimal(bestAskPrice*1.005).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        else
        {
        	askPrice = new BigDecimal(bestAskPrice*1.0025).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return askPrice;
	}
	
	public double randomInitBuyPrice(double Price, double spread)
	{
		double randomBuyPrice = (Price - Math.random()*spread);
		BigDecimal   bd   =   new   BigDecimal(randomBuyPrice);
		return   bd.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
	}
	
	public double randomInitSellPrice(double Price, double spread)
	{
		double randomSellPrice = (Price + Math.random()*spread);
		BigDecimal   bd   =   new   BigDecimal(randomSellPrice);
		return   bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
