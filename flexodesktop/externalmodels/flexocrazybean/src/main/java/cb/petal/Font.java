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
 * Represents font object.
 *
 * @version $Id: Font.java,v 1.3 2011/09/12 11:46:49 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Font extends PetalObject {
  public Font(PetalNode parent, java.util.Collection params) {
    super(parent, "Font", params);
  }

  public Font() {
    super("Font");
  }

  public int getSize() {
    return getPropertyAsInteger("size");
  }

  public void setSize(int o) {
    defineProperty("size", o);
  }

  public String getFace() {
    return getPropertyAsString("face");
  }

  public void setFace(String o) {
    defineProperty("face", o);
  }

  public boolean getBold() {
    return getPropertyAsBoolean("bold");
  }

  public void setBold(boolean o) {
    defineProperty("bold", o);
  }

  public boolean getItalics() {
    return getPropertyAsBoolean("italics");
  }

  public void setItalics(boolean o) {
    defineProperty("italics", o);
  }

  public boolean getUnderline() {
    return getPropertyAsBoolean("underline");
  }

  public void setUnderline(boolean o) {
    defineProperty("underline", o);
  }

  public boolean getStrike() {
    return getPropertyAsBoolean("strike");
  }

  public void setStrike(boolean o) {
    defineProperty("strike", o);
  }

  public int getColor() {
    return getPropertyAsInteger("color");
  }

  public void setColor(int o) {
    defineProperty("color", o);
  }

  public boolean getDefaultColor() {
    return getPropertyAsBoolean("default_color");
  }

  public void setDefaultColor(boolean o) {
    defineProperty("default_color", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
