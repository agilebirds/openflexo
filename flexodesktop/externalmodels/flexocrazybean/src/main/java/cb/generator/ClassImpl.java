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
package cb.generator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * Simple representation of a Java Class.
 * 
 * @version $Id: ClassImpl.java,v 1.3 2011/09/12 11:47:23 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class ClassImpl extends NodeImpl implements Class {
	private String pack;
	private ArrayList fields = new ArrayList();
	private ArrayList methods = new ArrayList();
	private ArrayList super_classes = new ArrayList();
	private ArrayList interfaces = new ArrayList();
	private ArrayList prefix = new ArrayList();
	private boolean isInterface;
	protected cb.petal.Class clazz;

	public ClassImpl() {
	}

	public void setClazz(cb.petal.Class c) {
		clazz = c;
	}

	public cb.petal.Class getClazz() {
		return clazz;
	}

	@Override
	public void setPackage(String p) {
		pack = p;
	}

	@Override
	public String getPackage() {
		return pack;
	}

	public void isInterface(boolean i) {
		isInterface = i;
	}

	@Override
	public boolean isInterface() {
		return isInterface;
	}

	@Override
	public void addField(Field f) {
		fields.add(f);
	}

	@Override
	public void removeField(Field f) {
		fields.remove(f);
	}

	@Override
	public void addMethod(Method f) {
		methods.add(f);
	}

	@Override
	public void removeMethod(Method f) {
		methods.remove(f);
	}

	@Override
	public void addSuperClass(String s) {
		super_classes.add(s);
	}

	@Override
	public void removeSuperClass(String s) {
		super_classes.remove(s);
	}

	@Override
	public void addImplementedInterface(String s) {
		interfaces.add(s);
	}

	@Override
	public void removeImplementedInterface(String s) {
		interfaces.remove(s);
	}

	@Override
	public String getQualifiedName() {
		if (pack != null && !"".equals(pack))
			return pack + "." + name;
		else
			return name;
	}

	@Override
	public void addPrefixCode(String c) {
		prefix.add(c);
	}

	@Override
	public Collection getMethods() {
		return methods;
	}

	@Override
	public Collection getFields() {
		return fields;
	}

	public Collection getSuperClasses() {
		return super_classes;
	}

	public Collection getImplementedInterfaces() {
		return interfaces;
	}

	protected static void print(PrintWriter stream, String pre, String o, String post) {
		if ((o != null) && !"".equals(o))
			stream.print(pre + o + post);
	}

	/**
	 * Default implementation prints Java code
	 */
	@Override
	public void dump(PrintWriter stream) {
		print(stream, "package ", pack, ";\n");

		for (Iterator i = prefix.iterator(); i.hasNext();)
			stream.println(i.next());

		stream.println("\n/** Created with Generator/" + "<a href=\"http://crazybeans.sourceforge.net/\">" + "\n * CrazyBeans</a> "
				+ new Date() + "\n *");

		// Print documentation if any
		if (clazz != null) {
			cb.petal.StringLiteral str = (cb.petal.StringLiteral) clazz.getProperty("documentation");

			if (str != null) {
				Collection lines = str.getLines();

				for (Iterator i = lines.iterator(); i.hasNext();) {
					stream.println(" * " + i.next());
				}
			}
		}

		stream.println(" * @cbversion " + cb.util.Constants.VERSION + "\n */");
		print(stream, "", getAccess(), " ");

		if (isInterface())
			stream.print("interface " + getName() + " ");
		else
			stream.print("class " + getName() + " ");

		if (!super_classes.isEmpty()) {
			stream.print("extends ");

			for (Iterator i = super_classes.iterator(); i.hasNext();) {
				stream.print(i.next());

				if (i.hasNext())
					stream.print(", ");
			}

			stream.print(" ");
		}

		if (!interfaces.isEmpty() && !isInterface()) {
			stream.print("implements ");

			for (Iterator i = interfaces.iterator(); i.hasNext();) {
				stream.print(i.next());
				if (i.hasNext())
					stream.print(", ");
			}

			stream.print(" ");
		}

		stream.println("{");

		for (Iterator i = getFields().iterator(); i.hasNext();)
			((Field) i.next()).dump(stream);

		stream.println();

		for (Iterator i = getMethods().iterator(); i.hasNext();) {
			((Method) i.next()).dump(stream);

			if (i.hasNext())
				stream.println();
		}

		stream.println("}");
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Class) {
			Class c = (Class) o;

			return getQualifiedName().equals(c.getQualifiedName());
		} else
			return false;
	}
}
