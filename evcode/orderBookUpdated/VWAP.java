package orderBookUpdated52_5;

import java.util.ArrayList;

public class VWAP {
	private double marketVWAP;
	private double traderVWAP;
	private double error;
	private Long timeStamp;
	
	public VWAP(){
		
	}
	
	public void setMarketPrice(double marketVWAP){
		this.marketVWAP = marketVWAP;
	}
	
	public void setTraderPrice(double traderVWAP){
		this.traderVWAP = traderVWAP;
	}
	
	public void setVWAPTime(Long timeStamp){
		this.timeStamp = timeStamp;
	}

	public double getMarketVWAP(){
		return marketVWAP;
	}
	
	public double getTraderVWAP(){
		return traderVWAP;
	}
	
	public double getError(){
		return error = traderVWAP - marketVWAP;
	}
	
	public Long getTime(){
		return timeStamp;
	}
}
