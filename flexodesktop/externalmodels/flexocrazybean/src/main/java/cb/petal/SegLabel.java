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
 * Represents SegLabel object attached to associations
 *
 * @version $Id: SegLabel.java,v 1.3 2011/09/12 11:46:49 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class SegLabel extends PetalObject implements Tagged {
  public SegLabel(PetalNode parent, Collection params) {
    super(parent, "SegLabel", params);
    setTag(tag);
  }

  public SegLabel() {
    super("SegLabel");
  }

  private int tag = -1;

  @Override
public void setTag(int t) { tag = t; }

  @Override
public int  getTag()      { return tag; }

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

  public Font getFont() {
    return (Font)getProperty("font");
  }

  public void setFont(Font o) {
    defineProperty("font", o);
  }

  public int getAnchor() {
    return getPropertyAsInteger("anchor");
  }

  public void setAnchor(int o) {
    defineProperty("anchor", o);
  }

  public int getAnchorLoc() {
    return getPropertyAsInteger("anchor_loc");
  }

  public void setAnchorLoc(int o) {
    defineProperty("anchor_loc", o);
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

  public int getJustify() {
    return getPropertyAsInteger("justify");
  }

  public void setJustify(int o) {
    defineProperty("justify", o);
  }

  public String getLabel() {
    return getPropertyAsString("label");
  }

  public void setLabel(String o) {
    defineProperty("label", o);
  }

  public double getPctDist() {
    return getPropertyAsFloat("pctDist");
  }

  public void setPctDist(double o) {
    defineProperty("pctDist", o);
  }

  public int getHeight() {
    return getPropertyAsInteger("height");
  }

  public void setHeight(int o) {
    defineProperty("height", o);
  }

  public int getOrientation() {
    return getPropertyAsInteger("orientation");
  }

  public void setOrientation(int o) {
    defineProperty("orientation", o);
  }

  public boolean getHidden() {
    return getPropertyAsBoolean("hidden");
  }

  public void setHidden(boolean o) {
    defineProperty("hidden", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
