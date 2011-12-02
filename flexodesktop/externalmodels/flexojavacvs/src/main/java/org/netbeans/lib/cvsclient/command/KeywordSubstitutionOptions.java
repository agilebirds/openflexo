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
package org.netbeans.lib.cvsclient.command;

/**
 * @author Thomas Singer
 */
public final class KeywordSubstitutionOptions {

	public static final KeywordSubstitutionOptions DEFAULT = new KeywordSubstitutionOptions("kv"); // NOI18N
	public static final KeywordSubstitutionOptions DEFAULT_LOCKER = new KeywordSubstitutionOptions("kvl"); // NOI18N
	public static final KeywordSubstitutionOptions ONLY_KEYWORDS = new KeywordSubstitutionOptions("k"); // NOI18N
	public static final KeywordSubstitutionOptions ONLY_VALUES = new KeywordSubstitutionOptions("v"); // NOI18N
	public static final KeywordSubstitutionOptions OLD_VALUES = new KeywordSubstitutionOptions("o"); // NOI18N
	public static final KeywordSubstitutionOptions BINARY = new KeywordSubstitutionOptions("b"); // NOI18N

	public static KeywordSubstitutionOptions findKeywordSubstOption(String keyword) {
		if (BINARY.toString().equals(keyword)) {
			return BINARY;
		}
		if (DEFAULT.toString().equals(keyword)) {
			return DEFAULT;
		}
		if (DEFAULT_LOCKER.toString().equals(keyword)) {
			return DEFAULT_LOCKER;
		}
		if (OLD_VALUES.toString().equals(keyword)) {
			return OLD_VALUES;
		}
		if (ONLY_KEYWORDS.toString().equals(keyword)) {
			return ONLY_KEYWORDS;
		}
		if (ONLY_VALUES.toString().equals(keyword)) {
			return ONLY_VALUES;
		}
		return null;
	}

	private String value;

	private KeywordSubstitutionOptions(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}