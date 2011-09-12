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
package cb.generator;

import java.io.PrintWriter;

import cb.petal.Parameter;

/**
 * Simple representation of a method parameter.
 *
 * @version $Id: ParameterImpl.java,v 1.3 2011/09/12 11:47:23 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class ParameterImpl extends NodeImpl implements cb.generator.Parameter {
  private   String  type;
  protected cb.petal.Parameter parameter;

  public ParameterImpl() {  }

  public void               setParameter(cb.petal.Parameter p) { parameter = p; }
  public cb.petal.Parameter getParameter()                     { return parameter; }

  @Override
public void   setType(String p) { type = p; }
  @Override
public String getType() { return type; }

  /** Default implementation prints Java code
   */
  @Override
public void dump(PrintWriter stream) {
    print(stream, "", getAccess(), " ");
    stream.print(getType() + " " + getName());
  }

  @Override
public boolean equals(Object o) {
    if(o instanceof Parameter) {
      Parameter p = (Parameter)o;

      return getType().equals(p.getType()) && getName().equals(p.getName());
    } else
      return false;
  }
}
