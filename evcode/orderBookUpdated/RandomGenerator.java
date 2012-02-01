package orderBookUpdated51_1;

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
        
        if(num <= 5)
        {
            return new BigDecimal(bestBidPrice - 0.4).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
        else if(num > 5 && num <= 25)
        {
        	return new BigDecimal(bestBidPrice - 0.3).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
        else if(num > 25 && num <= 55)
        {
        	return new BigDecimal(bestBidPrice - 0.2).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        }
        return new BigDecimal(bestBidPrice - 0.1).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
	}
	
	public double randomAskPrice(double bestAskPrice)
	{
		int num = random.nextInt(100) + 1;//1-100
        
        if(num <= 5)
        {
            return new BigDecimal(bestAskPrice + 0.4).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        else if(num > 5 && num <= 25)
        {
        	return new BigDecimal(bestAskPrice + 0.3).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        else if(num > 25 && num <= 55)
        {
        	return new BigDecimal(bestAskPrice + 0.2).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return new BigDecimal(bestAskPrice + 0.1).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
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
