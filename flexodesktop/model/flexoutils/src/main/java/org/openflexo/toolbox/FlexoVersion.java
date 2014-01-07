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
package org.openflexo.toolbox;

import java.util.Comparator;
import java.util.StringTokenizer;

public class FlexoVersion {

	public FlexoVersion copy() {
		return new FlexoVersion(major, minor, patch, rc, isAlpha, isBeta);
	}

	public static boolean isValidVersionString(String version) {
		StringTokenizer st = new StringTokenizer(version, ".");
		if (st.hasMoreTokens()) {
			String next = st.nextToken();
			try {
				Integer.valueOf(next);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		if (st.hasMoreTokens()) {
			String next = st.nextToken();
			try {
				Integer.valueOf(next);
			} catch (NumberFormatException e) {
				try {
					new FlexoVersion(version).parseAlphaBetaRC(version, next);
				} catch (NumberFormatException e2) {
					return false;
				}
			}
		}
		if (st.hasMoreTokens()) {
			String next = st.nextToken();
			try {
				new FlexoVersion(version).parseAlphaBetaRC(version, next);
			} catch (NumberFormatException e2) {
				return false;
			}
		}
		return true;
	}

	public int major = 0;

	public int minor = 0;

	public int patch = 0;

	public int rc = -1;

	public boolean isAlpha = false;

	public boolean isBeta = false;

	public static FlexoVersion versionByIncrementing(FlexoVersion v, int majorInc, int minorInc, int patchInc) {
		return new FlexoVersion(v.major + majorInc, v.minor + minorInc, v.patch + patchInc, 0, false, false);
	}

	public FlexoVersion(int major, int minor, int patch, int rc, boolean isAlpha, boolean isBeta) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.rc = rc;
		this.isAlpha = isAlpha;
		this.isBeta = isBeta;
	}

	public FlexoVersion(String versionAsString) {
		super();
		StringTokenizer st = new StringTokenizer(versionAsString, ".");
		if (st.hasMoreTokens()) {
			String next = st.nextToken();
			try {
				major = Integer.valueOf(next).intValue();
			} catch (NumberFormatException e) {
				System.err.println("Invalid major number: " + next + " is not valid (" + versionAsString + ")");
				major = 0;
			}
		}
		if (st.hasMoreTokens()) {
			String next = st.nextToken();
			try {
				minor = Integer.valueOf(next).intValue();
			} catch (NumberFormatException e) {
				try {
					minor = parseAlphaBetaRC(versionAsString, next);
				} catch (NumberFormatException e2) {
					minor = 0;
					new NumberFormatException("Invalid minor number: " + next + " is not valid (" + versionAsString + ")")
							.printStackTrace();
				}
			}
		}
		if (st.hasMoreTokens()) {
			String next = st.nextToken();
			try {
				patch = parseAlphaBetaRC(versionAsString, next);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	private int parseAlphaBetaRC(String versionAsString, String token) throws NumberFormatException {
		int returned = 0;
		if (token.toLowerCase().indexOf("rc") > -1) {
			try {
				returned = Integer.parseInt(token.substring(0, token.toLowerCase().indexOf("rc")));
			} catch (NumberFormatException e) {
				throw new NumberFormatException("Invalid patch number: " + token.substring(0, token.toLowerCase().indexOf("rc"))
						+ " is not valid (" + versionAsString + ")");
			}
			try {
				rc = Integer.parseInt(token.substring(token.toLowerCase().indexOf("rc") + 2));
			} catch (NumberFormatException e) {
				rc = -1;
				throw new NumberFormatException("Invalid RC number: " + token.substring(token.toLowerCase().indexOf("rc") + 2)
						+ " is not valid (" + versionAsString + ")");
			}
		} else {
			if (token.toLowerCase().indexOf("alpha") > -1) {
				isAlpha = true;
				try {
					returned = Integer.parseInt(token.substring(0, token.toLowerCase().indexOf("alpha")));
				} catch (NumberFormatException e) {
					throw new NumberFormatException("Invalid patch number: " + token.substring(0, token.toLowerCase().indexOf("alpha"))
							+ " is not valid (" + versionAsString + ")");
				}
			} else if (token.toLowerCase().indexOf("beta") > -1) {
				isBeta = true;
				try {
					returned = Integer.parseInt(token.substring(0, token.toLowerCase().indexOf("beta")));
				} catch (NumberFormatException e) {
					throw new NumberFormatException("Invalid patch number: " + token.substring(0, token.toLowerCase().indexOf("beta"))
							+ " is not valid (" + versionAsString + ")");
				}
			} else {
				returned = Integer.valueOf(token).intValue();
				rc = -1;
			}
		}
		return returned;
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean forceDisplayPatch) {
		String v = String.valueOf(major) + "." + String.valueOf(minor) + (patch > 0 || forceDisplayPatch ? "." + patch : "");
		if (rc > -1) {
			return v + "RC" + String.valueOf(rc);
		}
		if (isAlpha) {
			return v + "alpha";
		}
		if (isBeta) {
			return v + "beta";
		}
		return v;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof FlexoVersion) {
			return toString().equals(anObject.toString());
		} else if (anObject instanceof String) {
			return toString().equals(new FlexoVersion((String) anObject).toString());
		} else {
			return super.equals(anObject);
		}
	}

	public boolean isLesserThan(FlexoVersion version) {
		return comparator.compare(this, version) < 0;
	}

	public boolean isGreaterThan(FlexoVersion version) {
		return comparator.compare(this, version) > 0;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public static final VersionComparator comparator = new VersionComparator();

	/**
	 * Version have the following format MAJOR.MINOR.PATCH(RCX|alpha|beta) members in parenthesis are optional and are called additional
	 * members Priority of members is this (bigger the greater, of course): 1) MAJOR 2) MINOR 3) PATCH 4) NO additional member 5) beta 6)
	 * alpha 7) RC (RC2 is bigger than RC1 of course)
	 * 
	 * @author gpolet
	 * 
	 */
	public static class VersionComparator implements Comparator<FlexoVersion> {

		public VersionComparator() {
			super();
		}

		@Override
		public int compare(FlexoVersion v1, FlexoVersion v2) {
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
						if (v1.rc < 0 && v2.rc < 0) {
							if (v1.isAlpha) {
								if (v2.isAlpha) {
									return 0;
								} else {
									return -1;
								}
							} else if (v1.isBeta) {
								if (v2.isAlpha) {
									return 1;
								} else if (v2.isBeta) {
									return 0;
								} else {
									return -1;
								}
							} else {
								if (v2.isAlpha || v2.isBeta) {
									return 1;
								} else {
									return 0;
								}
							}
						} else if (v1.rc < 0 && v2.rc > -1 && !v1.isAlpha && !v1.isBeta) {
							return 1;
						} else if (v1.rc > -1 && v2.rc < 0 && !v2.isAlpha && !v2.isBeta) {
							return -1;
						} else {
							if (v1.rc < v2.rc) {
								return -1;
							} else if (v1.rc > v2.rc) {
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

	}

	public static void main(String[] args) {
		String[] s = { "0.9.0", "0.9.1", "0.9.0RC1", "0.9.0RC2", "0.9.0alpha", "0.9.0beta", "0.10.0", "0.10.1", "0.10.0RC1", "0.10.0RC2",
				"0.10.0alpha", "0.10.0beta", "0.9", "1.0alpha", "1.1alpha", "1.1RC8" };
		for (int i = 0; i < s.length; i++) {
			String string = s[i];
			FlexoVersion v = new FlexoVersion(string);
			for (int j = 0; j < s.length; j++) {
				String s2 = s[j];
				FlexoVersion v2 = new FlexoVersion(s2);
				if (v.isLesserThan(v2)) {
					System.out.println(string + " is smaller than " + s2 + "[" + v2.toString() + "]");
				}
				if (v.isGreaterThan(v2)) {
					System.out.println(string + " is bigger than " + s2 + "[" + v2.toString() + "]");
				}
				if (v.equals(v2)) {
					System.out.println(string + " equals " + s2 + "[" + v2.toString() + "]");
				}
			}
		}

	}

}
