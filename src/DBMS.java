/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class DBMS {
	private HashMap<String, Database> dbs;
	protected String nameOfDBUsing;
	protected Database DBUsing;
	
	public DBMS() {
		this.dbs = new HashMap<String, Database>();
		this.nameOfDBUsing = null;
		this.DBUsing = null;
	}
	
	public void executeStatement(Statement stmt) {
		long startTime = System.currentTimeMillis();
		if(stmt.getType().equals("CREATE DATABASE"))
			createDatabase((CreateDatabaseStatement)stmt);
		else if(stmt.getType().equals("USE"))
			useDatabase((UseStatement)stmt);
		else if(stmt.getType().equals("SHOW DATABASES"))
			showDatabases();
		else if(stmt.getType().equals("CREATE TABLE"))
			try {
				createTable((CreateTableStatement)stmt);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		else if(stmt.getType().equals("INSERT"))
			insertTuple((InsertStatement)stmt);
		else if(stmt.getType().equals("UPDATE"))
			update((UpdateStatement)stmt);
		else if(stmt.getType().equals("SELECT"))
			select((SelectStatement)stmt);
		System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
	/* Query Execution codes are written below */
	
	// CREATE DATABASE
	private boolean createDatabase(CreateDatabaseStatement cds) {
		if(dbs.containsKey(cds.dbname)) {
			System.out.println("Error: Can't create database " + cds.dbname + "; database exists");
			return false;
		}
		else {
			Database newdb = new Database();
			dbs.put(cds.dbname, newdb);
			return true;
			
		}
	}
	
	// USE
	private boolean useDatabase(UseStatement us) {
		if(!dbs.containsKey(us.dbname)) {
			System.out.println("Error: Unknown database " + us.dbname);
			return false;
		}
		else {
			nameOfDBUsing = us.dbname;
			DBUsing = dbs.get(nameOfDBUsing);
			return true;
		}
	}

	// SHOW DATABASES
	private void showDatabases() {
		if(dbs.isEmpty())
			System.out.println("No database exists.");
		else {
			System.out.println("Database");
			System.out.println("--------");
			for(String dbname : dbs.keySet())
				System.out.println(dbname);
		}
	}
	
	// CREATE TABLE
	private boolean createTable(CreateTableStatement cts) throws IOException {
		if(DBUsing.equals(null)) {
			System.out.println("Error: Not using any database.");
			return false;
		}
		else if(cts.tableName.toCharArray().length < 4) {
			System.out.println("Attention: Table name must be longer than 3 characters");
			return false;
		}
		else {
			// Dynamically create appropriate class representing a tuple of the table which implements 'Tuple' interface.
			PrintWriter fw = null;
	        String outFName = ".//src//" + cts.tableName + ".java";
	        fw = new PrintWriter(new BufferedWriter(new FileWriter(outFName)));
	        fw.print("/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */\n");
	        fw.print("// This file is created by DBMS.java\n");
	        fw.print("\n");
	        fw.print("public class " + cts.tableName + " extends Tuple {\n");
	        fw.print("\n");
	        
	        // Member variables
	        ArrayList<String> tabletypes = new ArrayList<String>();
	        ArrayList<String> tableAttributes = new ArrayList<String>();
	        for(int i = 0; i < cts.types.size(); i++) {
	        	String type = cts.types.get(i);

	        	String attributeName  = cts.names.get(i);
	        	String attributeType;
	        	
	        	if(type.toUpperCase().equals("INTEGER")) {
	        		attributeType = "int";
	        		tabletypes.add("int");
	        		tableAttributes.add(attributeName);
	        	}
	        	else if(type.toUpperCase().equals("STRING")) {
	        		attributeType = "String";
	        		tabletypes.add("String");
	        		tableAttributes.add(attributeName);
	        	}
	        	else if(type.toUpperCase().equals("DOUBLE")) {
	        		attributeType = "double";
	        		tabletypes.add("double");
	        		tableAttributes.add(attributeName);
	        	}
	        	else
	        		break;
	        	
	        	fw.print("    public " + attributeType + " " + attributeName + ";\n");
	        }
	        fw.print("\n");
	        
	        // Constructors
	        fw.print("    public " + cts.tableName + "() {\n");
	        fw.print("    }\n");
	        fw.print("\n");
	        fw.print("    public " + cts.tableName + "(");
	        for(int i = 0; i < tableAttributes.size(); i++) {
	        	fw.print(tabletypes.get(i) + " ");
	        	fw.print(tableAttributes.get(i));
	        	if(i < tableAttributes.size() - 1)
	        		fw.print(", ");
	        }
	        fw.print(") {\n");
	        for(int i = 0; i < tableAttributes.size(); i++) {
	        	fw.print("        this." + tableAttributes.get(i) + " = " + tableAttributes.get(i) + ";\n");
	        }
	        fw.print("    }\n");
	        fw.print("\n");
	        
	        // Member functions
	        
	        // End of class definition
	        fw.print("}\n");
	        fw.close();
	        
	        // Compile the file.
	        //System.out.print("Compilation.. ");
	        String fileToCompile = ".//src//" + cts.tableName + ".java";
	        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	        int compilationResult = compiler.run(null, null, null, fileToCompile);
	        
	        if (compilationResult != 0) {
	        	// Check for compilation
	            System.out.println("failed");
	            return false;
	        }
	        else {
	        	// Create a table
		        Table t = new Table(cts.tableName);
				DBUsing.addTable(cts.tableName, t);
				return true;
	        }
		}
	}

	// INSERT
	private boolean insertTuple(InsertStatement is) {
		if(!DBUsing.tables.containsKey(is.tableName)) {
			System.out.println("Error: Can't find table named " + is.tableName);
			return false;
		}
		else {
			Table t = DBUsing.getTable(is.tableName);
			ArrayList<String> input = is.tuple;
			
			try {
				File root = new File("src");
	        	URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
	        	//System.out.println("Created class loader with address: " + root.toURI().toURL().toString());
	        	Class<?> cl = Class.forName(t.getName(), true, classLoader);
	        	//System.out.println("Inserting a tuple to " + t.getName());
				Field fields[] = cl.getDeclaredFields();
				Class<?> parTypes[] = new Class[fields.length];
				for(int i = 0; i < fields.length; i++)
					parTypes[i] = fields[i].getType();
				
				if(input.size() != fields.length) {
					System.out.println("Error: " + is.tableName + " has " + fields.length + " attribute(s)." + input.size());
					return false;
				}
				
				Object argList[] = new Object[fields.length];
				Constructor<?> ct = cl.getConstructor(parTypes);
				
				for(int i = 0; i < fields.length; i++) {
					String typeName = parTypes[i].getName();
					if(typeName.equals("int"))
						argList[i] = new Integer(input.get(i));
					else if(typeName.equals("java.lang.String"))
						argList[i] = new String(input.get(i));
					else if(typeName.equals("double"))
						argList[i] = new Double(input.get(i));
				}

				t.addTuple((Tuple)ct.newInstance(argList));
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			catch (InstantiationException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			return true;
		}
	}
	
	// UPDATE
	// Supporting case: one "attr = val" and one target table.
	private boolean update(UpdateStatement us) {
		Table target = DBUsing.getTable(us.tableName);
		ArrayList<Tuple> tuples = (ArrayList<Tuple>) target.getTuples();
		Object updateValue = null;
		
		try {
			File root = new File("src");
        	URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        	Class<?> cl = Class.forName(target.getName(), true, classLoader);
			
			// Clarify update targets
			for(Field field : cl.getDeclaredFields()) {
				field.setAccessible(true);
				if(field.getType().getName().equals("int"))
					updateValue = new Integer(us.val);
				else if(field.getType().getName().equals("java.lang.String"))
					updateValue = new String(us.val);
				else if(field.getType().getName().equals("double"))
					updateValue = new Double(us.val);
				field.setAccessible(false);
			}
			
			//System.out.println(us.attr + " " + updateValue);
			
			// Filter and update
			if(us.where != null) {
				ConditionExpression cond = us.where;
				String attributeName = cond.left;
				String attributeValue = cond.right;
				
				for(Tuple tuple : tuples) {
					Field fields[] = tuple.getClass().getDeclaredFields();
					for(int i = 0; i <  fields.length; i++) {
						Field field = fields[i];
						field.setAccessible(true);
						//System.out.println("check field.getName(): " + field.getName() + ", attributeName: " + attributeName);
						if(field.getName().equals(attributeName)) {
							if(cond.operator.equals("=")) {
								boolean checkCondition = false;
								
								if(field.getType().getName().equals("int"))
									checkCondition = ((Object) new Integer(attributeValue)).equals(field.get(tuple));
								else if(field.getType().getName().equals("java.lang.String"))
									checkCondition = ((Object) new String(attributeValue)).equals(field.get(tuple));
								else if(field.getType().getName().equals("double"))
									checkCondition = ((Object) new Double(attributeValue)).equals(field.get(tuple));
								
								if(checkCondition) {
									Field toBeUpdated = tuple.getClass().getDeclaredField(us.attr);
									toBeUpdated.setAccessible(true);
									toBeUpdated.set(tuple, updateValue);
								}
							}
						}
						field.setAccessible(false);
					}
				}
			}
			else {
				for(Tuple tuple : tuples) {
					Field toBeUpdated = tuple.getClass().getDeclaredField(us.attr);
					toBeUpdated.setAccessible(true);
					toBeUpdated.set(tuple, updateValue);
					toBeUpdated.setAccessible(false);
				}
			}
			
			
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		System.out.println();
		
		return true;
	}
	
	// SELECT
	private boolean select(SelectStatement ss) {
		// Get tables from table names
		ArrayList<Table> tables = new ArrayList<Table>();
		for(String tableName : ss.relations)
			tables.add(DBUsing.getTable(tableName));
		
		// Get first intermediate result(which is either a table or an intermediate result from two or more joined tables)
		ArrayList<String> attributeNames = getJoinedAttributeNames(tables);
		ArrayList<ArrayList<Object>> intermediate = getJoinedIntermediate(tables);
		
		// Selection
		if(!ss.conditions.isEmpty())
			intermediate = getSelectedIntermediate(intermediate, attributeNames, ss.conditions);
		
		// Projection
		ArrayList<String> printedNames = getProjectedNames(intermediate, attributeNames, ss.attributes);
		intermediate = getProjectedIntermediate(intermediate, attributeNames, ss.attributes);
		
		// Print
		if(intermediate.isEmpty())
			System.out.println();
		else {
			StringBuilder sb = new StringBuilder();
			String newLine = System.getProperty("line.separator");
			for(String name : printedNames) {
				sb.append("\t" + name);
			}
			sb.append(newLine);
			for(ArrayList<Object> objectTuple : intermediate) {
				for(int i = 0; i <  objectTuple.size(); i++) {
					sb.append("\t" + objectTuple.get(i));
				}
				sb.append(newLine);
			}
			System.out.println(sb.toString());
		}
		
		return true;
	}
	
	/*
	 * Get attribute names for the joined table
	 */
	private ArrayList<String> getJoinedAttributeNames(List<Table> tables) {
		ArrayList<String> rtn = new ArrayList<String>(); 

		try {
			for(int iterator = 0; iterator < tables.size(); iterator++) {
				Table table = tables.get(iterator);
				File root = new File("src");
				URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
				Class<?> cl = Class.forName(table.getName(), true, classLoader);
				
				Field fields[] = cl.getDeclaredFields();
				for(int i = 0; i <  fields.length; i++) {
					Field field = fields[i];
					field.setAccessible(true);
					rtn.add(field.getName());
					field.setAccessible(false);
				}
			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (MalformedURLException e)	{
			e.printStackTrace();
		}
		
		return rtn;
	}
	
	/*
	 * Returns joined table or just a table
	 */
	private ArrayList<ArrayList<Object>> getJoinedIntermediate(List<Table> tables) {
		ArrayList<ArrayList<Object>> rtn = new ArrayList<ArrayList<Object>>(); 

		try {
			for(int iterator = 0; iterator < tables.size(); iterator++) {
				Table table = tables.get(iterator);
				if(rtn.isEmpty()) {					
					ArrayList<Tuple> tuples = (ArrayList<Tuple>) table.getTuples();
					for(Tuple tuple : tuples) {
						ArrayList<Object> objectTuple = new ArrayList<Object>();
						
						Field fields[] = tuple.getClass().getDeclaredFields();
						for(int i = 0; i <  fields.length; i++) {
							Field field = fields[i];
							field.setAccessible(true);
							objectTuple.add(field.get(tuple));
							field.setAccessible(false);
						}
						
						rtn.add(objectTuple);
					}
				}
				else {
					int originalTableSize = rtn.size();
					
					// Copy existing tuples for join
					for(int j = 0; j < tables.size() - 1; j++)
						for(int i = 0; i < originalTableSize; i++)
							rtn.add(rtn.get(i));
					
					// Join
					ArrayList<Tuple> tuples = (ArrayList<Tuple>) table.getTuples();
					int offset = 0;
					for(Tuple tuple : tuples) {
						for(int iter = offset * originalTableSize; iter < (offset + 1) * originalTableSize; iter++) {
							ArrayList<Object> objectTuple = rtn.get(iter);
							
							Field fields[] = tuple.getClass().getDeclaredFields();
							for(int i = 0; i <  fields.length; i++) {
								Field field = fields[i];
								field.setAccessible(true);
								objectTuple.add(field.get(tuple));
								field.setAccessible(false);
							}
							
							rtn.add(objectTuple);
						}
						offset++;
					}
				}
			}
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		/*catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (MalformedURLException e)	{
			e.printStackTrace();
		}*/
		
		return rtn;
	}

	/*
	 * Selection
	 */
	private ArrayList<ArrayList<Object>> getSelectedIntermediate(ArrayList<ArrayList<Object>> intermediate, ArrayList<String> attributeNames, ArrayList<ConditionExpression> conditions) {
		ArrayList<ArrayList<Object>> rtn = new ArrayList<ArrayList<Object>>();
		
		ArrayList<String> leftNames = new ArrayList<String>();
		ArrayList<String> condOperators = new ArrayList<String>();
		ArrayList<String> rightValues = new ArrayList<String>();
		for(ConditionExpression cond : conditions) {
			leftNames.add(cond.left);
			condOperators.add(cond.operator);
			rightValues.add(cond.right);
		}

		for(ArrayList<Object> objectTuple : intermediate) {
			for(int i = 0; i <  objectTuple.size(); i++) {
				int index = leftNames.indexOf(attributeNames.get(i));
				if(index >= 0) {
					if(condOperators.get(index).equals("=")) {
						boolean checkCondition = false;
						if(objectTuple.get(index).getClass().getName().equals("java.lang.Integer"))
							checkCondition = (Integer) objectTuple.get(index) == Integer.parseInt(rightValues.get(index));
						else if(objectTuple.get(index).getClass().getName().equals("java.lang.String"))
							checkCondition = (Double) objectTuple.get(index) == Double.parseDouble(rightValues.get(index));
						else if(objectTuple.get(index).getClass().getName().equals("java.lang.Double"))
							checkCondition = (Double) objectTuple.get(index) == Double.parseDouble(rightValues.get(index));
						if(checkCondition)
							rtn.add(objectTuple);
					}
					else if(condOperators.get(index).equals("<")) {
						boolean checkCondition = false;
						if(objectTuple.get(index).getClass().getName().equals("java.lang.Integer"))
							checkCondition = (Integer) objectTuple.get(index) < Integer.parseInt(rightValues.get(index));
						else if(objectTuple.get(index).getClass().getName().equals("java.lang.Double"))
							checkCondition = (Double) objectTuple.get(index) < Double.parseDouble(rightValues.get(index));
						if(checkCondition)
							rtn.add(objectTuple);
					}
					else if(condOperators.get(index).equals(">")) {
						boolean checkCondition = false;
						if(objectTuple.get(index).getClass().getName().equals("java.lang.Integer"))
							checkCondition = (Integer) objectTuple.get(index) > Integer.parseInt(rightValues.get(index));
						else if(objectTuple.get(index).getClass().getName().equals("java.lang.Double"))
							checkCondition = (Double) objectTuple.get(index) > Double.parseDouble(rightValues.get(index));
						if(checkCondition)
							rtn.add(objectTuple);
					}
				}
			}
		}
		
		return rtn;
	}
	
	private ArrayList<String> getProjectedNames(ArrayList<ArrayList<Object>> intermediate, ArrayList<String> attributeNames, ArrayList<String> projectionNames) {
		if(projectionNames.get(0).equals("*"))
			return attributeNames;
		else {
			ArrayList<String> rtn = new ArrayList<String>();
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			
			for(String proj : projectionNames)
				if(attributeNames.contains(proj))
					indexes.add(attributeNames.indexOf(proj));
			
			for(int i = 0; i <  indexes.size(); i++) {
				rtn.add(attributeNames.get(indexes.get(i)));
			}
			
			return rtn;
		}
	}
	
	private ArrayList<ArrayList<Object>> getProjectedIntermediate(ArrayList<ArrayList<Object>> intermediate, ArrayList<String> attributeNames, ArrayList<String> projectionNames) {
		if(projectionNames.get(0).equals("*"))
			return intermediate;
		else {
			ArrayList<ArrayList<Object>> rtn = new ArrayList<ArrayList<Object>>();
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			
			for(String proj : projectionNames)
				if(attributeNames.contains(proj))
					indexes.add(attributeNames.indexOf(proj));
			
			for(ArrayList<Object> objectTuple : intermediate) {
				ArrayList<Object> newObjectTuple = new ArrayList<Object>();
				for(int i = 0; i <  indexes.size(); i++) {
					newObjectTuple.add(objectTuple.get(indexes.get(i)));
				}
				rtn.add(newObjectTuple);
			}
			
			return rtn;
		}
	}
}
