package orderBookUpdated52_5;

import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


public class VWAPTrader extends Agent
{
	private int id = 0;
	private LinkedList<Order> buySideOrdersIV = new LinkedList<Order>();
	private LinkedList<Order> sellSideOrdersIV = new LinkedList<Order>();
	private ArrayList<Order> pendingOrderListIV = new ArrayList<Order>();
	private ArrayList<VWAP> vwapList = new ArrayList<VWAP>();
	private double totalPrice = 0;
	private int totalVolume = 0;
	private double traderPrice = 0;
	private int traderVolume = 0;
	private double marketVWAP = 0;
	private double traderVWAP = 0;
	final int LAST_TICK = 1;
	final int TIME_UP = 2;
	private final long START_TIME = System.currentTimeMillis();
	private final long FINAL_TIME = 1000*60*6;
	private long passedTime = 0;
	private final int NUMBER_OF_SLOTS = 10;
	private final long TIME_SLOT = FINAL_TIME/NUMBER_OF_SLOTS;
	private final long TRADE_FREQUENCY = 500;
	private int stockVolume = 10000;
	private final int INIT_VOLUME = 10000;

	protected void setup()
	{
		System.out.println("This is updated52_5 " + getAID().getName());
			        
        getContentManager().registerLanguage(MarketAgent.codec, FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(MarketAgent.ontology);

    	SequentialBehaviour LogonMarket = new SequentialBehaviour();
    	LogonMarket.addSubBehaviour(new TradingRequest(buySideOrdersIV,sellSideOrdersIV));
    	LogonMarket.addSubBehaviour(new RequestApproved());
    	LogonMarket.addSubBehaviour(new VWAPTradeBehaviour(this,TRADE_FREQUENCY));	
    	
    	addBehaviour(LogonMarket);
    	addBehaviour(new VWAPLocalManager());
    	addBehaviour(new VWAPCounter(this, TIME_SLOT));
	 }
	
	
	private class VWAPTradeBehaviour extends TickerBehaviour
	{
		//long tradingTime = START_TIME;
		int step = 0;
		
		public VWAPTradeBehaviour(Agent a, long period)
		{
			super(a, period);
		}

		protected void onTick() 
		{
			int count = getTickCount();
			try
			{
				if(System.currentTimeMillis() < START_TIME + FINAL_TIME)
				{
					if(marketVWAP !=0 )
					{
						if(stockVolume == 0)
						{
							System.out.println("----------VWAP Alogrithm completed----------");
							new Logger().createVWAPLog(vwapList);
							stop();
						}	
						if(count%(TIME_SLOT/TRADE_FREQUENCY) != 0)
						{
							switch(step)
							{
							case 0:
							{
								Order order = new InitializeOrder().createVWAPSellOrder(stockVolume, TRADE_FREQUENCY, FINAL_TIME - passedTime, 
										getLocalName() + String.valueOf(id++), marketVWAP, 25, 1.002);
										
								Action action = new Action(MarketAgent.marketAID, order);
								ACLMessage orderRequestMsg = new Messages(ACLMessage.CFP, MarketAgent.marketAID).createMessage();
								myAgent.getContentManager().fillContent(orderRequestMsg, action);
								myAgent.send(orderRequestMsg);	
								pendingOrderListIV.add(order);
								System.out.println("CASE1 Pending orders VWAP " + pendingOrderListIV);
								break;
							}	
						    case 1:
						    {
						    	Order order = new InitializeOrder().createVWAPSellOrder(stockVolume, TRADE_FREQUENCY, FINAL_TIME - passedTime, 
										getLocalName() + String.valueOf(id++), marketVWAP, 25, 1.001);
										
								Action action = new Action(MarketAgent.marketAID, order);
								ACLMessage orderRequestMsg = new Messages(ACLMessage.CFP, MarketAgent.marketAID).createMessage();
								myAgent.getContentManager().fillContent(orderRequestMsg, action);
								myAgent.send(orderRequestMsg);	
								pendingOrderListIV.add(order);
								System.out.println("CASE2 Pending orders VWAP " + pendingOrderListIV);
								break;
						    }
						    case 2:
								break;
							}	
						}
						else
						{
							//last tick of each slot
							System.out.println("----------recycling orders----------");//check
						    ArrayList<Order> cancelList = new ManageOrders().recyclingOrders(pendingOrderListIV,LAST_TICK);
						    if(cancelList.size() > 0)
						    {
							   	int i = 0;
							   	Action action = null;
							   	while(i < cancelList.size())
							   	{
							   		action = new Action(MarketAgent.marketAID, cancelList.get(i));
							    	ACLMessage cancelRequestMsg = new Messages(ACLMessage.CANCEL, MarketAgent.marketAID).createMessage();
								    myAgent.getContentManager().fillContent(cancelRequestMsg, action);
								    myAgent.send(cancelRequestMsg);	
								    System.out.println(getLocalName() + " To Cancel " + cancelList.get(i));
								    i++;
								}
						    }
						    System.out.println("----------recycling orders complete----------");
							passedTime += TIME_SLOT;
							if(passedTime >= FINAL_TIME/2 && stockVolume > INIT_VOLUME/2)
							{
								step = 1;
							}
						}			
					}
				}
				else//time up
				{
					System.out.println("----------recycling orders----------");//check
				    ArrayList<Order> cancelList = new ManageOrders().recyclingOrders(pendingOrderListIV,TIME_UP);
				    if(cancelList.size() > 0)
				    {
					   	int i = 0;
					   	Action action = null;
					   	while(i < cancelList.size())
					   	{
					   		action = new Action(MarketAgent.marketAID, cancelList.get(i));
					    	ACLMessage cancelRequestMsg = new Messages(ACLMessage.CANCEL, MarketAgent.marketAID).createMessage();
						    myAgent.getContentManager().fillContent(cancelRequestMsg, action);
						    myAgent.send(cancelRequestMsg);	
						    System.out.println(getLocalName() + " To Cancel " + cancelList.get(i));
						    i++;
						}
				    }
				    System.out.println("----------recycling orders complete----------");
				    
					Order order = new InitializeOrder().createVWAPMarketSell(stockVolume, getLocalName()+String.valueOf(id++));
					Action action = new Action(MarketAgent.marketAID, order);
					ACLMessage orderRequestMsg = new Messages(ACLMessage.CFP, MarketAgent.marketAID).createMessage();
					myAgent.getContentManager().fillContent(orderRequestMsg, action);
					myAgent.send(orderRequestMsg);	
					pendingOrderListIV.add(order);
					
					System.out.println("----------VWAP Alogrithm completed----------");
					new Logger().createVWAPLog(vwapList);
					stop();
				}
			}
			catch (CodecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OntologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class VWAPLocalManager extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), 
					MessageTemplate.MatchOntology(MarketAgent.ontology.getName())); 
			//blockingReceive() cannot use here, because it keeps messages, cyclicBehaviour will not stop
			ACLMessage processedOrderMsg = receive(mt);

			if(processedOrderMsg != null)
			{
				try
				{
					ContentElement ce = null;
				    ce = getContentManager().extractContent(processedOrderMsg);	
				    Action act = (Action) ce;
				    Order orderInfomation = (Order) act.getAction();
				    //System.out.println(orderInfomation);
				    
				    if(processedOrderMsg.getPerformative() == ACLMessage.INFORM)
				    {   
				    	//any orders
				    	new ManageOrders(orderInfomation).updateLocalOrderbook(buySideOrdersIV, sellSideOrdersIV);
				    	//any filled or partially filled orders, to calculate marketVWAP
				    	if(orderInfomation.isFilled()||orderInfomation.isPartiallyFilled())
				    	{
				    		totalPrice += orderInfomation.getProcessedVolume()*orderInfomation.getDealingPrice();
					    	totalVolume += orderInfomation.getProcessedVolume();
					    	marketVWAP = new Format().priceFormat(totalPrice/totalVolume);
				    	}
				    	//My order
				    	if(orderInfomation.getOrderID().contains(getLocalName()))
				    	{
				    		new ManageOrders(orderInfomation).updatePendingOrderList(pendingOrderListIV);
				    		if(orderInfomation.isFilled()||orderInfomation.isPartiallyFilled())
				    		{
				    			stockVolume -= orderInfomation.getProcessedVolume();
				    			//to calculate trader's VWAP
				    			traderPrice += orderInfomation.getProcessedVolume()*orderInfomation.getDealingPrice();
						    	traderVolume += orderInfomation.getProcessedVolume();
						    	traderVWAP = new Format().priceFormat(traderPrice/traderVolume);
				    		}
					    	System.out.println(myAgent.getLocalName() + " Updated Pending List" + pendingOrderListIV);
					    	System.out.println("Volume left: " + stockVolume);
				    	}
				    }
				    	//System.out.println(myAgent.getLocalName() + " BuyOrders: " + buySideOrdersIV.size());
				    	//System.out.println(myAgent.getLocalName() + " SellOrders: " + sellSideOrdersIV.size());
				}	
				catch(CodecException ce){
					ce.printStackTrace();
					}
				catch(OntologyException oe){
					oe.printStackTrace();
					}
				}
				else
					block();
			}
		}
	
	private class VWAPCounter extends TickerBehaviour
	{
		//private int mode;
		public VWAPCounter(Agent a, long period)//, int mode) 
		{
			super(a, period);
			//this.mode = mode;
		}

		@Override
		protected void onTick() {
			// TODO Auto-generated method stub
			VWAP vwap = new VWAP();
			vwap.setMarketPrice(marketVWAP);
			vwap.setTraderPrice(traderVWAP);
			vwap.setVWAPTime(System.currentTimeMillis());
			vwapList.add(vwap);
			/*if(mode == 2)
			{
				totalPrice = 0;
				totalVolume = 0;
				traderPrice = 0;
				traderVolume = 0;
				marketVWAP = 0;
				traderVWAP = 0;
			}*/
		}		
	}
}

