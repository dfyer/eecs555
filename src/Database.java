/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

import java.util.HashMap;

public class Database {
	protected String name;
	protected HashMap<String, Table> tables;
	
	public Database() {
		this.name = null;
		this.tables = new HashMap<String, Table>();
	}
	
	public String getName() {
		return name;
	}
	
	public void addTable(String tname, Table t) {
		tables.put(tname, t);
	}
	
	public Table getTable(String tname) {
		return tables.get(tname);
	}
	
	public void printAll() {
		for(String tableName : tables.keySet()) {
			System.out.println(tableName);
		}
	}
}
