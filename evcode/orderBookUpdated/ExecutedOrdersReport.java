package orderBookUpdated52_5;

import java.util.ArrayList;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

public class ExecutedOrdersReport extends WakerBehaviour
{
	ArrayList<Order> executedOrders = new ArrayList<Order>();
	
	public ExecutedOrdersReport(Agent a, long timeout, ArrayList<Order> executedOrders)
	{
		super(a, timeout);
		this.executedOrders = executedOrders;
		// TODO Auto-generated constructor stub
	}
	public void onWake()
	{
		new Logger().createOrdersLog(executedOrders);
		System.out.println("OutPut Completed");
	}
}
