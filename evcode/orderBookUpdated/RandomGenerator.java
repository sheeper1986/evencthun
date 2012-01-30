package orderBookUpdated50_91;

import java.math.BigDecimal;
import java.util.Random;

public class RandomGenerator 
{
	Random random = new Random(); 
	
	public RandomGenerator()
	{
		
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
        int result;
        
        if(num <= 5)
        {
            return bestBidPrice - 0.4;
        }
        else if(num > 5 && num <= 25)
        {
        	return bestBidPrice - 0.3;
        }
        else if(num > 25 && num <= 55)
        {
        	return bestBidPrice - 0.2;
        }
        return bestBidPrice - 0.1;
	}
	
	public double randomAskPrice(double bestAskPrice)
	{
		int num = random.nextInt(100) + 1;//1-100
        int result;
        
        if(num <= 5)
        {
            return bestAskPrice + 0.4;
        }
        else if(num > 5 && num <= 25)
        {
        	return bestAskPrice + 0.3;
        }
        else if(num > 25 && num <= 55)
        {
        	return bestAskPrice + 0.2;
        }
        return bestAskPrice + 0.1;
	}
	
	public double randomInitBuyPrice(double Price, double spread)
	{
		double randomBuyPrice = (Price - Math.random()*spread);
		BigDecimal   bd   =   new   BigDecimal(randomBuyPrice);
		return   bd.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
	}
	
	public double randomInitSellPrice(double Price, double spread)
	{
		double randomSellPrice = (Price + Math.random()*spread);
		BigDecimal   bd   =   new   BigDecimal(randomSellPrice);
		return   bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
