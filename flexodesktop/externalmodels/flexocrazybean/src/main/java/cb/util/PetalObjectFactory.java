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
package cb.util;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import cb.petal.AssocAttachView;
import cb.petal.Association;
import cb.petal.AssociationViewNew;
import cb.petal.AttachView;
import cb.petal.ClassAttribute;
import cb.petal.ClassDiagram;
import cb.petal.ClassUtility;
import cb.petal.ClassView;
import cb.petal.Font;
import cb.petal.InheritView;
import cb.petal.Inheritable;
import cb.petal.InheritanceRelationship;
import cb.petal.ItemLabel;
import cb.petal.LogicalCategory;
import cb.petal.NoteView;
import cb.petal.Operation;
import cb.petal.PetalFile;
import cb.petal.PetalNode;
import cb.petal.QuidObject;
import cb.petal.RealizeRelationship;
import cb.petal.RealizeView;
import cb.petal.Relationship;
import cb.petal.RelationshipView;
import cb.petal.Role;
import cb.petal.RoleView;
import cb.petal.SegLabel;
import cb.petal.StringLiteral;
import cb.petal.UseCase;
import cb.petal.UseCaseCategory;
import cb.petal.UseCaseDiagram;
import cb.petal.UseCaseView;
import cb.petal.UsesRelationship;
import cb.petal.UsesView;

/**
 * Create empty Petal objects with just some initial properties (the quid
 * in particular) set up. The user can then add them to model, i.e. PetalFile
 * object. Some objects are created by reading serialized templates from the
 * templates directory. init() is called on them usually automagically when they're
 * added to the model.
 *
 * @see PetalFile
 * @version $Id: PetalObjectFactory.java,v 1.2 2011/09/12 11:47:29 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class PetalObjectFactory {
  private static PetalObjectFactory instance = new PetalObjectFactory();

  public static PetalObjectFactory getInstance() {
    return instance;
  }

  public static void setInstance(PetalObjectFactory inst) {
    instance = inst;
  }

  protected PetalObjectFactory() {}

  /** Read object from templates directory
   */
  protected static PetalNode getTemplate(String name) {
    PetalNode obj = null;

    try {
      String file = "/templates/" + name + ".ser";
      InputStream is = PetalObjectFactory.class.getResourceAsStream(file);

      if(is == null)
	throw new RuntimeException("File not found: " + file);

      ObjectInputStream ois = new ObjectInputStream(is);

      obj = (PetalNode)ois.readObject();
      ois.close();
    } catch(Exception e) {
      throw new RuntimeException("Could not create template for " +
				 name + ":\n" + e);
    }

    return obj;
  }

  /******************* Data model create methods ********************/

  /** Creates empty model.
   */
  public PetalFile createModel() {
    PetalFile file = (PetalFile)getTemplate("PetalFile");

    return file;
  }

  /** Creates empty class object with just the name and the quid set.
   * init() is called on it after has been added to a model.
   *
   * @see LogicalCategory#addToModel(cb.petal.Class)
   */
  public cb.petal.Class createClass(String name) {
    cb.petal.Class clazz = new cb.petal.Class();

    clazz.setQuid(PetalFile.getQuid());
    clazz.setParameterList(new ArrayList(Arrays.asList(new String[]{ name })));

    return clazz;
  }

  /** Creates use case object with just the name and the quid set.
   * init() is called on it after has been added to a model.
   *
   * @see UseCaseCategory#addToModel(UseCase)
   */
  public UseCase createUseCase(String name) {
    UseCase caze = new UseCase();

    caze.setQuid(PetalFile.getQuid());
    caze.setParameterList(new ArrayList(Arrays.asList(new String[]{ name })));

    return caze;
  }

  /** Just like createClass() except that it sets the stereotype to "Interface".
   */
  public cb.petal.Class createInterface(String name) {
    cb.petal.Class clazz = createClass(name);

    clazz.setStereotype("Interface");
    return clazz;
  }

  /** Creates empty utility class object with just the name and the quid set.
   * init() is called on it after has been added to a model.
   *
   * @see LogicalCategory#addToModel(cb.petal.Class)
   */
  public cb.petal.ClassUtility createClassUtility(String name) {
    ClassUtility   clazz = new ClassUtility();
    clazz.setParameterList(new ArrayList(Arrays.asList(new String[]{ name })));
    clazz.setQuid(PetalFile.getQuid());
    return clazz;
  }

  /** Creates new operation (aka method)
   *
   * @see cb.petal.Class#addOperation(cb.petal.Operation)
   */
  public Operation createOperation(String name, String result, cb.petal.List params) {
    Operation op = (Operation)getTemplate("Operation");

    op.setQuid(PetalFile.getQuid());
    op.setNameParameter(name);

    if(result != null)
      op.setResult(result);
    else
      op.removeProperty("result");

    op.setParameters(params);

    return op;
  }

  /** Creates new class attribute (aka field)
   *
   * @see cb.petal.Class#addClassAttribute(cb.petal.ClassAttribute)
   */
  public ClassAttribute createClassAttribute(String name, String type) {
    ClassAttribute attr = new ClassAttribute(null, Arrays.asList(new String[]{name}));

    attr.setQuid(PetalFile.getQuid());
    attr.setType(type);

    return attr;
  }

  /** Creates empty class category (use case view).
   */
  public UseCaseCategory createUseCaseCategory(String name) {
   UseCaseCategory cat = (UseCaseCategory)getTemplate("UseCaseCategory");

    cat.setQuid(PetalFile.getQuid());
    cat.setNameParameter(name);
    cat.getFirstUseCaseDiagram().setQuid(PetalFile.getQuid());

    return cat;
  }

  /** Creates empty class category (logical view).
   *
   * @see LogicalCategory#addToModel(cb.petal.LogicalCategory)
   */
  public LogicalCategory createLogicalCategory(String name) {
    LogicalCategory cat = (LogicalCategory)getTemplate("LogicalCategory");

    cat.setQuid(PetalFile.getQuid());
    cat.setNameParameter(name);
    cat.getFirstClassDiagram().setQuid(PetalFile.getQuid());
    cat.removeProperty("quidu");
    cat.removeProperty("subsystem");
    cat.removeProperty("global");

    return cat;
  }

  private void setupRelationship(Relationship rel, Inheritable clazz, // Should be Inheritable
				 Inheritable super_class) {
    rel.setQuid(PetalFile.getQuid());
    rel.setSupplier(super_class.getQualifiedName());
    rel.setQuidu(super_class.getQuid());
    rel.setParent(clazz);
  }

  /** Create InheritanceRelationship between two classes, this method
   * is called by Class.addSuperClass().  Shouldn't be called directly
   * I think. If you really want to, don't forget to call init().
   *
   * @see cb.petal.Inheritable#addSuperClassifier(cb.petal.Inheritable)
   */
  public InheritanceRelationship createInheritanceRelationship(Inheritable clazz,
							       Inheritable super_class) {
    InheritanceRelationship rel = new InheritanceRelationship();
    setupRelationship(rel, clazz, super_class);
    return rel;
  }

  /** Create RealizeRelationship between class and an interface,
   * called by Class.addImplementedInterface().  Shouldn't be called
   * directly I think. If you really want to, don't forget to call
   * init().
   *
   * @see cb.petal.Class#addImplementedInterface(cb.petal.Class)
   */
  public RealizeRelationship createRealizeRelationship(cb.petal.Class clazz,
						       cb.petal.Class inter) {
    RealizeRelationship rel = new RealizeRelationship();
    setupRelationship(rel, clazz, inter);
    return rel;
  }

  /** Create UsesRelationship between class and an interface,
   * called by Class.addUsedClass().  Shouldn't be called
   * directly I think. If you really want to, don't forget to call
   * init().
   *
   * @see cb.petal.Class#addUsedClass(cb.petal.Class)
   */
  public UsesRelationship createUsesRelationship(cb.petal.Class clazz,
						 cb.petal.Class clazz2) {
    UsesRelationship rel = new UsesRelationship();
    setupRelationship(rel, clazz, clazz2);
    return rel;
  }

  private int assoc_counter = 1;

  public final String getAnonymousName() {
    return "$UNNAMED$" + assoc_counter++;
  }

  /** Create Association between two classes. Cardinality and other properties
   * can be configured in the roles of the returned object.
   * Classes must have already been added to the model.
   */
  public Association createAssociation(String name, cb.petal.Class clazz1,
				       cb.petal.Class clazz2) {
    return createAssociation_(name, clazz1, clazz2);
  }

  /** Create association without explicit name, it will have an invisible
   * anonymous name.
   */
  public Association createAssociation(cb.petal.Class clazz1,
				       cb.petal.Class clazz2) {
    return createAssociation_(getAnonymousName(), clazz1, clazz2);
  }

  /** Create Association between two use cases.
   * Use cases must have already been added to the model.
   */
  public Association createAssociation(String name, UseCase case1, UseCase case2) {
    return createAssociation_(name, case1, case2);
  }

  public Association createAssociation(String name, cb.petal.Class clazz, UseCase caze) {
    return createAssociation_(name, clazz, caze);
  }

  public Association createAssociation(cb.petal.Class clazz, UseCase caze) {
    return createAssociation_(getAnonymousName(), clazz, caze);
  }

  /** Create association without explicit name, it will have an invisible
   * anonymous name.
   */
  public Association createAssociation(UseCase case1, UseCase case2) {
    return createAssociation_(getAnonymousName(), case1, case2);
  }

  private Association createAssociation_(String name, QuidObject clazz1, QuidObject clazz2) {
    Association assoc = (Association)getTemplate("Association");
    assoc.setQuid(PetalFile.getQuid());
    assoc.setNameParameter(name);

    Role first  = assoc.getFirstRole();
    Role second = assoc.getSecondRole();

    first.setNameParameter(getAnonymousName());
    second.setNameParameter(getAnonymousName());
    first.setQuid(PetalFile.getQuid());
    second.setQuid(PetalFile.getQuid());
    first.setQuidu(clazz1.getQuid());
    second.setQuidu(clazz2.getQuid());
    first.setParent(assoc);
    second.setParent(assoc);
    first.setSupplier(clazz1.getQualifiedName());
    second.setSupplier(clazz2.getQualifiedName());

    return assoc;
  }

  /******************* View create methods ********************/

  /** Creates class view for given class and sets the qualified name
   * and quidu for the referenced class accordingly. The tag, i.e.,
   * the index in the views list (@12, e.g.), is set when the view is
   * added to the model. It is also set them for the ItemLabel objects
   * associated with the view.
   *
   * <p>These properties/view objects will be set (if defined in given class):
   * quidu, label, stereotype, QualifiedNameParameter
   * </p>
   * @see ClassDiagram#addToView(ClassView)
   */
  public ClassView createClassView(cb.petal.Class clazz) {
    ClassView view;
    String stereo = clazz.getStereotype();

    if(clazz.isActor()) {
      view = (ClassView)getTemplate("ActorView");
    } else if(stereo != null) {
      view = (ClassView)getTemplate("StereotypeView");
      ItemLabel label = (ItemLabel)view.getProperty("stereotype");
      label.setLabel("<<" + stereo + ">>");
    } else
      view = (ClassView)getTemplate("ClassView");

    view.setQuidu(clazz.getQuid());
    view.setNameParameter(clazz.getClassType());
    view.setQualifiedNameParameter(clazz.getQualifiedName());

    ItemLabel label = (ItemLabel)view.getProperty("label");
    label.setLabel(clazz.getNameParameter());

    return view;
  }

  private static Dimension getNoteViewSize(StringLiteral text) {
    int rows = 0, max_columns = 1;

    for(Iterator i=text.getLines().iterator(); i.hasNext(); ) {
      String line = (String)i.next();

      if(line.length() > max_columns)
        max_columns = line.length();

      rows++;
    }

    return new Dimension(max_columns * 30, rows * 50);
  }

  /** @return new note view with given text
   */
  public NoteView createNoteView(String text) {
    NoteView view = (NoteView)getTemplate("NoteView");
    StringLiteral literal = new StringLiteral(text);
    Dimension dim = getNoteViewSize(literal);

    ItemLabel label = view.getLabel();
    label.setParent(view);
    label.setNlines(literal.getLines().size());
    label.setMaxWidth((int)dim.getWidth());
    label.defineProperty("label", literal);
    view.setWidth((int)dim.getWidth() + 20);
    view.setHeight((int)dim.getHeight() + 20);

    return view;
  }

  public AttachView createAttachView() {
    AttachView view = (AttachView)getTemplate("AttachView");
    return view;
  }

  /** @return new item label with given text
   */
  public ItemLabel createItemLabel(String text) {
    ItemLabel label = (ItemLabel)getTemplate("ItemLabel");
    label.setLabel(text);
    label.setMaxWidth(getMaxWidth(text));
    return label;
  }

  /** @return new segment abel with given text
   */
  public SegLabel createSegLabel(String text) {
    SegLabel label = (SegLabel)getTemplate("SegLabel");
    label.setLabel(text);
    label.setMaxWidth(getMaxWidth(text));
    return label;
  }

  private static int getMaxWidth(String s) {
    return s.length() * 25; // Heuristic!
  }

  public AssocAttachView createAssocAttachView() {
    AssocAttachView view = (AssocAttachView)getTemplate("AssocAttachView");
    return view;
  }

  private void setupRelationshipView(RelationshipView view, Relationship rel) {
     String label = rel.getLabel();

     if(label != null) {
    	ItemLabel l = createItemLabel(label);
    	l.setParent(view);
    	view.setLabel(l);
    	view.setNameParameter(label);
     }

     String stereo = rel.getStereotype();

     if(stereo != null) {
    	SegLabel l = createSegLabel("<<" + stereo + ">>");
    	l.setParent(view);
    	view.setStereotype(l);
     }

  }

  /** @return list of inherit view objects since a class may extend
   * multiple classes (not in Java, I know).  The client and supplier
   * tags (@5, e.g. which are references to the corresponding class
   * view indexes) are set when the views are added to the model.
   *
   * @see ClassDiagram#addToView(cb.petal.InheritView)
   */
  public java.util.List createInheritViews(Inheritable clazz) {
    ArrayList list = new ArrayList();

    for(Iterator i = clazz.getSuperclassList().getElements().iterator();
	i.hasNext();)
    {
      InheritanceRelationship rel = (InheritanceRelationship)i.next();

      InheritView view = (InheritView)getTemplate("InheritView");
      view.setQuidu(rel.getQuid());
      setupRelationshipView(view, rel);
      list.add(view);
    }

    return list;
  }

  /** @return list of realize view objects since a class may implement
   * multiple interfaces.  The client and supplier
   * tags (@5, e.g. which are references to the corresponding class
   * view indexes) are set when the views are added to the model.
   *
   * @see ClassDiagram#addToView(cb.petal.RealizeView)
   */
  public java.util.List createRealizeViews(cb.petal.Class clazz) {
    ArrayList list = new ArrayList();

    for(Iterator i = clazz.getRealizedInterfacesList().getElements().iterator();
	i.hasNext();)
    {
      RealizeRelationship rel = (RealizeRelationship)i.next();
      RealizeView view = (RealizeView)getTemplate("RealizeView");
      view.setQuidu(rel.getQuid());
      setupRelationshipView(view, rel);
      list.add(view);
    }

    return list;
  }

  /** @return list of uses view objects. The client and supplier
   * tags (@5, e.g. which are references to the corresponding class
   * view indexes) are set when the views are added to the model.
   *
   * @see ClassDiagram#addToView(cb.petal.UsesView)
   */
  public java.util.List createUsesViews(cb.petal.Class clazz) {
    ArrayList list = new ArrayList();

    for(Iterator i = clazz.getUsedClassesList().getElements().iterator();
  	i.hasNext();)
    {
      UsesRelationship rel = (UsesRelationship)i.next();
      UsesView view = (UsesView)getTemplate("UsesView");
      view.setQuidu(rel.getQuid());
      setupRelationshipView(view, rel);
      list.add(view);
    }

    return list;
  }

  /** Creates view for given association.
   *
   * @param show_label show association name
   * @see ClassDiagram#addToView(cb.petal.ClassView)
   */
  public AssociationViewNew createAssociationView(Association assoc, boolean show_label) {
    AssociationViewNew view = (AssociationViewNew)getTemplate("AssociationViewNew");

    view.setQuidu(assoc.getQuid());
    view.setNameParameter(assoc.getNameParameter());

    if(show_label) {
      SegLabel label = createSegLabel(assoc.getNameParameter());
      label.setParent(view);
      label.setAnchor(1);
      label.setMaxWidth(200);

      Font font = new Font();
      font.setParent(label);
      font.setItalics(true);
      label.setFont(font);

      view.setLabel(label);
    }

    Role role1      = assoc.getFirstRole();
    Role role2      = assoc.getSecondRole();
    RoleView first  = view.getFirstRoleView();
    RoleView second = view.getSecondRoleView();

    first.setNameParameter(role1.getNameParameter());
    second.setNameParameter(role2.getNameParameter());
    first.setQuidu(role1.getQuid());
    second.setQuidu(role2.getQuid());
    first.setParent(view);
    second.setParent(view);

    addLabels(first, role1);
    addLabels(second, role2);

    return view;
  }

  private void addLabels(RoleView view, Role role) {
    String name = role.getRoleName();

    if(name != null) {
      SegLabel label = createSegLabel("+" + name);
      label.setParent(view);
      label.setMaxWidth(200);
      label.setAnchor(1);
      view.addProperty("label", label);
      view.setNameParameter(name);
    }

    String constraint = role.getConstraints();

    if(constraint != null) {
      SegLabel label = createSegLabel("{" + constraint + "}");
      label.setParent(view);
      label.setAnchor(2);
      view.addProperty("label", label);
    }
  }

  public AssociationViewNew createAssociationView(Association assoc) {
    return createAssociationView(assoc, !assoc.getNameParameter().startsWith("$"));
  }

  /** Creates use case view for given class and sets the qualified name
   * and quidu for the referenced class accordingly. The tag, i.e.,
   * the index in the views list (@12, e.g.), is set when the view is
   * added to the model. It is also set them for the ItemLabel objects
   * associated with the view.
   *
   * @see UseCaseDiagram#addToView(UseCaseView)
   */
  public UseCaseView createUseCaseView(UseCase caze) {
    UseCaseView view;
    String stereo = caze.getStereotype();

    if(stereo != null) {
      view = (UseCaseView)getTemplate("UseCaseStereotypeView");
      ItemLabel label = (ItemLabel)view.getProperty("stereotype");
      label.setLabel("<<" + stereo + ">>");
    } else
      view = (UseCaseView)getTemplate("UseCaseView");

    view.setQuidu(caze.getQuid());
    view.setQualifiedNameParameter(caze.getQualifiedName());

    ItemLabel label = (ItemLabel)view.getProperty("label");
    label.setLabel(caze.getNameParameter());

    return view;
  }
}

