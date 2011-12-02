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
package org.openflexo.wysiwyg;

import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.openflexo.toolbox.HTMLUtils;

public class FlexoWysiwygHtmlCleaner extends ParserCallback {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FlexoWysiwygHtmlCleaner.class.getPackage().getName());

	public static String cleanHtml(String originalHtml, List<?> availableStyleClasses) throws IOException {
		if (originalHtml != null) {
			FlexoWysiwygHtmlCleaner cleaner = new FlexoWysiwygHtmlCleaner(availableStyleClasses);
			Reader reader = new StringReader(originalHtml);

			new ParserDelegator().parse(reader, cleaner, false);
			return cleaner.getCleanedHtml();
		}

		return null;
	}

	private StringBuilder newHtml = new StringBuilder();
	private List<?> availableStyleClasses;
	private Stack<String> closeTagsStack = new Stack<String>();

	public FlexoWysiwygHtmlCleaner(List<?> availableStyleClasses) {
		this.availableStyleClasses = availableStyleClasses;
	}

	private String getCleanedHtml() {
		return newHtml.toString();
	}

	@Override
	public void handleComment(char[] data, int pos) {
		// remove comments
	}

	@Override
	public void handleEndOfLineString(String eol) {
		newHtml.append(eol);
	}

	@Override
	public void handleText(char[] data, int pos) {
		newHtml.append(HTMLUtils.escapeStringForHTML(new String(data), false));
	}

	@Override
	public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
		newHtml.append(getCleanedTag(t, a, true));
	}

	@Override
	public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
		newHtml.append(getCleanedTag(t, a, false));
	}

	@Override
	public void handleEndTag(Tag t, int pos) {
		closeNextTag();
	}

	private void closeNextTag() {
		newHtml.append(closeTagsStack.pop());
	}

	private String getCleanedTag(Tag tag, MutableAttributeSet attributeSet, boolean closeTag) {
		if (!isTagKept(tag)) {
			if (!closeTag) {
				closeTagsStack.push(""); // All tags must be in closeTagsStack
			}
			return "";
		}

		StringBuilder sbMainTag = new StringBuilder();
		StringBuilder sbAdditionalTag = new StringBuilder();

		sbMainTag.append("<" + tag.toString());

		for (Enumeration<?> en = attributeSet.getAttributeNames(); en.hasMoreElements();) {
			Object attributeName = en.nextElement();
			String cleanedAttribute = getCleanedAttribute(tag, attributeName, attributeSet.getAttribute(attributeName));

			if (attributeName == Attribute.STYLE && tag != Tag.FONT && sbAdditionalTag.length() == 0) {
				String fontAttributes = extractFontAttributesFromStyles(attributeSet.getAttribute(attributeName).toString());
				if (fontAttributes != null && fontAttributes.length() > 0) {
					sbAdditionalTag.append("<" + Tag.FONT + fontAttributes + ">");
				}
			}

			if (cleanedAttribute.length() > 0) {
				sbMainTag.append(" " + cleanedAttribute);
			}
		}

		if (closeTag) {
			sbMainTag.append(" /");
		}
		sbMainTag.append(">");

		if (closeTag) { // The eventual additional tag must be outside the main tag
			if (sbAdditionalTag.length() > 0) {
				return sbAdditionalTag.toString() + sbMainTag.toString() + "</" + Tag.FONT + ">";
			}
			return sbMainTag.toString();
		}

		// The eventual additional tag must be inside the main tag
		if (sbAdditionalTag.length() > 0) {
			closeTagsStack.push("</" + Tag.FONT + "></" + tag + ">");
			return sbMainTag.toString() + sbAdditionalTag.toString();
		}

		closeTagsStack.push("</" + tag + ">");
		return sbMainTag.toString();
	}

	private boolean isTagKept(Tag tag) {
		return tag == HTML.Tag.A || tag == HTML.Tag.B || tag == HTML.Tag.BR || tag == HTML.Tag.DIV || tag == HTML.Tag.FONT
				|| tag == HTML.Tag.H1 || tag == HTML.Tag.H2 || tag == HTML.Tag.H3 || tag == HTML.Tag.H4 || tag == HTML.Tag.H5
				|| tag == HTML.Tag.H6 || tag == HTML.Tag.I || tag == HTML.Tag.IMG || tag == HTML.Tag.LI || tag == HTML.Tag.OL
				|| tag == HTML.Tag.P || tag == HTML.Tag.SPAN || tag == HTML.Tag.U || tag == HTML.Tag.UL;
	}

	private String getCleanedAttribute(Tag tag, Object attributeName, Object attributeValue) {
		if (attributeName == null) {
			return "";
		}

		if (attributeName == Attribute.STYLE) {
			if (attributeValue != null) {
				String cleanedStyle = getCleanedStyleValue(attributeValue.toString());
				if (cleanedStyle != null && cleanedStyle.length() > 0) {
					return "style=\"" + cleanedStyle + "\"";
				}
			}

			return "";
		}

		if (attributeName == Attribute.CLASS) {
			if (attributeValue != null) {
				String cleanedClass = getCleanedClassValue(attributeValue.toString());
				if (cleanedClass != null && cleanedClass.length() > 0) {
					return "class=\"" + cleanedClass + "\"";
				}
			}

			return "";
		}

		boolean includeAttribute = false;

		if (tag == Tag.A) {
			includeAttribute = attributeName == Attribute.HREF || attributeName == Attribute.TARGET || attributeName == Attribute.TITLE;
		} else if (tag == Tag.FONT) {
			includeAttribute = attributeName == Attribute.SIZE || attributeName == Attribute.COLOR;
		} else if (tag == Tag.IMG) {
			includeAttribute = attributeName == Attribute.WIDTH || attributeName == Attribute.HEIGHT || attributeName == Attribute.SRC;
		} else if (tag == Tag.P) {
			includeAttribute = attributeName == Attribute.ALIGN;
		}

		if (includeAttribute) {
			return attributeName + "=\"" + attributeValue + "\"";
		}
		return "";
	}

	private String getCleanedStyleValue(String styleValue) {
		StringBuilder sb = new StringBuilder();

		for (String styleEffect : styleValue.split(";")) {
			String cleanedStyleEffect = getCleanedStyleEffect(styleEffect);
			if (cleanedStyleEffect.length() > 0) {
				sb.append(cleanedStyleEffect + ";");
			}
		}

		return sb.toString();
	}

	private String extractFontAttributesFromStyles(String styleValue) {
		StringBuilder sb = new StringBuilder();

		for (String styleEffect : styleValue.split(";")) {
			String[] keyAndValue = getStyleEffectKeyAndValue(styleEffect);
			if (keyAndValue != null) {
				String effectKey = keyAndValue[0];
				String effectValue = keyAndValue[1];

				CSS.Attribute attribute = CSS.getAttribute(effectKey);

				if (attribute == CSS.Attribute.COLOR) {
					sb.append(" " + Attribute.COLOR + "=\"" + effectValue + "\"");
				} else if (attribute == CSS.Attribute.FONT_SIZE) {
					Integer fontSizeInPoints = HTMLUtils.getFontSizeInPoints(effectValue);
					if (fontSizeInPoints != null) {
						sb.append(" " + Attribute.STYLE + "=\"" + CSS.Attribute.FONT_SIZE + ":" + fontSizeInPoints + "pt;\"");
					}
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Effect must contains [effect name]:[effect value] ie. "xxxx: yyyy"
	 * 
	 * @param effect
	 * @return
	 */
	private String getCleanedStyleEffect(String effect) {
		String[] keyAndValue = getStyleEffectKeyAndValue(effect);
		if (keyAndValue == null) {
			return "";
		}

		String effectKey = keyAndValue[0];
		String effectValue = keyAndValue[1];

		CSS.Attribute attribute = CSS.getAttribute(effectKey);

		if (attribute == null) {
			return "";
		} else if (attribute == CSS.Attribute.BACKGROUND_COLOR) {
			return attribute.toString() + ": " + effectValue;
		} else if (attribute == CSS.Attribute.FONT_WEIGHT) {
			if (effectValue.equals("bold") || effectValue.equals("bolder")) {
				return attribute.toString() + ": bold";
			}
		} else if (attribute == CSS.Attribute.TEXT_DECORATION) {
			if (effectValue.equals("underline")) {
				return attribute.toString() + ": underline";
			}
		} else if (attribute == CSS.Attribute.FONT_STYLE) {
			if (effectValue.equals("italic")) {
				return attribute.toString() + ": italic";
			}
		} else if (attribute == CSS.Attribute.TEXT_ALIGN) {
			if (effectValue.equals("left") || effectValue.equals("right") || effectValue.equals("center") || effectValue.equals("justify")) {
				return attribute.toString() + ": " + effectValue;
			}
		} else if (attribute == CSS.Attribute.BACKGROUND) { // Get only the color if any
			for (String backgroundItem : effectValue.split(" ")) {
				Color color = HTMLUtils.extractColorFromString(backgroundItem.trim());
				if (color != null) {
					return CSS.Attribute.BACKGROUND_COLOR + ": " + backgroundItem.trim();
				}
			}
		}

		return "";
	}

	private String[] getStyleEffectKeyAndValue(String effect) {
		effect = effect.trim();

		int indexOf = effect.indexOf(':');
		if (indexOf == -1 || effect.length() <= indexOf + 1) {
			return null;
		}

		String[] keyAndValue = new String[2];
		keyAndValue[0] = effect.substring(0, indexOf).trim();
		keyAndValue[1] = effect.substring(indexOf + 1).trim();

		return keyAndValue;
	}

	private String getCleanedClassValue(String classValue) {
		for (String classItem : classValue.split(" ")) {
			if (availableStyleClasses.contains(classItem)) {
				return classItem; // Don't handle multiple class because we cannot set multiple styles in docx
			}
		}

		return null;
	}
}
