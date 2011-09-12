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
package cb.petal;

/**
 * Denote that a petal object may be extended, i.e. classes and use cases.
 *
 * @version $Id: Inheritable.java,v 1.2 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public abstract class Inheritable extends AccessObject {
  protected Inheritable(PetalNode parent, String name, java.util.Collection params) {
    super(parent, name, params);
  }

  protected Inheritable(String name) {
    super(name);
  }

  /** @return list of InheritanceRelationship objects
   */
  public List getSuperclassList() {
    return (List)getProperty("superclasses");
  }

  /** Set list of InheritanceRelationship objects
   */
  public void setSuperclassList(List c) {
    defineProperty("superclasses", c);
  }

  /** Add super classifier of this use case/class, i.e. add InheritanceRelationship to
   * "superclasses" list.
   * @return implicitly created relationship object
   */
  public InheritanceRelationship addSuperClassifier(Inheritable clazz) {
    InheritanceRelationship rel =
      cb.util.PetalObjectFactory.getInstance().createInheritanceRelationship(this, clazz);

    rel.init(); // Parent is already set

    addToList("superclasses", "inheritance_relationship_list", rel);
    return rel;
  }
}
