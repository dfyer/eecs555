/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

import java.util.ArrayList;


public class InsertStatement extends Statement{
	protected String tableName;
	protected ArrayList<String> tuple = new ArrayList<String>();
	
	public InsertStatement() {
		this.type = "INSERT";
		this.tableName = null;
	}

	public InsertStatement(String tableName) {
		this.type = "INSERT";
		this.tableName = tableName;
	}

	public void add(String value) {
		tuple.add(value);
	}
}
