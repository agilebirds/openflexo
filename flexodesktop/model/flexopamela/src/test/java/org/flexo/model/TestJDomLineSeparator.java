package org.flexo.model;

import junit.framework.Assert;

import org.jdom2.JDOMConstants;
import org.jdom2.output.LineSeparator;
import org.junit.Test;

public class TestJDomLineSeparator {

	@Test
	public void testLineSeparatorFromSystemProperty() {
		System.setProperty(JDOMConstants.JDOM2_PROPERTY_LINE_SEPARATOR, "SYSTEM");
		Assert.assertEquals(System.getProperty("line.separator"), LineSeparator.DEFAULT.value());
	}
}
