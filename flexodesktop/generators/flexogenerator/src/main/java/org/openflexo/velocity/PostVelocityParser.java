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
package org.openflexo.velocity;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class PostVelocityParser
{
	private static final Logger logger = FlexoLogger.getLogger(PostVelocityParser.class.getPackage().getName());

	private static final String TAG_END = "end";

	private static final String[] TAGS = new String[] { "single", "onereturn", TAG_END };

	private static final String TAG_START = "@";

	public static String parseAndRenderCustomTag(String s)
	{
		try {
			Stack<String> tags = new Stack<String>();
			Stack<String> output = new Stack<String>();
			int index = 0, next;
			String nextTag = findNextTag(s, index);
			boolean lastMatchWasEndTag = false;
			if (nextTag == null) {
				return s;
			}
			while (nextTag != null) {
				next = s.indexOf(TAG_START + nextTag, index);
				String substring = s.substring(index, next);
				if (substring.length() > 0) {
					boolean startsWithTilde = substring.charAt(0) == '~';
					boolean endsWithTilde = substring.charAt(substring.length() - 1) == '~';
					if (startsWithTilde && endsWithTilde) {
						substring = substring.substring(1, substring.length() - 1);
					} else if (endsWithTilde) {
						substring = substring.substring(0, substring.length() - 1);
					} else if (startsWithTilde) {
						substring = substring.substring(1);
					}
				}
				if (lastMatchWasEndTag) {
					output.push(output.pop() + substring);
				} else {
					output.push(substring);
				}
				lastMatchWasEndTag = false;
				if (nextTag.equals(TAG_END)) {
					int tmp = s.indexOf('\n', next);
					if (next + nextTag.length() + 1 < s.length() && s.charAt(next + nextTag.length() + 1) == '@' && tmp > -1
							&& tmp < s.length() - 1) {
						index = tmp+1;
					} else {
						index = next + nextTag.length() + 1;
					}
					lastMatchWasEndTag = true;
					String tagToRender = tags.pop();
					String middleText = output.pop();
					output.push(output.pop() + render(tagToRender, middleText));
				} else {
					tags.push(nextTag);
					index = next + nextTag.length() + 1;
					int nextNewLine = s.indexOf('\n',index);
					if (nextNewLine>-1) {
						if (s.substring(index, nextNewLine).trim().length()==0) {
							index = nextNewLine+1;
						}
					}
				}

				nextTag = findNextTag(s, index);
			}
			if (index < s.length() && s.charAt(index) == '~') {
				index++;
			}
			return output.firstElement() + s.substring(index);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * @param tagToRender
	 * @param middleText
	 * @return
	 */
	private static String render(String tagToRender, String middleText)
	{
		if (tagToRender.equals(TAGS[0])) {
			return single(middleText);
		} else if (tagToRender.equals(TAGS[1])) {
			return onereturn(middleText);
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Unknown tag: " + tagToRender);
		}
		return null;
	}

	/**
	 * @param middleText
	 * @return
	 */
	private static String onereturn(String middleText)
	{
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("\\s*?\n\\s*").matcher(middleText);
		while(m.find()) {
			m.appendReplacement(sb, "\n");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * @param middleText
	 * @return
	 */
	private static String single(String middleText)
	{
		return middleText.replaceAll("\\s+", " ");
	}

	/**
	 * @param index
	 * @return
	 */
	private static String findNextTag(String input, int index)
	{
		int next = input.indexOf(TAG_START, index);
		while (next > -1) {
			for (String tag : TAGS) {
				if (input.regionMatches(next + 1, tag, 0, tag.length())) {
					return tag;
				}
			}
			next = input.indexOf(TAG_START, next+1);
		}
		return null;
	}

	public static void main(String[] args)
	{
		String text = "    public void appendToResponse(WOResponse r, WOContext c) {\n" + "        hiddenFieldValue = null;\n"
				+ "                pageDA = getUrlForOperation(context() @single\n" + "@end@\n"
				+ " , getOperationComponentInstanceID());\n" + "		\n" + "        super.appendToResponse(r,c);\n" + "    }\n" + "\n" + "";
		System.out.println(parseAndRenderCustomTag(text));
		System.out.println("");
		String s = "coucou~@onereturn~middle\n\n\n~blabla~@end~";
		System.out.println(s);
		System.out.println("##########################");
		System.out.println(parseAndRenderCustomTag(s));
	}
}
