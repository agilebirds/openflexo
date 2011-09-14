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
 * Represents uses relationship in use case diagrams, in class
 * diagrams this is also used to express dependency relationships.
 *
 * @version $Id: UsesRelationship.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class UsesRelationship extends Relationship implements AccessQualified {
  public UsesRelationship(PetalNode parent, Collection params) {
    super(parent, "Uses_Relationship", params);
  }

  public UsesRelationship() {
    super("Uses_Relationship");
  }

  public List getAttributes() {
    return (List)getProperty("attributes");
  }

  public void setAttributes(List o) {
    defineProperty("attributes", o);
  }

  @Override
public String getExportControl() {
    return getPropertyAsString("exportControl");
  }

  @Override
public void setExportControl(String o) {
    defineProperty("exportControl", o);
  }

  public Value getClientCardinality() {
    return (Value)getProperty("client_cardinality");
  }

  public void setClientCardinality(Value o) {
    defineProperty("client_cardinality", o);
  }

  public Value getSupplierCardinality() {
    return (Value)getProperty("supplier_cardinality");
  }

  public void setSupplierCardinality(Value o) {
    defineProperty("supplier_cardinality", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
