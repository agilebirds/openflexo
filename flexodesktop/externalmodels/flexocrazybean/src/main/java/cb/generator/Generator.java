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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;

import cb.parser.PetalParser;
import cb.petal.Association;
import cb.petal.ClassAttribute;
import cb.petal.HasQuidu;
import cb.petal.Operation;
import cb.petal.PetalFile;
import cb.petal.PetalObject;
import cb.petal.QuidObject;
import cb.petal.RealizeRelationship;
import cb.petal.Role;
import cb.petal.UsesRelationship;
import cb.util.PiggybackVisitor;

/**
 * Convert a petal file into a set of (Java) classes. This class is mainly responsible for the traversal while the factory is responsible
 * for creation objects and setting up relationships.
 * 
 * @version $Id: Generator.java,v 1.3 2011/09/12 11:47:23 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Generator extends cb.petal.EmptyVisitor {
	private File dump;
	protected Factory factory = Factory.getInstance();
	private String suffix;
	private PetalFile tree;

	/**
	 * @param dump
	 *            where to dump generated files
	 */
	public Generator(PetalFile tree, File dump, String suffix) {
		this.dump = dump;
		this.suffix = suffix;
		this.tree = tree;
	}

	public Generator(PetalFile tree, String dump_path, String suffix) {
		this(tree, new File(dump_path), suffix);
	}

	/**
	 * @return class given by quid or null if it isn't a class
	 */
	protected Class getClass(String quid) {
		Class clazz = (Class) factory.getObject(quid);

		if (clazz == null) {
			QuidObject obj = tree.getQuidObject(quid);

			if (obj instanceof cb.petal.Class) {
				visit((cb.petal.Class) obj);
				clazz = (Class) factory.getObject(quid);
			}
		}

		return clazz;
	}

	protected Class getClass(HasQuidu obj) {
		return getClass(obj.getQuidu());
	}

	/**
	 * @return containing class or null if it isn't a class
	 */
	protected Class getParentClass(PetalObject obj) {
		return getClass(((QuidObject) obj.getParent()).getQuid());
	}

	@Override
	public void visit(cb.petal.InheritanceRelationship rel) {
		Class c = getParentClass(rel);
		if (c != null) {
			factory.addSuperClass(c, getClass(rel));
		}
	}

	@Override
	public void visit(RealizeRelationship rel) {
		Class c = getParentClass(rel);
		if (c != null) {
			factory.addImplementedInterface(c, getClass(rel));
		}
	}

	@Override
	public void visit(UsesRelationship rel) {
		Class c = getParentClass(rel);
		if (c != null) {
			factory.addUsedClass(c, getClass(rel), rel);
		}
	}

	@Override
	public void visit(Association assoc) {
		Role first = assoc.getFirstRole();
		Role second = assoc.getSecondRole();
		Class class1 = getClass(first);
		Class class2 = getClass(second);

		if (class1 != null && class2 != null) {
			cb.petal.Class assc = assoc.getAssociationClass();
			Class ac = null;
			if (assc != null) {
				ac = getClass(assc.getQuid());
			}

			factory.addAssociation(class1, first, class2, second, ac);
		}
	}

	@Override
	public void visit(cb.petal.Class clazz) {
		String quid = clazz.getQuid();

		if (factory.getObject(quid) == null) {
			Class cl = factory.createClass(clazz);
			factory.addObject(quid, cl);
		}
	}

	@Override
	public void visit(ClassAttribute attr) {
		Field f = factory.createField(attr);
		factory.addObject(attr.getQuid(), f);
		Class c = getParentClass(attr);
		factory.addField(c, f);
	}

	@Override
	public void visit(Operation op) {
		Method m = factory.createMethod(op);
		factory.addObject(op.getQuid(), m);
		Class c = getParentClass(op);
		if (c != null) {
			factory.addMethod(c, m);
		}
	}

	public void dump() throws IOException {
		for (Iterator i = factory.getObjects().iterator(); i.hasNext();) {
			Node n = (Node) i.next();

			if (n instanceof Class) {
				Class clazz = (Class) n;

				String path = clazz.getPackage().replace('.', File.separatorChar);
				File file = new File(dump, path);
				file.mkdirs();

				file = new File(file, File.separatorChar + clazz.getName() + suffix);

				FileOutputStream out = new FileOutputStream(file);
				PrintWriter stream = new PrintWriter(new OutputStreamWriter(out));

				clazz.dump(stream);
				stream.close();
				out.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			PetalFile tree = PetalParser.parse(args);
			String dump = cb.util.Constants.isDOS() ? "C:\\TEMP" : "/tmp";
			Generator gen = new Generator(tree, dump, ".java");

			tree.accept(new PiggybackVisitor(gen));

			gen.dump();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
