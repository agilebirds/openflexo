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
package org.openflexo.fps;

import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class CVSRevisionIdentifier extends FlexoObject implements StringConvertable, Cloneable {
	protected static final Logger logger = Logger.getLogger(CVSRevisionIdentifier.class.getPackage().getName());

	private static Converter<CVSRevisionIdentifier> converter = StringEncoder.addConverter(new Converter<CVSRevisionIdentifier>(
			CVSRevisionIdentifier.class) {

		@Override
		public CVSRevisionIdentifier convertFromString(String value) {
			try {
				return new CVSRevisionIdentifier(value);
			} catch (InvalidVersionFormatException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public String convertToString(CVSRevisionIdentifier value) {
			return value.toString();
		}

	});

	public int major = 0;

	public int minor = 0;

	public int patch = 0;

	public int minorPatch = 0;

	public class InvalidVersionFormatException extends Exception {

	}

	public CVSRevisionIdentifier(String versionAsString) throws InvalidVersionFormatException {
		super();
		if (versionAsString == null) {
			// Means the last one
			major = Integer.MAX_VALUE;
		} else {
			StringTokenizer st = new StringTokenizer(versionAsString, ".");
			if (st.hasMoreTokens()) {
				String unparsed = st.nextToken();
				try {
					major = Integer.valueOf(unparsed).intValue();
				} catch (NumberFormatException e) {
					logger.warning("Cannot parse " + unparsed);
					throw new InvalidVersionFormatException();
				}
			}
			if (st.hasMoreTokens()) {
				String unparsed = st.nextToken();
				try {
					minor = Integer.valueOf(unparsed).intValue();
				} catch (NumberFormatException e) {
					logger.warning("Cannot parse " + unparsed);
					throw new InvalidVersionFormatException();
				}
			}
			if (st.hasMoreTokens()) {
				String unparsed = st.nextToken();
				try {
					patch = Integer.valueOf(unparsed).intValue();
				} catch (NumberFormatException e) {
					logger.warning("Cannot parse " + unparsed);
					throw new InvalidVersionFormatException();
				}
			}
			if (st.hasMoreTokens()) {
				String unparsed = st.nextToken();
				try {
					minorPatch = Integer.valueOf(unparsed).intValue();
				} catch (NumberFormatException e) {
					logger.warning("Cannot parse " + unparsed);
					throw new InvalidVersionFormatException();
				}
			}
		}
	}

	public static CVSRevisionIdentifier DEFAULT_VERSION_ID() {
		try {
			return new CVSRevisionIdentifier("1.0");
		} catch (InvalidVersionFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isLast() {
		return major == Integer.MAX_VALUE;
	}

	@Override
	public String toString() {
		return versionAsString();
	}

	public String versionAsString() {
		return "" + major + "." + minor + (patch > 0 ? "." + patch + (minorPatch > 0 ? "." + minorPatch : "") : "");
	}

	// Take care here that kind (type) of version is ignored when testing equality
	// (used in Hashtable to retrieve versions)
	@Override
	public int hashCode() {
		return versionAsString().hashCode();
	}

	// (used in Hashtable to retrieve versions)
	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof CVSRevisionIdentifier) {
			return versionAsString().equals(((CVSRevisionIdentifier) anObject).versionAsString());
		} else {
			return super.equals(anObject);
		}
	}

	@Override
	public CVSRevisionIdentifier clone() {
		try {
			return new CVSRevisionIdentifier(toString());
		} catch (InvalidVersionFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isLesserThan(CVSRevisionIdentifier version) {
		return COMPARATOR.compare(this, version) < 0;
	}

	public boolean isGreaterThan(CVSRevisionIdentifier version) {
		return COMPARATOR.compare(this, version) > 0;
	}

	@Override
	public StringEncoder.Converter<CVSRevisionIdentifier> getConverter() {
		return converter;
	}

	public static final VersionComparator COMPARATOR = new VersionComparator();

	public static class VersionComparator implements Comparator<CVSRevisionIdentifier> {

		private VersionComparator() {
			super();
		}

		@Override
		public int compare(CVSRevisionIdentifier v1, CVSRevisionIdentifier v2) {
			if (v1.major < v2.major) {
				return -1;
			} else if (v1.major > v2.major) {
				return 1;
			} else {
				if (v1.minor < v2.minor) {
					return -1;
				} else if (v1.minor > v2.minor) {
					return 1;
				} else {
					if (v1.patch < v2.patch) {
						return -1;
					} else if (v1.patch > v2.patch) {
						return 1;
					} else {
						if (v1.minorPatch < v2.minorPatch) {
							return -1;
						} else if (v1.minorPatch > v2.minorPatch) {
							return 1;
						} else {
							// equals object !!!
							return 0;
						}
					}
				}
			}
		}

	}

	public CVSRevisionIdentifier newVersionByIncrementingMajor() {
		CVSRevisionIdentifier returned = clone();
		returned.major++;
		returned.minor = 0;
		returned.patch = 0;
		return returned;
	}

	public CVSRevisionIdentifier newVersionByIncrementingMinor() {
		CVSRevisionIdentifier returned = clone();
		returned.minor++;
		returned.patch = 0;
		return returned;
	}
}
