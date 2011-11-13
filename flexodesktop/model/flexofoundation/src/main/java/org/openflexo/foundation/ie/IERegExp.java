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
package org.openflexo.foundation.ie;

import java.util.regex.Pattern;

import org.openflexo.swing.JTextFieldRegExp;

import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class IERegExp {
	public static final String JAVA_CLASS_NAME_REGEXP = "[_a-zA-Z][_a-zA-Z0-9$]*";
	public static final String JAVA_NICE_CLASS_NAME_REGEXP = "[A-Z][_a-zA-Z0-9$]*";
	public static final String ALPHA_NUM_NO_SPACE = "[_a-zA-Z][_a-zA-Z0-9]*";
	public static final String ALPHA_NUM = "[_a-zA-Z0-9 ]*";
	public static final String MAX_10_REG_EXP = ".{0,10}";
	public static final String UPPERCASE_REGEXP = "[_A-Z]*";
	public static final String MAX_50_REG_EXP = ".{0,50}";

	public static final Pattern JAVA_CLASS_NAME_PATTERN = Pattern.compile(JAVA_CLASS_NAME_REGEXP);

	public static final String NO_BLANK_REGEXP = "[^\\s]+";

	public static final Pattern NO_BLANK_PATTERN = Pattern.compile(NO_BLANK_REGEXP);

	public static JTextFieldRegExp getJavaClassNameValidationTextField(int size) {
		JTextFieldRegExp answer = new JTextFieldRegExp(size);
		answer.addToErrors(JAVA_CLASS_NAME_REGEXP,
				FlexoLocalization.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number"));
		answer.addToWarnings(JAVA_NICE_CLASS_NAME_REGEXP,
				FlexoLocalization.localizedForKey("must_start_with_a_capital_letter_followed_by_any_letter_or_number"));
		return answer;
	}

	public static JTextFieldRegExp getMaxLength10ValidationTextField(int size) {
		JTextFieldRegExp answer = new JTextFieldRegExp(size);
		answer.addToErrors(MAX_10_REG_EXP, FlexoLocalization.localizedForKey("10_chars_maximum"));
		answer.addToErrors(ALPHA_NUM, FlexoLocalization.localizedForKey("accept_only_chars_or_digit"));
		answer.addToWarnings(UPPERCASE_REGEXP, FlexoLocalization.localizedForKey("a_key_is_usually_uppercase"));
		return answer;
	}

	public static JTextFieldRegExp getMaxLength50ValidationTextField(int size) {
		JTextFieldRegExp answer = new JTextFieldRegExp(size);
		answer.addToErrors(MAX_50_REG_EXP, FlexoLocalization.localizedForKey("50_chars_maximum"));
		return answer;
	}

}
