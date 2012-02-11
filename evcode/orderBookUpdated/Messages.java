package orderBookUpdated52_2;

import jade.content.Concept;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

public class Messages {
	private int messageType;
	private AID messageAID;
	//private String ontologyName;
	//private String language;
	ACLMessage message;
	//Action action;
	
	public Messages(int messageType, AID messageAID)//, String ontologyName, String language, AID aid, Concept concept)
	{
		this.messageType = messageType;
		this.messageAID = messageAID;
		//this.ontologyName = ontologyName;
		//this.language = language;
		this.message = new ACLMessage(messageType);
		//this.action = new Action(aid, concept);
	}

	public ACLMessage createMessage()
	{
		message.addReceiver(messageAID);
		message.setOntology(MarketAgent.ontology.getName());
		message.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		
		return message;
	}
}
