
package orderBookUpdated52_2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;


public class ExecutionReport 
{

    public ExecutionReport()
    {
 
    }
 
    public void createHistoryOrderLogger(ArrayList<Order> historyOrders)
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
               
                out.write(orderID+ "," + symbol +", "+OrderSide.getSide(order).toString()+", " +OrderType.getOrderType(order).toString() +", "+Double.toString(dealingPrice)+", "+Integer.toString(ProcessedVolume)+", "+ OrderStatus.getOrderStatus(order).toString() +  "," + Long.toString(openTime));
                out.newLine();
            }
            out.close();
        }
        catch (Exception e){System.err.println("Could not write to file" + e.getMessage());}
    }
    
    public void createHistoryVWAPLogger(ArrayList<VWAP> historyVWAP)
    {
        try
        {
            FileWriter fstream = new FileWriter("C:/Users/Ev/Desktop/historyOfVWAP.csv");
            BufferedWriter out = new BufferedWriter(fstream);
            for (VWAP vwap:historyVWAP){
            	
                double vwapPrice = vwap.getVwapPrice();
                Long vwapTime = vwap.getVwapTime();
               
                out.write(Double.toString(vwapPrice) + " , " + Long.toString(vwapTime));
                out.newLine();
            }
            out.close();
        }
        catch (Exception e){System.err.println("Could not write to file" + e.getMessage());}
    }
}
