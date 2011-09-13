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

import java.util.Stack;

public class Token {
  public static final Token LPAREN = new Token(Lexer.LPAREN, "(", -1);
  public static final Token RPAREN = new Token(Lexer.RPAREN, ")", -1);
  public static final Token COMMA  = new Token(Lexer.COMMA, ",", -1);
  public static final Token TRUE   = new Token(Lexer.BOOLEAN, "TRUE", -1);
  public static final Token FALSE  = new Token(Lexer.BOOLEAN, "FALSE", -1);
  public static final Token EOF    = new Token(Lexer.EOF, "<EOF>", -1);

  private Token() {}

  private Token(int kind, String image, int line) {
    init(kind, image, line);
  }

  private void init(int kind, String image, int line) {
    this.kind  = kind;
    this.image = image;
    this.line  = line;
  }
  
  private static Stack stack = new Stack();

  public static Token createToken(int kind, String image, int line) {
    Token t;

    if(stack.isEmpty())
      t = new Token();
    else
      t = (Token)stack.pop();

    t.init(kind, image, line);
    return t;
  }

  public static void dispose(Token t) {
    if(t != null)
      stack.push(t);
  }

  public int    kind;
  public String image;
  public int    line;
}
