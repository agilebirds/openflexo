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

import java.util.Collection;

/**
 * Represents a class consisting of fields and methods, should be useful
 * for Java and C++.
 *
 * @version $Id: Class.java,v 1.2 2011/09/12 11:47:23 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public interface Class extends Node {
  public void   addPrefixCode(String c); // Inserted before class itself, e.g. imports
  public void   addSuperClass(String c);
  public void   removeSuperClass(String f);
  public void   addImplementedInterface(String c);
  public void   removeImplementedInterface(String f);
  public void   addField(Field f);
  public void   removeField(Field f);
  public void   addMethod(Method m);
  public void   removeMethod(Method m);
  public void   setPackage(String p);
  public String getPackage();
  public boolean isInterface();
  public Collection getFields();
  public Collection getMethods();
  public String getQualifiedName();
}
