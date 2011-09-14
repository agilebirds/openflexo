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

import cb.parser.PetalParser;
import cb.petal.PetalFile;
import cb.xmi.XMIGenerator;

/**
 * Convert Rose file into XMI format. You'll need to install
 * <a href="http://nsuml.sourceforge.net/">NSUML</a> to run this example.
 * It also shows how to reduce memory consumption by omitting certain nodes
 * when parsing the petal file.
 *
 * @version $Id: Test4.java,v 1.2 2011/09/12 11:47:32 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Test4 {
  public static void main(String[] args) throws Exception {
    try {
      Class.forName("ru.novosoft.uml.MBase");
    } catch(ClassNotFoundException e) {
      System.err.println("Please install NSUML first: http://nsuml.sourceforge.net/");
      return;
    }

    PetalParser parser = PetalParser.createParser(args[0]);

    // Abandon all diagrams since they're not used by the generator anyway
    // This saves us a lot of memory
    parser.setIgnoredNodes(new Class[] { cb.petal.Diagram.class,
      cb.petal.View.class });

    PetalFile    tree = parser.parse();
    XMIGenerator gen  = new XMIGenerator(tree, args[1]);
    
    gen.start();
    gen.dump();
  }
}

