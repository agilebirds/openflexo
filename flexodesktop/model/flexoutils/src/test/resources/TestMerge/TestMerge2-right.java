package org.openflexo.rtpg;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import jparse.Method;
import jparse.Type;
import junit.framework.TestCase;
import junit.framework.TestCase2;

/**
 * Some javadoc for this TestMerge class
 * This is the right version
 * 
 * @author sylvain
 */
public class TestMerge extends TestCase {

	private static final Logger logger = Logger.getLogger(TestRTPG1.class.getPackage().getName());

	private static JavaParser javaParser = new JavaParser();
	
	public void test1()
	{
		JavaClass parsedClass = test(new FileResource("TestRTPG1/TestJava1.java"));
		assertEquals(parsedClass.getMethods().length,12);
		assertEquals(parsedClass.getDeclaredMethods().length,0);
		assertEquals(parsedClass.getDeclaredFields().length,0);
		assertEquals(parsedClass.getDeclaredConstructor().length,0);
		assertEquals(parsedClass.getDeclaredInnerClasses().length,0);
		assertNotNull(parsedClass.getJavadoc());
	}

	/**
	 * Added some Javadoc here
	 * @throws ClassNotFoundException
	 */
	public void test2() throws ClassNotFoundException
	{
		JavaClass parsedClass = test(new FileResource("TestRTPG1/TestJava2.java"));
		logger.fine("parsedClass="+parsedClass+" "+parsedClass.getName()+"\n");
		logger.fine("parsedClass="+parsedClass+" "+parsedClass.getJavadoc()+"\n");
		assertNotNull(parsedClass.getJavadoc());
		assertEquals(parsedClass.getDeclaredMethods().length,2);
		JavaMethod method1 = parsedClass.getMethod("method1", null, null);
		assertNotNull(method1);
		assertTrue(method1.isStatic());
		assertNotNull(method1.getJavadoc());
		JavaClass STRING = parsedClass.getJavaClass(String.class);
		JavaClass BOOL = parsedClass.getJavaClass(Boolean.TYPE);
		JavaClass[] params = { STRING, BOOL };
		JavaMethod method2 = parsedClass.getMethod("method2", params, null);
		assertNotNull(method2);
		assertTrue(method2.isPrivate());
		assertTrue(method2.isSynchronized());
		assertFalse(method2.isPublic());
		assertFalse(method2.isProtected());
		assertTrue(method2.isPrivate());
		assertFalse(method2.isAccessible(STRING));
		logger.fine("Method2 javadoc "+method2.getJavadoc());
		assertNull(method2.getJavadoc());
		assertEquals(parsedClass.getDeclaredFields().length,4);
		JavaField field1 = parsedClass.getField("anInteger");
		assertNotNull(field1);
		assertNotNull(field1.getJavadoc());
		JavaField field2 = parsedClass.getField("aString");
		logger.fine("field2 javadoc "+field2.getJavadoc());
		assertNotNull(field2);
		assertTrue(field2.isPublic());
		assertTrue(field2.isStatic());
		assertTrue(field2.isFinal());
		assertNotNull(field2.getJavadoc());
		JavaField field3 = parsedClass.getField("aFloat");
		assertNotNull(field3);
		assertNull(field3.getJavadoc());
		assertEquals(parsedClass.getDeclaredConstructor().length,0);
		assertEquals(parsedClass.getDeclaredInnerClasses().length,0);
	}
	
	public void test3() throws ClassNotFoundException
	{
		// Add something there
		// On an other line
		JavaClass parsedClass = test(new FileResource("TestRTPG1/TestJava3.java"));
		assertEquals(parsedClass.getDeclaredMethods().length,2);
		assertEquals(parsedClass.getDeclaredFields().length,5);
		assertEquals(parsedClass.getDeclaredConstructor().length,1);
		assertEquals(parsedClass.getDeclaredInnerClasses().length,2);
		JavaClass innerClass = parsedClass.getInner("AnInnerClass");
		assertNotNull(innerClass);
		assertTrue(innerClass.isPublic());
		assertTrue(innerClass.isStatic());
		logger.fine("innerClass javadoc "+innerClass.getJavadoc());
		assertNotNull(innerClass.getJavadoc());
		JavaClass TESTJAVA1 = parsedClass.getJavaClass("test.TestJava1");
		JavaClass[] params = { TESTJAVA1 };
		JavaMethod method1 = innerClass.getMethod("aMethod", params, null);
		assertNotNull(method1);
		assertNotNull(method1.getJavadoc());
		JavaClass innerInterface = parsedClass.getInner("AnInterface");
		assertNotNull(innerInterface);
		assertTrue(innerInterface.isInterface());
		logger.fine("innerInterface javadoc "+innerInterface.getJavadoc());
		assertNotNull(innerInterface.getJavadoc());
		/**
		 * This javadoc is a a wrong place
		 */
	}
	
	private JavaClass test(File aFile)
	{
		String originalContent = null;
		String prettyPrintedContent;
		String formattedContent;
		JavaClass type;
		
		assertTrue(aFile.exists());
		try {
			originalContent = FileUtils.fileContents(aFile);
		} catch (IOException e) {
			fail();
		}
		
		JavaFile testJava = javaParser.parseFile(aFile);
		
		if (!testJava.isSuccessfullyParsed()) {
			logger.warning("Could not parse: "+testJava.getJavaParseException());
			fail();
		}
		assertEquals(testJava.getTopLevelTypes().size(),1);
		assertNotNull(type = testJava.getRootClass());
		logger.info("Successfully parsed: "+type.getName());
		
		prettyPrintedContent = testJava.getOriginalRepresentation();
		logger.fine("prettyPrintedContent:\n"+prettyPrintedContent);
		assertEquals(originalContent, prettyPrintedContent);	
		
		formattedContent = testJava.getFormattedRepresentation();		
		logger.fine("formattedContent:\n"+formattedContent);
		
		//JavaClass rootClass = testJava.getRootClass();
		//logger.fine("prettyPrintedClass:\n"+rootClass.getOriginalRepresentation());
		//logger.fine("formattedClass:\n"+rootClass.getFormattedRepresentation());
		//logger.fine("javadoc:\n"+rootClass.getJavadoc());
		
		// comment right
		// comment


		/*for (Method method : type.getMethods()) { /* here */
			logger.info(">>>>>> "+method);
			logger.info("PrettyPrintedContent:"+testJava.getOriginalRepresentation(method)); /*there*/
			/*logger.info("FormattedContent:"+testJava.getFormattedRepresentation(method));
		}*/
		
		return type;
	}
	
	public interface AnInterface {
		
		public void method();
		public void anOtherMethod();
	}

}
