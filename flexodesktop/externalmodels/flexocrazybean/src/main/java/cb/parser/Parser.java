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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

import cb.petal.Design;
import cb.petal.List;
import cb.petal.Petal;
import cb.petal.PetalFile;
import cb.petal.PetalNode;
import cb.petal.PetalObject;
import cb.petal.StringLiteral;
import cb.petal.Value;

public class Parser {
  private Lexer     lexer;

  private PetalNode current_parent = null;
  private Stack     parent_stack   = new Stack(); // Stack<PetalNode>

  private void saveParent(PetalNode new_parent) {
    parent_stack.push(current_parent);
    current_parent = new_parent;
  }

  private void restoreParent() {
    current_parent = (PetalNode)parent_stack.pop();
  }

  private static ObjectFactory factory = ObjectFactory.getInstance();

  public Parser(Reader r) {
    lexer = new Lexer(r);
  }

  private java.util.List ignored_nodes = Collections.EMPTY_LIST;

  /** If the parser finds such a node while building the petal tree, the node
   * will be ignored and not added to the tree. E.g, setIgnoredNodes(Diagram.class) will
   * abandon all diagrams of the model.
   */
  public void setIgnoredNodes(java.lang.Class[] nodes) {
    ignored_nodes = new ArrayList(Arrays.asList(nodes));
  }

  public java.lang.Class[] getIgnoredNodes() {
    java.lang.Class[] nodes = new java.lang.Class[ignored_nodes.size()];
    ignored_nodes.toArray(nodes);
    return nodes;
  }

  private boolean ignored(PetalNode obj) {
    for(Iterator i = ignored_nodes.iterator(); i.hasNext(); ) {
      java.lang.Class clazz = (java.lang.Class)i.next();
      if(clazz.isInstance(obj)) {
	return true;
      }
    }

    return false;
  }
    
  private Token match(int kind, String match) {
    Token t = lexer.getToken();

    if(t.kind != kind)
      throw new RuntimeException("Mismatch: Expected " + kind + " but got " + t.kind +
				 " at line " + t.line);

    if((match != null) && !match.equals(t.image))
      throw new RuntimeException("Mismatch: Expected " + match + " but got " + t.image +
				 " at line " + t.line);

    return t;
  }

  private Token match(int kind) {
    return match(kind, null);
  }

  private ArrayList list = new ArrayList(); // Reused list to collect docs

  /** (...)* wildcard
   * @return images
   */
  private ArrayList matchAll(int kind) {
    list.clear();

    Token t = lexer.getToken();

    while(t.kind == kind) {
      list.add(t.image);
      t = lexer.getToken();
    }

    lexer.ungetToken(t);

    return list;
  }

  /** [...] optional
   * @return image
   */
  private Token matchAny(int kind) {
    Token t = lexer.getToken();

    if(t.kind == kind)
      return t;
    else {
      lexer.ungetToken(t);
      return null;
    }
  }

  public static PetalFile parse(String file_name) {
    return parse(new File(file_name));
  }

  public static PetalFile parse(java.net.URL url) {
    try {
      return parse(url.openStream());
    } catch(IOException e) {
      throw new RuntimeException(e.toString());
    }
  }

  public static PetalFile parse(File file) {
    try {
      PetalFile tree  = parse(new FileReader(file));
      String    name  = file.getName();
      int       index = name.lastIndexOf('.');

      if(index > 0)
	name = name.substring(0, index);

      tree.setModelName(name);
      
      return tree;
    } catch(IOException e) {
      throw new RuntimeException(e.toString());
    }
  }

  public static PetalFile parse(Reader stream) {
    return new Parser(stream).parse();
  }

  public static PetalFile parse(InputStream stream) {
    return parse(new InputStreamReader(stream));
  }

  /** Top level construct are always petal and design objects
   */
  public PetalFile parse() {
    PetalObject petal, design;
    PetalFile file = new PetalFile();
    current_parent = file;

    petal  = parseObject();
    design = parseObject();
  
    file.setPetal((Petal)petal);
    file.setDesign((Design)design);
    return file;
  }

  /* Example: (object ClassView "Class" "Use Case View::Student" @76	
   *             location   	(160, 176))
   */
  public PetalObject parseObject() {
    match(Lexer.LPAREN);  match(Lexer.IDENT, "object");

    Token     t1   = match(Lexer.IDENT);
    ArrayList docs = matchAll(Lexer.STRING);
    Token     t3   = matchAny(Lexer.TAG);

    PetalNode   parent = ignored(current_parent)? null : current_parent;
    PetalObject obj    = factory.createObject(parent, t1.image, docs,
					      t3 == null? null : t3.image);
    saveParent(obj);

    /* List of properties
     */
    Token t4 = matchAny(Lexer.IDENT);

    while(t4 != null) {
      PetalNode prop = parseValue(false);

      if(prop != null)
	obj.addProperty(t4.image, prop);

      t4 = matchAny(Lexer.IDENT);
    }
  
    match(Lexer.RPAREN);

    restoreParent();

    if(!ignored(obj)) {
      obj.init();
      return obj;
    } else
      return null;
  }

  public PetalNode parseValue(boolean rparen_ok) {
    Token t = lexer.getToken();

    switch(t.kind) {
    case Lexer.STRING:
      return factory.createString(t.image, false);

    case Lexer.MULTI_STRING:
      return factory.createString(t.image, true);

    case Lexer.INTEGER:
      return factory.createInteger(t.image);

    case Lexer.FLOAT:
      return factory.createFloat(t.image);

    case Lexer.BOOLEAN:
      return factory.createBoolean(t.image);

    case Lexer.TAG:
      return factory.createTag(t.image);

    case Lexer.LPAREN:
      Token t2 = lexer.getToken();
      
      switch(t2.kind) {
      case Lexer.IDENT:
	lexer.ungetToken(t2);
	lexer.ungetToken(t);

	if(t2.image.equals("object")) {
	  return parseObject();
	} else if(t2.image.equals("list")) {
	  return parseList();
	} else if(t2.image.equals("value")) {
	  return parseValueObject();
	} else
	  throw new RuntimeException("Unexpected " + t2.image + " after (");

      case Lexer.INTEGER:
	match(Lexer.COMMA);
	Token t3 = match(Lexer.INTEGER);
	match(Lexer.RPAREN);

	return factory.createLocation(t2.image, t3.image);

      case Lexer.STRING:
	Token t4 = match(Lexer.INTEGER);
	match(Lexer.RPAREN);
	return factory.createTuple(t2.image, t4.image);

      default:
	throw new RuntimeException("Unexpected " + t2.image + "after (");
      }

    default:
      if((t.kind == Lexer.RPAREN) && rparen_ok)
	return null;
      else
	throw new RuntimeException("Unexpected " + t.image);
    }
  }

  /* Example: (list unit_reference_list (object Module_Diagram "Main"
   *		quid       	"35CB163B03CF"))
   *
   */
  public List parseList() {
    match(Lexer.LPAREN);  match(Lexer.IDENT, "list");

    Token t    = matchAny(Lexer.IDENT);
    List  list = factory.createList(t == null ? null : t.image);
  
    PetalNode obj;

    while((obj = parseValue(true)) != null) // null == RPAREN
      list.add(obj);
    return list;
  }

  public Value parseValueObject() {
    match(Lexer.LPAREN);  match(Lexer.IDENT, "value");
    Token         t   = match(Lexer.IDENT);
    Token         t2  = lexer.getToken();
    StringLiteral str;

    switch(t2.kind) {
    case Lexer.STRING:
      str = factory.createString(t2.image, false);
      break;
    case Lexer.MULTI_STRING:
      str = factory.createString(t2.image, true);
      break;
    default:
      throw new RuntimeException("Unexpected " + t2.image + " in (value ...)");
    }

    match(Lexer.RPAREN);
    return factory.createValue(t.image, str);
  }

  public static void main(String[] args) throws Exception {
    FileReader r = new FileReader(args[0]);

    Parser parser = new Parser(r);
    parser.parse();
  }
}
