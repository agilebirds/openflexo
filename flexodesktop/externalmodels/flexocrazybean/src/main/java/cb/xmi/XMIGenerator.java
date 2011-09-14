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
package cb.xmi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import ru.novosoft.uml.foundation.core.MAbstraction;
import ru.novosoft.uml.foundation.core.MAssociation;
import ru.novosoft.uml.foundation.core.MAttribute;
import ru.novosoft.uml.foundation.core.MClassifier;
import ru.novosoft.uml.foundation.core.MDependency;
import ru.novosoft.uml.foundation.core.MGeneralization;
import ru.novosoft.uml.foundation.core.MModelElement;
import ru.novosoft.uml.foundation.core.MOperation;
import ru.novosoft.uml.foundation.core.MUsage;
import ru.novosoft.uml.model_management.MModel;
import ru.novosoft.uml.model_management.MPackage;
import ru.novosoft.uml.xmi.XMIWriter;
import cb.petal.Association;
import cb.petal.ClassAttribute;
import cb.petal.DependencyRelationship;
import cb.petal.DescendingVisitor;
import cb.petal.InheritanceRelationship;
import cb.petal.LogicalCategory;
import cb.petal.Module;
import cb.petal.Operation;
import cb.petal.PetalFile;
import cb.petal.PetalObject;
import cb.petal.QuidObject;
import cb.petal.RealizeRelationship;
import cb.petal.SubSystem;
import cb.petal.UseCase;
import cb.petal.UseCaseCategory;
import cb.petal.UsesRelationship;

/**
 * Convert a Rose petal file into the <a href="http://xml.coverpages.org/xmi.html">XMI</a>
 * format using NovoSoft's <a href="http://nsuml.sourceforge.net/">
 * NSUML</a> package which implements the entities defined in the
 * <a href="http://www.omg.org/uml/">UML</a> specification 1.3.
 *
 * @version $Id: XMIGenerator.java,v 1.3 2011/09/12 11:47:01 gpolet Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class XMIGenerator extends DescendingVisitor {
  /** Where to dump the XMI file
   */
  protected String     dump;

  /** Which factory to use
   */
  protected XMIFactory factory;

  /** The Rose Petal file to convert
   */
  protected PetalFile  tree;

  /** The XMI model being set up
   */
  protected MModel     model;

  /**  Stack<MPackage>
   */
  Stack packages = new Stack();

  /** The current package level (may be nested)
   */
  MPackage pack;

  /** Register created objects by the quid of the petal object.
   */
  protected HashMap quid_map = new HashMap(); // Map<quid, MClassifier>

  protected HashMap package_map = new HashMap(); // Map<ClassCategory, MPackage>

  protected final void addObject(String quid, MClassifier obj) {
    quid_map.put(quid, obj);
  }

  protected void removeObject(String quid) {
    quid_map.remove(quid);
  }

  protected final MClassifier getClassifier(String quid) {
    return (MClassifier)quid_map.get(quid);
  }

  /**
   * @param tree the Rose petal file to convert
   * @param dump where to dump the generated XMI file
   */
  public XMIGenerator(PetalFile tree, String dump) {
    this.dump = dump;
    this.tree = tree;

    factory = getFactory();
    model   = factory.createModel();
    pack    = model;
  }

  /** Start generation of XMI code.
   */
  public void start() {
    /* Run a first pass visitor to add all packages, classes, use
     * cases, etc.  that may be referenced from different places, or
     * even be referenced in a forward declaration.
     */
    tree.accept(new DescendingVisitor() {
      private void addPackage(QuidObject obj, MPackage p) {
	package_map.put(obj, p);

	pack.addOwnedElement(p);
	packages.push(pack); // Save old value
	pack = p;

	super.visitObject(obj); // Default traversal

	pack = (MPackage)packages.pop();
      }

      @Override
	public void visit(LogicalCategory obj) {
	addPackage(obj, factory.createPackage(obj));
      }

      @Override
	public void visit(UseCaseCategory obj) {
	addPackage(obj, factory.createPackage(obj));
      }

      @Override
	public void visit(SubSystem sub) {
	/** ArgoUML can't handle subsystems
	 */
	addPackage(sub, factory.createPackage(sub));
      }

      @Override
	public void visit(cb.petal.Class clazz) {
	String quid = clazz.getQuid();

	MClassifier cl = factory.createClass(clazz);
	pack.addOwnedElement(cl);
	addObject(quid, cl);
      }

      @Override
	public void visit(UseCase caze) {
	String quid = caze.getQuid();

	MClassifier cl = factory.createUseCase(caze);
	pack.addOwnedElement(cl);
	addObject(quid, cl);
      }

      @Override
	public void visit(Module module) {
	String quid = module.getQuid();

	MClassifier cl = factory.createComponent(module);
	pack.addOwnedElement(cl);
	addObject(quid, cl);
      }
    });

    tree.accept(this);
  }

  /** Override this if you don't like the default factory
   */
  protected XMIFactory getFactory() {
    return new XMIFactory(tree, this);
  }

  private void setPackage(QuidObject obj) {
    MPackage p = (MPackage)package_map.get(obj);

    packages.push(pack); // Save old value
    pack = p;

    visitObject(obj); // Default traversal

    pack = (MPackage)packages.pop();
  }

  @Override
public void visit(LogicalCategory obj) {
    setPackage(obj);
  }

  @Override
public void visit(UseCaseCategory obj) {
    setPackage(obj);
  }

  @Override
public void visit(SubSystem obj) {
    /** ArgoUML can't handle subsystems
     */
    setPackage(obj);
  }

  @Override
public void visit(ClassAttribute attr) {
    MAttribute  a     = factory.createAttribute(attr);
    MClassifier clazz = getContainingClassifier(attr);

    clazz.addFeature(a);
  }

  @Override
public void visit(Operation op) {
    MOperation m = factory.createOperation(op);
    MClassifier clazz = getContainingClassifier(op);
    clazz.addFeature(m);
  }

  @Override
public void visit(InheritanceRelationship rel) {
    MGeneralization gen = factory.createGeneralization(rel);
    pack.addOwnedElement(gen);
  }

  @Override
public void visit(UsesRelationship rel) {
    MUsage usage = factory.createUsage(rel);
    pack.addOwnedElement(usage);
  }

  @Override
public void visit(DependencyRelationship rel) {
    MDependency dependency = factory.createDependency(rel);
    pack.addOwnedElement(dependency);
  }

  @Override
public void visit(RealizeRelationship rel) {
    MAbstraction real = factory.createRealization(rel);
    pack.addOwnedElement(real);
  }

  /** If this association contains an association class, use that object,
   * otherwise create new object.
   */
  @Override
public void visit(Association assoc) {
    MAssociation a;

    /* ArgoUML/Poseidon can't handle AssociationClass correctly
     */
    cb.petal.Class clazz = assoc.getAssociationClass();

    if(clazz != null) {
      a = (MAssociation)getClassifier(clazz.getQuid());
      factory.setupAssociation(assoc, a);
    } else{
      a = factory.createAssociation(assoc);
      pack.addOwnedElement(a);
    }
  }

  /*************************** Utility method *******************************/

  /** Search for element of given name in model (and all sub-packages)
   * @param name name to look for with getName()
   * @param clazz Class searched element is an instance of
   * @return found element or null
   */
  public MModelElement searchElement(String name, java.lang.Class clazz) {
    return searchElement(model, name, clazz);
  }

  private static MModelElement searchElement(MPackage pack, String name,
					     java.lang.Class clazz)
  {
    for(Iterator i = pack.getOwnedElements().iterator(); i.hasNext(); ) {
      MModelElement elem = (MModelElement)i.next();

      if(name.equals(elem.getName()) && clazz.isInstance(elem))
	return elem;
      else if(elem instanceof MPackage) {
	MModelElement found = searchElement((MPackage)elem, name, clazz);

	if(found != null)
	  return found;
      }
    }

    return null;
  }

  /** @return XMI object for containing class of obj
   */
  protected final MClassifier getContainingClassifier(PetalObject obj) {
    return getClassifier(((QuidObject)obj.getParent()).getQuid());
  }

  public void dump() throws IOException, ru.novosoft.uml.xmi.IncompleteXMIException{
    XMIWriter writer = new XMIWriter(model, dump);
    writer.gen();
  }

  /** @return generated model
   */
  public MModel getModel() {
    return model;
  }

  /** @return current package
   */
  public MPackage getPackage()           { return pack; }
}
