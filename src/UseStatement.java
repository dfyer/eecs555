/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

public class UseStatement extends Statement {
	protected String dbname;
	
	public UseStatement() {
		this.type = "USE";
		this.dbname = null;
	}

	public UseStatement(String dbname) {
		this.type = "USE";
		this.dbname = dbname;
	}
}
