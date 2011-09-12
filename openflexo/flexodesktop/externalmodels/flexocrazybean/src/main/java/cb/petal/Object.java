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
 * Represents Object object
 *
 * @version $Id: Object.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Object extends QuiduObject implements Named {
  public Object(PetalNode parent, Collection params) {
    super(parent, "Object", params);
  }

  public Object() {
    super("Object");
  }

  /** @return instantiated class
   */
  public Class getInstantiatedClass() {
    return getRoot().getClassByQuidu(this);
  }

  @Override
public void setNameParameter(String o) {
    params.set(0, o);
  }

  @Override
public String getNameParameter() {
    return (String)params.get(0);
  }

  public List getCollaborators() {
    return (List)getProperty("collaborators");
  }

  public void setCollaborators(List o) {
    defineProperty("collaborators", o);
  }

  public String getClassName() {
    return getPropertyAsString("class");
  }

  public void setClassName(String o) {
    defineProperty("class", o);
  }

  public String getPersistence() {
    return getPropertyAsString("persistence");
  }

  public void setPersistence(String o) {
    defineProperty("persistence", o);
  }

  public boolean getMulti() {
    return getPropertyAsBoolean("multi");
  }

  public void setMulti(boolean o) {
    defineProperty("multi", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
