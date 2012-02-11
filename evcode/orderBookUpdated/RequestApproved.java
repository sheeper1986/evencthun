package orderBookUpdated52_2;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RequestApproved extends Behaviour
{
	int i = 0;
	public void action() 
	{
		MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.AGREE),
				MessageTemplate.MatchConversationId("TradingPermission")); 
        ACLMessage tradingRequestMsg = myAgent.receive(mt);
        
        if(tradingRequestMsg != null)
        {
			System.out.println(myAgent.getLocalName() + " Start Trading...... ");
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