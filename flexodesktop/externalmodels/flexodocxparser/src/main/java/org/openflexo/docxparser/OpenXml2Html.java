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
package org.openflexo.docxparser;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.dom4j.Element;
import org.openflexo.docxparser.dto.ParsedHtml;
import org.openflexo.docxparser.dto.ParsedHtmlResource;
import org.openflexo.toolbox.HTMLUtils;

public class OpenXml2Html {
	private static final Logger logger = Logger.getLogger(OpenXml2Html.class.getPackage().toString());

	private Set<String> availableCssClasses;
	private String resourcesDirectory;

	private PackagePart documentPart;
	private DocxNumberingParser docxNumbering;

	private String currentNumId = null;
	private Integer currentNumLevel = null;
	private HTMLProperties currentParagraphProperties = new HTMLProperties();
	private HTMLProperties currentSpanProperties = new HTMLProperties();

	public OpenXml2Html(PackagePart documentPart, Set<String> availableCssClasses, String resourcesDirectory) {
		this.documentPart = documentPart;
		this.availableCssClasses = availableCssClasses;
		this.resourcesDirectory = resourcesDirectory;

		if (!this.resourcesDirectory.endsWith("/") && !this.resourcesDirectory.endsWith("\\")) {
			this.resourcesDirectory = resourcesDirectory + "/";
		}
	}

	public static ParsedHtml getHtml(Element element, PackagePart documentPart, Set<String> availableCssClasses, String resourcesDirectory) {
		return new OpenXml2Html(documentPart, availableCssClasses, resourcesDirectory).getHtml(element);
	}

	// Used as entry point
	public ParsedHtml getHtml(Element element) {
		currentNumId = null;
		currentNumLevel = null;

		ParsedHtml parsedHtml = new ParsedHtml();

		parsedHtml.append(getRecursiveHtml(element));

		handleNumberingLevel(parsedHtml, null, null);

		return parsedHtml;
	}

	// Used in recursive iteration
	private ParsedHtml getRecursiveHtml(Element element) {
		ParsedHtml parsedHtml = new ParsedHtml();

		OpenXmlTag elementTag = OpenXmlTag.getOpenXmlTag(element);
		switch (elementTag) {
		case w_p:
			parsedHtml.append(getHtmlFromW_PElement(element));
			break;
		case w_r:
			parsedHtml.append(getHtmlFromW_RElement(element));
			break;
		case w_t:
			parsedHtml.append(getHtmlFromW_TElement(element));
			break;
		case w_hyperlink:
			parsedHtml.append(getHtmlFromW_HyperlinkElement(element));
			break;
		case w_drawing:
			parsedHtml.append(getHtmlFromW_DrawingElement(element));
			break;
		case w_tbl:
			parsedHtml.append(getHtmlFromW_TableElement(element));
			break;
		case w_tr:
			parsedHtml.append(getHtmlFromW_TableRowElement(element));
			break;
		case w_tc:
			parsedHtml.append(getHtmlFromW_TableCellElement(element));
			break;
		default:
			// find all w:p inside this element
			Iterator<?> iterator = element.elementIterator();
			while (iterator.hasNext()) {
				Element childElement = (Element) iterator.next();
				parsedHtml.append(getRecursiveHtml(childElement));
			}
			break;

		}

		return parsedHtml;
	}

	private ParsedHtml getHtmlFromW_TableCellElement(Element element) {
		ParsedHtml parsedHtml = new ParsedHtml();
		List<Element> paragraphs = element.selectNodes("descendant::w:p");
		List<Element> descendants;
		StringBuilder style = new StringBuilder();
		boolean singleParagraph = paragraphs.size() == 1;
		String align = null;
		if (singleParagraph) {
			Element alignElement = (Element) element.selectSingleNode("w:p/w:pPr/w:jc");
			if (alignElement != null) {
				String alignValue = alignElement.attributeValue("val");
				if (alignValue != null) {
					if (alignValue.equals("both")) {
						align = "justify";
					} else if (alignValue.equals("center")) {
						align = "center";
					} else if (alignValue.equals("start")) {
						align = "left";
					} else if (alignValue.equals("end")) {
						align = "right";
					}
				}
			}
			// If there is only one paragraph we do not repeat it in the HTML, we put directly the text inside the td
			descendants = element.selectNodes("descendant::w:t");
		} else {
			descendants = element.selectNodes("descendant::w:p");
		}
		Element shading = (Element) element.selectSingleNode("w:tcPr/w:shd");
		if (shading != null) {
			String shadingValue = shading.attributeValue("val");
			if (shadingValue != null) {
				Color color = HTMLUtils.extractColorFromString(shadingValue);
				if (color == null) {
					color = HTMLUtils.extractColorFromString("#" + shadingValue);
				}
				if (color != null) {
					style.append("background-color: ").append("#").append(HTMLUtils.toHexString(color)).append(";");
				}
			}
		}
		String valign = null;
		Element vAlignElement = (Element) element.selectSingleNode("w:tcPr/w:vAlign");
		if (vAlignElement != null && vAlignElement.attributeValue("val") != null) {
			valign = vAlignElement.attributeValue("val");
		}
		parsedHtml.append("<td ");
		if (style.length() > 0) {
			parsedHtml.append("style=\"").append(style.toString()).append("\" ");
		}
		if (align != null) {
			parsedHtml.append("align=\"").append(align).append("\" ");
		}
		if (valign != null) {
			parsedHtml.append("valign=\"").append(valign).append("\" ");
		}
		parsedHtml.append(">");
		for (Element childElement : descendants) {
			parsedHtml.append(getRecursiveHtml(childElement));
		}

		parsedHtml.append("</td>");
		return parsedHtml;
	}

	private ParsedHtml getHtmlFromW_TableRowElement(Element element) {
		ParsedHtml parsedHtml = new ParsedHtml();
		StringBuilder style = new StringBuilder();
		Element shading = (Element) element.selectSingleNode("w:tcPr/w:shd");
		if (shading != null) {
			String shadingValue = shading.attributeValue("val");
			if (shadingValue != null) {
				Color color = HTMLUtils.extractColorFromString(shadingValue);
				if (color == null) {
					color = HTMLUtils.extractColorFromString("#" + shadingValue);
				}
				if (color != null) {
					style.append("background-color: ").append("#").append(HTMLUtils.toHexString(color)).append(";");
				}
			}
		}
		parsedHtml.append("<tr ");
		if (style.length() > 0) {
			parsedHtml.append("style=\"").append(style.toString()).append("\" ");
		}
		parsedHtml.append(">");
		appendChildRecursively(element, parsedHtml);
		parsedHtml.append("</tr>");
		return parsedHtml;
	}

	private ParsedHtml getHtmlFromW_TableElement(Element element) {
		ParsedHtml parsedHtml = new ParsedHtml();
		parsedHtml.append("<table ");
		Element captionElement = (Element) element.selectSingleNode("w:tblPr/w:tblCaption");
		if (captionElement != null && captionElement.attributeValue("val") != null) {
			parsedHtml.append("title=\"").append(StringEscapeUtils.escapeHtml(captionElement.attributeValue("val").toString()))
					.append("\" ");
		}
		parsedHtml.append(">");
		appendChildRecursively(element, parsedHtml);
		parsedHtml.append("</table>");
		return parsedHtml;
	}

	private ParsedHtml getHtmlFromW_PElement(Element element) throws InvalidElementException {
		if (OpenXmlTag.getOpenXmlTag(element) != OpenXmlTag.w_p) {
			throw new InvalidElementException("Cannot transform element to html, expecting element w:p and get '"
					+ element.getQualifiedName() + "'");
		}

		Element numPrElement = (Element) element.selectSingleNode("w:pPr/w:numPr");
		String foundNumId = null;
		Integer foundNumLevel = null;
		if (numPrElement != null) {
			Element ilvlElement = numPrElement.element(DocxQName.getQName(OpenXmlTag.w_ilvl));
			Element numIdElement = numPrElement.element(DocxQName.getQName(OpenXmlTag.w_numId));
			if (ilvlElement != null && numIdElement != null) {
				try {
					foundNumLevel = new Integer(ilvlElement.attributeValue(DocxQName.getQName(OpenXmlTag.w_val)));
					foundNumId = numIdElement.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));
				} catch (NumberFormatException e) {
				}
			}
		}

		ParsedHtml parsedHtml = new ParsedHtml();

		currentParagraphProperties = new HTMLProperties(element);

		if (foundNumId != null || foundNumLevel != null || element.selectSingleNode("w:pPr/w:pStyle[@w:val = 'ListParagraph']") == null) {
			handleNumberingLevel(parsedHtml, foundNumId, foundNumLevel);
		}

		parsedHtml.append(currentParagraphProperties.getOpenTag());

		appendChildRecursively(element, parsedHtml);

		closeOpenedSpan(parsedHtml);

		parsedHtml.append(currentParagraphProperties.getCloseTag());

		return parsedHtml;
	}

	private ParsedHtml getHtmlFromW_HyperlinkElement(Element element) throws InvalidElementException {
		if (OpenXmlTag.getOpenXmlTag(element) != OpenXmlTag.w_hyperlink) {
			throw new InvalidElementException("Cannot transform element to html, expecting element w:hyperlink and get '"
					+ element.getQualifiedName() + "'");
		}

		ParsedHtml parsedHtml = new ParsedHtml();

		String href = null;

		String linkRid = element.attributeValue(DocxQName.getQName(OpenXmlTag.r_id));
		if (linkRid != null) {
			PackageRelationship linkRelationship = documentPart.getRelationship(linkRid);
			if (linkRelationship != null) {
				href = linkRelationship.getTargetURI().toString();
			}
		}

		if (href == null) { // Anchor ?
			String anchor = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_anchor));
			if (anchor != null) {
				href = "#" + anchor;
			}
		}

		String closeTag;

		if (href != null) {
			String target = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_tgtFrame));
			String title = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_tooltip));

			parsedHtml.append("<a href=\"" + href + "\"");
			if (target != null) {
				parsedHtml.append(" target=\"" + StringEscapeUtils.escapeHtml(target) + "\"");
			}
			if (title != null) {
				parsedHtml.append(" title=\"" + StringEscapeUtils.escapeHtml(title) + "\"");
			}

			parsedHtml.append(">");

			closeTag = "</a>";
		} else {
			logger.log(Level.WARNING, "OpenXml to Html: cannot get hyperlink relationship with id '" + linkRid + "'");
			closeTag = "";
		}

		appendChildRecursively(element, parsedHtml);

		parsedHtml.append(closeTag);

		return parsedHtml;
	}

	private ParsedHtml getHtmlFromW_RElement(Element element) throws InvalidElementException {
		if (OpenXmlTag.getOpenXmlTag(element) != OpenXmlTag.w_r) {
			throw new InvalidElementException("Cannot transform element to html, expecting element w:r and get '"
					+ element.getQualifiedName() + "'");
		}

		ParsedHtml parsedHtml = new ParsedHtml();

		HTMLProperties elementHTMLProperties = new HTMLProperties(element);
		elementHTMLProperties.removePropertiesFrom(currentParagraphProperties);

		if (!elementHTMLProperties.equals(currentSpanProperties)) {
			closeOpenedSpan(parsedHtml);
			currentSpanProperties = elementHTMLProperties;
			if (!currentSpanProperties.isEmpty()) {
				parsedHtml.append(currentSpanProperties.getOpenTag());
			}
		}

		appendChildRecursively(element, parsedHtml);

		return parsedHtml;
	}

	protected void appendChildRecursively(Element element, ParsedHtml parsedHtml) {
		for (Iterator<?> iterator = element.elementIterator(); iterator.hasNext();) {
			Element childElement = (Element) iterator.next();
			parsedHtml.append(getRecursiveHtml(childElement));
		}
	}

	private ParsedHtml getHtmlFromW_DrawingElement(Element element) {
		if (OpenXmlTag.getOpenXmlTag(element) != OpenXmlTag.w_drawing) {
			throw new InvalidElementException("Cannot transform element to html, expecting element w:drawing and get '"
					+ element.getQualifiedName() + "'");
		}

		ParsedHtml parsedHtml = new ParsedHtml();

		try {
			Element ablipElement = (Element) element.selectSingleNode("descendant::a:blip");
			if (ablipElement == null) {
				logger.warning("Cannot handle drawing tag: a:blip element not found");
				return parsedHtml;
			}

			String imageRid = ablipElement.attributeValue(DocxQName.getQName(OpenXmlTag.r_embed));
			if (imageRid == null) {
				logger.warning("Cannot handle drawing tag: r:embed attribute in a:blip element not found");
				return parsedHtml;
			}

			PackageRelationship imageRelationship = documentPart.getRelationship(imageRid);
			if (imageRelationship == null) {
				logger.warning("Cannot handle drawing tag: imageRelationship with id '" + imageRid + "' not found");
				return parsedHtml;
			}

			PackagePartName imagePartName = PackagingURIHelper.createPartName(imageRelationship.getTargetURI());
			PackagePart imagePart = documentPart.getPackage().getPart(imagePartName);
			String imageFileName;
			if (imagePart != null) {
				byte[] imageBytes = DocxXmlUtil.getByteArrayFromInputStream(imagePart.getInputStream());

				imageFileName = imagePartName.getName().substring(imagePartName.getName().lastIndexOf('/') + 1);

				parsedHtml.addNeededResource(new ParsedHtmlResource(imageFileName, imageBytes));
			} else {
				imageFileName = "";
			}
			Integer imageWidth = null;
			Integer imageHeight = null;

			Element extentElement = (Element) element.selectSingleNode("wp:inline/wp:extent");
			if (extentElement != null) {
				String imageCx = extentElement.attributeValue("cx");
				String imageCy = extentElement.attributeValue("cy");

				if (imageCx != null) {
					imageWidth = getEnglishMetricUnitInPixel(imageCx);
				}
				if (imageCy != null) {
					imageHeight = getEnglishMetricUnitInPixel(imageCy);
				}
			}

			parsedHtml.append("<img src=\"" + resourcesDirectory + imageFileName + "\"");
			if (imageWidth != null) {
				parsedHtml.append(" width=\"" + imageWidth + "\"");
			}
			if (imageHeight != null) {
				parsedHtml.append(" height=\"" + imageHeight + "\"");
			}
			parsedHtml.append(" />");

			return parsedHtml;
		} catch (InvalidFormatException e) {
			logger.log(Level.WARNING, "Cannot handle drawing tag: InvalidFormatException catched", e);
			return new ParsedHtml();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Cannot handle drawing tag: IOException catched", e);
			return new ParsedHtml();
		}
	}

	private Integer getEnglishMetricUnitInPixel(String value) {
		try {
			return Integer.parseInt(value) / 9525;
		} catch (NumberFormatException e) {
			logger.warning("Cannot transform EMU in pixel, input = '" + value + "'");
			return null;
		}
	}

	private ParsedHtml getHtmlFromW_TElement(Element element) throws InvalidElementException {
		if (OpenXmlTag.getOpenXmlTag(element) != OpenXmlTag.w_t) {
			throw new InvalidElementException("Cannot transform element to html, expecting element w:t and get '"
					+ element.getQualifiedName() + "'");
		}

		ParsedHtml parsedHtml = new ParsedHtml();

		parsedHtml.append(StringEscapeUtils.escapeHtml(element.getText()));

		return parsedHtml;
	}

	private void handleNumberingLevel(ParsedHtml parsedHtml, String foundNumId, Integer foundNumLevel) {
		Integer usedNumLevel = foundNumId == null || !foundNumId.equals(currentNumId) ? null : foundNumLevel;

		boolean needCloseOpenLi = true;
		while (currentNumLevel != null && (usedNumLevel == null || !currentNumLevel.equals(usedNumLevel))) {
			if (usedNumLevel == null || currentNumLevel > usedNumLevel) {
				parsedHtml.append("</li>");

				if (isOrderedNumbering(currentNumId, currentNumLevel)) {
					parsedHtml.append("</ol>");
				} else {
					parsedHtml.append("</ul>");
				}

				currentNumLevel--;
				if (currentNumLevel < 0) {
					currentNumLevel = null;
				}
			} else {
				currentNumLevel++;

				if (isOrderedNumbering(currentNumId, currentNumLevel)) {
					parsedHtml.append("<ol>");
				} else {
					parsedHtml.append("<ul>");
				}

				parsedHtml.append("<li>");
				needCloseOpenLi = false;
			}
		}

		if (foundNumId != null && !foundNumId.equals(currentNumId)) {
			for (int i = 0; i <= foundNumLevel; i++) {
				if (isOrderedNumbering(foundNumId, i)) {
					parsedHtml.append("<ol>");
				} else {
					parsedHtml.append("<ul>");
				}

				parsedHtml.append("<li>");
			}
			currentNumLevel = foundNumLevel;
			needCloseOpenLi = false;
		}

		currentNumId = foundNumId;

		if (needCloseOpenLi && currentNumId != null) {
			parsedHtml.append("</li><li>");
		}
	}

	private DocxNumberingParser getDocxNumbering() {
		if (docxNumbering == null) {
			docxNumbering = new DocxNumberingParser(documentPart);
		}
		return docxNumbering;
	}

	private boolean isOrderedNumbering(String numId, Integer levelNumber) {
		return getDocxNumbering().isOrderedNumbering(numId, levelNumber.toString());
	}

	private void closeOpenedSpan(ParsedHtml parsedHtml) {
		if (!currentSpanProperties.isEmpty()) {
			parsedHtml.append(currentSpanProperties.getCloseTag());
			currentSpanProperties = new HTMLProperties();
		}
	}

	@SuppressWarnings("serial")
	private class InvalidElementException extends RuntimeException {
		public InvalidElementException(String msg) {
			super(msg);
		}
	}

	private class HTMLProperties {
		private Map<String, String> properties = new HashMap<String, String>();
		private HTML.Tag tagToUse;

		public HTMLProperties() {

		}

		public HTMLProperties(Element element) {
			OpenXmlTag openXmlTag = OpenXmlTag.getOpenXmlTag(element);
			Element propertyElement = null;

			switch (openXmlTag) {
			case w_p:
				propertyElement = (Element) element.selectSingleNode("w:pPr");
				tagToUse = HTML.Tag.P; // Can be changed to h1, h2 ... in fillPropertyMap
				break;
			case w_r:
				propertyElement = (Element) element.selectSingleNode("w:rPr");
				tagToUse = HTML.Tag.SPAN;
				break;
			case w_pPr:
				propertyElement = element;
				tagToUse = HTML.Tag.P; // Can be changed to h1, h2 ... in fillPropertyMap
				break;
			case w_rPr:
				propertyElement = element;
				tagToUse = HTML.Tag.SPAN;
				break;
			}

			if (propertyElement != null) {
				fillPropertyMap(propertyElement);
			}
		}

		public void removePropertiesFrom(HTMLProperties src) {
			for (String key : src.properties.keySet()) {
				if (src.properties.get(key).equals(this.properties.get(key))) {
					this.properties.remove(key);
				}
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof HTMLProperties)) {
				return false;
			}

			if (((HTMLProperties) obj).properties.size() != this.properties.size()) {
				return false;
			}

			for (String key : this.properties.keySet()) {
				if (!this.properties.get(key).equals(((HTMLProperties) obj).properties.get(key))) {
					return false;
				}
			}

			return true;
		}

		/**
		 * Return true if the htmlProperties in argument is entirely covered by 'this'
		 * 
		 * @param htmlProperties
		 * @return
		 */
		public boolean isContained(HTMLProperties htmlProperties) {
			for (String key : htmlProperties.properties.keySet()) {
				if (!htmlProperties.properties.get(key).equals(this.properties.get(key))) {
					return false;
				}
			}

			return true;
		}

		public String getOpenTag() {
			StringBuilder styles = new StringBuilder();
			String classValue = null;
			StringBuilder fontAttributes = new StringBuilder();

			for (String key : properties.keySet()) {
				if (HTML.Attribute.CLASS.toString().equals(key)) {
					classValue = properties.get(key);
				} else if (CSS.Attribute.FONT_SIZE.toString().equals(key)) {
					fontAttributes.append(" " + HTML.Attribute.STYLE + "=\"" + key + ": " + properties.get(key) + ";\"");
				} else if (CSS.Attribute.COLOR.toString().equals(key)) {
					fontAttributes.append(" " + HTML.Attribute.COLOR + "=\"" + properties.get(key) + "\"");
				} else {
					styles.append(key + ": " + properties.get(key) + ";");
				}
			}

			return "<" + tagToUse + (classValue != null ? " class=\"" + classValue + "\"" : "")
					+ (styles.length() > 0 ? " style=\"" + styles + "\"" : "") + ">"
					+ (fontAttributes.length() > 0 ? "<" + HTML.Tag.FONT + fontAttributes + ">" : "");
		}

		public String getCloseTag() {
			if (properties.containsKey(CSS.Attribute.FONT_SIZE.toString()) || properties.containsKey(CSS.Attribute.COLOR.toString())) {
				return "</" + HTML.Tag.FONT + "></" + tagToUse + ">";
			}
			return "</" + tagToUse + ">";
		}

		public boolean isEmpty() {
			return properties.isEmpty();
		}

		private void fillPropertyMap(Element propertyElement) {
			for (OpenXmlTag tag : OpenXmlTag.getStylePropertyTags()) {
				Element element = propertyElement.element(DocxQName.getQName(tag));
				if (element != null) {
					String value;
					switch (tag) {
					case w_b:
						properties.put(CSS.Attribute.FONT_WEIGHT.toString(), "bold");
						break;
					case w_u:
						properties.put(CSS.Attribute.TEXT_DECORATION.toString(), "underline");
						break;
					case w_i:
						properties.put(CSS.Attribute.FONT_STYLE.toString(), "italic");
						break;
					case w_color:
						value = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));
						if (value != null) {
							Color color = HTMLUtils.extractColorFromString(value);
							if (color == null) {
								color = HTMLUtils.extractColorFromString("#" + value);
							}

							if (color != null) {
								properties.put(CSS.Attribute.COLOR.toString(), "#" + HTMLUtils.toHexString(color));
							}

						}
						break;
					case w_highlight:
						value = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));
						if (value != null) {
							Color color = HTMLUtils.extractColorFromString(value);
							if (color == null) {
								color = HTMLUtils.extractColorFromString("#" + value);
							}

							if (color != null) {
								properties.put(CSS.Attribute.BACKGROUND_COLOR.toString(), "#" + HTMLUtils.toHexString(color));
							}
						}
						break;
					case w_shd:
						value = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_fill));
						if (value != null) {
							Color color = HTMLUtils.extractColorFromString(value);
							if (color == null) {
								color = HTMLUtils.extractColorFromString("#" + value);
							}

							if (color != null) {
								properties.put(CSS.Attribute.BACKGROUND_COLOR.toString(), "#" + HTMLUtils.toHexString(color));
							}
						}
						break;
					case w_jc:
						value = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));
						String alignValue = null;
						if ("left".equals(value)) {
							alignValue = "left";
						} else if ("right".equals(value)) {
							alignValue = "right";
						} else if ("center".equals(value)) {
							alignValue = "center";
						} else if ("both".equals(value)) {
							alignValue = "justify";
						}

						if (alignValue != null) {
							properties.put(CSS.Attribute.TEXT_ALIGN.toString(), alignValue);
						}
						break;
					case w_szCs:
					case w_sz:
						value = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));
						if (value != null) {
							try {
								int size = Integer.parseInt(value);
								properties.put(CSS.Attribute.FONT_SIZE.toString(), String.valueOf(size / 2) + "pt");
							} catch (NumberFormatException e) {
								// Ok not a number, skip it
							}
						}
						break;
					case w_pStyle:
						value = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));
						if (value != null) {
							if (value.equals("Heading1")) {
								tagToUse = HTML.Tag.H1;
							} else if (value.equals("Heading2")) {
								tagToUse = HTML.Tag.H2;
							} else if (value.equals("Heading3")) {
								tagToUse = HTML.Tag.H3;
							} else if (value.equals("Heading4")) {
								tagToUse = HTML.Tag.H4;
							} else if (value.equals("Heading5")) {
								tagToUse = HTML.Tag.H5;
							} else if (value.equals("Heading6")) {
								tagToUse = HTML.Tag.H6;
							} else if (availableCssClasses.contains(value)) {
								properties.put(HTML.Attribute.CLASS.toString(), value);
							}
						}
						break;
					case w_rStyle:
						value = element.attributeValue(DocxQName.getQName(OpenXmlTag.w_val));
						if (value != null && availableCssClasses.contains(value)) {
							properties.put(HTML.Attribute.CLASS.toString(), value);
						}
						break;
					}
				}
			}
		}
	}
}
