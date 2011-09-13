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
 * Represents Compartment object
 *
 * @version $Id: Compartment.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Compartment extends PetalObject {
  public Compartment(PetalNode parent, Collection params) {
    super(parent, "Compartment", params);
  }

  public Compartment() {
    super("Compartment");
  }

  public Tag getParentView() {
    return (Tag)getProperty("Parent_View");
  }

  public void setParentView(Tag o) {
    defineProperty("Parent_View", o);
  }

  public Location getLocation() {
    return (Location)getProperty("location");
  }

  public void setLocation(Location o) {
    defineProperty("location", o);
  }

  public String getIconStyle() {
    return getPropertyAsString("icon_style");
  }

  public void setIconStyle(String o) {
    defineProperty("icon_style", o);
  }

  public int getFillColor() {
    return getPropertyAsInteger("fill_color");
  }

  public void setFillColor(int o) {
    defineProperty("fill_color", o);
  }

  public int getAnchor() {
    return getPropertyAsInteger("anchor");
  }

  public void setAnchor(int o) {
    defineProperty("anchor", o);
  }

  public int getNlines() {
    return getPropertyAsInteger("nlines");
  }

  public void setNlines(int o) {
    defineProperty("nlines", o);
  }

  public int getMaxWidth() {
    return getPropertyAsInteger("max_width");
  }

  public void setMaxWidth(int o) {
    defineProperty("max_width", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
