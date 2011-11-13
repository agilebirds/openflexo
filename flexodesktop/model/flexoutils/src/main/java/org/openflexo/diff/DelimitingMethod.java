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
package org.openflexo.diff;

public interface DelimitingMethod {
	public String getDelimiters();

	public String getNonSignifiantDelimiters();

	public String getName();

	public static final String DEFAULT_NON_SIGNIFIANT_DELIMS = "\t\n\r\f ";

	public static final DelimitingMethod LINES = new DelimitingMethod() {
		private static final String LINES_DELIMS = "\r\n";

		@Override
		public String getDelimiters() {
			return LINES_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "LINES";
		}
	};

	public static final DelimitingMethod DEFAULT = new DelimitingMethod() {
		private static final String DEFAULT_DELIMS = "\t\n\r\f ";

		@Override
		public String getDelimiters() {
			return DEFAULT_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "DEFAULT";
		}
	};

	public static final DelimitingMethod XML = new DelimitingMethod() {
		private static final String XML_DELIMS = "\t\n\r\f=<>?/ " + '"';

		@Override
		public String getDelimiters() {
			return XML_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "XML";
		}
	};

	public static final DelimitingMethod HTML = new DelimitingMethod() {
		private static final String HTML_DELIMS = "\t\n\r\f=<>?/ " + '"';

		@Override
		public String getDelimiters() {
			return HTML_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "HTML";
		}
	};

	public static final DelimitingMethod PLIST = new DelimitingMethod() {
		private static final String PLIST_DELIMS = "\t\n\r\f:={} " + '"';

		@Override
		public String getDelimiters() {
			return PLIST_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "PLIST";
		}
	};

	public static final DelimitingMethod JAVA = new DelimitingMethod() {
		private static final String JAVA_DELIMS = "\t\n\r\f=.,;?<>()[]{}+-/!&*| " + '"';

		@Override
		public String getDelimiters() {
			return JAVA_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "JAVA";
		}
	};

	public static final DelimitingMethod TEX = new DelimitingMethod() {
		private static final String JAVA_DELIMS = "\t\n\r\f=\\{}:[]%& " + '"';

		@Override
		public String getDelimiters() {
			return JAVA_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "TEX";
		}
	};

	public static final DelimitingMethod SQL = new DelimitingMethod() {
		private static final String JAVA_DELIMS = "\t\n\r\f=(); ";

		@Override
		public String getDelimiters() {
			return JAVA_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "SQL";
		}
	};

}