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
package org.openflexo.dg.docx;

import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.lang.StringEscapeUtils;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.HTMLUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.XMLCoder;

public class DocxVelocityXmlFromHtml {

	protected static final Logger logger = FlexoLogger.getLogger(DocxVelocityXmlFromHtml.class.getPackage().getName());
	private String docxXmlString;
	private Set<String> additionalRelationships;
	private List<String> orderedBulletsIds;
	private List<String> bulletsIds;
	private int currentUniqueId;

	public DocxVelocityXmlFromHtml(String htmlString, int currentUniqueId, boolean isWithinP, ProjectDocDocxGenerator projectDocxGenerator) {
		if (htmlString != null) {
			Html2DocxVelocityXmlParserCallback callback = new Html2DocxVelocityXmlParserCallback(currentUniqueId, isWithinP,
					projectDocxGenerator);
			Reader reader = new StringReader(htmlString);
			try {
				new ParserDelegator().parse(reader, callback, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			this.docxXmlString = callback.getBuildedString();
			this.additionalRelationships = callback.getAdditionalRelationships();
			this.orderedBulletsIds = callback.getOrderedBulletsIds();
			this.bulletsIds = callback.getBulletsIds();
			this.currentUniqueId = callback.getCurrentUniqueId();
		} else {
			this.currentUniqueId = currentUniqueId;
		}
	}

	public String getDocxXmlString() {
		return docxXmlString;
	}

	public Set<String> getAdditionalRelationships() {
		return additionalRelationships;
	}

	public List<String> getOrderedBulletsIds() {
		return orderedBulletsIds;
	}

	public List<String> getBulletsIds() {
		return bulletsIds;
	}

	public int getCurrentUniqueId() {
		return currentUniqueId;
	}

	private static class Html2DocxVelocityXmlParserCallback extends HTMLEditorKit.ParserCallback {
		private boolean withinHead = false;
		private boolean withinParagraph = false;
		private boolean withinR = false;
		private boolean isWithinA = false;
		private int currentUniqueId;

		private HTMLToDocxPropertiesStack rProperties = new HTMLToDocxPropertiesStack(false);
		private HTMLToDocxPropertiesStack pProperties = new HTMLToDocxPropertiesStack(true);

		private StringBuilder sb = new StringBuilder();
		private Set<String> additionalRelationships = new HashSet<String>();

		private int currentNumberingId = 0;
		private Integer currentNumberingLevel = null;
		private List<String> orderedBulletsIds = new ArrayList<String>();
		private List<String> bulletsIds = new ArrayList<String>();
		private boolean needCloseParagraph;
		private ProjectDocDocxGenerator projectDocxGenerator;

		public Html2DocxVelocityXmlParserCallback(int currentUniqueId, boolean isWithinP, ProjectDocDocxGenerator projectDocxGenerator) {
			super();
			this.needCloseParagraph = !isWithinP;
			this.withinParagraph = isWithinP;
			this.currentUniqueId = currentUniqueId;
			this.projectDocxGenerator = projectDocxGenerator;
		}

		@Override
		public void handleText(char[] data, int pos) {
			ensureIsWithinParagraph();
			ensureIsWithinR();

			sb.append(StringUtils.LINE_SEPARATOR + "<w:t xml:space=\"preserve\">");
			sb.append(StringEscapeUtils.escapeXml(XMLCoder.removeUnacceptableChars(new String(data))));
			sb.append("</w:t>");
			sb.append(StringUtils.LINE_SEPARATOR + "</w:r>");
			withinR = false;
		}

		@Override
		public void handleComment(char[] data, int pos) {
		}

		@Override
		public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
			if (t == HTML.Tag.IMG) {
				handleImage(a);
			} else if (t == HTML.Tag.BR) {
				handleBR();
			}
		}

		@Override
		public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
			if (t == HTML.Tag.HTML || t == HTML.Tag.BODY) {
				return;
			}
			if (t == HTML.Tag.HEAD) {
				withinHead = true;
				return;
			}
			if (withinHead) {
				return;
			}

			if (t == HTML.Tag.P || t == HTML.Tag.DIV) {
				handleStartParagraph(a);
			} else if (t == HTML.Tag.H1 || t == HTML.Tag.H2 || t == HTML.Tag.H3 || t == HTML.Tag.H4 || t == HTML.Tag.H5 || t == HTML.Tag.H6) {
				handleStartHeading(t, a);
			} else if (t == HTML.Tag.A) {
				handleStartAnchor(a);
			} else if (t == HTML.Tag.B) {
				handleStartBold(a);
			} else if (t == HTML.Tag.I) {
				handleStartItalic(a);
			} else if (t == HTML.Tag.U) {
				handleStartUnderline(a);
			} else if (t == HTML.Tag.FONT) {
				handleStartFont(a);
			} else if (t == HTML.Tag.UL || t == HTML.Tag.OL) {
				handleStartList(t, a);
			} else if (t == HTML.Tag.LI) {
				handleStartListItem(a);
			} else if (t == HTML.Tag.SPAN) {
				handleStartSpan(a);
			} else {
				handleUnknownStartTag(t, pos);
			}
		}

		@Override
		public void handleEndTag(Tag t, int pos) {
			if (t == HTML.Tag.HEAD) {
				withinHead = false;
				return;
			}

			if (t == HTML.Tag.P || t == HTML.Tag.DIV || t == HTML.Tag.LI || t == HTML.Tag.H1 || t == HTML.Tag.H2 || t == HTML.Tag.H3
					|| t == HTML.Tag.H4 || t == HTML.Tag.H5 || t == HTML.Tag.H6) {
				pProperties.pop();
				closeParagraph();
			} else if (t == HTML.Tag.A) {
				rProperties.pop();
				if (isWithinA) {
					sb.append("</w:hyperlink>");
					isWithinA = false;
				}
			} else if (t == HTML.Tag.B) {
				rProperties.pop();
			} else if (t == HTML.Tag.I) {
				rProperties.pop();
			} else if (t == HTML.Tag.U) {
				rProperties.pop();
			} else if (t == HTML.Tag.FONT) {
				rProperties.pop();
			} else if (t == HTML.Tag.UL || t == HTML.Tag.OL) {
				pProperties.pop();
				currentNumberingLevel--;
				if (currentNumberingLevel < 0) {
					currentNumberingLevel = null;
				}
			} else if (t == HTML.Tag.SPAN) {
				rProperties.pop();
			} else {
				handleUnknownEndTag(t, pos);
			}
		}

		private boolean ensureIsWithinParagraph() {
			return ensureIsWithinParagraph(false);
		}

		private boolean ensureIsWithinParagraph(boolean isStartListItem) {
			if (!withinParagraph) {
				sb.append(StringUtils.LINE_SEPARATOR + "<w:p>");
				withinParagraph = true;
				sb.append(getCurrentpPr(isStartListItem));
				return true;
			}
			return false;
		}

		private void closeParagraph() {
			if (withinParagraph) {
				if (withinR) {
					sb.append(StringUtils.LINE_SEPARATOR + "<w:t></w:t>");
					sb.append(StringUtils.LINE_SEPARATOR + "</w:r>");
					withinR = false;
				} else if (sb.toString().endsWith("<w:p>")) {
					sb.append(StringUtils.LINE_SEPARATOR + "<w:r>");
					sb.append(StringUtils.LINE_SEPARATOR + "<w:t></w:t>");
					sb.append(StringUtils.LINE_SEPARATOR + "</w:r>");
				}
				sb.append(StringUtils.LINE_SEPARATOR + "</w:p>");
				withinParagraph = false;
			}
		}

		public void ensureIsWithinR() {
			if (!withinR) {
				sb.append(StringUtils.LINE_SEPARATOR + "<w:r>");

				String rPr = rProperties.getCurrentDocxProperties();
				if (rPr.length() > 0) {
					sb.append(StringUtils.LINE_SEPARATOR + "<w:rPr>");
					sb.append(rPr);
					sb.append(StringUtils.LINE_SEPARATOR + "</w:rPr>");
				}
				withinR = true;
			}
		}

		private void handleStartSpan(MutableAttributeSet a) {
			rProperties.push(HTML.Tag.SPAN, a);
		}

		private void handleStartListItem(MutableAttributeSet a) {
			closeParagraph();

			pProperties.push(HTML.Tag.LI, a);

			ensureIsWithinParagraph(true);
		}

		private void handleStartList(Tag tag, MutableAttributeSet a) {
			pProperties.push(tag, a);

			if (currentNumberingLevel == null) {
				currentNumberingId = getUniqueId();
				currentNumberingLevel = 0;
				if (tag == HTML.Tag.UL) {
					bulletsIds.add("" + currentNumberingId);
				} else {
					orderedBulletsIds.add("" + currentNumberingId);
				}
			} else {
				currentNumberingLevel++;
			}
		}

		private void handleImage(MutableAttributeSet a) {
			String imageSubPath = (String) a.getAttribute(HTML.Attribute.SRC);
			ImageIcon imageIcon = projectDocxGenerator.getImageIconForImportedImageNamed(imageSubPath);

			if (imageIcon == null) {
				return;
			}

			String widthAttributeString = (String) a.getAttribute(HTML.Attribute.WIDTH);
			String heightAttributeString = (String) a.getAttribute(HTML.Attribute.HEIGHT);
			Integer imageWidth = null;
			Integer imageHeight = null;

			if (widthAttributeString != null) {
				try {
					imageWidth = Integer.parseInt(widthAttributeString);
				} catch (NumberFormatException e) {
					// Not a number, let it null
				}
			}

			if (heightAttributeString != null) {
				try {
					imageHeight = Integer.parseInt(heightAttributeString);
				} catch (NumberFormatException e) {
					// Not a number, let it null
				}
			}

			double imageRatio = (double) imageIcon.getIconWidth() / (double) imageIcon.getIconHeight();

			if (imageWidth == null) {
				if (imageHeight != null) {
					imageWidth = new Double(imageHeight * imageRatio).intValue();
				} else {
					imageWidth = imageIcon.getIconWidth();
				}
			}

			if (imageHeight == null) {
				if (imageWidth != null) {
					imageHeight = new Double(imageWidth / imageRatio).intValue();
				} else {
					imageHeight = imageIcon.getIconHeight();
				}
			}

			if (imageWidth > 514) {
				imageHeight = 514 * imageHeight / imageWidth;
				imageWidth = 514;
			}
			if (imageHeight > 734) {
				imageWidth = 734 * imageWidth / imageHeight;
				imageHeight = 734;
			}

			imageWidth = imageWidth * 9525;
			imageHeight = imageHeight * 9525;

			ensureIsWithinParagraph();
			ensureIsWithinR();

			int uniqueID = getUniqueId();

			StringBuilder imageXml = new StringBuilder();
			imageXml.append(StringUtils.LINE_SEPARATOR + "<w:drawing>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "	<wp:inline distT=\"0\" distB=\"0\" distL=\"0\" distR=\"0\">");
			imageXml.append(StringUtils.LINE_SEPARATOR + "		<wp:extent cx=\"" + imageWidth + "\" cy=\"" + imageHeight + "\" />");
			imageXml.append(StringUtils.LINE_SEPARATOR + "		<wp:docPr id=\"" + uniqueID + "\" name=\"Picture\" />");
			imageXml.append(StringUtils.LINE_SEPARATOR + "		<wp:cNvGraphicFramePr>");
			imageXml.append(StringUtils.LINE_SEPARATOR
					+ "			<a:graphicFrameLocks xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" noChangeAspect=\"1\" />");
			imageXml.append(StringUtils.LINE_SEPARATOR + "		</wp:cNvGraphicFramePr>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "		<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">");
			imageXml.append(StringUtils.LINE_SEPARATOR
					+ "			<a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">");
			imageXml.append(StringUtils.LINE_SEPARATOR
					+ "				<pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">");
			imageXml.append(StringUtils.LINE_SEPARATOR + "					<pic:nvPicPr>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "						<pic:cNvPr id=\"0\" name=\"\" />");
			imageXml.append(StringUtils.LINE_SEPARATOR + "						<pic:cNvPicPr/>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "					</pic:nvPicPr>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "					<pic:blipFill>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "						<a:blip r:embed=\"" + projectDocxGenerator.getRIdForString(imageSubPath)
					+ "\" />");
			imageXml.append(StringUtils.LINE_SEPARATOR + "						<a:stretch>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "							<a:fillRect/>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "						</a:stretch>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "					</pic:blipFill>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "					<pic:spPr>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "						<a:xfrm>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "							<a:off x=\"0\" y=\"0\" />");
			imageXml.append(StringUtils.LINE_SEPARATOR + "							<a:ext cx=\"" + imageWidth + "\" cy=\"" + imageHeight + "\" />");
			imageXml.append(StringUtils.LINE_SEPARATOR + "						</a:xfrm>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "						<a:prstGeom prst=\"rect\">");
			imageXml.append(StringUtils.LINE_SEPARATOR + "							<a:avLst/>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "						</a:prstGeom>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "					</pic:spPr>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "				</pic:pic>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "			</a:graphicData>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "		</a:graphic>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "	</wp:inline>");
			imageXml.append(StringUtils.LINE_SEPARATOR + "</w:drawing>");

			imageXml.append(StringUtils.LINE_SEPARATOR + "</w:r>");
			sb.append(imageXml);
			withinR = false;
			String rel = "<Relationship Id=\"" + projectDocxGenerator.getRIdForString(imageSubPath)
			+ "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image\" Target=\""
			+ projectDocxGenerator.getMediaRelativePath(imageSubPath) + "\"/>";
			getAdditionalRelationships().add(rel);

		}

		private void handleStartFont(AttributeSet attributeSet) {
			rProperties.push(HTML.Tag.FONT, attributeSet);
		}

		private void handleStartUnderline(AttributeSet attributeSet) {
			rProperties.push(HTML.Tag.U, attributeSet);
		}

		private void handleStartItalic(AttributeSet attributeSet) {
			rProperties.push(HTML.Tag.I, attributeSet);
		}

		private void handleStartBold(AttributeSet attributeSet) {
			rProperties.push(HTML.Tag.B, attributeSet);
		}

		private void handleStartAnchor(AttributeSet attributeSet) {
			String href = (String) attributeSet.getAttribute(HTML.Attribute.HREF);

			rProperties.push(HTML.Tag.A, attributeSet);

			if (href == null) {
				href = (String) attributeSet.getAttribute(HTML.Attribute.NAME);
			}
			if (href != null) {
				href = href.trim();
				boolean isAnchor = href.startsWith("#");
				String target = (String) attributeSet.getAttribute(HTML.Attribute.TARGET);
				String title = (String) attributeSet.getAttribute(HTML.Attribute.TITLE);

				if (href.startsWith("file:/") && !href.startsWith("file:///")) {
					href = "file:///" + href.substring(6);
				}

				if (href.startsWith("\\\\")) {
					href = "file:///" + href;
				}

				href = href.replaceAll(" ", "%20");

				ensureIsWithinParagraph();
				sb.append("<w:hyperlink");
				isWithinA = true;
				if (isAnchor) {
					sb.append(" w:anchor=\"" + href.substring(1) + "\"");
				} else {
					String rId = projectDocxGenerator.getRIdForString(href);
					sb.append(" r:id=\"" + rId + "\"");

					String rel = "<Relationship Id=\"" + rId
					+ "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink\" Target=\"" + href
					+ "\" TargetMode=\"External\"/>";
					getAdditionalRelationships().add(rel);
				}
				if (target != null && target.length() > 0) {
					sb.append(" w:tgtFrame=\"" + target + "\"");
				}
				if (title != null && title.length() > 0) {
					sb.append(" w:tooltip=\"" + title + "\"");
				}
				sb.append(">");
			}
		}

		private void handleBR() {
			closeParagraph();
			ensureIsWithinParagraph();
		}

		private void handleStartParagraph(AttributeSet attributeSet) {
			pProperties.push(HTML.Tag.P, attributeSet);
			ensureIsWithinParagraph();
		}

		private void handleStartHeading(Tag tag, AttributeSet attributeSet) {
			pProperties.push(tag, attributeSet);
			ensureIsWithinParagraph();
		}

		private void handleUnknownStartTag(Tag t, int pos) {
		}

		private void handleUnknownEndTag(Tag t, int pos) {
		}

		private String getCurrentpPr(boolean isStartListItem) {
			StringBuilder tmpSb = new StringBuilder();
			if (currentNumberingLevel != null) {
				tmpSb.append(StringUtils.LINE_SEPARATOR + "<w:pStyle w:val=\"ListParagraph\"/>");
				if (isStartListItem) {
					tmpSb.append(StringUtils.LINE_SEPARATOR + "<w:numPr>");
					tmpSb.append(StringUtils.LINE_SEPARATOR + "<w:ilvl w:val=\"" + currentNumberingLevel + "\"/>");
					tmpSb.append(StringUtils.LINE_SEPARATOR + "<w:numId w:val=\"" + currentNumberingId + "\"/>");
					tmpSb.append(StringUtils.LINE_SEPARATOR + "</w:numPr>");
				}
			}

			tmpSb.append(pProperties.getCurrentDocxProperties());

			if (tmpSb.length() > 0) {
				tmpSb.insert(0, StringUtils.LINE_SEPARATOR + "<w:pPr>");
				tmpSb.append(StringUtils.LINE_SEPARATOR + "</w:pPr>");
			}

			return tmpSb.toString();
		}

		public String getBuildedString() {
			if (needCloseParagraph) {
				closeParagraph();
			} else {
				ensureIsWithinParagraph();
			}
			return sb.toString();
		}

		public Set<String> getAdditionalRelationships() {
			return additionalRelationships;
		}

		public List<String> getBulletsIds() {
			return bulletsIds;
		}

		public List<String> getOrderedBulletsIds() {
			return orderedBulletsIds;
		}

		public int getCurrentUniqueId() {
			return currentUniqueId;
		}

		public int getUniqueId() {
			return currentUniqueId++;
		}

		private static class HTMLToDocxPropertiesStack {
			private static final String RSTYLEKEY = "rStyle";
			private static final String PSTYLEKEY = "pStyle";
			private static final String LINKSTYLEKEY = "linkStyle"; // Specific key because it applies only if no other rStyle has been set

			private Stack<Map<String, String>> propertiesStack = new Stack<Map<String, String>>();
			private boolean paragraphLevel;

			public HTMLToDocxPropertiesStack(boolean paragraphLevel) {
				this.paragraphLevel = paragraphLevel;
			}

			public void push(HTML.Tag tag, AttributeSet attributeSet) {
				StringBuilder computedStyle = new StringBuilder();
				Map<String, String> map = new HashMap<String, String>();

				if (tag == HTML.Tag.B) {
					computedStyle.append(CSS.Attribute.FONT_WEIGHT + ": bold");
				} else if (tag == HTML.Tag.U) {
					computedStyle.append(CSS.Attribute.TEXT_DECORATION + ": underline");
				} else if (tag == HTML.Tag.I) {
					computedStyle.append(CSS.Attribute.FONT_STYLE + ": italic");
				} else if (tag == HTML.Tag.A) {
					map.put(LINKSTYLEKEY, "<w:rStyle w:val=\"Hyperlink\"/>");
				}

				for (Enumeration<?> en = attributeSet.getAttributeNames(); en.hasMoreElements();) {
					Object attributeName = en.nextElement();
					Object attributeValue = attributeSet.getAttribute(attributeName);

					if (attributeName == null) {
						continue;
					}

					if (attributeName == Attribute.STYLE) {
						if (attributeValue != null && attributeValue.toString().trim().length() > 0) {
							computedStyle.append(attributeValue.toString().trim());
						}
					} else if (attributeName == Attribute.CLASS) {
						if (attributeValue != null && attributeValue.toString().trim().length() > 0 && !attributeValue.equals("no-style")) {
							map.put((paragraphLevel ? PSTYLEKEY : RSTYLEKEY), "<w:" + (paragraphLevel ? "p" : "r") + "Style w:val=\""
									+ attributeValue.toString().trim() + "\"/>");
						}
					} else if (tag == HTML.Tag.FONT) {
						if (attributeName == Attribute.SIZE) {
							if (attributeValue != null) {
								computedStyle.append(CSS.Attribute.FONT_SIZE + ": "
										+ HTMLUtils.getFontSizeInPointsFromFontValue(attributeValue.toString()) + "pt");
							}
						} else if (attributeName == Attribute.COLOR) {
							computedStyle.append(CSS.Attribute.COLOR + ": " + attributeValue);
						}
					} else if (tag == HTML.Tag.P) {
						if (attributeName == Attribute.ALIGN) {
							computedStyle.append(CSS.Attribute.TEXT_ALIGN + ": " + attributeValue);
						}
					}

					if (computedStyle.length() > 0 && !computedStyle.toString().endsWith(";")) {
						computedStyle.append(";");
					}
				}

				map.putAll(getMapForStyle(computedStyle.toString()));

				if (tag == HTML.Tag.H1) {
					map.put(PSTYLEKEY, "<w:pStyle w:val=\"Heading1\"/>");
				} else if (tag == HTML.Tag.H2) {
					map.put(PSTYLEKEY, "<w:pStyle w:val=\"Heading2\"/>");
				} else if (tag == HTML.Tag.H3) {
					map.put(PSTYLEKEY, "<w:pStyle w:val=\"Heading3\"/>");
				} else if (tag == HTML.Tag.H4) {
					map.put(PSTYLEKEY, "<w:pStyle w:val=\"Heading4\"/>");
				} else if (tag == HTML.Tag.H5) {
					map.put(PSTYLEKEY, "<w:pStyle w:val=\"Heading5\"/>");
				} else if (tag == HTML.Tag.H6) {
					map.put(PSTYLEKEY, "<w:pStyle w:val=\"Heading6\"/>");
				}

				propertiesStack.push(map);
			}

			public void pop() {
				if (propertiesStack.size() > 0) {
					propertiesStack.pop();
				}
			}

			public String getCurrentDocxProperties() {
				Map<String, String> currentProperties = new HashMap<String, String>();

				// Remove double equals properties and keep only the last one
				for (Map<String, String> propertiesMap : propertiesStack) {
					for (String attribute : propertiesMap.keySet()) {
						currentProperties.put(attribute, propertiesMap.get(attribute));
					}
				}

				// Hack for link style -> only applies if no rStyle set
				if (currentProperties.containsKey(LINKSTYLEKEY) && currentProperties.containsKey(RSTYLEKEY)) {
					currentProperties.remove(LINKSTYLEKEY);
				}

				StringBuilder propertiesString = new StringBuilder();
				for (String property : currentProperties.values()) {
					propertiesString.append(StringUtils.LINE_SEPARATOR + property);
				}

				return propertiesString.toString();
			}

			private Map<String, String> getMapForStyle(String styleValue) {
				Map<String, String> map = new HashMap<String, String>();

				for (String styleEffect : styleValue.split(";")) {
					styleEffect = styleEffect.trim();

					int indexOf = styleEffect.indexOf(':');
					if (indexOf == -1 || styleEffect.length() <= indexOf + 1) {
						continue;
					}

					String effectKey = styleEffect.substring(0, indexOf).trim();
					String effectValue = styleEffect.substring(indexOf + 1).trim();

					CSS.Attribute attribute = CSS.getAttribute(effectKey);

					if (attribute == null) {
						continue;
					}

					if (attribute == CSS.Attribute.COLOR) {
						Color color = HTMLUtils.extractColorFromString(effectValue);
						if (color != null) {
							map.put(attribute.toString(), StringUtils.LINE_SEPARATOR + "<w:color w:val=\"" + HTMLUtils.toHexString(color)
									+ "\"/>");
						}
					} else if (attribute == CSS.Attribute.BACKGROUND_COLOR) {
						Color color = HTMLUtils.extractColorFromString(effectValue);
						if (color != null) {
							map.put(attribute.toString(), StringUtils.LINE_SEPARATOR + "<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\""
									+ HTMLUtils.toHexString(color) + "\" />");
						}
					} else if (attribute == CSS.Attribute.FONT_SIZE) {
						Integer sizeInPoints = HTMLUtils.getFontSizeInPoints(effectValue);
						if (sizeInPoints != null) {
							map.put(attribute.toString(), StringUtils.LINE_SEPARATOR + "<w:sz w:val=\"" + sizeInPoints * 2 + "\"/>"
									+ StringUtils.LINE_SEPARATOR + "<w:szCs w:val=\"" + sizeInPoints * 2 + "\"/>");
						}
					} else if (attribute == CSS.Attribute.FONT_WEIGHT) {
						if (effectValue.equals("bold")) {
							map.put(attribute.toString(), StringUtils.LINE_SEPARATOR + "<w:b/>");
						}
					} else if (attribute == CSS.Attribute.TEXT_DECORATION) {
						if (effectValue.equals("underline")) {
							map.put(attribute.toString(), StringUtils.LINE_SEPARATOR + "<w:u w:val=\"single\"/>");
						}
					} else if (attribute == CSS.Attribute.FONT_STYLE) {
						if (effectValue.equals("italic")) {
							map.put(attribute.toString(), StringUtils.LINE_SEPARATOR + "<w:i/>");
						}
					} else if (attribute == CSS.Attribute.TEXT_ALIGN) {
						String alignValue = null;
						if ("left".equals(effectValue)) {
							alignValue = "left";
						} else if ("right".equals(effectValue)) {
							alignValue = "right";
						} else if ("center".equals(effectValue)) {
							alignValue = "center";
						} else if ("justify".equals(effectValue)) {
							alignValue = "both";
						}

						if (alignValue != null) {
							map.put(attribute.toString(), StringUtils.LINE_SEPARATOR + "<w:jc w:val=\"" + alignValue + "\"/>");
						}
					} else if (attribute == CSS.Attribute.BACKGROUND) { // Get only the color if any
						for (String backgroundItem : effectValue.split(" ")) {
							Color color = HTMLUtils.extractColorFromString(backgroundItem.trim());
							if (color != null) {
								map.put(CSS.Attribute.BACKGROUND_COLOR.toString(), StringUtils.LINE_SEPARATOR
										+ "<w:shd w:val=\"clear\" w:color=\"auto\" w:fill=\"" + HTMLUtils.toHexString(color) + "\" />");
							}
						}
					}
				}

				return map;
			}
		}
	}
}
