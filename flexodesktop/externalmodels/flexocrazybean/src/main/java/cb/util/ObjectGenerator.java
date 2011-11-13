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
package cb.util;

import java.util.HashSet;
import java.util.Iterator;

import cb.petal.BooleanLiteral;
import cb.petal.DescendingVisitor;
import cb.petal.FloatLiteral;
import cb.petal.IntegerLiteral;
import cb.petal.PetalFile;
import cb.petal.PetalNode;
import cb.petal.PetalObject;
import cb.petal.StringLiteral;

/**
 * Generate class derived from petal object for given type.
 * 
 * @version $Id: ObjectGenerator.java,v 1.3 2011/09/12 11:47:29 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class ObjectGenerator extends DescendingVisitor {
	private String ident;
	private boolean first = false;
	private HashSet found = new HashSet();

	private ObjectGenerator(String id) {
		this.ident = id;
	}

	@Override
	public void visitObject(PetalObject obj) {
		if (obj.getName().equals(ident)) {
			if (!first) {
				String class_name = Constants.makeName(obj.getName());

				System.out.println("package cb.petal;\nimport java.util.Collection;\n\n" + "/**\n * Represents " + obj.getName()
						+ " object\n" + " *\n" + " * @version $Id: ObjectGenerator.java,v 1.3 2011/09/12 11:47:29 gpolet Exp $\n"
						+ " * @author  <A HREF=\"http://www.berlin.de/~markus.dahm/\">" + "M. Dahm</A>\n */");

				System.out.println("public class " + class_name + " extends PetalObject {");
				System.out.println("  public " + class_name + "(PetalObject parent, Collection params) {");
				System.out.println("    super(parent, \"" + ident + "\", params);");
				System.out.println("  }\n");

				System.out.println("  public " + class_name + "() {");
				System.out.println("    super(\"" + ident + "\");");
				System.out.println("  }\n");

				int k = 0;
				for (Iterator i = obj.getParameterList().iterator(); i.hasNext(); k++) {
					System.out.println("  public void setViewParameter(String o) {");
					System.out.println("    params.set(" + k + ", o);\n  }\n");

					System.out.println("  public String getViewParameter() {");
					System.out.println("    return (String)params.get(" + k + ");\n  }\n");
					i.next();
				}

				// for DefaultVisitor and Visitor
				System.err.println("  public void visit(" + class_name + " obj) { visitObject(obj); }");
			}

			for (Iterator i = obj.getNames().iterator(), j = obj.getPropertyList().iterator(); i.hasNext();) {
				String name = (String) i.next();
				PetalNode node = (PetalNode) j.next();
				String type = getShortType(node);

				if (!found.contains(name) && (type != null) && !name.equals("quid") && !name.equals("quidu")) {
					String type2 = getLongType(type);
					String method = Constants.makeName(name);
					boolean prim = true;

					if (name.equals("name"))
						method = obj.getName() + "Name";

					if (name.equals("value"))
						type = type2 = "PetalNode";

					try { // Hack warning
						java.lang.Class cl = java.lang.Class.forName("java.lang." + type2);
					} catch (ClassNotFoundException e) {
						prim = false;
					}

					if (prim) {
						System.out.println("  public " + type + " get" + method + "() {\n" + "    return getPropertyAs" + type2 + "(\""
								+ name + "\");\n" + "  }\n");

						System.out.println("  public void set" + method + "(" + type + " o) {\n" + "    defineProperty(\"" + name
								+ "\", o);\n" + "  }\n");
					} else {
						System.out.println("  public " + type + " get" + method + "() {\n" + "    return (" + type + ")getProperty(\""
								+ name + "\");\n  }\n");

						System.out.println("  public void set" + method + "(" + type + " o) {\n" + "    defineProperty(\"" + name
								+ "\", o);\n" + "  }\n");
					}
				}

				found.add(name);
			}

			first = true;
		}// else
			// System.err.println("Unknown object type " + obj.getName());

		super.visitObject(obj);
	}

	private static String getShortType(PetalNode node) {
		if (node instanceof StringLiteral)
			return "String";
		else if (node instanceof IntegerLiteral)
			return "int";
		else if (node instanceof BooleanLiteral)
			return "boolean";
		else if (node instanceof FloatLiteral)
			return "double";
		else {
			String name = node.getClass().getName();
			int index = name.lastIndexOf('.');

			return name.substring(index + 1);
		}
	}

	private static String getLongType(String type) {
		if (type.equals("double"))
			return "Float";
		else if (type.equals("int"))
			return "Integer";
		else if (type.equals("boolean"))
			return "Boolean";
		else
			return type;
	}

	public static void main(String[] args) {
		PetalFile tree = cb.parser.PetalParser.parse(args);

		tree.accept(new ObjectGenerator(args[1]));

		System.out.println("  public void accept(Visitor v) {\n    v.visit(this);\n  }");
		System.out.println("}");
	}
}
