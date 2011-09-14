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
 * Values like in (value Text "foo")
 *
 * @version $Id: Value.java,v 1.3 2011/09/12 11:46:47 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Value extends Literal {
  static final long serialVersionUID=-1364810248783653817L;

  private String        name;
  private StringLiteral value;

  public Value(String name, StringLiteral value) {
    super("value");
    setValueName(name);
    setValue(value);
  }

  public void   setValueName(String n) { name = n.intern(); }
  public String getValueName()         { return name; }
  public void          setValue(StringLiteral v) { value = v; }
  public StringLiteral getValue()                { return value; }

  public String getStringValue() { return value.getValue(); }

  @Override
public String toString() { return "(value " + name + " " + value + ")"; }
  
  @Override
public void accept(Visitor v) {
    v.visit(this);
  }

  @Override
public java.lang.Object getLiteralValue() {
    return name + " \"" + value.getLiteralValue() + '"';
  }

  @Override
public boolean equals(java.lang.Object o) {
    return (o instanceof Value) && (((Value)o).value.equals(this.value)) &&
      (((Value)o).name.equals(this.name));
  }
}
