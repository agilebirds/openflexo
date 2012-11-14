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
package org.netbeans.lib.cvsclient.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Thomas Singer
 */
public class SimpleStringPattern implements StringPattern {

	private static final char MATCH_EACH = '*';
	private static final char MATCH_ONE = '?';

	private final List subPatterns = new LinkedList();

	/**
	 * Creates a SimpleStringPattern for the specified definition. The definition might contain two special characters ('*' and '?').
	 */
	public SimpleStringPattern(String definition) {
		splitInSubPattern(definition);
	}

	/**
	 * Returns whether the specified string matches thiz pattern.
	 */
	@Override
	public boolean doesMatch(String string) {
		int index = 0;
		SubPattern subPattern = null;
		for (Iterator it = subPatterns.iterator(); it.hasNext();) {
			subPattern = (SubPattern) it.next();
			index = subPattern.doesMatch(string, index);
			if (index < 0) {
				return false;
			}
		}

		if (index == string.length()) {
			return true;
		}
		if (subPattern == null) {
			return false;
		}
		return subPattern.checkEnding(string, index);
	}

	private void splitInSubPattern(String definition) {
		char prevSubPattern = ' ';

		int prevIndex = 0;
		for (int index = 0; index >= 0;) {
			prevIndex = index;

			index = definition.indexOf(MATCH_EACH, prevIndex);
			if (index >= 0) {
				String match = definition.substring(prevIndex, index);
				addSubPattern(match, prevSubPattern);
				prevSubPattern = MATCH_EACH;
				index++;
				continue;
			}
			index = definition.indexOf(MATCH_ONE, prevIndex);
			if (index >= 0) {
				String match = definition.substring(prevIndex, index);
				addSubPattern(match, prevSubPattern);
				prevSubPattern = MATCH_ONE;
				index++;
				continue;
			}
		}
		String match = definition.substring(prevIndex);
		addSubPattern(match, prevSubPattern);
	}

	private void addSubPattern(String match, char subPatternMode) {
		SubPattern subPattern = null;
		switch (subPatternMode) {
		case MATCH_EACH:
			subPattern = new MatchEachCharPattern(match);
			break;
		case MATCH_ONE:
			subPattern = new MatchOneCharPattern(match);
			break;
		default:
			subPattern = new MatchExactSubPattern(match);
			break;
		}

		subPatterns.add(subPattern);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Iterator it = subPatterns.iterator(); it.hasNext();) {
			SubPattern subPattern = (SubPattern) it.next();
			buffer.append(subPattern.toString());
		}
		return buffer.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleStringPattern)) {
			return false;
		}
		return subPatterns.equals(((SimpleStringPattern) obj).subPatterns);
	}

	@Override
	public int hashCode() {
		return -subPatterns.hashCode();
	}

	public static void main(String[] arguments) {
		StringPattern sp = new SimpleStringPattern("a*b"); // NOI18N

		test(sp, "ab", true); // NOI18N
		test(sp, "aab", true); // NOI18N
		test(sp, "ba", false); // NOI18N
		test(sp, "abc", false); // NOI18N

		sp = new SimpleStringPattern("*.txt"); // NOI18N

		test(sp, "datei.txt", true); // NOI18N
		test(sp, ".txt", true); // NOI18N
		test(sp, "datei.tx", false); // NOI18N
		test(sp, "datei.txt.txt", true); // NOI18N

		sp = new SimpleStringPattern("datei*1*"); // NOI18N

		test(sp, "datei0.txt", false); // NOI18N
		test(sp, "datei1.txt", true); // NOI18N
		test(sp, "datei.tx", false); // NOI18N
		test(sp, "datei1.txt.txt", true); // NOI18N

		sp = new SimpleStringPattern("Makefile"); // NOI18N

		test(sp, "Makefile", true); // NOI18N
		test(sp, "Makefile.mak", false); // NOI18N
		test(sp, "Makefile1", false); // NOI18N
		test(sp, ".Makefile", false); // NOI18N
		test(sp, ".Makefile.", false); // NOI18N

		sp = new SimpleStringPattern("*~"); // NOI18N

		test(sp, "datei~", true); // NOI18N
		test(sp, "datei~1", false); // NOI18N
		test(sp, "datei~1~", true); // NOI18N

		// Equality:
		SimpleStringPattern pattern1 = new SimpleStringPattern("*.class");
		SimpleStringPattern pattern2 = new SimpleStringPattern("*.class");
		System.err.println(pattern1 + ".equals(" + pattern2 + ") = " + pattern1.equals(pattern2));

		pattern1 = new SimpleStringPattern("?.class");
		pattern2 = new SimpleStringPattern("*.class");
		System.err.println(pattern1 + ".equals(" + pattern2 + ") = " + pattern1.equals(pattern2));

		pattern1 = new SimpleStringPattern("*.clazz");
		pattern2 = new SimpleStringPattern("*.class");
		System.err.println(pattern1 + ".equals(" + pattern2 + ") = " + pattern1.equals(pattern2));
	}

	private static void test(StringPattern sp, String testString, boolean shouldResult) {
		System.err.print('"' + sp.toString() + '"' + ": " + testString + " " + shouldResult); // NOI18N

		boolean doesMatch = sp.doesMatch(testString);

		if (doesMatch == shouldResult) {
			System.err.println(" proved"); // NOI18N
		} else {
			System.err.println(" **denied**"); // NOI18N
		}
	}

	private static abstract class SubPattern {
		protected final String match;

		protected SubPattern(String match) {
			this.match = match;
		}

		/**
		 * @parameter string ... the whole string to test for matching
		 * @parameter index ... the index in string where this' test should begin
		 * @returns ... if successful the next test-position, if not -1
		 */
		public abstract int doesMatch(String string, int index);

		public boolean checkEnding(String string, int index) {
			return false;
		}

		@Override
		public boolean equals(Object obj) {
			if (!this.getClass().isInstance(obj)) {
				return false;
			}
			return match.equals(((SubPattern) obj).match);
		}

		@Override
		public int hashCode() {
			return -match.hashCode();
		}
	}

	private static class MatchExactSubPattern extends SubPattern {
		public MatchExactSubPattern(String match) {
			super(match);
		}

		@Override
		public int doesMatch(String string, int index) {
			if (!string.startsWith(match, index)) {
				return -1;
			}
			return index + match.length();
		}

		@Override
		public String toString() {
			return match;
		}
	}

	private static class MatchEachCharPattern extends SubPattern {
		public MatchEachCharPattern(String match) {
			super(match);
		}

		@Override
		public int doesMatch(String string, int index) {
			int matchIndex = string.indexOf(match, index);
			if (matchIndex < 0) {
				return -1;
			}
			return matchIndex + match.length();
		}

		@Override
		public boolean checkEnding(String string, int index) {
			return string.endsWith(match);
		}

		@Override
		public String toString() {
			return MATCH_EACH + match;
		}
	}

	private static class MatchOneCharPattern extends MatchExactSubPattern {
		public MatchOneCharPattern(String match) {
			super(match);
		}

		@Override
		public int doesMatch(String string, int index) {
			index++;
			if (string.length() < index) {
				return -1;
			}
			return super.doesMatch(string, index);
		}

		@Override
		public String toString() {
			return MATCH_ONE + match;
		}
	}
}
