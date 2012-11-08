/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

public class CreateDatabaseStatement extends Statement{
	protected String dbname;
	
	public CreateDatabaseStatement() {
		this.type = "CREATE DATABASE";
		this.dbname = null;
	}

	public CreateDatabaseStatement(String dbname) {
		this.type = "CREATE DATABASE";
		this.dbname = dbname;
	}
}
