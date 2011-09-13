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
 * Represents defaults for top level design object.
 *
 * @version $Id: Defaults.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Defaults extends PetalObject {
  public Defaults(PetalNode parent, java.util.Collection params) {
    super(parent, "defaults", params);
  }

  public Defaults() {
    super("defaults");
  }

  public double getRightMargin() {
    return getPropertyAsFloat("rightMargin");
  }

  public void setRightMargin(double o) {
    defineProperty("rightMargin", o);
  }

  public double getLeftMargin() {
    return getPropertyAsFloat("leftMargin");
  }

  public void setLeftMargin(double o) {
    defineProperty("leftMargin", o);
  }

  public double getTopMargin() {
    return getPropertyAsFloat("topMargin");
  }

  public void setTopMargin(double o) {
    defineProperty("topMargin", o);
  }

  public double getBottomMargin() {
    return getPropertyAsFloat("bottomMargin");
  }

  public void setBottomMargin(double o) {
    defineProperty("bottomMargin", o);
  }
  public double getPageOverlap() {
    return getPropertyAsFloat("pageOverlap");
  }

  public void setPageOverlap(double o) {
    defineProperty("pageOverlap", o);
  }

  public boolean getClipIconLabels() {
    return getPropertyAsBoolean("clipIconLabels");
  }

  public void setClipIconLabels(boolean o) {
    defineProperty("clipIconLabels", o);
  }

  public boolean getAutoResize() {
    return getPropertyAsBoolean("autoResize");
  }

  public void setAutoResize(boolean o) {
    defineProperty("autoResize", o);
  }

  public boolean getSnapToGrid() {
    return getPropertyAsBoolean("snapToGrid");
  }

  public void setSnapToGrid(boolean o) {
    defineProperty("snapToGrid", o);
  }

  public int getGridX() {
    return getPropertyAsInteger("gridX");
  }

  public void setGridX(int o) {
    defineProperty("gridX", o);
  }

  public int getGridY() {
    return getPropertyAsInteger("gridY");
  }

  public void setGridY(int o) {
    defineProperty("gridY", o);
  }

  public Font getDefaultFont() {
    return (Font)getProperty("defaultFont");
  }

  public void setDefaultFont(Font o) {
    defineProperty("defaultFont", o);
  }

  public int getShowMessageNum() {
    return getPropertyAsInteger("showMessageNum");
  }

  public void setShowMessageNum(int o) {
    defineProperty("showMessageNum", o);
  }

  public boolean getShowClassOfObject() {
    return getPropertyAsBoolean("showClassOfObject");
  }
  public void setShowClassOfObject(boolean o) {
    defineProperty("showClassOfObject", o);
  }

  public String getNotation() {
    return getPropertyAsString("notation");
  }

  public void setNotation(String o) {
    defineProperty("notation", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
