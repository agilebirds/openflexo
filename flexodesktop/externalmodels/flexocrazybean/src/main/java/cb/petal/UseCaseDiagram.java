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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Represents UseCaseDiagram object
 *
 * @version $Id: UseCaseDiagram.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class UseCaseDiagram extends Diagram {
  static final long serialVersionUID = -2214424470131913554L;

  public UseCaseDiagram(PetalNode parent, Collection params) {
    super(parent, "UseCaseDiagram", params);
  }

  public UseCaseDiagram() {
    super("UseCaseDiagram");
  }

  /** Adds a use case view to the presentation view, sets location and tags
   * and calls init().
   */
  public void addToView(UseCaseView view) {
    int index = addToViewsList(view);

    view.setLocation(new Location(getX(), getY()));

    ItemLabel label = view.getLabel();

    if(label != null)
      label.setParentView(new Tag(index));

    label = view.getStereotype();

    if(label != null)
      label.setParentView(new Tag(index));
  }

  private static HashSet set = new HashSet(Arrays.asList(new java.lang.Object[] {
    UseCaseView.class, ClassView.class }));

  @Override
protected View searchView(String qual_name) {
    return searchView(qual_name, set);
  }

  /** Class and super class and the according views must have been added
   * to the model already. Sets tag as well as client and supplier tags.
   */
  public void addToView(InheritView view) {
    view.setParent(this);
    addRelationship(view, (InheritanceRelationship)view.getReferencedObject());
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
