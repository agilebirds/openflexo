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
 * Boolean literal which may obviously either be true or false.
 *
 * @version $Id: BooleanLiteral.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class BooleanLiteral extends Literal {
  private boolean value;
  static final long serialVersionUID=-6022972681974975784L;

  public BooleanLiteral(boolean value) {
    super("<boolean>");
    this.value = value;
  }

  public boolean getValue()          { return value; }

  public void setValue(boolean v)          { value = v; }

  @Override
public String toString() { return ("" + value).toUpperCase(); }

  @Override
public java.lang.Object getLiteralValue() {
    if(value)
      return Boolean.TRUE;
    else
      return Boolean.FALSE;
  }
  
  @Override
public void accept(Visitor v) {
    v.visit(this);
  }

  @Override
public boolean equals(java.lang.Object o) {
    return (o instanceof BooleanLiteral) && (((BooleanLiteral)o).value == this.value);
  }
}
