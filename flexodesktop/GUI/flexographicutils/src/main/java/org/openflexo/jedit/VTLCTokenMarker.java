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
 * CTokenMarker.java - C token marker
 * Copyright (C) 1998, 1999 Slava Pestov
 *
 * You may use and modify this package for any purpose. Redistribution is
 * permitted, in both source and binary form, provided that this notice
 * remains intact in all source distributions of this package.
 */

import javax.swing.text.Segment;

/**
 * VTL-C token marker.
 * 
 * @author gpolet
 * @version $Id: VTLCTokenMarker.java,v 1.2 2011/09/12 11:47:09 gpolet Exp $
 */
public class VTLCTokenMarker extends TokenMarker {
	public VTLCTokenMarker() {
		this(true, getKeywords());
	}

	public VTLCTokenMarker(boolean _cpp, KeywordMap _keywords) {
		this.keywords = _keywords;
	}

	@Override
	public byte markTokensImpl(byte token, Segment line, int lineIndex) {
		char[] array = line.array;
		int offset = line.offset;
		lastOffset = offset;
		lastKeyword = offset;
		int length = line.count + offset;
		boolean backslash = false;
		byte previousToken = Token.NULL;
		loop: for (int i = offset; i < length; i++) {
			int i1 = (i + 1);

			char c = array[i];
			if (c == '\\') {
				backslash = !backslash;
				continue;
			}

			switch (token) {
			case Token.NULL:
				switch (c) {
				case '"':
					doKeyword(line, i, c);
					if (backslash) {
						backslash = false;
					} else {
						addToken(i - lastOffset, token);
						token = Token.LITERAL1;
						lastOffset = lastKeyword = i;
					}
					break;
				case '\'':
					doKeyword(line, i, c);
					if (backslash) {
						backslash = false;
					} else {
						addToken(i - lastOffset, token);
						token = Token.LITERAL2;
						lastOffset = lastKeyword = i;
					}
					break;
				case ':':
					if (lastKeyword == offset) {
						if (doKeyword(line, i, c)) {
							break;
						}
						backslash = false;
						addToken(i1 - lastOffset, Token.LABEL);
						lastOffset = lastKeyword = i1;
					} else if (doKeyword(line, i, c)) {
						break;
					}
					break;
				case '/':
					backslash = false;
					doKeyword(line, i, c);
					if (length - i > 1) {
						switch (array[i1]) {
						case '*':
							addToken(i - lastOffset, token);
							lastOffset = lastKeyword = i;
							if (length - i > 2 && array[i + 2] == '*') {
								token = Token.COMMENT2;
							} else {
								token = Token.COMMENT1;
							}
							break;
						case '/':
							addToken(i - lastOffset, token);
							addToken(length - i, Token.COMMENT1);
							lastOffset = lastKeyword = length;
							break loop;
						}
					}
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
					boolean bracketsNotation = (tmp < array.length && array[tmp] == '{')
							|| (tmp + 1 < array.length && array[tmp + 1] == '{');
					if (bracketsNotation) {
						while (tmp < array.length && acceptable) {
							if (array[tmp] == '{') {
								openingB++;
							} else if (array[tmp] == '}') {
								if (openingB > 1) {
									openingB--;
								} else {
									acceptable = false;
								}
							}
							tmp++;
						}
						tmp++;
					} else {
						while (tmp < array.length && acceptable) {
							if (Character.isLetterOrDigit(array[tmp]) || array[tmp] == '_' || array[tmp] == '.') {

							} else if (array[tmp] == '(') {
								openingP++;
							} else if (array[tmp] == '{') {
								openingB++;
							} else if (array[tmp] == ')') {
								if (openingP > 0) {
									openingP--;
								} else {
									acceptable = false;
								}
							} else if (array[tmp] == '}') {
								if (openingB > 0) {
									openingB--;
								} else {
									acceptable = false;
								}
							} else if (array[tmp] == '!' && array[tmp - 1] == '$') {
								// OK - silent reference notation
							} else {
								acceptable = false;
							}
							tmp++;
						}
					}
					if (tmp < array.length) {
						i = tmp - 3;
						token = Token.KEYWORD4;
					} else {
						addToken(tmp - i1 - 1, Token.KEYWORD4);
						lastOffset = lastKeyword = i1;
						token = Token.NULL;
					}
					break;
				default:
					backslash = false;
					if (!Character.isLetterOrDigit(c) && c != '_') {
						doKeyword(line, i, c);
					}
					break;
				}
				break;
			case Token.COMMENT1:
			case Token.COMMENT2:
				backslash = false;
				switch (c) {
				case '#':
					addToken(i - lastOffset, token);
					previousToken = token;
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
							case '/':
								addToken(i - lastOffset, token);
								addToken(length - i, Token.COMMENT3);
								lastOffset = lastKeyword = length;
								break loop;
							}
						}
					} else {
						int tmp = i1;
						while (tmp < array.length && array[tmp] != '(') {
							tmp++;
						}
						if (array[tmp] == '(') {
							i = tmp - 1;
							token = Token.KEYWORD2;
						} else {
							token = Token.NULL;
						}
					}
					break;
				case '$':
					addToken(i - lastOffset, token);
					previousToken = token;
					lastOffset = lastKeyword = i;
					int tmp = i1;
					boolean acceptable = true;
					int openingP = 0; // # Opening parenthesis
					int openingB = 0; // # Opening brackets
					boolean bracketsNotation = (tmp < array.length && array[tmp] == '{')
							|| (tmp + 1 < array.length && array[tmp + 1] == '{');
					if (bracketsNotation) {
						while (tmp < array.length && acceptable) {
							if (array[tmp] == '{') {
								openingB++;
							} else if (array[tmp] == '}') {
								if (openingB > 1) {
									openingB--;
								} else {
									acceptable = false;
								}
							}
							tmp++;
						}
						tmp++;
					} else {
						while (tmp < array.length && acceptable) {
							if (Character.isLetterOrDigit(array[tmp]) || array[tmp] == '_' || array[tmp] == '.') {

							} else if (array[tmp] == '(') {
								openingP++;
							} else if (array[tmp] == '{') {
								openingB++;
							} else if (array[tmp] == ')') {
								if (openingP > 0) {
									openingP--;
								} else {
									acceptable = false;
								}
							} else if (array[tmp] == '}') {
								if (openingB > 0) {
									openingB--;
								} else {
									acceptable = false;
								}
							} else if (array[tmp] == '!' && array[tmp - 1] == '$') {
								// OK - silent reference notation
							} else {
								acceptable = false;
							}
							tmp++;
						}
					}
					if (tmp < array.length) {
						i = tmp - 3;
						token = Token.KEYWORD4;
					} else {
						addToken(i + tmp - i1 - lastOffset, Token.KEYWORD4);
						lastOffset = lastKeyword = i1;
						token = Token.NULL;
						if (previousToken != Token.NULL) {
							token = previousToken;
							previousToken = Token.NULL;
						}
					}
					break;
				default:
					if (c == '*' && length - i > 1) {
						if (array[i1] == '/') {
							i++;
							addToken((i + 1) - lastOffset, token);
							token = Token.NULL;
							lastOffset = lastKeyword = i + 1;
						}
					}
				}
				break;
			case Token.LITERAL1:
				if (backslash) {
					backslash = false;
				} else if (c == '"') {
					addToken(i1 - lastOffset, token);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			case Token.LITERAL2:
				if (backslash) {
					backslash = false;
				} else if (c == '\'') {
					addToken(i1 - lastOffset, Token.LITERAL1);
					token = Token.NULL;
					lastOffset = lastKeyword = i1;
				}
				break;
			case Token.KEYWORD2: // VTL macros
				backslash = false;
				if (c == '(') {
					addToken(i1 - lastOffset - 1, token);
					lastOffset = lastKeyword = i;
					token = Token.NULL;
					if (previousToken != Token.NULL) {
						token = previousToken;
						previousToken = Token.NULL;
					}
					break;
				}
				break;
			case Token.KEYWORD5: // VTL directives
				backslash = false;
				if (SyntaxUtilities.regionMatches(false, line, i, "if")) {
					addToken((i + 2) - lastOffset, token);
					lastOffset = lastKeyword = i + 2;
					i += 2;
					token = Token.NULL;
					if (previousToken != Token.NULL) {
						token = previousToken;
						previousToken = Token.NULL;
					}
				} else if (SyntaxUtilities.regionMatches(false, line, i, "else")) {
					addToken((i + 4) - lastOffset, token);
					lastOffset = lastKeyword = i + 4;
					i += 4;
					token = Token.NULL;
					if (previousToken != Token.NULL) {
						token = previousToken;
						previousToken = Token.NULL;
					}
				} else if (SyntaxUtilities.regionMatches(false, line, i, "elseif")) {
					addToken((i + 6) - lastOffset, token);
					lastOffset = lastKeyword = i + 6;
					i += 6;
					token = Token.NULL;
					if (previousToken != Token.NULL) {
						token = previousToken;
						previousToken = Token.NULL;
					}
				} else if (SyntaxUtilities.regionMatches(false, line, i, "end")) {
					addToken((i + 3) - lastOffset, token);
					lastOffset = lastKeyword = i + 3;
					i += 3;
					token = Token.NULL;
					if (previousToken != Token.NULL) {
						token = previousToken;
						previousToken = Token.NULL;
					}
				} else if (SyntaxUtilities.regionMatches(false, line, i, "foreach")) {
					addToken((i + 7) - lastOffset, token);
					lastOffset = lastKeyword = i + 7;
					i += 7;
					token = Token.NULL;
					if (previousToken != Token.NULL) {
						token = previousToken;
						previousToken = Token.NULL;
					}
				} else if (SyntaxUtilities.regionMatches(false, line, i, "macro")) {
					addToken((i + 5) - lastOffset, token);
					lastOffset = lastKeyword = i + 5;
					i += 5;
					token = Token.NULL;
					if (previousToken != Token.NULL) {
						token = previousToken;
						previousToken = Token.NULL;
					}
				} else if (SyntaxUtilities.regionMatches(false, line, i, "set")) {
					addToken((i + 3) - lastOffset, token);
					lastOffset = lastKeyword = i + 3;
					i += 3;
					token = Token.NULL;
					if (previousToken != Token.NULL) {
						token = previousToken;
						previousToken = Token.NULL;
					}
				}
				break;
			case Token.KEYWORD4: // VTL references
				addToken(i1 - lastOffset, token);
				lastOffset = lastKeyword = i1;
				token = Token.NULL;
				if (previousToken != Token.NULL) {
					token = previousToken;
					previousToken = Token.NULL;
				}
				break;
			case Token.COMMENT3: // VTL comments
			case Token.COMMENT4:
				backslash = false;
				if (c == '*' && length - i > 1) {
					if (array[i1] == '#') {
						i++;
						addToken((i + 1) - lastOffset, token);
						token = Token.NULL;
						if (previousToken != Token.NULL) {
							token = previousToken;
							previousToken = Token.NULL;
						}
						lastOffset = lastKeyword = i + 1;
					}
				}
				break;
			default:
				throw new InternalError("Invalid state: " + token);
			}
		}

		if (token == Token.NULL) {
			doKeyword(line, length, '\0');
		}

		switch (token) {
		case Token.LITERAL1:
		case Token.LITERAL2:
			addToken(length - lastOffset, Token.INVALID);
			token = Token.NULL;
			break;
		case Token.KEYWORD2:
			addToken(length - lastOffset, token);
			if (!backslash) {
				token = Token.NULL;
			}
		default:
			addToken(length - lastOffset, token);
			break;
		}

		return token;
	}

	public static KeywordMap getKeywords() {
		if (cKeywords == null) {
			cKeywords = new KeywordMap(false);
			cKeywords.add("char", Token.KEYWORD3);
			cKeywords.add("double", Token.KEYWORD3);
			cKeywords.add("enum", Token.KEYWORD3);
			cKeywords.add("float", Token.KEYWORD3);
			cKeywords.add("int", Token.KEYWORD3);
			cKeywords.add("long", Token.KEYWORD3);
			cKeywords.add("short", Token.KEYWORD3);
			cKeywords.add("signed", Token.KEYWORD3);
			cKeywords.add("struct", Token.KEYWORD3);
			cKeywords.add("typedef", Token.KEYWORD3);
			cKeywords.add("union", Token.KEYWORD3);
			cKeywords.add("unsigned", Token.KEYWORD3);
			cKeywords.add("void", Token.KEYWORD3);
			cKeywords.add("auto", Token.KEYWORD1);
			cKeywords.add("const", Token.KEYWORD1);
			cKeywords.add("extern", Token.KEYWORD1);
			cKeywords.add("register", Token.KEYWORD1);
			cKeywords.add("static", Token.KEYWORD1);
			cKeywords.add("volatile", Token.KEYWORD1);
			cKeywords.add("break", Token.KEYWORD1);
			cKeywords.add("case", Token.KEYWORD1);
			cKeywords.add("continue", Token.KEYWORD1);
			cKeywords.add("default", Token.KEYWORD1);
			cKeywords.add("do", Token.KEYWORD1);
			cKeywords.add("else", Token.KEYWORD1);
			cKeywords.add("for", Token.KEYWORD1);
			cKeywords.add("goto", Token.KEYWORD1);
			cKeywords.add("if", Token.KEYWORD1);
			cKeywords.add("return", Token.KEYWORD1);
			cKeywords.add("sizeof", Token.KEYWORD1);
			cKeywords.add("switch", Token.KEYWORD1);
			cKeywords.add("while", Token.KEYWORD1);
			cKeywords.add("asm", Token.KEYWORD2);
			cKeywords.add("asmlinkage", Token.KEYWORD2);
			cKeywords.add("far", Token.KEYWORD2);
			cKeywords.add("huge", Token.KEYWORD2);
			cKeywords.add("inline", Token.KEYWORD2);
			cKeywords.add("near", Token.KEYWORD2);
			cKeywords.add("pascal", Token.KEYWORD2);
			cKeywords.add("true", Token.LITERAL2);
			cKeywords.add("false", Token.LITERAL2);
			cKeywords.add("NULL", Token.LITERAL2);
		}
		return cKeywords;
	}

	// private members
	private static KeywordMap cKeywords;

	private KeywordMap keywords;

	private int lastOffset;

	private int lastKeyword;

	private boolean doKeyword(Segment line, int i, char c) {
		int i1 = i + 1;

		int len = i - lastKeyword;
		byte id = keywords.lookup(line, lastKeyword, len);
		if (id != Token.NULL) {
			if (lastKeyword != lastOffset) {
				addToken(lastKeyword - lastOffset, Token.NULL);
			}
			addToken(len, id);
			lastOffset = i;
		}
		lastKeyword = i1;
		return false;
	}
}
