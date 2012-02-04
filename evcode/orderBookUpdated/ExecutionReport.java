
package orderBookUpdated52_1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;


public class ExecutionReport 
{
    private final ArrayList <Order> historyOrders;
    
    public ExecutionReport()
    {
        this (new ArrayList<Order>());
    }
    public ExecutionReport(final ArrayList<Order> historyOrders)
    {
        this.historyOrders = new ArrayList<Order> (historyOrders);
    }
    
    public void addOrder(Order order)
    {
        historyOrders.add(order);
    }
    
    public ArrayList <Order> getOrders()
    {
        return historyOrders;
    }
    
    public void createHistoryLogger()
    {
        try
        {
            FileWriter fstream = new FileWriter("C:/Users/Ev/Desktop/history.csv");
            BufferedWriter out = new BufferedWriter(fstream);
            for (Order order:historyOrders){
            	
                String orderID = order.getOrderID();
                String symbol = order.getSymbol();
                //int side = order.getSide();
                //int type = order.getOrderType();
                double dealingPrice = order.getDealingPrice();
                int ProcessedVolume = order.getProcessedVolume();
                //int status = order.getStatus();
                long openTime = order.getOpenTime();
               
                out.write(orderID+ "," + symbol +", "+OrderSide.getSide(order).toString()+", " +OrderType.getOrderType(order).toString() +", "+Double.toString(dealingPrice)+", "+Integer.toString(ProcessedVolume)+", "+ OrderStatus.getOrderStatus(order).toString() +  "," + Long.toString(openTime));
                out.newLine();
            }
            out.close();
        }
        catch (Exception e){System.err.println("Could not write to file" + e.getMessage());}
    }
    
}
