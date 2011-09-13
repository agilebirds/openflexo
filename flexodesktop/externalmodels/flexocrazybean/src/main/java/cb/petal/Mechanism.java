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
 * Represents Mechanism object
 *
 * @version $Id: Mechanism.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Mechanism extends PetalObject implements Tagged {
  public Mechanism(PetalNode parent, Collection params) {
    super(parent, "Mechanism", params);
    setTag(tag);
  }

  public Mechanism() {
    super("Mechanism");
  }

  private int tag = -1;

  @Override
public void setTag(int t) { tag = t; }

  @Override
public int  getTag()      { return tag; }

  public List getLogicalModels() {
    return (List)getProperty("logical_models");
  }

  public void setLogicalModels(List o) {
    defineProperty("logical_models", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
