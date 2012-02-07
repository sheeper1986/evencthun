package orderBookUpdated52_2;

import examples.content.eco.elements.Sell;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;

public class OrderBookOntology extends Ontology
{
	//The name identifying this ontology
	public static final String ONTOLOGY_NAME = "Order-book-ontology";
	//Vocabulary
	public static final String ORDER = "order";
	public static final String ORDER_ID = "orderID";
	public static final String ORDER_TYPE = "orderType";
	public static final String ORDER_SIDE = "side";
	public static final String ORDER_SYMBOL = "symbol";
	public static final String ORDER_ORIGINAL_VOLUME = "volume";
	public static final String ORDER_PROCESSED_VOLUME = "processedVolume";
	//public static final String ORDER_LEFT_VOLUME = "leftVolume";
	public static final String ORDER_PRICE = "price";
	public static final String ORDER_DEALING_PRICE = "dealingPrice";
	public static final String ORDER_OPENTIME = "openTime";
	public static final String ORDER_STATUS = "status";
	
	private static Ontology theInstance = new OrderBookOntology();
	
	public static Ontology getInstance()
	{
		return theInstance;
	}
	//private constructor
	private OrderBookOntology()
	{	
		super(ONTOLOGY_NAME,BasicOntology.getInstance());
	
	try
	{
		add(new AgentActionSchema(ORDER), Order.class);
		
		AgentActionSchema aas = (AgentActionSchema)getSchema(ORDER);
		aas.add(ORDER_ID, (PrimitiveSchema)getSchema(BasicOntology.STRING),ObjectSchema.MANDATORY);
		aas.add(ORDER_TYPE, (PrimitiveSchema)getSchema(BasicOntology.INTEGER));
		aas.add(ORDER_SIDE, (PrimitiveSchema)getSchema(BasicOntology.INTEGER));
		aas.add(ORDER_SYMBOL, (PrimitiveSchema)getSchema(BasicOntology.STRING));
		aas.add(ORDER_ORIGINAL_VOLUME, (PrimitiveSchema)getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
		aas.add(ORDER_PROCESSED_VOLUME, (PrimitiveSchema)getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
		//aas.add(ORDER_LEFT_VOLUME, (PrimitiveSchema)getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
		aas.add(ORDER_PRICE, (PrimitiveSchema)getSchema(BasicOntology.FLOAT),ObjectSchema.MANDATORY);
		aas.add(ORDER_DEALING_PRICE, (PrimitiveSchema)getSchema(BasicOntology.FLOAT),ObjectSchema.MANDATORY);
		aas.add(ORDER_OPENTIME, (PrimitiveSchema)getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
        aas.add(ORDER_STATUS, (PrimitiveSchema)getSchema(BasicOntology.INTEGER),ObjectSchema.MANDATORY);
	}		
	catch (OntologyException oe){
		oe.printStackTrace();
		}
	}
}
