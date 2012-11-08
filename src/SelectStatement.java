/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

import java.util.ArrayList;

public class SelectStatement extends Statement{

    protected ArrayList<String> select;
    protected String from;
    protected ConditionExpression where;

    public SelectStatement () {
    	this.type = "SELECT";
        this.select = new ArrayList<String>();
        this.from = null;
        this.where = null;
    }
    
    public SelectStatement (String select, String from, String where) {
    	this.type = "SELECT";
        this.select = new ArrayList<String>();
        this.select.add(select);
        this.from = from;
        this.where = new ConditionExpression(where);
    }
    
    public void addProjection(String select) {
    	if(this.select.contains("*"))
    		return;
    	else if(select.equals("*"))
	    	this.select.clear();
	    this.select.add(select);	
    }
}