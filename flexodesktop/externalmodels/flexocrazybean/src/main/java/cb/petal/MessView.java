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
 * Represents MessView object
 *
 * @version $Id: MessView.java,v 1.3 2011/09/12 11:46:49 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class MessView extends View {
  public MessView(PetalNode parent, Collection params, int tag) {
    super(parent, "MessView", params, tag);
  }

  public MessView() {
    super("MessView");
  }

  public SegLabel getLabel() {
    return (SegLabel)getProperty("label");
  }

  public void setLabel(SegLabel o) {
    defineProperty("label", o);
  }

  public Tag getObjectArc() {
    return (Tag)getProperty("object_arc");
  }

  public void setObjectArc(Tag o) {
    defineProperty("object_arc", o);
  }

  public double getPctDist() {
    return getPropertyAsFloat("pctDist");
  }

  public void setPctDist(double o) {
    defineProperty("pctDist", o);
  }

  public int getOrientation() {
    return getPropertyAsInteger("orientation");
  }

  public void setOrientation(int o) {
    defineProperty("orientation", o);
  }

  public int getDir() {
    return getPropertyAsInteger("dir");
  }

  public void setDir(int o) {
    defineProperty("dir", o);
  }

  public Location getOrigin() {
    return (Location)getProperty("origin");
  }

  public void setOrigin(Location o) {
    defineProperty("origin", o);
  }

  public Location getTerminus() {
    return (Location)getProperty("terminus");
  }

  public void setTerminus(Location o) {
    defineProperty("terminus", o);
  }

  public Tag getDataFlowView() {
    return (Tag)getProperty("DataFlowView");
  }

  public void setDataFlowView(Tag o) {
    defineProperty("DataFlowView", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
