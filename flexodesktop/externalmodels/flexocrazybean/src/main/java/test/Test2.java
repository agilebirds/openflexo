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
package test;

import java.io.File;

import cb.generator.Factory;
import cb.generator.Generator;
import cb.parser.PetalParser;
import cb.petal.PetalFile;
import cb.petal.Role;
import cb.util.PiggybackVisitor;

/**
 * Create sources for the university model. Also create <a href=
 * "http://barat.sourceforge.net/">Barat</A> class sources from model. In
 * the latter example it shows how to subclass the Generator and Factory
 * in order to adapt them to the behaviour you want.
 *
 * @version $Id: Test2.java,v 1.3 2011/09/12 11:47:32 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Test2 {
  public static void main(String[] args) throws Exception {
    /* For the university classes we just use the predefined behaviour
     */
    PetalFile tree = PetalParser.createParser(".." + File.separatorChar +
					      "examples" + File.separatorChar +
					      "uni.mdl").parse();
    String    dump = cb.util.Constants.isDOS()? "C:\\TEMP\\Test2" : "/tmp/Test2";

    Generator gen  = new Generator(tree, dump, ".java");
    tree.accept(new PiggybackVisitor(gen));
    gen.dump();


    /* With Barat there are some special considerations: We suppress the generation
     * of associations, and take the all classes in barat.reflect are marked as interfaces.
     */
    tree = PetalParser.createParser(".." + File.separatorChar + "examples" +
				    File.separatorChar + "Barat.mdl").parse();
    Factory   f    = new Factory() {
      @Override
	public void addAssociation(cb.generator.Class class1, Role role1,
				 cb.generator.Class class2, Role role2,
				 cb.generator.Class assoc_class) {
	return; // Just do nothing here
      }
    };

    Factory.setInstance(f);

    gen = new Generator(tree, dump, ".java") {
      @Override
	public void visit(cb.petal.Class clazz) {
	if(clazz.getQualifiedName().startsWith("Logical View::barat::reflect"))
	  clazz.setStereotype("Interface"); // Meant to be interfaces
	super.visit(clazz);
      }
    };

    tree.accept(new PiggybackVisitor(gen));
    gen.dump();
  }
}
