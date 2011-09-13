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
 * Use case class category.
 *
 * @version $Id: UseCaseCategory.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class UseCaseCategory extends ClassCategory {
  static final long serialVersionUID = 824437667323709051L;

  public UseCaseCategory(PetalObject parent) {
    super(parent, "Use Case View");
  }

  public UseCaseCategory() {
    super(null, "Use Case View");
  }

  public void addToModel(UseCaseCategory cat) {
    add(cat);
  }

  public void removeFromModel(UseCaseCategory cat) {
    remove(cat);
  }

  /** Add a use case to the model. Sets parent and calls init() on use case.
   */
  public void addToModel(UseCase caze) {
    add(caze);
  }

  public void removeFromModel(UseCase caze) {
    remove(caze);
  }

  /** Typically every Use Case view has just one "Main" use case diagram
   */
  public UseCaseDiagram getFirstUseCaseDiagram() {
    return (UseCaseDiagram)lookupDiagram(UseCaseDiagram.class);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
