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
 * Represents Attribute object
 *
 * @version $Id: Attribute.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Attribute extends PetalObject {
  public Attribute(PetalNode parent, Collection params) {
    super(parent, "Attribute", params);
  }

  public Attribute() {
    super("Attribute");
  }

  public String getTool() {
    return getPropertyAsString("tool");
  }

  public void setTool(String o) {
    defineProperty("tool", o);
  }

  public String getAttributeName() {
    return getPropertyAsString("name");
  }

  public void setAttributeName(String o) {
    defineProperty("name", o);
  }

  public PetalNode getValue() {
    return getProperty("value");
  }

  public void setValue(PetalNode o) {
    defineProperty("value", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
