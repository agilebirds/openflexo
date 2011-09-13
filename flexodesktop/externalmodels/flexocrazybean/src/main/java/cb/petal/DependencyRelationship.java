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
 * Represents Dependency_Relationship object
 *
 * @version $Id: DependencyRelationship.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class DependencyRelationship extends Relationship {
  public DependencyRelationship(PetalNode parent, Collection params) {
    super(parent, "Dependency_Relationship", params);
  }

  public DependencyRelationship() {
    super("Dependency_Relationship");
  }

  public boolean getSupplierIsSubsystem() {
    return getPropertyAsBoolean("supplier_is_subsystem");
  }

  public void setSupplierIsSubsystem(boolean o) {
    defineProperty("supplier_is_subsystem", o);
  }

  public boolean getSupplierIsSpec() {
    return getPropertyAsBoolean("supplier_is_spec");
  }

  public void setSupplierIsSpec(boolean o) {
    defineProperty("supplier_is_spec", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
