package orderBookUpdated52_5;

import java.util.ArrayList;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

public class Test2 extends Agent
{
	private ArrayList<Integer> list;
	private Random random;
	
	public Test2()
	{
		this.list = new ArrayList<Integer>();
		this.random = new Random();
	}
	protected void setup()
	{
		System.out.println("Hello! This is " + getLocalName());
		
		addBehaviour(new cyclicAdding(this,2000));
		this.startAdding();
	}
	
	private class cyclicAdding extends TickerBehaviour
	{

		public cyclicAdding(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onTick() {
			// TODO Auto-generated method stub
			list.add(random.nextInt(50)+1);
			System.out.println(getLocalName() + " list contains " + list);
		}		
	}
	
	protected void startAdding() 
    {
		AgentContainer c = getContainerController();; // get a container controller for creating new agents
		System.out.println("Agent Starter " +getAID().getName()+ " has been intitialised");
	      int numberOfAgents = 10;
	      
	      try{
	          //AgentController marketAgent = c.createNewAgent( "market" , "abms.MarketAgent", null );
	          //marketAgent.start();
	          for (int i = 0; i < numberOfAgents; i++){
	              AgentController trader = c.createNewAgent("NoiseTrader"+Integer.toString(i), "orderBookUpdated52_5.Test2", null);
	              trader.start();
	          }  
	      }
	      catch (Exception e){}
    }
}
