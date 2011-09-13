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
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Floating point literal (in fact a double).
 *
 * @version $Id: FloatLiteral.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class FloatLiteral extends Literal {
  static final long serialVersionUID=1505488095051523558L;

  private double value;
  private static NumberFormat format;

  static {
    format = NumberFormat.getInstance(Locale.ENGLISH);
    format.setMaximumFractionDigits(6);
    format.setMinimumFractionDigits(6);
  }

  public FloatLiteral(double value) {
    super("<float>");
    this.value = value;
  }

  public double getValue()         { return value; }
  public void   setValue(double d) { value = d; }

  @Override
public java.lang.Object getLiteralValue() {
    return new Float(value);
  }

  @Override
public String toString() {
    return format.format(value);
  }
  
  @Override
public void accept(Visitor v) {
    v.visit(this);
  }

  @Override
public boolean equals(java.lang.Object o) {
    return (o instanceof FloatLiteral) && (((FloatLiteral)o).value == this.value);
  }
}
