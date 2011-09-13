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
import java.io.PrintStream;
import java.util.Iterator;

import cb.petal.Association;
import cb.petal.HasQuidu;
import cb.petal.Named;
import cb.petal.PetalFile;
import cb.petal.PetalNode;
import cb.petal.PetalObject;
import cb.petal.QuidObject;
import cb.petal.Role;

/**
 * (Experimental) Just prints some information about the traversed class.
 *
 * @version $Id: MyPrintVisitor.java,v 1.3 2011/09/12 11:47:21 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class MyPrintVisitor extends PrintVisitor {
  public MyPrintVisitor(PrintStream out) {
    super(out);
  }

  @Override
public void visitObject(PetalObject obj) {
    PetalNode node = obj.getProperty("quidu");

    if(node != null) {
      if(!(obj instanceof HasQuidu))
	System.err.println(obj.getName() + " has quidu " + node);
    } else if(obj instanceof HasQuidu) {
      System.err.println(obj.getName() + " has NO quidu ");
    }


    super.visitObject(obj);
  }

  @Override
public void visit(cb.petal.Class obj) {
    cb.petal.Class super_ = obj.getSuperclass();
    java.util.List list   = obj.getAssociations();

    System.err.println(obj.getQualifiedName());

    if(super_ != null) {
      System.err.println("Super class of " + obj.getNameParameter() + ":" +
			 super_.getNameParameter());
    } else if(list != null) {

      System.err.print("Associations of " + obj.getNameParameter() + ":");

      for(Iterator i = list.iterator(); i.hasNext(); ) {
	Association a = (Association)i.next();
	System.err.println(((Named)a.getFirstClient()).getNameParameter() + " <-> " +
			   ((Named)a.getSecondClient()).getNameParameter());

	cb.petal.Class clazz = a.getAssociationClass();
	if(clazz != null)
	  System.err.println("ASSOCIATIONCLASS:" + clazz.getNameParameter());
      }
    }

    visitObject(obj);
  }

  @Override
public void visit(Association obj) {
    java.util.List list = obj.getRoles().getElements();

    Role role1   = (Role)list.get(0);
    Role role2   = (Role)list.get(1);

    QuidObject class1 = obj.getFirstClient();
    QuidObject class2 = obj.getSecondClient();

    String name1 = role1.getNameParameter();
    String name2 = role2.getNameParameter();
    String card1 = role1.getCardinality();
    String card2 = role2.getCardinality();

    System.err.println("Association named " + obj.getNameParameter() + " between:\n" +
		       ((Named)class1).getNameParameter() +
		       "(" + name1 + ":" + card1 +
		       ":" + role1.isNavigable() + ":" + role1.isAggregate() +
		       ")" +
		       "\n" +
		       ((Named)class2).getNameParameter() +
		       "(" + name2 + ":" + card2 +
		       ":" + role2.isNavigable() + ":" + role2.isAggregate() +
		       ")"
		       );
    visitObject(obj);
  }

  public static void main(String[] args) {
    PetalFile tree = PetalParser.parse(args);
    tree.accept(new MyPrintVisitor(System.out));
  }
}
