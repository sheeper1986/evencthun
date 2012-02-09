package orderBookUpdated52_2;

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
	private ArrayList<Order> pendingOrderListIV = new ArrayList<Order>();
	private LinkedList<Order> buySideOrdersIV = new LinkedList<Order>();
	private LinkedList<Order> sellSideOrdersIV = new LinkedList<Order>();
	private RandomGenerator rg = new RandomGenerator();
	private ArrayList<VWAP> vwapList = new ArrayList<VWAP>();
	private double totalPrice = 0;
	private int totalVolume = 0;
	private double traderPrice = 0;
	private int traderVolume = 0;
	private double marketVWAP = 0;
	private double traderVWAP = 0;
	private final long START_TIME = System.currentTimeMillis();
	private final long FINAL_TIME = 1000*60*6;
	private long passedTime = 0;
	private final long TIME_SLOT = 1000*36;
	private final long TRADE_FREQUENCY = 1000*6;
	private int stockVolume = 10000;

	protected void setup()
	{
		System.out.println("This is updated52_1 " + getAID().getName());
			        
        getContentManager().registerLanguage(MarketAgent.codecI, FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(MarketAgent.ontology);

    	SequentialBehaviour LogonMarket = new SequentialBehaviour();
    	LogonMarket.addSubBehaviour(new TradingRequest());
    	LogonMarket.addSubBehaviour(new TradingPermission());
    	LogonMarket.addSubBehaviour(new VWAPTradeBehaviour(this,TRADE_FREQUENCY));
    		
    	addBehaviour(LogonMarket);
    	addBehaviour(new LocalOrderManager());
	 }
	
	private class TradingRequest extends OneShotBehaviour
	{
		public void action() 
		{
			buySideOrdersIV.addAll(MarketAgent.buySideQueue);
    		Collections.sort(buySideOrdersIV);
    		sellSideOrdersIV.addAll(MarketAgent.sellSideQueue);
    		Collections.sort(sellSideOrdersIV);
    		
    		System.out.println(getAID().getLocalName() + " LocalBuyOrders: " + buySideOrdersIV.size());
    		System.out.println(getAID().getLocalName() + " LocalSellOrders: " + sellSideOrdersIV.size());
    		
			ACLMessage tradingRequestMsg = new ACLMessage(ACLMessage.REQUEST);
			tradingRequestMsg.setConversationId("TradingRequest");
			tradingRequestMsg.setContent("ReadyToStart");
			tradingRequestMsg.addReceiver(MarketAgent.marketAID);
			myAgent.send(tradingRequestMsg);				
		}	
	}
	
	private class TradingPermission extends Behaviour
	{
		int i = 0;
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.AGREE),
					MessageTemplate.MatchConversationId("TradingPermission")); 
            ACLMessage tradingRequestMsg = receive(mt);
            
            if(tradingRequestMsg != null)
            {
    			System.out.println(getAID().getLocalName() + " Start Trading...... ");
    			i++;
            }
		}

		public boolean done() {
			if(i < 1)
			{
				return false;
			}
			else
				return true;
		}
	}
	
	private class VWAPTradeBehaviour extends TickerBehaviour
	{
		long tradingTime = START_TIME;
		public VWAPTradeBehaviour(Agent a, long period) 
		{
			super(a, period);
		}

		protected void onTick() 
		{
			try
			{
				if(marketVWAP !=0 )
				{
					Order order = new InitializeOrder().createVWAPSellOrder(stockVolume, TRADE_FREQUENCY, FINAL_TIME - passedTime, 
							getLocalName()+ String.valueOf(id++), marketVWAP);
						
					Action action = new Action(MarketAgent.marketAID, order);
					ACLMessage orderRequestMsg = new ACLMessage(ACLMessage.CFP);
					orderRequestMsg.addReceiver(MarketAgent.marketAID);
					orderRequestMsg.setOntology(MarketAgent.ontology.getName());
					orderRequestMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
					myAgent.getContentManager().fillContent(orderRequestMsg, action);
					myAgent.send(orderRequestMsg);	
					pendingOrderListIV.add(order);
					System.out.println("Pending orders VWAP " + pendingOrderListIV);
				}
				
				if(System.currentTimeMillis() >= tradingTime + TIME_SLOT)
				{
					System.out.println("!!!!!!!!!!recycling orders!!!!!!!!!!!");//check
				    ArrayList<Order> cancelList = new ManageOrders().recyclingOrders(pendingOrderListIV);
				    if(cancelList.size() > 0)
				    {
				    	int i = 0;
				    	while(i < cancelList.size())
				    	{
				    		Action actionI = new Action(MarketAgent.marketAID, cancelList.get(i));
				    		ACLMessage cancelRequestMsg = new ACLMessage(ACLMessage.CANCEL);
				    		cancelRequestMsg.addReceiver(MarketAgent.marketAID);
						    cancelRequestMsg.setOntology(MarketAgent.ontology.getName());
						    cancelRequestMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
						    myAgent.getContentManager().fillContent(cancelRequestMsg, actionI);
						    myAgent.send(cancelRequestMsg);	
						    System.out.println(getLocalName() + " Cancel " + cancelList.get(i));
						    i++;
						}
				    }
				    tradingTime += TIME_SLOT;
				    passedTime += TIME_SLOT;
				 }
				if(System.currentTimeMillis() >= START_TIME + FINAL_TIME)
				{
					System.out.println("Sell all, VWAP Alogrithm completed!");
					Order order = new InitializeOrder().createVWAPMarketSell(stockVolume, getLocalName()+String.valueOf(id++));
					Action action = new Action(MarketAgent.marketAID, order);
					ACLMessage orderRequestMsg = new ACLMessage(ACLMessage.CFP);
					orderRequestMsg.addReceiver(MarketAgent.marketAID);
					orderRequestMsg.setOntology(MarketAgent.ontology.getName());
					orderRequestMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
					myAgent.getContentManager().fillContent(orderRequestMsg, action);
					myAgent.send(orderRequestMsg);	
					pendingOrderListIV.add(order);
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
	
	
	private class LocalOrderManager extends CyclicBehaviour
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
				    	if(orderInfomation.isLimitOrder()&&(orderInfomation.isFilled()||orderInfomation.isPartiallyFilled()))
				    	{
				    		totalPrice += orderInfomation.getProcessedVolume()*orderInfomation.getDealingPrice();
					    	totalVolume += orderInfomation.getProcessedVolume();
					    	marketVWAP = new BigDecimal(totalPrice/totalVolume).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
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
						    	traderVWAP = new BigDecimal(traderPrice/traderVolume).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				    		}
					    	System.out.println("Updated Pending List VWAP " + pendingOrderListIV);
					    	System.out.println("Volume left: " + stockVolume);
				    	}
				    	new VWAP().calculateVWAP(vwapList, marketVWAP, traderVWAP);
				    	ExecutionReport  vr = new ExecutionReport ();
				    	vr.createHistoryVWAPLogger(vwapList);
				    }
				    	System.out.println(getAID().getLocalName() + " BuyOrdersIV: " + buySideOrdersIV.size());
				    	System.out.println(getAID().getLocalName() + " SellOrdersIV: " + sellSideOrdersIV.size());
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
	}
//new VWAP().calculateVWAP(orderInfomation, vwapList, totalPrice, totalVolume);
//v.setVwapPrice(totalPrice/totalVolume);
//v.setVwapTime(orderInfomation.getOpenTime());
//vwapList.add(v);
//ExecutionReport  vr = new ExecutionReport ();
//vr.createHistoryVWAPLogger(vwapList);
