/**
 * This class is used as a test
 * @author sylvain
 */

// This is the package declaration
package test;

         import test.TestJava1;
import test.*;

// This is a comment

/**
 * Javadoc for class definition 
 * tag1
 * tag2
 * @author sylvain
 */
public class TestJava2 {

	/**
	 * This is an integer
	 */
	private int anInteger;
	
	// An other comment here
	
	protected float aFloat;
	
	/**
	 * Some javadoc for this String
	 */
	public static final String aString;
	
	/**
	 * Javadoc for method1
	 */
	  public static void method1() {
		return;
	}
	
	private Vector newStringVector;  
	  
	static {
		aString = "essai";
	}
	
	/*
	 * Non-Javadoc for method2
	 */
			private synchronized TestJava1 method2(String myString, boolean aBoolean) 
	// comment here
	{
		    // This is a comment
		
		/* dsfdsfsdfds
		 * dfsfdssfdfds
		 */
		// comment again
		
		aString = myString;
		return null;
	}
			
	// This will be removed
	protected int i;
			

	
}
