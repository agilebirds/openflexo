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
 * Represents ClassView object
 *
 * @version $Id: ClassView.java,v 1.3 2011/09/12 11:46:48 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class ClassView extends QuiduView implements Qualified {
  static final long serialVersionUID = 2535811997000685425L;

  public ClassView(PetalNode parent, Collection params, int tag) {
    super(parent, "ClassView", params, tag);
  }

  protected ClassView(PetalNode parent, String name, Collection params, int tag) {
    super(parent, name, params, tag);
  }

  public ClassView() {
    super("ClassView");
  }

  protected ClassView(String name) {
    super(name);
  }

  /**
   * @param name String like "Logical View::University::Professor"
   */
  @Override
public void setQualifiedNameParameter(String name) {
    params.set(1, name);
  }

  @Override
public String getQualifiedNameParameter() {
    return (String)params.get(1);
  }

  public boolean getShowCompartmentStereotypes() {
    return getPropertyAsBoolean("ShowCompartmentStereotypes");
  }

  public void setShowCompartmentStereotypes(boolean o) {
    defineProperty("ShowCompartmentStereotypes", o);
  }

  public boolean getIncludeAttribute() {
    return getPropertyAsBoolean("IncludeAttribute");
  }

  public void setIncludeAttribute(boolean o) {
    defineProperty("IncludeAttribute", o);
  }

  public boolean getIncludeOperation() {
    return getPropertyAsBoolean("IncludeOperation");
  }

  public void setIncludeOperation(boolean o) {
    defineProperty("IncludeOperation", o);
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

  public int getAnnotation() {
    return getPropertyAsInteger("annotation");
  }

  public void setAnnotation(int o) {
    defineProperty("annotation", o);
  }

  public Compartment getCompartment() {
    return (Compartment)getProperty("compartment");
  }

  public void setCompartment(Compartment o) {
    defineProperty("compartment", o);
  }

  public boolean getAutoResize() {
    return getPropertyAsBoolean("autoResize");
  }

  public void setAutoResize(boolean o) {
    defineProperty("autoResize", o);
  }

  @Override
public void accept(Visitor v) {
    v.visit(this);
  }
}
