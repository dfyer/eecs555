/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

import java.util.ArrayList;
import java.util.List;

public class Table {
	private String name;
	private Tuple sample;
	private ArrayList<Tuple> tuples = new ArrayList<Tuple>();
	
	public Table() {
		this.name = null;
	}
	
	public Table(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addTuple(Tuple t) {
		tuples.add(t);
	}
	
	public List<Tuple> getTuples() {
		return tuples;
	}
	
	public void printAll() {
		System.out.println(tuples.size());
		for(int i = 0; i < tuples.size(); i++)
			System.out.println(tuples.get(i).toString());
	}
}
