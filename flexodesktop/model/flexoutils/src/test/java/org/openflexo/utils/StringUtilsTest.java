package org.openflexo.utils;

import junit.framework.TestCase;

import org.openflexo.toolbox.StringUtils;

public class StringUtilsTest extends TestCase {

	public void testReplaceNonMatchingPatterns() {
		String string1 = "Coucou";
		String string2 = "éèîï";
		String regexp = "\\p{ASCII}+";
		String replacement = "zorglub";
		assertEquals(string1, StringUtils.replaceNonMatchingPatterns(string1, regexp, replacement));
		assertEquals(replacement, StringUtils.replaceNonMatchingPatterns(string2, regexp, replacement));
		assertEquals(string1 + replacement, StringUtils.replaceNonMatchingPatterns(string1 + string2, regexp, replacement));
		replacement = "-";
		regexp = "\\p{ASCII}";
		assertEquals(string1, StringUtils.replaceNonMatchingPatterns(string1, regexp, replacement, true));
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string2.length(); i++) {
			sb.append(replacement);
		}
		assertEquals(sb.toString(), StringUtils.replaceNonMatchingPatterns(string2, regexp, replacement, true));
		assertEquals(string1 + sb.toString(), StringUtils.replaceNonMatchingPatterns(string1 + string2, regexp, replacement, true));

	}
}
