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
 * Represents State object
 *
 * @version $Id: State.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class State extends QuidObject implements Named {
  public State(PetalNode parent, Collection params) {
    super(parent, "State", params);
  }

  public State() {
    super("State");
  }

  @Override
public void setNameParameter(String o) {
    params.set(0, o);
  }

  @Override
public String getNameParameter() {
    return (String)params.get(0);
  }

  public List getTransitions() {
    return (List)getProperty("transitions");
  }

  public void setTransitions(List o) {
    defineProperty("transitions", o);
  }

  public String getType() {
    return getPropertyAsString("type");
  }

  public void setType(String o) {
    defineProperty("type", o);
  }

  public List getActions() {
    return (List)getProperty("actions");
  }

  public void setActions(List o) {
    defineProperty("actions", o);
  }

  public StateMachine getStateMachine() {
    return (StateMachine)getProperty("statemachine");
  }

  public void setStateMachine(StateMachine o) {
    defineProperty("statemachine", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
