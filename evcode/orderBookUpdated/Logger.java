
package orderBookUpdated52_2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.ArrayList;


public class Logger 
{

    public Logger()
    {
 
    }
 
    public void createOrdersLog(ArrayList<Order> historyOrders)
    {
        try
        {
            FileWriter fstream = new FileWriter("C:/Users/Ev/Desktop/history.csv");
            BufferedWriter out = new BufferedWriter(fstream);
            for (Order order:historyOrders){
            	
                String orderID = order.getOrderID();
                String symbol = order.getSymbol();
                double dealingPrice = order.getDealingPrice();
                int ProcessedVolume = order.getProcessedVolume();
                long openTime = order.getOpenTime();
               
                out.write(orderID+ "," + symbol +", "+OrderSide.getSide(order).toString()+", " +OrderType.getOrderType(order).toString() 
                		+", "+Double.toString(dealingPrice)+", "+Integer.toString(ProcessedVolume)+", "+ OrderStatus.getOrderStatus(order).toString()
                		+  "," + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(openTime));
                out.newLine();
            }
            out.close();
        }
        catch (Exception e){System.err.println("Could not write to file" + e.getMessage());}
    }
    
    public void createVWAPLog(ArrayList<VWAP> historyVWAP)
    {
        try
        {
            FileWriter fstream = new FileWriter("C:/Users/Ev/Desktop/historyOfVWAP.csv");
            BufferedWriter out = new BufferedWriter(fstream);
            for (VWAP vwap:historyVWAP){
            	
                double marketVWAP = vwap.getMarketVWAP();
                double traderVWAP = vwap.getTraderVWAP();
                Long vwapTime = vwap.getTime();
               
                out.write(Double.toString(marketVWAP) + " , " + Double.toString(traderVWAP) + " , " + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(vwapTime));
                out.newLine();
            }
            out.close();
        }
        catch (Exception e){System.err.println("Could not write to file" + e.getMessage());}
    }
}
