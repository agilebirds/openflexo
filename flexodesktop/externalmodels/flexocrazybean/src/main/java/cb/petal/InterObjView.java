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
 * Represents InterObjView object
 *
 * @version $Id: InterObjView.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class InterObjView extends QuiduView {
  public InterObjView(PetalNode parent, Collection params, int tag) {
    super(parent, "InterObjView", params, tag);
  }

  public InterObjView() {
    super("InterObjView");
  }

  public ItemLabel getLabel() {
    return (ItemLabel)getProperty("label");
  }

  public void setLabel(ItemLabel o) {
    defineProperty("label", o);
  }

  public int getIconHeight() {
    return getPropertyAsInteger("icon_height");
  }

  public void setIconHeight(int o) {
    defineProperty("icon_height", o);
  }

  public int getIconWidth() {
    return getPropertyAsInteger("icon_width");
  }

  public void setIconWidth(int o) {
    defineProperty("icon_width", o);
  }

  public int getAnnotation() {
    return getPropertyAsInteger("annotation");
  }

  public void setAnnotation(int o) {
    defineProperty("annotation", o);
  }

  public FocusOfControl getFocusOfControl() {
    return (FocusOfControl)getProperty("Focus_Of_Control");
  }

  public void setFocusOfControl(FocusOfControl o) {
    defineProperty("Focus_Of_Control", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
