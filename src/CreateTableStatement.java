/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

import java.util.ArrayList;
import java.util.HashMap;


public class CreateTableStatement extends Statement{
	protected String tableName;
	protected ArrayList<String> names = new ArrayList<String>();
	protected ArrayList<String> types = new ArrayList<String>();
	
	public CreateTableStatement() {
		this.type = "CREATE TABLE";
		this.tableName = null;
	}

	public CreateTableStatement(String tableName) {
		this.type = "CREATE TABLE";
		this.tableName = tableName;
	}

	public void add(String name, String type) {
		names.add(name);
		types.add(type);
	}
}
