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
 * Represents TransView object
 *
 * @version $Id: TransView.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class TransView extends QuiduView {
  public TransView(PetalNode parent, Collection params, int tag) {
    super(parent, "TransView", params, tag);
  }

  public TransView() {
    super("TransView");
  }

  public boolean getXOffset() {
    return getPropertyAsBoolean("x_offset");
  }

  public void setXOffset(boolean o) {
    defineProperty("x_offset", o);
  }

  public SegLabel getLabel() {
    return (SegLabel)getProperty("label");
  }

  public void setLabel(SegLabel o) {
    defineProperty("label", o);
  }

  public List getVertices() {
    return (List)getProperty("vertices");
  }

  public void setVertices(List o) {
    defineProperty("vertices", o);
  }

  public Location getOriginAttachment() {
    return (Location)getProperty("origin_attachment");
  }

  public void setOriginAttachment(Location o) {
    defineProperty("origin_attachment", o);
  }

  public Location getTerminalAttachment() {
    return (Location)getProperty("terminal_attachment");
  }

  public void setTerminalAttachment(Location o) {
    defineProperty("terminal_attachment", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
