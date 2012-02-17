package orderBookUpdated52_5;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;

import java.util.ArrayList;
import java.util.Iterator;

public class InitOrderbookResponder extends CyclicBehaviour
{		
	private ArrayList<AID> aidList;
	private int investorCount = 0;
	
	public InitOrderbookResponder(ArrayList<AID> aidList)
	{
		this.aidList = aidList;
	}
	public void action()
	{
		MessageTemplate pt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
				MessageTemplate.MatchConversationId("TradingRequest"));
		ACLMessage tradingRequestMsg = myAgent.receive(pt);

		if(tradingRequestMsg != null)
		{
            if (tradingRequestMsg.getContent().equals("ReadyToStart"))
            {
                investorCount++;
                System.out.println( "Investors (" + investorCount + " have arrived)" );

                if (investorCount == MarketAgent.NUMBER_OF_NOISETRADER +1) 
                {
                    System.out.println( "All investors are ready, now start......" );
                    for (Iterator it = aidList.iterator();  it.hasNext();) 
					{
                    	ACLMessage replyTradingRequest = new ACLMessage(ACLMessage.AGREE);
                        replyTradingRequest.setConversationId("TradingPermission");
                        replyTradingRequest.addReceiver((AID) it.next());
						myAgent.send(replyTradingRequest);
					}							
                }
            }
		}
		else
			block();
	}
}
