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
 * Represents UseCaseView object
 *
 * @version $Id: UseCaseView.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class UseCaseView extends QuiduView implements Qualified {
  static final long serialVersionUID = 8100428687661863064L;

  public UseCaseView(PetalNode parent, Collection params, int tag) {
    super(parent, "UseCaseView", params, tag);
  }

  /**
   * @param name String like "Logical View::University::Professor"
   */
  @Override
public void setQualifiedNameParameter(String name) {
    params.set(0, name);
  }

  @Override
public String getQualifiedNameParameter() {
    return (String)params.get(0);
  }

  public UseCaseView() {
    super("UseCaseView");
  }

  public ItemLabel getLabel() {
    return (ItemLabel)getProperty("label");
  }

  public void setLabel(ItemLabel o) {
    defineProperty("label", o);
  }

  public ItemLabel getStereotype() {
    return (ItemLabel)getProperty("stereotype");
  }

  public void setStereotype(ItemLabel o) {
    defineProperty("stereotype", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}

