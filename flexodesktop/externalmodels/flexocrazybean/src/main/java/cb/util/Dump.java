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

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import cb.parser.PetalParser;
import cb.petal.AssocAttachView;
import cb.petal.Association;
import cb.petal.AssociationViewNew;
import cb.petal.AttachView;
import cb.petal.ClassView;
import cb.petal.DescendingVisitor;
import cb.petal.InheritView;
import cb.petal.ItemLabel;
import cb.petal.NoteView;
import cb.petal.Operation;
import cb.petal.PetalFile;
import cb.petal.PetalNode;
import cb.petal.RealizeView;
import cb.petal.SegLabel;
import cb.petal.UseCaseView;
import cb.petal.UsesView;

/**
 * Create serialized templates.
 * 
 * @version $Id: Dump.java,v 1.3 2011/09/12 11:47:29 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Dump {
	private static class Traverser extends DescendingVisitor {
		@Override
		public void visit(cb.petal.Class obj) {
			if (obj.getNameParameter().equals("SomeClass")) {
				obj.setParent(null);

				dump("Class", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(ClassView obj) {
			if (obj.getQualifiedNameParameter().equals("Logical View::templates::SomeClass")) {
				obj.setParent(null);
				dump("ClassView", obj);
			} else if (obj.getQualifiedNameParameter().equals("Logical View::templates::Actor")) {
				obj.setParent(null);
				dump("ActorView", obj);
			} else if (obj.getQualifiedNameParameter().equals("Logical View::templates::Interface")) {
				obj.setParent(null);
				dump("StereotypeView", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(UseCaseView obj) {
			if (obj.getQualifiedNameParameter().equals("Use Case View::Examination")) {
				obj.setParent(null);
				dump("UseCaseView", obj);
			} else if (obj.getQualifiedNameParameter().equals("Use Case View::Lecture")) {
				obj.setParent(null);
				dump("UseCaseStereotypeView", obj);
			}
		}

		@Override
		public void visit(InheritView obj) {
			if (obj.getQuidu().equals("3B29C6640186")) {
				obj.setParent(null);
				dump("InheritView", obj);
			}
		}

		@Override
		public void visit(RealizeView obj) {
			if (obj.getQuidu().equals("3B29C6F203D4")) {
				obj.setParent(null);
				dump("RealizeView", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(UsesView obj) {
			if (obj.getQuidu().equals("3B29C88B0014")) {
				obj.setParent(null);
				dump("UsesView", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(Association obj) {
			if (obj.getQuid().equals("3B29CAD600E6")) {
				obj.setParent(null);
				dump("Association", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(AssociationViewNew obj) {
			if (obj.getQuidu().equals("3B29CAD600E6")) {
				obj.setParent(null);
				dump("AssociationViewNew", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(ItemLabel obj) {
			if (obj.getLabel().equals("SomeClass")) {
				obj.setParent(null);
				dump("ItemLabel", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(NoteView obj) {
			if (obj.getTag() == 25) {
				obj.setParent(null);
				dump("NoteView", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(AttachView obj) {
			if (obj.getTag() == 30) {
				obj.setParent(null);
				dump("AttachView", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(AssocAttachView obj) {
			if (obj.getTag() == 27) {
				obj.setParent(null);
				dump("AssocAttachView", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(SegLabel obj) {
			if (obj.getLabel().equals("1..*")) {
				obj.setParent(null);
				obj.removeProperty("anchor");
				obj.removeProperty("anchor_loc");
				dump("SegLabel", obj);
			}

			visitObject(obj);
		}

		@Override
		public void visit(Operation obj) {
			if (obj.getNameParameter().equals("setAddress")) {
				obj.setParent(null);
				dump("Operation", obj);
			}

			visitObject(obj);
		}
	}

	static void dump(String name, PetalNode node) {
		try {
			File file = new File("templates" + File.separatorChar + name + ".ser");
			ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(file));
			s.writeObject(node);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args) {
		PetalFile tree = null;

		try {
			tree = PetalParser.createParser("examples" + File.separatorChar + "empty.mdl").parse();

			dump("PetalFile", tree);
			dump("LogicalCategory", tree.getLogicalCategory());
			dump("UseCaseCategory", tree.getUseCaseCategory());

			tree = PetalParser.createParser("examples" + File.separatorChar + "uni.mdl").parse();

			Traverser t = new Traverser();
			tree.accept(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
