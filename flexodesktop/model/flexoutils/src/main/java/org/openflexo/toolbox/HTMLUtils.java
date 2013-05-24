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

import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;

public class HTMLUtils {
	public static final String LATEX_BACKSLASH = "\\textbackslash";

	private static final String START_HTML_TAG = "<HTML>";
	private static final String END_HTML_TAG = "</HTML>";
	private static final String START_HEAD_TAG = "<HEAD>";
	private static final String END_HEAD_TAG = "</HEAD>";
	private static final String START_BODY_TAG = "<BODY>";
	private static final String END_BODY_TAG = "</BODY>";

	private static final String START_BOLD_TAG = "<B>";
	private static final String END_BOLD_TAG = "</B>";
	private static final String START_ITALIC_TAG = "<I>";
	private static final String END_ITALIC_TAG = "</I>";
	private static final String START_UNDERLINE_TAG = "<U>";
	private static final String END_UNDERLINE_TAG = "</U>";
	private static final String START_UNORDERED_TAG = "<UL>";
	private static final String END_UNORDERED_TAG = "</UL>";
	private static final String START_ORDERED_TAG = "<OL>";
	private static final String END_ORDERED_TAG = "</OL>";
	private static final String START_LIST_ITEM_TAG = "<LI>";
	private static final String END_LIST_ITEM_TAG = "</LI>";

	private static final String BREAK = "<BR>";
	private static final String XHTML_BREAK = "<BR/>";
	private static final String START_PARAGRAPH_TAG = "<P>";
	private static final String END_PARAGRAPH_TAG = "</P>";

	private static final String SMALLER = "&lt;";
	private static final String GREATER = "&gt;";
	private static final String AMPERSAND = "&amp;";
	private static final String QUOTE = "&quot;";
	private static final String a_GRAVE = "&agrave;";
	private static final String A_GRAVE = "&Agrave;";
	private static final String a_CIRC = "&acirc;";
	private static final String A_CIRC = "&Acirc;";
	private static final String a_UML = "&auml;";
	private static final String A_UML = "&Auml;";
	private static final String a_RING = "&aring;";
	private static final String A_RING = "&Aring;";
	private static final String ae_LIGATURE = "&aelig;";
	private static final String AE_LIGATURE = "&AElig;";
	private static final String c_CEDILLA = "&ccedil;";
	private static final String C_CEDILLA = "&Ccedil;";
	private static final String e_ACUTE = "&eacute;";
	private static final String E_ACUTE = "&Eacute;";
	private static final String e_GRAVE = "&egrave;";
	private static final String E_GRAVE = "&Egrave;";
	private static final String e_CIRC = "&ecirc;";
	private static final String E_CIRC = "&Ecirc;";
	private static final String e_UML = "&euml;";
	private static final String E_UML = "&Euml;";
	private static final String i_UML = "&iuml;";
	private static final String I_UML = "&Iuml;";
	private static final String o_CIRC = "&ocirc;";
	private static final String O_CIRC = "&Ocirc;";
	private static final String o_UML = "&ouml;";
	private static final String O_UML = "&Ouml;";
	private static final String u_GRAVE = "&ugrave;";
	private static final String U_GRAVE = "&Ugrave;";
	private static final String u_CIRC = "&ucirc;";
	private static final String U_CIRC = "&Ucirc;";
	private static final String u_UML = "&uuml;";
	private static final String U_UML = "&Uuml;";
	private static final String REGISTERED = "&reg;";
	private static final String COPYRIGHT = "&copy;";
	private static final String EURO = "&euro;";
	private static final String NON_BREAKING_SPACE = "&nbsp;";
	private static final String FOOTNOTE_TAG = "footnote";

	private static final String EMPTY_PARAGRAPH_REGEXP = "\\s*" + START_PARAGRAPH_TAG + "\\s*" + END_PARAGRAPH_TAG + "\\s*";
	private static final Pattern EMPTY_PARAGRAPH_PATTERN = Pattern.compile(EMPTY_PARAGRAPH_REGEXP, Pattern.CASE_INSENSITIVE);

	public static String convertHTML2Latex(final String htmlString) {
		if (htmlString == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();

		HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {

			private boolean withinHead = false;
			private boolean withinA = false;
			private boolean lastTagWasPar = false;

			@Override
			public void handleText(char[] data, int pos) {
				String convertedData = convertManuallyHTML2Latex(new String(data));
				if (lastTagWasPar) {
					if (convertedData.trim().length() > 0) {
						sb.append("\\par ");
					}
					lastTagWasPar = false;
				}
				sb.append(convertedData);
			}

			@Override
			public void handleComment(char[] data, int pos) {
			}

			@Override
			public void handleEndTag(Tag t, int pos) {
				if (t == HTML.Tag.HEAD) {
					withinHead = false;
					return;
				}
				if (t == HTML.Tag.A) {
					if (withinA) {
						withinA = false;
						sb.append("}");
					}
				}
				if (t == HTML.Tag.B) {
					sb.append("}");
				} else if (t == HTML.Tag.I) {
					sb.append("}");
				} else if (t == HTML.Tag.U) {
					sb.append("}");
				} else if (t == HTML.Tag.P) {
					;
				} else if (t == HTML.Tag.UL) {
					sb.append("\\myitemsep\\end{itemize}\n");
				} else if (t == HTML.Tag.OL) {
					sb.append("\\myitemsep\\end{enumerate}\n");
				} else if (t == HTML.Tag.LI) {
					;
				} else if (t == HTML.Tag.FONT) {
					sb.append("}");
				} else {
					handleUnknownEndTag(t, pos);
				}
			}

			@Override
			public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
				if ('/' == htmlString.charAt(pos + 1)) {
					handleEndTag(t, pos);
				} else {
					handleStartTag(t, a, pos);
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
				if (t != HTML.Tag.P) {
					lastTagWasPar = false;
				}
				if (t == HTML.Tag.B) {
					sb.append("\\textbf{");
				} else if (t == HTML.Tag.I) {
					sb.append("\\textit{");
				} else if (t == HTML.Tag.A) {
					if (a.getAttribute(HTML.Attribute.HREF) != null) {
						String href = (String) a.getAttribute(HTML.Attribute.HREF);
						if (href.startsWith("#")) {
							sb.append("\\hyperlink{" + href.substring(1) + "}{");
						} else {
							sb.append("\\href{" + href + "}{");
						}
						withinA = true;
					} else if (a.getAttribute(HTML.Attribute.NAME) != null) {
						String name = (String) a.getAttribute(HTML.Attribute.NAME);
						sb.append("\\hypertarget{" + name + "}{}");
					}
				} else if (t == HTML.Tag.U) {
					sb.append("\\underline{");
				} else if (t == HTML.Tag.P) {
					sb.append("\n\n");
					// lastTagWasPar = true;
				} else if (t == HTML.Tag.BR) {
					sb.append("\\par ").append(StringUtils.LINE_SEPARATOR);
				} else if (t == HTML.Tag.UL) {
					sb.append("\\doitemsep\\begin{itemize}").append(StringUtils.LINE_SEPARATOR);
				} else if (t == HTML.Tag.OL) {
					sb.append("\\doitemsep\\begin{enumerate}").append(StringUtils.LINE_SEPARATOR);
				} else if (t == HTML.Tag.LI) {
					sb.append("\\item ");
				} else if (t == HTML.Tag.FONT) {
					String size = (String) a.getAttribute(HTML.Attribute.SIZE);
					String latexSize = null;
					if (size != null) {
						try {
							int sizeInt = Integer.parseInt(size);
							switch (sizeInt) {
							case 1:
								latexSize = "{\\footnotesize ";
								break;
							case 2:
								latexSize = "{\\small ";
								break;
							case 3:
								latexSize = "{\\normalsize ";
								break;
							case 4:
								latexSize = "{\\large ";
								break;
							case 5:
								latexSize = "{\\Large ";
								break;
							case 6:
								latexSize = "{\\LARGE ";
								break;
							case 7:
								latexSize = "{\\huge ";
								break;
							}
							if (sizeInt > 7) {
								latexSize = "{\\Huge ";
							}
						} catch (NumberFormatException e) {
							// ignore, test on latexSize will cover it
						}
					}
					if (latexSize == null) {
						latexSize = "{"; // always put a { because the end tag will close the }
					}
					sb.append(latexSize);
				} else if (t == HTML.Tag.IMG) {
					String src = (String) a.getAttribute(HTML.Attribute.SRC);
					String width = (String) a.getAttribute(HTML.Attribute.WIDTH);
					String height = (String) a.getAttribute(HTML.Attribute.HEIGHT);
					if (src != null && !withinHead) {
						sb.append("\\includegraphics");
						if (height != null || width != null) {
							sb.append("[");
							if (width != null) {
								sb.append("width=");
								if (width.indexOf("%") > -1) {
									try {
										int widthValue = Integer.parseInt(width.substring(0, width.indexOf("%")).trim());
										sb.append(widthValue / 100);
									} catch (NumberFormatException e) {
										e.printStackTrace();
										sb.append("1");
									}
								} else {
									sb.append(width.trim()).append("pt");
								}
							}
							if (height != null) {
								if (width != null) {
									sb.append(',');
								}
								sb.append("height=");
								if (height.indexOf("%") > -1) {
									try {
										int heightValue = Integer.parseInt(height.substring(0, height.indexOf("%")).trim());
										sb.append(heightValue / 100);
									} catch (NumberFormatException e) {
										e.printStackTrace();
										sb.append("1");
									}
								} else {
									sb.append(height.trim()).append("pt");
								}
							}
							sb.append("]");
						}
						sb.append("{Figures/").append(src).append("}");
					} else if (withinHead) {

					} else {
						System.err.println("WARNING:\tI could not find 'src' attribute");
					}
				} else {
					handleUnknownStartTag(t, pos);
				}

			}

			private void handleUnknownStartTag(Tag t, int pos) {
				if (FOOTNOTE_TAG.equals(t.toString())) {
					sb.append("\\footnote{");
				}

			}

			private void handleUnknownEndTag(Tag t, int pos) {
				if (FOOTNOTE_TAG.equals(t.toString())) {
					sb.append("}");
				}
			}
		};
		Reader reader = new StringReader(htmlString);
		try {
			new ParserDelegator().parse(reader, callback, false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return LatexUtils.fixQuotesAndLineReturns(sb.toString());
	}

	/**
	 * 
	 * @param htmlString
	 * @return
	 */
	protected static String convertManuallyHTML2Latex(String htmlString) {
		boolean withinHead = false;
		StringBuilder sb = new StringBuilder(htmlString.length());
		for (int i = 0; i < htmlString.length(); i++) {
			char c = htmlString.charAt(i);
			switch (c) {
			case '~':
				sb.append("$\\sim$");
				break;
			case '{':
			case '}':
			case '$':
			case '%':
			case '#':
				sb.append("\\").append(c);
				break;
			case '\\':
				sb.append(LATEX_BACKSLASH).append("{}");
				break;
			case '<':
				if (htmlString.regionMatches(true, i, START_HTML_TAG, 0, START_HTML_TAG.length())) {
					i += START_HTML_TAG.length();
				} else if (htmlString.regionMatches(true, i, END_HTML_TAG, 0, END_HTML_TAG.length())) {
					i += END_HTML_TAG.length();
				} else if (htmlString.regionMatches(true, i, START_HEAD_TAG, 0, START_HEAD_TAG.length())) {
					i += START_HEAD_TAG.length();
					withinHead = true;
				} else if (htmlString.regionMatches(true, i, END_HEAD_TAG, 0, END_HEAD_TAG.length())) {
					i += END_HEAD_TAG.length();
					withinHead = false;
				} else if (htmlString.regionMatches(true, i, START_BODY_TAG, 0, START_BODY_TAG.length())) {
					i += START_BODY_TAG.length();
				} else if (htmlString.regionMatches(true, i, END_BODY_TAG, 0, END_BODY_TAG.length())) {
					i += END_BODY_TAG.length();
				} else if (htmlString.regionMatches(true, i, START_BOLD_TAG, 0, START_BOLD_TAG.length())) {
					i += START_BOLD_TAG.length();
					if (!withinHead) {
						sb.append("\\textbf{");
					}
				} else if (htmlString.regionMatches(true, i, END_BOLD_TAG, 0, END_BOLD_TAG.length())) {
					i += END_BOLD_TAG.length();
					if (!withinHead) {
						sb.append("}");
					}
				} else if (htmlString.regionMatches(true, i, START_ITALIC_TAG, 0, START_ITALIC_TAG.length())) {
					i += START_ITALIC_TAG.length();
					if (!withinHead) {
						sb.append("\\textit{");
					}
				} else if (htmlString.regionMatches(true, i, END_ITALIC_TAG, 0, END_ITALIC_TAG.length())) {
					i += END_ITALIC_TAG.length();
					if (!withinHead) {
						sb.append("}");
					}
				} else if (htmlString.regionMatches(true, i, START_UNDERLINE_TAG, 0, START_UNDERLINE_TAG.length())) {
					i += START_UNDERLINE_TAG.length();
					if (!withinHead) {
						sb.append("\\underline{");
					}
				} else if (htmlString.regionMatches(true, i, END_UNDERLINE_TAG, 0, END_UNDERLINE_TAG.length())) {
					i += END_UNDERLINE_TAG.length();
					if (!withinHead) {
						sb.append("}");
					}
				} else if (htmlString.regionMatches(true, i, START_UNORDERED_TAG, 0, START_UNORDERED_TAG.length())) {
					i += START_UNORDERED_TAG.length();
					if (!withinHead) {
						sb.append("\\begin{itemize}");
					}
				} else if (htmlString.regionMatches(true, i, END_UNORDERED_TAG, 0, END_UNORDERED_TAG.length())) {
					i += END_UNORDERED_TAG.length();
					if (!withinHead) {
						sb.append("\\end{itemize}");
					}
				} else if (htmlString.regionMatches(true, i, START_ORDERED_TAG, 0, START_ORDERED_TAG.length())) {
					i += START_ORDERED_TAG.length();
					if (!withinHead) {
						sb.append("\\begin{enumerate}");
					}
				} else if (htmlString.regionMatches(true, i, END_ORDERED_TAG, 0, END_ORDERED_TAG.length())) {
					i += END_ORDERED_TAG.length();
					if (!withinHead) {
						sb.append("\\end{enumerate}");
					}
				} else if (htmlString.regionMatches(true, i, START_LIST_ITEM_TAG, 0, START_LIST_ITEM_TAG.length())) {
					i += START_LIST_ITEM_TAG.length();
					if (!withinHead) {
						sb.append("\\item ");
					}
				} else if (htmlString.regionMatches(true, i, END_LIST_ITEM_TAG, 0, END_LIST_ITEM_TAG.length())) {
					i += END_LIST_ITEM_TAG.length();
				} else if (htmlString.regionMatches(true, i, BREAK, 0, BREAK.length())) {
					i += BREAK.length();
					if (!withinHead) {
						sb.append("\\\\");
					}
				} else if (htmlString.regionMatches(true, i, XHTML_BREAK, 0, XHTML_BREAK.length())) {
					i += XHTML_BREAK.length();
					if (!withinHead) {
						sb.append("\\\\");
					}
				} else if (htmlString.regionMatches(true, i, START_PARAGRAPH_TAG, 0, START_PARAGRAPH_TAG.length())) {
					i += START_PARAGRAPH_TAG.length();
					if (!withinHead) {
						sb.append("\\par ");
					}
				} else if (htmlString.regionMatches(true, i, END_PARAGRAPH_TAG, 0, END_PARAGRAPH_TAG.length())) {
					i += END_PARAGRAPH_TAG.length();
				} else if (htmlString.regionMatches(true, i, "IMG", 0, "IMG".length())) {
					int j = i + 1;
					for (; j < htmlString.length(); j++) {
						if (htmlString.charAt(j) == '>') {
							break;
						}
					}
					String img = htmlString.substring(i, j + 1);
					String src = extractImageSource(img);
					String width = extractImageWidth(img);
					String height = extractImageHeight(img);
					if (src != null && !withinHead) {
						sb.append("\\includegraphics");
						if (height != null || width != null) {
							sb.append("[");
							if (width != null) {
								sb.append("width=");
								if (width.indexOf("%") > -1) {
									try {
										int widthValue = Integer.parseInt(width.substring(0, width.indexOf("%")).trim());
										sb.append(widthValue / 100);
									} catch (NumberFormatException e) {
										e.printStackTrace();
										sb.append("1");
									}
								} else {
									sb.append(width.trim()).append("pt");
								}
							}
							if (height != null) {
								if (width != null) {
									sb.append(',');
								}
								sb.append("height=");
								if (height.indexOf("%") > -1) {
									try {
										int heightValue = Integer.parseInt(height.substring(0, height.indexOf("%")).trim());
										sb.append(heightValue / 100);
									} catch (NumberFormatException e) {
										e.printStackTrace();
										sb.append("1");
									}
								} else {
									sb.append(height.trim()).append("pt");
								}
							}
							sb.append("]");
						}
						sb.append("{Figures/").append(src).append("}");
					} else if (withinHead) {

					} else {
						System.err.println("WARNING:\tI could not find 'src' attribute within: " + img);
					}
					i += j - i;
				}
				break;
			case '&':
				if (htmlString.regionMatches(true, i, SMALLER, 0, SMALLER.length())) {
					i += SMALLER.length();
					if (!withinHead) {
						sb.append("\\textsmaller");
					}
				} else if (htmlString.regionMatches(true, i, GREATER, 0, GREATER.length())) {
					i += GREATER.length();
					if (!withinHead) {
						sb.append("\\textgreater");
					}
				} else if (htmlString.regionMatches(true, i, AMPERSAND, 0, AMPERSAND.length())) {
					i += AMPERSAND.length();
					if (!withinHead) {
						sb.append("\\&");
					}
				} else if (htmlString.regionMatches(true, i, QUOTE, 0, QUOTE.length())) {
					i += QUOTE.length();
					if (!withinHead) {
						sb.append("\"");
					}
				} else if (htmlString.regionMatches(true, i, a_GRAVE, 0, a_GRAVE.length())) {
					i += a_GRAVE.length();
					if (!withinHead) {
						sb.append("à");
					}
				} else if (htmlString.regionMatches(true, i, A_GRAVE, 0, A_GRAVE.length())) {
					i += A_GRAVE.length();
					if (!withinHead) {
						sb.append("À");
					}
				} else if (htmlString.regionMatches(true, i, a_CIRC, 0, a_CIRC.length())) {
					i += a_CIRC.length();
					if (!withinHead) {
						sb.append("â");
					}
				} else if (htmlString.regionMatches(true, i, A_CIRC, 0, A_CIRC.length())) {
					i += A_CIRC.length();
					if (!withinHead) {
						sb.append("Â");
					}
				} else if (htmlString.regionMatches(true, i, a_UML, 0, a_UML.length())) {
					i += a_UML.length();
					if (!withinHead) {
						sb.append("ä");
					}
				} else if (htmlString.regionMatches(true, i, A_UML, 0, A_UML.length())) {
					i += A_UML.length();
					if (!withinHead) {
						sb.append("Ä");
					}
				} else if (htmlString.regionMatches(true, i, a_RING, 0, a_RING.length())) {
					i += a_RING.length();
					if (!withinHead) {
						sb.append("å");
					}
				} else if (htmlString.regionMatches(true, i, A_RING, 0, A_RING.length())) {
					i += A_RING.length();
					if (!withinHead) {
						sb.append("Å");
					}
				} else if (htmlString.regionMatches(true, i, ae_LIGATURE, 0, ae_LIGATURE.length())) {
					i += ae_LIGATURE.length();
					if (!withinHead) {
						sb.append("æ");
					}
				} else if (htmlString.regionMatches(true, i, AE_LIGATURE, 0, AE_LIGATURE.length())) {
					i += AE_LIGATURE.length();
					if (!withinHead) {
						sb.append("Æ");
					}
				} else if (htmlString.regionMatches(true, i, c_CEDILLA, 0, c_CEDILLA.length())) {
					i += c_CEDILLA.length();
					if (!withinHead) {
						sb.append("ç");
					}
				} else if (htmlString.regionMatches(true, i, C_CEDILLA, 0, C_CEDILLA.length())) {
					i += C_CEDILLA.length();
					if (!withinHead) {
						sb.append("Ç");
					}
				} else if (htmlString.regionMatches(true, i, e_ACUTE, 0, e_ACUTE.length())) {
					i += e_ACUTE.length();
					if (!withinHead) {
						sb.append("é");
					}
				} else if (htmlString.regionMatches(true, i, E_ACUTE, 0, E_ACUTE.length())) {
					i += E_ACUTE.length();
					if (!withinHead) {
						sb.append("É");
					}
				} else if (htmlString.regionMatches(true, i, e_GRAVE, 0, e_GRAVE.length())) {
					i += e_GRAVE.length();
					if (!withinHead) {
						sb.append("è");
					}
				} else if (htmlString.regionMatches(true, i, E_GRAVE, 0, E_GRAVE.length())) {
					i += E_GRAVE.length();
					if (!withinHead) {
						sb.append("É");
					}
				} else if (htmlString.regionMatches(true, i, e_CIRC, 0, e_CIRC.length())) {
					i += e_CIRC.length();
					if (!withinHead) {
						sb.append("ê");
					}
				} else if (htmlString.regionMatches(true, i, E_CIRC, 0, E_CIRC.length())) {
					i += E_CIRC.length();
					if (!withinHead) {
						sb.append("È");
					}
				} else if (htmlString.regionMatches(true, i, e_UML, 0, e_UML.length())) {
					i += e_UML.length();
					if (!withinHead) {
						sb.append("ë");
					}
				} else if (htmlString.regionMatches(true, i, E_UML, 0, E_UML.length())) {
					i += E_UML.length();
					if (!withinHead) {
						sb.append("Ë");
					}
				} else if (htmlString.regionMatches(true, i, i_UML, 0, i_UML.length())) {
					i += i_UML.length();
					if (!withinHead) {
						sb.append("ë");
					}
				} else if (htmlString.regionMatches(true, i, I_UML, 0, I_UML.length())) {
					i += I_UML.length();
					if (!withinHead) {
						sb.append("Ë");
					}
				} else if (htmlString.regionMatches(true, i, o_CIRC, 0, o_CIRC.length())) {
					i += o_CIRC.length();
					if (!withinHead) {
						sb.append("ô");
					}
				} else if (htmlString.regionMatches(true, i, O_CIRC, 0, O_CIRC.length())) {
					i += O_CIRC.length();
					if (!withinHead) {
						sb.append("Ô");
					}
				} else if (htmlString.regionMatches(true, i, o_UML, 0, o_UML.length())) {
					i += o_UML.length();
					if (!withinHead) {
						sb.append("ö");
					}
				} else if (htmlString.regionMatches(true, i, O_UML, 0, O_UML.length())) {
					i += O_UML.length();
					if (!withinHead) {
						sb.append("Ö");
					}
				} else if (htmlString.regionMatches(true, i, u_GRAVE, 0, u_GRAVE.length())) {
					i += u_GRAVE.length();
					if (!withinHead) {
						sb.append("ù");
					}
				} else if (htmlString.regionMatches(true, i, U_GRAVE, 0, U_GRAVE.length())) {
					i += U_GRAVE.length();
					if (!withinHead) {
						sb.append("Ù");
					}
				} else if (htmlString.regionMatches(true, i, u_CIRC, 0, u_CIRC.length())) {
					i += u_CIRC.length();
					if (!withinHead) {
						sb.append("û");
					}
				} else if (htmlString.regionMatches(true, i, U_CIRC, 0, U_CIRC.length())) {
					i += U_CIRC.length();
					if (!withinHead) {
						sb.append("Ù");
					}
				} else if (htmlString.regionMatches(true, i, u_UML, 0, u_UML.length())) {
					i += u_UML.length();
					if (!withinHead) {
						sb.append("ü");
					}
				} else if (htmlString.regionMatches(true, i, U_UML, 0, U_UML.length())) {
					i += U_UML.length();
					if (!withinHead) {
						sb.append("Ü");
					}
				} else if (htmlString.regionMatches(true, i, REGISTERED, 0, REGISTERED.length())) {
					i += REGISTERED.length();
					if (!withinHead) {
						sb.append("\\textregistered");
					}
				} else if (htmlString.regionMatches(true, i, COPYRIGHT, 0, COPYRIGHT.length())) {
					i += COPYRIGHT.length();
					if (!withinHead) {
						sb.append("\\copyright");
					}
				} else if (htmlString.regionMatches(true, i, EURO, 0, EURO.length())) {
					i += EURO.length();
					if (!withinHead) {
						sb.append("€");
					}
				} else if (htmlString.regionMatches(true, i, NON_BREAKING_SPACE, 0, NON_BREAKING_SPACE.length())) {
					i += NON_BREAKING_SPACE.length();
					if (!withinHead) {
						sb.append("~");
					}
				} else {
					if (!withinHead) {
						if (i + 2 < htmlString.length() && htmlString.charAt(i + 1) == '#' && htmlString.indexOf(';', i + 1) > -1) {
							int entity = Integer.parseInt(htmlString.substring(i + 2, htmlString.indexOf(';', i + 1)));
							if (entity > 127) {
								sb.append((char) entity);
							} else {
								try {
									sb.append(new String(new byte[] { (byte) entity }, "ISO-8859-1"));
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							}
							i = htmlString.indexOf(';', i + 1);
							break;
						}
						sb.append("\\&");

					}
				}
				break;
			default:
				if (!withinHead) {
					sb.append(c);
				}
				break;
			}
		}
		return sb.toString();
	}

	private static String extractImageHeight(String img) {
		return extractAttributeNamed(img, "HEIGHT");
	}

	private static String extractImageWidth(String img) {
		return extractAttributeNamed(img, "WIDTH");
	}

	private static String extractImageSource(String img) {
		return extractAttributeNamed(img, "SRC");
	}

	private static String extractAttributeNamed(String tag, String attribute) {
		boolean backslash = false;
		boolean withinQuotes = false;
		for (int i = 0; i < tag.length(); i++) {
			char c = tag.charAt(i);
			switch (c) {
			case '\\':
				backslash = !backslash;
				break;
			case '"':
				if (!backslash) {
					withinQuotes = !withinQuotes;
				} else {
					backslash = false;
				}
				break;
			default:
				if (!backslash && !withinQuotes) {
					if (tag.regionMatches(true, i, attribute, 0, attribute.length())) {
						int j = i + attribute.length();
						for (; j < tag.length(); j++) {
							if (tag.charAt(j) == ' ' || tag.charAt(j) == '=' || tag.charAt(j) == '\t' || tag.charAt(j) == '\n'
									|| tag.charAt(j) == '\r') {
								continue;
							}
							break;
						}
						StringBuilder src = new StringBuilder();
						if (tag.charAt(j) == '"') {
							j++;
							for (; j < tag.length(); j++) {
								if (tag.charAt(j) != '"') {
									src.append(tag.charAt(j));
								} else {
									return src.toString();
								}
							}
						} else {
							for (; j < tag.length(); j++) {
								if (tag.charAt(j) != ' ' && tag.charAt(j) != '>' && tag.charAt(j) != '\t' && tag.charAt(j) != '\n'
										&& tag.charAt(j) != '\r') {
									src.append(tag.charAt(j));
								} else {
									return src.toString();
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static String extractBodyContent(String html) {
		return extractBodyContent(html, false);
	}

	public static String extractBodyContent(String html, boolean returnHtmlIfNoBodyFound) {
		if (html == null) {
			return null;
		}

		String htmlUpperCase = html.toUpperCase();
		int startBodyIndex = htmlUpperCase.indexOf(START_BODY_TAG);

		if (startBodyIndex == -1 || html.length() < startBodyIndex + START_BODY_TAG.length() + 1) {
			return returnHtmlIfNoBodyFound ? html : null;
		}
		startBodyIndex = startBodyIndex + START_BODY_TAG.length() + 1;
		int endBodyIndex = htmlUpperCase.indexOf(END_BODY_TAG, startBodyIndex);

		if (endBodyIndex == -1) {
			return html.substring(startBodyIndex);
		}

		return html.substring(startBodyIndex, endBodyIndex);
	}

	public static String escapeStringForHTML(String s, boolean removeNewLine) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int n = s.length();
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("&#146;");
				break;
			case '\n':
				if (!removeNewLine) {
					sb.append("<br/>");
				} else {
					sb.append(' ');
				}
				break;
			case '\r':
				break;
			case 'à':
				sb.append("&agrave;");
				break;
			case 'À':
				sb.append("&Agrave;");
				break;
			case 'â':
				sb.append("&acirc;");
				break;
			case 'Â':
				sb.append("&Acirc;");
				break;
			case 'ä':
				sb.append("&auml;");
				break;
			case 'Ä':
				sb.append("&Auml;");
				break;
			case 'å':
				sb.append("&aring;");
				break;
			case 'Å':
				sb.append("&Aring;");
				break;
			case 'æ':
				sb.append("&aelig;");
				break;
			case 'Æ':
				sb.append("&AElig;");
				break;
			case 'ç':
				sb.append("&ccedil;");
				break;
			case 'Ç':
				sb.append("&Ccedil;");
				break;
			case 'é':
				sb.append("&eacute;");
				break;
			case 'É':
				sb.append("&Eacute;");
				break;
			case 'è':
				sb.append("&egrave;");
				break;
			case 'È':
				sb.append("&Egrave;");
				break;
			case 'ê':
				sb.append("&ecirc;");
				break;
			case 'Ê':
				sb.append("&Ecirc;");
				break;
			case 'ë':
				sb.append("&euml;");
				break;
			case 'Ë':
				sb.append("&Euml;");
				break;
			case 'ï':
				sb.append("&iuml;");
				break;
			case 'Ï':
				sb.append("&Iuml;");
				break;
			case 'ô':
				sb.append("&ocirc;");
				break;
			case 'Ô':
				sb.append("&Ocirc;");
				break;
			case 'ö':
				sb.append("&ouml;");
				break;
			case 'Ö':
				sb.append("&Ouml;");
				break;
			case 'ø':
				sb.append("&oslash;");
				break;
			case 'Ø':
				sb.append("&Oslash;");
				break;
			case 'ß':
				sb.append("&szlig;");
				break;
			case 'ù':
				sb.append("&ugrave;");
				break;
			case 'Ù':
				sb.append("&Ugrave;");
				break;
			case 'û':
				sb.append("&ucirc;");
				break;
			case 'Û':
				sb.append("&Ucirc;");
				break;
			case 'ü':
				sb.append("&uuml;");
				break;
			case 'Ü':
				sb.append("&Uuml;");
				break;
			case '®':
				sb.append("&reg;");
				break;
			case '©':
				sb.append("&copy;");
				break;
			case '€':
				sb.append("&euro;");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * Return a new string containing plain text defined in supplied HTML text. All HTML tags will be removed, but contents of them are kept
	 * in the returned string
	 * 
	 * @param s
	 * @param removeNewLine
	 * @return
	 */
	public static String convertHTMLToPlainText(String s, boolean removeNewLine) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		int n = s.length();
		boolean keepText = true;
		for (int i = 0; i < n; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				keepText = false;
				break;
			case '>':
				keepText = true;
				break;
			case '\n':
				if (!removeNewLine) {
					sb.append('\n');
				} else {
					sb.append(' ');
				}
				break;
			default:
				if (keepText) {
					sb.append(c);
				}
				break;
			}
		}
		return sb.toString();
	}

	public static boolean isEmtpyParagraph(String html) {
		return html != null && EMPTY_PARAGRAPH_PATTERN.matcher(html).matches();
	}

	public static void main(String[] args) {
		System.err.println(toHexString(Color.WHITE));
		System.err.println(toHexString(Color.BLACK));
		System.err.println(toHexString(new Color(1, 2, 3)));
	}

	public enum HTMLColors {

		indianred("#cd5c5c"), lightcoral("#f08080"), salmon("#fa8072"), darksalmon("#e9967a"), lightsalmon("#ffa07a"), crimson("#dc143c"), red(
				"#ff0000"), firebrick("#b22222"), darkred("#8b0000"), pink("#ffc0cb"), lightpink("#ffb6c1"), hotpink("#ff69b4"), deeppink(
				"#ff1493"), mediumvioletred("#c71585"), palevioletred("#db7093"), coral("#ff7f50"), tomato("#ff6347"), orangered("#ff4500"), darkorange(
				"#ff8c00"), orange("#ffa500"), gold("#ffd700"), yellow("#ffff00"), lightyellow("#ffffe0"), lemonchiffon("#fffacd"), lightgoldenrodyellow(
				"#fafad2"), papayawhip("#ffefd5"), moccasin("#ffe4b5"), peachpuff("#ffdab9"), palegoldenrod("#eee8aa"), khaki("#f0e68c"), darkkhaki(
				"#bdb76b"), lavender("#e6e6fa"), thistle("#d8bfd8"), plum("#dda0dd"), violet("#ee82ee"), orchid("#da70d6"), fuchsia(
				"#ff00ff"), magenta("#ff00ff"), mediumorchid("#ba55d3"), mediumpurple("#9370db"), amethyst("#9966cc"), blueviolet("#8a2be2"), darkviolet(
				"#9400d3"), darkorchid("#9932cc"), darkmagenta("#8b008b"), purple("#800080"), indigo("#4b0082"), slateblue("#6a5acd"), darkslateblue(
				"#483d8b"), mediumslateblue("#7b68ee"), greenyellow("#adff2f"), chartreuse("#7fff00"), lawngreen("#7cfc00"), lime("#00ff00"), limegreen(
				"#32cd32"), palegreen("#98fb98"), lightgreen("#90ee90"), mediumspringgreen("#00fa9a"), springgreen("#00ff7f"), mediumseagreen(
				"#3cb371"), seagreen("#2e8b57"), forestgreen("#228b22"), green("#008000"), darkgreen("#006400"), yellowgreen("#9acd32"), olivedrab(
				"#6b8e23"), olive("#808000"), darkolivegreen("#556b2f"), mediumaquamarine("#66cdaa"), darkseagreen("#8fbc8f"), lightseagreen(
				"#20b2aa"), darkcyan("#008b8b"), teal("#008080"), aqua("#00ffff"), cyan("#00ffff"), lightcyan("#e0ffff"), paleturquoise(
				"#afeeee"), aquamarine("#7fffd4"), turquoise("#40e0d0"), mediumturquoise("#48d1cc"), darkturquoise("#00ced1"), cadetblue(
				"#5f9ea0"), steelblue("#4682b4"), lightsteelblue("#b0c4de"), powderblue("#b0e0e6"), lightblue("#add8e6"), skyblue("#87ceeb"), lightskyblue(
				"#87cefa"), deepskyblue("#00bfff"), dodgerblue("#1e90ff"), cornflowerblue("#6495ed"), royalblue("#4169e1"), blue("#0000ff"), mediumblue(
				"#0000cd"), darkblue("#00008b"), navy("#000080"), midnightblue("#191970"), cornsilk("#fff8dc"), blanchedalmond("#ffebcd"), bisque(
				"#ffe4c4"), navajowhite("#ffdead"), wheat("#f5deb3"), burlywood("#deb887"), tan("#d2b48c"), rosybrown("#bc8f8f"), sandybrown(
				"#f4a460"), goldenrod("#daa520"), darkgoldenrod("#b8860b"), peru("#cd853f"), chocolate("#d2691e"), saddlebrown("#8b4513"), sienna(
				"#a0522d"), brown("#a52a2a"), maroon("#800000"), white("#ffffff"), snow("#fffafa"), honeydew("#f0fff0"), mintcream(
				"#f5fffa"), azure("#f0ffff"), aliceblue("#f0f8ff"), ghostwhite("#f8f8ff"), whitesmoke("#f5f5f5"), seashell("#fff5ee"), beige(
				"#f5f5dc"), oldlace("#fdf5e6"), floralwhite("#fffaf0"), ivory("#fffff0"), antiquewhite("#faebd7"), linen("#faf0e6"), lavenderblush(
				"#fff0f5"), mistyrose("#ffe4e1"), gainsboro("#dcdcdc"), lightgrey("#d3d3d3"), silver("#c0c0c0"), darkgray("#a9a9a9"), gray(
				"#808080"), dimgray("#696969"), lightslategray("#778899"), slategray("#708090"), darkslategray("#2f4f4f"), black("#000000");

		private String hexValue;

		private HTMLColors(String hexValue) {
			this.hexValue = hexValue;
		}

		public String getHexValue() {
			return hexValue;
		}

		public Color getColor() {
			return extractColorFromHexValue(getHexValue().substring(1));
		}
	}

	public static Color extractColorFromString(String color) {
		color = color.trim();
		try {
			if (color.startsWith("#")) {
				return extractColorFromHexValue(color.substring(1));
			} else if (color.toLowerCase().startsWith("rgb(") && color.indexOf(')') > -1) {
				color = color.substring(4, color.indexOf(')'));
				String[] rgb = color.split(",");
				if (rgb.length == 3) {
					if (color.indexOf('%') > -1) {
						return new Color(Float.valueOf(rgb[0]) / 100, Float.valueOf(rgb[1]) / 100, Float.valueOf(rgb[2]) / 100);
					} else {
						// Need to trim integers but not floats
						return new Color(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim()));
					}
				}
			} else {
				Color returned = extractColorFromHexValue(color);
				if (returned != null) {
					return returned;
				}
				try {
					return HTMLColors.valueOf(color.toLowerCase()).getColor();
				} catch (IllegalArgumentException e) {
					// Not an Html color
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		System.err.println("String color '" + color + "' is not a valid string color");
		return null;
	}

	private static Color extractColorFromHexValue(String color) {
		if (color.length() == 3) {
			color = String.valueOf(color.charAt(0)) + color.charAt(0) + color.charAt(1) + color.charAt(1) + color.charAt(2)
					+ color.charAt(2);
		}
		if (color.length() == 6 && color.matches("[0-9A-Fa-f]+")) {
			return new Color(Integer.parseInt(color.substring(0, 2), 16), Integer.parseInt(color.substring(2, 4), 16), Integer.parseInt(
					color.substring(4, 6), 16));
		}
		return null;
	}

	public static String toHexString(Color color) {
		return String.format("%1$02X%2$02X%3$02X", color.getRed(), color.getGreen(), color.getBlue());
	}

	public static String extractSourceFromEmbeddedTag(String htmlCode) {
		if (htmlCode == null || htmlCode.length() < 7) {
			return null;
		}
		if (!htmlCode.substring(0, 7).toLowerCase().startsWith("<html>")) {
			htmlCode = "<html>" + htmlCode + "</html>";
		}
		// 1. Let's try with XML parsers (it works most of the time and it is a lot more reliable as a parser)
		final String embeddedVideoCode = htmlCode;
		Reader reader = new StringReader(embeddedVideoCode.replaceAll("&", "&amp;"));
		try {
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(reader);
			Iterator objectIterator = document.getDescendants(new ElementFilter("object"));
			while (objectIterator.hasNext()) {
				Element e = (Element) objectIterator.next();
				for (Object param : e.getChildren("param")) {
					String paramName = ((Element) param).getAttributeValue("name");
					if (paramName != null && paramName.equals("movie")) {
						return ((Element) param).getAttributeValue("value");
					}
				}
			}
			Iterator embedIterator = document.getDescendants(new ElementFilter("embed"));
			while (embedIterator.hasNext()) {
				Element e = (Element) embedIterator.next();
				if (e.getAttributeValue("src") != null) {
					return e.getAttributeValue("src");
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 2. Ok XML parsers failed, let's see HTML ones
		final StringBuilder sb = new StringBuilder();
		HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
			@Override
			public void handleStartTag(Tag t, MutableAttributeSet a, int pos) {
				if (sb.length() > 0) {
					return;
				}
				if (t == Tag.OBJECT) {
					int indexOfParamMovie = embeddedVideoCode.indexOf("<param name=\"movie\"", pos);
					if (indexOfParamMovie > -1) {
						int indexOfMovieValue = embeddedVideoCode.indexOf("value=\"", indexOfParamMovie);
						if (indexOfMovieValue > -1) {
							int endIndexOfMovieValue = embeddedVideoCode.indexOf('"', indexOfMovieValue + 7);
							if (endIndexOfMovieValue > -1) {
								sb.append(embeddedVideoCode.substring(indexOfMovieValue + 7, endIndexOfMovieValue));
							}
						}
					}
				}
			}
		};
		reader = new StringReader(embeddedVideoCode);
		try {
			new ParserDelegator().parse(reader, callback, false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (sb.length() > 0) {
			return sb.toString();
		}
		// 3. Last resort: Manual parsing
		int indexOfEmbed = embeddedVideoCode.indexOf("<embed");
		if (indexOfEmbed > -1) {
			int indexOfSrc = embeddedVideoCode.indexOf("src=\"", indexOfEmbed);
			if (indexOfSrc > -1) {
				int endIndexOfSrc = embeddedVideoCode.indexOf('"', indexOfSrc + 5);
				if (endIndexOfSrc > -1) {
					return embeddedVideoCode.substring(indexOfSrc + 5, endIndexOfSrc);
				}
			}
		}
		return null;
	}

	public static Integer getFontSizeInPoints(String fontSizeWithUnit) {
		fontSizeWithUnit = fontSizeWithUnit.trim();

		DecimalFormat formatter = new DecimalFormat();
		DecimalFormatSymbols formatterSymbol = new DecimalFormatSymbols();
		formatterSymbol.setDecimalSeparator('.');
		formatter.setDecimalFormatSymbols(formatterSymbol);

		ParsePosition position = new ParsePosition(0);
		Number size = formatter.parse(fontSizeWithUnit, position);

		if (size == null) {
			return null;
		}

		String unit = "px";
		if (position.getIndex() < fontSizeWithUnit.length()) {
			unit = fontSizeWithUnit.substring(position.getIndex()).trim().toLowerCase();
		}

		if ("px".equals(unit)) {
			return new Double(size.doubleValue() * (92 / 72)).intValue(); // Round to transform px to points, 92 dpi usually, 1 inch = 72
		}
		// points
		if ("pt".equals(unit)) {
			return new Double(size.doubleValue()).intValue();
		}

		// Don't handle % or em

		return null;
	}

	public static Integer getFontSizeInPointsFromFontValue(String fontSizeString) {
		fontSizeString = fontSizeString.trim();
		try {
			int fontSize = Integer.parseInt(fontSizeString);
			return getFontSizeInPointsFromFontValue(fontSize);
		} catch (NumberFormatException e) {
			// Ok not a number, lets return null
		}

		return null;
	}

	public static Integer getFontSizeInPointsFromFontValue(int fontSize) {
		switch (fontSize) {
		case 1:
			return 8;
		case 2:
			return 10; // Default
		case 3:
			return 12;
		case 4:
			return 14;
		case 5:
			return 18;
		case 6:
			return 24;
		case 7:
			return 36;
		default:
			return 36 + fontSize;
		}
	}

	public static int getFontValueFromFontSizeInPoints(int fontSizeInPoints) {
		if (fontSizeInPoints <= 8) {
			return 1;
		}

		if (fontSizeInPoints <= 11) {
			return 2;
		}

		if (fontSizeInPoints <= 13) {
			return 3;
		}

		if (fontSizeInPoints <= 16) {
			return 4;
		}

		if (fontSizeInPoints <= 21) {
			return 5;
		}

		if (fontSizeInPoints <= 30) {
			return 6;
		}

		return 7;
	}
}
