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
package cb.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Stack;

public class Lexer {
  private PushbackReader _reader;
  private int            _line = 1;

  public static final int LPAREN       = 0;
  public static final int RPAREN       = 1;
  public static final int IDENT        = 2;
  public static final int INTEGER      = 3;
  public static final int FLOAT        = 4;
  public static final int BOOLEAN      = 5;
  public static final int TAG          = 6;
  public static final int STRING       = 7;
  public static final int MULTI_STRING = 8;
  public static final int COMMA        = 9;
  public static final int EOF          = -1;

  public Lexer(Reader r) {
    _reader = new PushbackReader(new BufferedReader(r, 8192));
  }

  private void skipWS() throws IOException {
    int ch;

    while(Character.isWhitespace((char)(ch = _reader.read()))) {
      if(ch == '\n') {
	_line++;
      }
    }

    _reader.unread(ch);
  }

  private StringBuffer buf = new StringBuffer();

  public Token parseNumber() throws IOException {
    int kind = INTEGER;
    int ch   = _reader.read();

    buf.setLength(0);

    if((ch == '+') || (ch == '-')) {
      buf.append((char)ch);
      ch = _reader.read();
    }

    while(Character.isDigit((char)ch) || (ch == '.')) {
      if(ch == '.') {
	if(kind == FLOAT)
	  throw new RuntimeException("Lexer Error: Found second . in float");
	kind = FLOAT;
      }

      buf.append((char)ch);
      ch = _reader.read();
    }

    _reader.unread(ch);

    return Token.createToken(kind, buf.toString(), _line);  
  }

  public Token parseString() throws IOException {
    int ch = _reader.read();

    buf.setLength(0);

    buf.append('"');

    ch = _reader.read();
    while(ch != '"') {
      if(ch == '\\') {
	buf.append('\\');
	ch = _reader.read();
	buf.append((char)ch);
      } else
	buf.append((char)ch);
      ch = _reader.read();
    }

    buf.append('"');

    return Token.createToken(STRING, buf.toString(), _line);  
  }

  public Token parseMultiString() throws IOException {
    int ch = _reader.read();

    buf.setLength(0);
    
    while(ch == '|') {
      buf.append('|');

      while((ch = _reader.read()) != '\n') {
	buf.append((char)ch);
      }

      buf.append('\n');
      _line++;
      ch = _reader.read();
    }

    _reader.unread(ch);

    return Token.createToken(MULTI_STRING, buf.toString(), _line);  
  }    

  public Token parseIdent() throws IOException {
    int ch = _reader.read();

    buf.setLength(0);

    while(Character.isJavaIdentifierPart((char)ch)) {
      buf.append((char)ch);
      ch = _reader.read();
    }

    _reader.unread(ch);

    String img = buf.toString();

    if(img.equals("TRUE"))
      return Token.TRUE;
    else if(img.equals("FALSE"))
      return Token.FALSE;
    else
      return Token.createToken(IDENT, img, _line);  
  }

  private Stack stack = new Stack();

  public void ungetToken(Token t) {
    stack.push(t);
  }

  public Token getToken() {
    if(!stack.empty())
      return (Token)stack.pop();


    int    kind;
    String image;

    try {
      skipWS();
      int ch = _reader.read();

      switch(ch) {
      case '(':
	return Token.LPAREN;

      case ')':
	return Token.RPAREN;

      case ',':
	return Token.COMMA;

      case '@':
	Token t = parseNumber();
	t.image = "@" + t.image;
	t.kind = TAG;

	return t;

      case '0': case '1': case '2': case '3': case '4': case '5':
      case '6': case '7': case '8': case '9': case '-': case '+': case '.':
	_reader.unread(ch);
	return parseNumber();

      case '"':
	_reader.unread(ch);
	return parseString();

      case '|':
	_reader.unread(ch);
	return parseMultiString();

      case 65535: // -1
	return Token.EOF;

      default:
	if(Character.isJavaIdentifierStart((char)ch)) {
	  _reader.unread(ch);
	  return parseIdent();
	} else
	  throw new RuntimeException("Lexer error: Unknown token: " + (char)ch +
				     "(" + ch + ")" + (ch < 0));
      }
    } catch(IOException e) {
      throw new RuntimeException("Lexer error: " + e);
    }
  }

  public int getLine() { return _line; }
}
