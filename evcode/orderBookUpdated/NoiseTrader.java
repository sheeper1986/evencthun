package orderBookUpdated52_5;

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

public class NoiseTrader extends Agent
{
	private int id = 0;
	private ArrayList<Order> pendingOrderList = new ArrayList<Order>();
	private LinkedList<Order> buySideOrders = new LinkedList<Order>();
	private LinkedList<Order> sellSideOrders = new LinkedList<Order>();

	protected void setup()
	{
		System.out.println("This is updated52_5 " + getAID().getName());
			        
        getContentManager().registerLanguage(MarketAgent.codec, FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(MarketAgent.ontology);

    	SequentialBehaviour LogonMarket = new SequentialBehaviour();
    	LogonMarket.addSubBehaviour(new TradingRequest(buySideOrders,sellSideOrders));
    	LogonMarket.addSubBehaviour(new RequestApproved());
    	LogonMarket.addSubBehaviour(new NoiseTraderBehaviour(this,2000, buySideOrders, sellSideOrders, pendingOrderList));
    		
    	addBehaviour(LogonMarket);
    	addBehaviour(new LocalOrderManager(buySideOrders, sellSideOrders, pendingOrderList));
	 }
}
