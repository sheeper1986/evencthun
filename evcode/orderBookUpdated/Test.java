package orderBookUpdated52_2;

import java.text.DateFormat;
import java.util.Timer;
import java.util.TimerTask;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;

public class Test extends Agent
{
	private int i = 1;
	private final Long startTime = System.currentTimeMillis();
	DateFormat shortFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
	
	protected void setup()
	{
		System.out.println("Hello! This is " + getLocalName() + " start at " + shortFormat.format(startTime));
		SequentialBehaviour timeTable = new SequentialBehaviour();
		timeTable.addSubBehaviour(new TimeStop(this,1000));
		//timeTable.addSubBehaviour(new newTask());
		addBehaviour(timeTable);
	}
	
	
	private class TimeStop extends TickerBehaviour
	{
		Long tradeTime = startTime;
		
		public TimeStop(Agent a, long period) {
			super(a, period);
			//
			// TODO Auto-generated constructor stub
		}
		//public void onStart()
		//{
			//reset(100);
		//}
		@Override
		protected void onTick() {
			// TODO Auto-generated method stub
			//orderList
			//for(int j = 0; j < 20; j++)
			//{
				//try
				//{
			
				//System.out.println(getTickCount());
			
				if(getTickCount()!= 18)
				{
					System.out.println(i++);
					
				}
				else
				{
					System.out.println("last tick");
				}
					
					//Thread.sleep(50);
				//} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				//}
			//}

		    //reset(100);
			
			if(System.currentTimeMillis() >= tradeTime + 1000*6)
			{
				//stop();
				tradeTime += 1000*6;
				System.out.println("Do something");
				//reset();
			}
			//if(getTickCount()==1)
			//{
				
			//}

			if(System.currentTimeMillis() >= startTime + 1000*18)
			{
				System.out.println("Well, current time is " + shortFormat.format(System.currentTimeMillis()));
				stop();
			}			
		}
		
			
	}
	
	//private class newTask extends OneShotBehaviour
	//{

		//@Override
		//public void action() {
			// TODO Auto-generated method stub
			//System.out.println("Well, current time is " + shortFormat.format(System.currentTimeMillis()));
			//doDelete();
		//}
		
	//}
}
