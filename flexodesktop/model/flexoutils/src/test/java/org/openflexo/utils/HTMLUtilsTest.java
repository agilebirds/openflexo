/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.utils;

import junit.framework.TestCase;

import org.openflexo.toolbox.HTMLUtils;

public class HTMLUtilsTest extends TestCase {

	public void testEmptyParagraph() {
		assertTrue(HTMLUtils.isEmtpyParagraph(" <p>\r\n" + "\r\n" + "</p>\r\n" + "\r\n" + ""));
		assertFalse(HTMLUtils.isEmtpyParagraph(" <p>a\r\n" + "\r\n" + "</p>\r\n" + "\r\n" + ""));
	}

	public void testExtractSourceFromEmbedded() {
		String src = "http://www.youtube.com/v/NmmELsWBscM&hl=en&fs=1";
		String html = "<object width=\"425\" height=\"344\"><param name=\"movie\" value=\""
				+ src
				+ "\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\""
				+ src
				+ "\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"425\" height=\"344\"></embed></object>";
		assertEquals(src, HTMLUtils.extractSourceFromEmbeddedTag(html));
	}

}
