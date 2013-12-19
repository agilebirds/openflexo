package org.openflexo.foundation.viewpoint;

import org.junit.Test;

/**
 * This unit test is intented to test ViewPoint creation facilities
 * 
 * @author sylvain
 * 
 */
public class TestCreateViewPoint extends ViewPointTestCase {

	@Test
	public void testCreateViewPoint() {
		instanciateTestServiceManager();
		System.out.println("ResourceCenter= " + resourceCenter);
		ViewPoint newViewPoint = ViewPoint.newViewPoint("TestViewPoint", "http://openflexo.org/test/TestViewPoint",
				resourceCenter.getDirectory(), serviceManager.getViewPointLibrary());
	}

}
