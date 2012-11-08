/* Hangyu Park 2009-11722 fjii789@gmail.com/gominidive@hotmail.com */

import java.util.StringTokenizer;

public class ConditionExpression {
	
	protected String left;
	protected String operator;
	protected String right;
	
	public ConditionExpression (){
		this.left = null;
		this.operator = null;
		this.right = null;
	}
	
	public ConditionExpression (String left, String operator, String right){
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	
	public ConditionExpression (String condition){
		/* It only can be used with "=" in the expression */
		StringTokenizer st = new StringTokenizer(condition, "=");
		
		this.left = st.nextToken();
		this.operator = "=";
		this.right = st.nextToken();
	}
}
