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
 * Tuple of integer values like (1520, 96). Usually used to define coordinates.
 *
 * @version $Id: Location.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Location extends Literal {
  static final long serialVersionUID=7569774575687169931L;

  private int value1, value2;

  public Location(int first, int second) {
    super("<location>");
    value1 = first;
    value2 = second;
  }

  public int getFirstValue() { return value1; }
  public void setFirstValue(int v) { value1 = v; }

  public int getSecondValue() { return value2; }
  public void setSecondValue(int v) { value2 = v; }

  @Override
public String toString() { return "(" + value1 + ", " + value2 + ")"; }

  @Override
public java.lang.Object getLiteralValue() {
    return toString();
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }

  @Override
public boolean equals(java.lang.Object o) {
    return (o instanceof Location) && (((Location)o).value1 == this.value1)
      && (((Location)o).value2 == this.value2);
  }
}
