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
 * Represents SubSystem object
 *
 * @version $Id: SubSystem.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class SubSystem extends QuidObject implements Named, StereoTyped {
  public SubSystem(PetalNode parent, Collection params) {
    super(parent, "SubSystem", params);
  }

  public SubSystem() {
    super("SubSystem");
  }

  @Override
public void setNameParameter(String o) {
    params.set(0, o);
  }

  @Override
public String getNameParameter() {
    return (String)params.get(0);
  }

  @Override
public String getStereotype() {
    return getPropertyAsString("stereotype");
  }

  @Override
public void setStereotype(String o) {
    defineProperty("stereotype", o);
  }

  public List getPhysicalModels() {
    return (List)getProperty("physical_models");
  }

  public void setPhysicalModels(List o) {
    defineProperty("physical_models", o);
  }

  public List getPhysicalPresentations() {
    return (List)getProperty("physical_presentations");
  }

  public void setPhysicalPresentations(List o) {
    defineProperty("physical_presentations", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
