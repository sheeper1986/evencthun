package orderBookUpdated52_5;

import java.math.BigDecimal;
import java.text.DateFormat;

public class Format {
	
	public Format(){
		
	}
	
	public double priceFormat(double price){
		double formattedPrice = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return formattedPrice;
	}

	public String timeFormat(Long time){
		String formattedTime = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(time);
		return formattedTime;
	}
}
