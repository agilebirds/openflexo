/**
 * 
 */
package org.openflexo.toolbox;

import junit.framework.TestCase;

/**
 * @author Nicolas Daniels
 * 
 */
public class TestStringUtils extends TestCase {

	public void testCamelCase() {
		assertEquals("TestIt", StringUtils.camelCase("test it", true));
		assertEquals("testIt", StringUtils.camelCase("test it", false));
		assertEquals("Test_It", StringUtils.camelCase("test_it", true));
		assertEquals("test_It", StringUtils.camelCase("test_it", false));
		assertEquals("TestItPlease", StringUtils.camelCase("test it please", true));
		assertEquals("testItPlease", StringUtils.camelCase("test it please", false));
		assertEquals("TeSt_It_PleAse", StringUtils.camelCase("teSt_it_pleAse", true));
		assertEquals("test_It_Please", StringUtils.camelCase("test_it_please", false));
		assertEquals("TestItPlease", StringUtils.camelCase("test	it    	please", true));
		assertEquals("_Test_It_Please", StringUtils.camelCase("_test_it_please", true));
		assertEquals("_Test_It_Please", StringUtils.camelCase("_test_it_please", false));
		assertEquals("TEST_ItPlease", StringUtils.camelCase("   tEST_it please  	", true));
		assertEquals("tEST_ItPlease", StringUtils.camelCase("   tEST_it please  	", false));
		assertEquals("TESTItPlease", StringUtils.camelCase("TEST it please  	", true));
		assertEquals("testItPlease", StringUtils.camelCase("TEST it please  	", false));
		assertEquals("testItPlease52", StringUtils.camelCase("TEST it please52", false));
	}
}
