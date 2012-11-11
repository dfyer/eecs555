/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

import java.util.ArrayList;

public class SelectStatement extends Statement{

    protected ArrayList<String> attributes;
    protected ArrayList<String> relations;
    protected ArrayList<ConditionExpression> conditions;

    public SelectStatement () {
    	this.type = "SELECT";
        this.attributes = new ArrayList<String>();
        this.relations = new ArrayList<String>();
        this.conditions = new ArrayList<ConditionExpression>();
    }
    
    /* deprecated
    public SelectStatement (String select, String from, String where) {
    	this.type = "SELECT";
        this.attributes = new ArrayList<String>();
        this.attributes.add(select);
        this.relations = new ArrayList<String>();
        this.relations.add(from);
        this.conditions = new ConditionExpression(where);
    }*/
    
    public void addAttribute(String select) {
    	if(this.attributes.contains("*"))
    		return;
    	else if(select.equals("*"))
	    	this.attributes.clear();
	    this.attributes.add(select);	
    }
    
    public void addRelation(String from) {
	    this.relations.add(from);	
    }
    
    public void addCondition(ConditionExpression where) {
    	this.conditions.add(where);
    }
}