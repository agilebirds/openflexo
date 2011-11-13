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
package org.openflexo.jedit;

/*
 * HTMLTokenMarker.java - HTML token marker
 * Copyright (C) 1998, 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

import javax.swing.text.Segment;

/**
 * VTL/HTML token marker.
 * 
 * @author gpolet
 */
public class VTLHTMLTokenMarker extends TokenMarker {
	public static final byte JAVASCRIPT = Token.INTERNAL_FIRST;

	public VTLHTMLTokenMarker() {
		this(true);
	}

	public VTLHTMLTokenMarker(boolean js) {
		this.js = js;
		keywords = JavaScriptTokenMarker.getKeywords();
	}

	@Override
	public byte markTokensImpl(byte token, Segment line, int lineIndex) {
		char[] array = line.array;
		int offset = line.offset;
		lastOffset = offset;
		lastKeyword = offset;
		int length = line.count + offset;
		boolean backslash = false;

		loop: for (int i = offset; i < length; i++) {
			int i1 = (i + 1);

			char c = array[i];
			if (c == '\\') {
				backslash = !backslash;
				continue;
			}
			switch (token) {
			case Token.NULL: // HTML text
				backslash = false;
				switch (c) {
				case '<':
					addToken(i - lastOffset, token);
					lastOffset = lastKeyword = i;
					if (SyntaxUtilities.regionMatches(false, line, i1, "!--")) {
						i += 3;
						token = Token.COMMENT1;
					} else if (js && SyntaxUtilities.regionMatches(true, line, i1, "script>")) {
						addToken(8, Token.KEYWORD3);
						lastOffset = lastKeyword = (i += 8);
						token = JAVASCRIPT;
					} else if (SyntaxUtilities.regionMatches(false, line, i1, "WEBOBJECT")) {
						i += 9;
						token = Token.WEBOBJECT;
					} else if (SyntaxUtilities.regionMatches(false, line, i1, "/WEBOBJECT")) {
						i += 10;
						token = Token.WEBOBJECT;
					} else if (SyntaxUtilities.regionMatches(false, line, i1, "TABLE")) {
						i += 5;
						token = Token.TABLE;
					} else if (SyntaxUtilities.regionMatches(false, line, i1, "/TABLE")) {
						i += 6;
						token = Token.TABLE;
					} else if (SyntaxUtilities.regionMatches(false, line, i1, "TR")) {
						i += 2;
						token = Token.TR;
					} else if (SyntaxUtilities.regionMatches(false, line, i1, "/TR")) {
						i += 3;
						token = Token.TR;
					} else if (SyntaxUtilities.regionMatches(false, line, i1, "TD")) {
						i += 2;
						token = Token.TD;
					} else if (SyntaxUtilities.regionMatches(false, line, i1, "/TD")) {
						i += 3;
						token = Token.TD;
					} else {
						token = Token.KEYWORD3;
					}
					break;
				case '&':
					addToken(i - lastOffset, token);
					lastOffset = lastKeyword = i;
					token = Token.KEYWORD2;
					break;
				case '#':
					addToken(i - lastOffset, token);
					lastOffset = lastKeyword = i;
					if (SyntaxUtilities.regionMatches(false, line, i1, "if")) {
						token = Token.KEYWORD5;
					} else if (SyntaxUtilities.regionMatches(true, line, i1, "else")) {
						token = Token.KEYWORD5;
					} else if (SyntaxUtilities.regionMatches(true, line, i1, "elseif")) {
						token = Token.KEYWORD5;
					} else if (SyntaxUtilities.regionMatches(true, line, i1, "foreach")) {
						token = Token.KEYWORD5;
					} else if (SyntaxUtilities.regionMatches(true, line, i1, "end")) {
						token = Token.KEYWORD5;
					} else if (SyntaxUtilities.regionMatches(true, line, i1, "set")) {
						token = Token.KEYWORD5;
					} else if (SyntaxUtilities.regionMatches(true, line, i1, "macro")) {
						token = Token.KEYWORD5;
					} else if (SyntaxUtilities.regionMatches(true, line, i1, "#") || SyntaxUtilities.regionMatches(true, line, i1, "*")) {
						if (length - i > 1) {
							switch (array[i1]) {
							case '*':
								addToken(i - lastOffset, token);
								lastOffset = lastKeyword = i;
								token = Token.COMMENT4;
								break;
							case '#':
								addToken(i - lastOffset, token);
								addToken(length - i, Token.COMMENT3);
								lastOffset = lastKeyword = length;
								break loop;
							}
						}
					} else {
						int tmp = i1;
						while (tmp < length && array[tmp] != '(') {
							tmp++;
						}
						if (tmp < length && array[tmp] == '(') {
							i = tmp - 1;
							token = Token.KEYWORD2;
						} else {
							token = Token.NULL;
						}
					}
					break;
				case '$':
					addToken(i - lastOffset, token);
					lastOffset = lastKeyword = i;
					int tmp = i1;
					boolean acceptable = true;
					int openingP = 0; // # Opening parenthesis
					int openingB = 0; // # Opening brackets
					boolean bracketsNotation = (tmp < length && array[tmp] == '{') || (tmp + 1 < length && array[tmp + 1] == '{');
					if (bracketsNotation) {
						while (tmp < length && acceptable) {
							if (array[tmp] == '{') {
								openingB++;
							} else if (array[tmp] == '}') {
								if (openingB > 1)
									openingB--;
								else
									acceptable = false;
							}
							tmp++;
						}
						tmp++;
					} else {
						while (tmp < length && acceptable) {
							if (Character.isLetterOrDigit(array[tmp]) || array[tmp] == '_' || array[tmp] == '.') {

							} else if (array[tmp] == '(') {
								openingP++;
							} else if (array[tmp] == '{') {
								openingB++;
							} else if (array[tmp] == ')') {
								if (openingP > 0)
									openingP--;
								else
									acceptable = false;
							} else if (array[tmp] == '}') {
								if (openingB > 0)
									openingB--;
								else
									acceptable = false;
							} else if (array[tmp] == '!' && array[tmp - 1] == '$') {
								// OK - silent reference notation
							} else
								acceptable = false;
							tmp++;
						}
					}
					if (tmp < length) {
						i = tmp - 3;
						token = Token.KEYWORD4;
					} else {
						addToken(tmp - i1, Token.KEYWORD4);
						lastOffset = lastKeyword = i = tmp - 1;
						token = Token.NULL;
					}
					break;
				}
				break;
			case Token.KEYWORD3: // Inside a tag
				backslash = false;
				if (c == '>') {
					addToken(i1 - lastOffset, token);
					lastOffset = lastKeyword = i1;
					token = Token.NULL;
				}
				break;
			case Token.KEYWORD2:
				backslash = false;
				if (c == ';') {// Inside an entity
					addToken(i1 - lastOffset, token);
					lastOffset = lastKeyword = i1;
					token = Token.NULL;
					break;
				} else if (c == '(') {// VTL macros
					backslash = false;
					addToken(i1 - lastOffset - 1, token);
					lastOffset = lastKeyword = i;
					token = Token.NULL;
					break;
				}
				break;
			case Token.COMMENT1: // Inside a comment
				backslash = false;
				if (SyntaxUtilities.regionMatches(false, line, i, "-->")) {
					addToken((i + 3) - lastOffset, token);
					lastOffset = lastKeyword = i + 3;
					token = Token.NULL;
				}
				break;
			case Token.WEBOBJECT: // Inside a comment
				backslash = false;
				if (SyntaxUtilities.regionMatches(false, line, i, ">")) {
					addToken((i + 1) - lastOffset, token);
					lastOffset = lastKeyword = i + 1;
					token = Token.NULL;
				}
				break;
			case Token.TABLE: // Inside a comment
				backslash = false;
				if (SyntaxUtilities.regionMatches(false, line, i, ">")) {
					addToken((i + 1) - lastOffset, token);
					lastOffset = lastKeyword = i + 1;
					token = Token.NULL;
				}
				break;
			case Token.TR: // Inside a comment
				backslash = false;
				if (SyntaxUtilities.regionMatches(false, line, i, ">")) {
					addToken((i + 1) - lastOffset, token);
					lastOffset = lastKeyword = i + 1;
					token = Token.NULL;
				}
				break;
			case Token.TD: // Inside a comment
				backslash = false;
				if (SyntaxUtilities.regionMatches(false, line, i, ">")) {
					addToken((i + 1) - lastOffset, token);
					lastOffset = lastKeyword = i + 1;
					token = Token.NULL;
				}
				break;
			case Token.KEYWORD5: // VTL directives
				backslash = false;
				if (SyntaxUtilities.regionMatches(false, line, i, "if")) {
					addToken((i + 2) - lastOffset, token);
					lastOffset = lastKeyword = i + 2;
					i += 2;
					token = Token.NULL;
				} else if (SyntaxUtilities.regionMatches(false, line, i, "else")) {
					addToken((i + 4) - lastOffset, token);
					lastOffset = lastKeyword = i + 4;
					i += 4;
					token = Token.NULL;
				} else if (SyntaxUtilities.regionMatches(false, line, i, "elseif")) {
					addToken((i + 6) - lastOffset, token);
					lastOffset = lastKeyword = i + 6;
					i += 6;
					token = Token.NULL;
				} else if (SyntaxUtilities.regionMatches(false, line, i, "end")) {
					addToken((i + 3) - lastOffset, token);
					lastOffset = lastKeyword = i + 3;
					i += 3;
					token = Token.NULL;
				} else if (SyntaxUtilities.regionMatches(false, line, i, "foreach")) {
					addToken((i + 7) - lastOffset, token);
					lastOffset = lastKeyword = i + 7;
					i += 7;
					token = Token.NULL;
				} else if (SyntaxUtilities.regionMatches(false, line, i, "macro")) {
					addToken((i + 5) - lastOffset, token);
					lastOffset = lastKeyword = i + 5;
					i += 5;
					token = Token.NULL;
				} else if (SyntaxUtilities.regionMatches(false, line, i, "set")) {
					addToken((i + 3) - lastOffset, token);
					lastOffset = lastKeyword = i + 3;
					i += 3;
					token = Token.NULL;
				}
				break;
			case Token.KEYWORD4: // VTL references
				addToken(i1 - lastOffset, token);
				lastOffset = lastKeyword = i1;
				token = Token.NULL;
				break;
			case Token.COMMENT3: // VTL comments
			case Token.COMMENT4:
				backslash = false;
				if (c == '*' && length - i > 1) {
					if (array[i1] == '#') {
						i++;
						addToken((i + 1) - lastOffset, token);
						token = Token.NULL;
						lastOffset = lastKeyword = i + 1;
					}
				}
				break;
			case JAVASCRIPT: // Inside a JavaScript
				switch (c) {
				case '<':
					backslash = false;
					doKeyword(line, i, c);
					if (SyntaxUtilities.regionMatches(true, line, i1, "/script>")) {
						addToken(i - lastOffset, Token.NULL);
						addToken(9, Token.KEYWORD3);
						lastOffset = lastKeyword = (i += 9);
						token = Token.NULL;
					}
					break;
				case '"':
					if (backslash)
						backslash = false;
					else {
						doKeyword(line, i, c);
						addToken(i - lastOffset, Token.NULL);
						lastOffset = lastKeyword = i;
						token = Token.LITERAL1;
					}
					break;
				case '\'':
					if (backslash)
						backslash = false;
					else {
						doKeyword(line, i, c);
						addToken(i - lastOffset, Token.NULL);
						lastOffset = lastKeyword = i;
						token = Token.LITERAL2;
					}
					break;
				case '/':
					backslash = false;
					doKeyword(line, i, c);
					if (length - i > 1) {
						addToken(i - lastOffset, Token.NULL);
						lastOffset = lastKeyword = i;
						if (array[i1] == '/') {
							addToken(length - i, Token.COMMENT2);
							lastOffset = lastKeyword = length;
							break loop;
						} else if (array[i1] == '*') {
							token = Token.COMMENT2;
						}
					}
					break;
				default:
					backslash = false;
					if (!Character.isLetterOrDigit(c) && c != '_')
						doKeyword(line, i, c);
					break;
				}
				break;
			case Token.LITERAL1: // JavaScript "..."
				if (backslash)
					backslash = false;
				else if (c == '"') {
					addToken(i1 - lastOffset, Token.LITERAL1);
					lastOffset = lastKeyword = i1;
					token = JAVASCRIPT;
				}
				break;
			case Token.LITERAL2: // JavaScript '...'
				if (backslash)
					backslash = false;
				else if (c == '\'') {
					addToken(i1 - lastOffset, Token.LITERAL1);
					lastOffset = lastKeyword = i1;
					token = JAVASCRIPT;
				}
				break;
			case Token.COMMENT2: // Inside a JavaScript comment
				backslash = false;
				if (c == '*' && length - i > 1 && array[i1] == '/') {
					addToken((i += 2) - lastOffset, Token.COMMENT2);
					lastOffset = lastKeyword = i;
					token = JAVASCRIPT;
				}
				break;
			default:
				throw new InternalError("Invalid state: " + token);
			}
		}

		switch (token) {
		case Token.LITERAL1:
		case Token.LITERAL2:
			addToken(length - lastOffset, Token.INVALID);
			token = JAVASCRIPT;
			break;
		case Token.KEYWORD2:
			addToken(length - lastOffset, Token.INVALID);
			token = Token.NULL;
			break;
		case JAVASCRIPT:
			doKeyword(line, length, '\0');
			addToken(length - lastOffset, Token.NULL);
			break;
		default:
			addToken(length - lastOffset, token);
			break;
		}

		return token;
	}

	// private members
	private KeywordMap keywords;

	private boolean js;

	private int lastOffset;

	private int lastKeyword;

	private boolean doKeyword(Segment line, int i, char c) {
		int i1 = i + 1;

		int len = i - lastKeyword;
		byte id = keywords.lookup(line, lastKeyword, len);
		if (id != Token.NULL) {
			if (lastKeyword != lastOffset)
				addToken(lastKeyword - lastOffset, Token.NULL);
			addToken(len, id);
			lastOffset = i;
		}
		lastKeyword = i1;
		return false;
	}
}
