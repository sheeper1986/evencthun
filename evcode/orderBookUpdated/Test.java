package orderBookUpdated52_2;

import java.text.DateFormat;
import java.util.Timer;

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
		timeTable.addSubBehaviour(new TimeStop());
		timeTable.addSubBehaviour(new newTask());
		addBehaviour(timeTable);
	}
	
	
	/*private class TimeStop extends TickerBehaviour
	{

		public TimeStop(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onTick() {
			// TODO Auto-generated method stub
			System.out.println(i++);
		    reset(100);
			
			if(System.currentTimeMillis() > startTime + 1000*6)
			{
				stop();
			}
		}
	}*/
	
	private class TimeStop extends Behaviour
	{


		@Override
		public void action() {
			{
				
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			if(System.currentTimeMillis() > startTime + 1000*6)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	private class newTask extends OneShotBehaviour
	{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			System.out.println("Well, current time is " + shortFormat.format(System.currentTimeMillis()));
			doDelete();
		}
		
	}
}
