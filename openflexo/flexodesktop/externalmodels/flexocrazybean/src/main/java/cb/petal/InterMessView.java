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
 * Represents InterMessView object
 *
 * @version $Id: InterMessView.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class InterMessView extends View {
  public InterMessView(PetalNode parent, Collection params, int tag) {
    super(parent, "InterMessView", params, tag);
  }

  public InterMessView() {
    super("InterMessView");
  }

  public SegLabel getLabel() {
    return (SegLabel)getProperty("label");
  }

  public void setLabel(SegLabel o) {
    defineProperty("label", o);
  }

  public Tag getFocusSrc() {
    return (Tag)getProperty("Focus_Src");
  }

  public void setFocusSrc(Tag o) {
    defineProperty("Focus_Src", o);
  }

  public Tag getFocusEntry() {
    return (Tag)getProperty("Focus_Entry");
  }

  public void setFocusEntry(Tag o) {
    defineProperty("Focus_Entry", o);
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

  public int getOrdinal() {
    return getPropertyAsInteger("ordinal");
  }

  public void setOrdinal(int o) {
    defineProperty("ordinal", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
