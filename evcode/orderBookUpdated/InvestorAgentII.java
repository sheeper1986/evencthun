package orderBookUpdated50_6;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class InvestorAgentII extends Agent
{
	//private Strategy tradeStrategy = new Strategy();
	private ArrayList<Order> pendingOrderListII = new ArrayList<Order>();
	//private ArrayList<Asset> assetList = new ArrayList<Asset>();
	//private Asset asset = new Asset();
	//private double fundAvailable;
	private int id = 0;
	private LinkedList<Order> buySideOrdersII = new LinkedList<Order>();
	private LinkedList<Order> sellSideOrdersII = new LinkedList<Order>();
	
	protected void setup()
	{
		try 
		{
			System.out.println("This is updated50_6 " + getAID().getName());
			
			ServiceDescription sd = new ServiceDescription();
            sd.setType( "NoisyTrader" );
            sd.setName( "NoisyTraderDescription" );
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            dfd.addServices( sd );
            DFService.register( this, dfd );
            
            getContentManager().registerLanguage(MarketAgent.codecI, FIPANames.ContentLanguage.FIPA_SL0);
    		getContentManager().registerOntology(MarketAgent.ontology);

    		SequentialBehaviour LogonMarket = new SequentialBehaviour();
    		LogonMarket.addSubBehaviour(new TradingRequest());
    		LogonMarket.addSubBehaviour(new TradingPermission());
    		LogonMarket.addSubBehaviour(new NoisyTradeBehaviour(this,1000));
    		
    		addBehaviour(LogonMarket);
    		addBehaviour(new LocalOrderManager());
    		//addBehaviour(new AutoCancel());
		} 
		catch (FIPAException e) {
			e.printStackTrace();
		}
	 }
	
	private class TradingRequest extends OneShotBehaviour
	{
		public void action() 
		{
			buySideOrdersII.addAll(MarketAgent.buySideOrders);
    		Collections.sort(buySideOrdersII);
    		sellSideOrdersII.addAll(MarketAgent.sellSideOrders);
    		Collections.sort(sellSideOrdersII);
    		
    		System.out.println(getAID().getLocalName() + " LocalBuyOrdersII: " + buySideOrdersII.size());
    		System.out.println(getAID().getLocalName() + " LocalSellSellOrdersII: " + sellSideOrdersII.size());
    		
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
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.AGREE), MessageTemplate.MatchConversationId("TradingPermission")); 
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
	
	private class NoisyTradeBehaviour extends TickerBehaviour
	{	
		public NoisyTradeBehaviour(Agent a, long period) 
		{
			super(a, period);
		}

		protected void onTick()
		{
			int randomVolume = (int)(30+Math.random()*70);
			int randomSide = (int)(1+Math.random()*2);
			int randomTime = (int)(1000 + Math.random()*1000);
			int randomType = (int)(1+Math.random()*2);
			int randomSellPrice = (int)(50+Math.random()*6);
			int randomBuyPrice = (int)(45+Math.random()*6);
			
		    Order newOrder = new Order();
			try
			{
				newOrder.setOrderType(randomType);
				
				if(newOrder.getOrderType() == 1)
				{
					newOrder.setOrderID(myAgent.getAID().getLocalName()+String.valueOf(id++));
					newOrder.setSymbol("GOOGLE");
					newOrder.setSide(randomSide);
					newOrder.setOriginalVolume(randomVolume);
					newOrder.setOpenTime(System.currentTimeMillis());
				}
				else if(newOrder.getOrderType() == 2)
				{
					newOrder.setSide(randomSide);
					
					if(newOrder.getSide() == 1)
					{
						newOrder.setOrderID(myAgent.getAID().getLocalName()+String.valueOf(id++));
						newOrder.setSymbol("GOOGLE");
						newOrder.setOriginalVolume(randomVolume);
						newOrder.setPrice(randomBuyPrice);
						newOrder.setOpenTime(System.currentTimeMillis());
					}
					else if(newOrder.getSide() == 2)
					{
						newOrder.setOrderID(myAgent.getAID().getLocalName()+String.valueOf(id++));
						newOrder.setSymbol("GOOGLE");
						newOrder.setOriginalVolume(randomVolume);
						newOrder.setPrice(randomSellPrice);
						newOrder.setOpenTime(System.currentTimeMillis());
					}	
				}
				
				pendingOrderListII.add(newOrder);
				System.out.println("Pending ordersII " + pendingOrderListII);
				
				Action action = new Action(MarketAgent.marketAID, newOrder);
				ACLMessage orderRequestMsg = new ACLMessage(ACLMessage.CFP);
				orderRequestMsg.addReceiver(MarketAgent.marketAID);
				orderRequestMsg.setOntology(MarketAgent.ontology.getName());
				orderRequestMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
				myAgent.getContentManager().fillContent(orderRequestMsg, action);
				myAgent.send(orderRequestMsg);	
				reset(randomTime);
			}
			catch(Exception ex){
				System.out.println(ex);
			}
		}
	}
	
	private class LocalOrderManager extends CyclicBehaviour
	{
		public void action()
		{
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchLanguage(FIPANames.ContentLanguage.FIPA_SL0), MessageTemplate.MatchOntology(MarketAgent.ontology.getName())); 
			//blockingReceive() cannot use here, because it keeps messages, cyclicBehaviour will not stop
			ACLMessage processedOrderMsg = receive(mt);
			//System.out.println("LocalBuy~~~~~~ " + buySideOrders.size());
			//System.out.println("LocalSell~~~~~~ " + sellSideOrders.size());
			if(processedOrderMsg != null){
			try
			{
				ContentElement ce = null;
				ce = getContentManager().extractContent(processedOrderMsg);	
				Action act = (Action) ce;
				Order processedOrder = (Order) act.getAction();
				
				if(processedOrderMsg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
				{
					System.out.println("Filled !" + processedOrder);
					processedOrder.updatePendingOrderList(pendingOrderListII);
					//asset.updateAssetList(assetList, processedOrder);
				}
					
				else if(processedOrderMsg.getPerformative() == ACLMessage.INFORM)
				{
					System.out.println("Great PartlyFilled !" + processedOrder);
					processedOrder.updatePendingOrderList(pendingOrderListII);
						//asset.updateAssetList(assetList, processedOrder);
				}
					
				else if(processedOrderMsg.getPerformative() == ACLMessage.REJECT_PROPOSAL)
				{
					System.out.println("Rejected !" + processedOrder);
					processedOrder.updatePendingOrderList(pendingOrderListII);
				}
					
				else if(processedOrderMsg.getPerformative() == ACLMessage.CONFIRM)
				{
					System.out.println("Cancel Successful !" + processedOrder);
					processedOrder.updatePendingOrderList(pendingOrderListII);
				}
					
					System.out.println("Updated Pending ListII " + pendingOrderListII);
					//System.out.println("Market Price: " + MarketAgent.currentPrice);
					//System.out.println("Updated Asset List " + assetList);
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

	/*private class PriceChecker extends TickerBehaviour
	{
		public PriceChecker(Agent a, long period) 
		{
			super(a, period);
		}

		protected void onTick()
		{
			try
			{
				ACLMessage checkPriceMsg = new ACLMessage(ACLMessage.REQUEST);
				checkPriceMsg.setConversationId("CheckPrice");
				checkPriceMsg.addReceiver(MarketAgent.marketAID);
				myAgent.send(checkPriceMsg);	
			}
			catch(Exception ex){
				System.out.println(ex);
			}
		}
	}*/
	
	/*private class AutoCancel extends CyclicBehaviour
	{
		public void action() 
		{
			//MessageTemplate pt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.MatchConversationId("PriceInform"));
			//ACLMessage receiPrice = receive(pt);
			
			//if(receiPrice != null)
			//{
				double marketPrice = MarketAgent.currentPrice;//Double.parseDouble(receiPrice.getContent());
				//System.out.println(marketPrice);
				if(pendingOrderList.size()>5 && marketPrice != 0)
				{
					ArrayList<Order> temp = new ArrayList();
					temp.addAll(tradeStrategy.matchedOrderSpread(pendingOrderList, marketPrice, 3));
					
					int i = 0;
					while (i < temp.size())
					{
						try
						{
							Action cancelAct = new Action(MarketAgent.marketAID, temp.get(i));
							ACLMessage cancelMsg = new ACLMessage(ACLMessage.CANCEL);
							cancelMsg.addReceiver(MarketAgent.marketAID);
							cancelMsg.setOntology(MarketAgent.ontology.getName());
							cancelMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
                            myAgent.getContentManager().fillContent(cancelMsg, cancelAct);
							myAgent.send(cancelMsg);
							//temp.remove(i);
							i++;
						 }
						catch (CodecException e){
							 e.printStackTrace();
							 } 
						catch (OntologyException e){
								e.printStackTrace();
								}	
					 }
					block();	
				}
				else
					block();
			//}
			//else
			//	block();	
		}
	}
	*/
}