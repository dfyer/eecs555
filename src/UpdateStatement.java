/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

public class UpdateStatement extends Statement{

    protected String tableName;
    protected String attr;
    protected String val;
    protected ConditionExpression where;

    public UpdateStatement () {
    	this.type = "UPDATE";
        this.attr = null;
        this.val = null;
        this.where = null;
    }
    
    public UpdateStatement (String attr, String val, String where) {
    	this.type = "UPDATE";
        this.attr = attr;
        this.val = val;
        this.where = new ConditionExpression(where);
    }
    
}
