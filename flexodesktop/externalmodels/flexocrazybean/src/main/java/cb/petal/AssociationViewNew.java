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
import java.util.Collection;

/**
 * Represents AssociationViewNew object, i.e. the view for an association.
 *
 * @version $Id: AssociationViewNew.java,v 1.3 2011/09/12 11:46:49 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 * @see Association
 */
public class AssociationViewNew extends QuiduView implements SegLabeled {
  static final long serialVersionUID = 5927996457897905931L;

  public AssociationViewNew(PetalNode parent, Collection params, int tag) {
    super(parent, "AssociationViewNew", params, tag);
  }

  public AssociationViewNew() {
    super("AssociationViewNew");
  }

  public RoleView getFirstRoleView() {
    return (RoleView)getRoleviewList().getElements().get(0);
  }

  public RoleView getSecondRoleView() {
    return (RoleView)getRoleviewList().getElements().get(1);
  }

  public List getRoleviewList() {
    return (List)getProperty("roleview_list");
  }

  public void setRoleviewList(List o) {
    defineProperty("roleview_list", o);
  }

  @Override
public SegLabel getLabel() {
    return (SegLabel)getProperty("label");
  }

  @Override
public void setLabel(SegLabel o) {
    defineProperty("label", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
