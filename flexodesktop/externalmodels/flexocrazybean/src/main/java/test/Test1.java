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
package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import cb.parser.PrintVisitor;
import cb.petal.Association;
import cb.petal.ClassDiagram;
import cb.petal.ClassUtility;
import cb.petal.ClassView;
import cb.petal.InheritView;
import cb.petal.LogicalCategory;
import cb.petal.NoteView;
import cb.petal.PetalFile;
import cb.petal.RealizeView;
import cb.petal.Role;
import cb.petal.UseCase;
import cb.petal.UseCaseCategory;
import cb.petal.UseCaseDiagram;
import cb.petal.UsesRelationship;
import cb.petal.UsesView;
import cb.util.PetalObjectFactory;

/**
 * Create nonsense university model found in examples/uni.mdl from scratch.
 *
 * @version $Id: Test1.java,v 1.2 2011/09/12 11:47:32 gpolet Exp $
 * @author  <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class Test1 {
  public static void main(String[] args) {
    PetalObjectFactory factory = PetalObjectFactory.getInstance();

    ///////////////// Create data and view for class diagram /////////////

    // Create objects via factory
    PetalFile      model      = factory.createModel();
    cb.petal.Class person     = factory.createClass("Person");
    cb.petal.Class prof       = factory.createClass("Professor");
    cb.petal.Class student    = factory.createClass("Student");
    cb.petal.Class mentor     = factory.createInterface("Mentor");
    cb.petal.Class money      = factory.createClass("Money");
    cb.petal.Class department = factory.createClass("Department");
    cb.petal.Class uni        = factory.createClass("University");
    cb.petal.Class period     = factory.createClass("Period");

    person.setDocumentation("Super class of all beings\nrelevant to the system");

    /* Create objects on the fly yourself, just for demonstration. Class objects have
     * a name parameter, i.e. the name is not defined as a property. Therefore we just
     * set the parameter list to a singleton list just containing the name of the class.
     */
    ClassUtility   address = new ClassUtility();
    address.setParameterList(new ArrayList(Arrays.asList(new String[]{"Address"})));

    // Its important to give the class a unique id, the getQuid() method ensures that
    address.setQuid(PetalFile.getQuid());

    /* Every petal model has a class category "Logical View" and a class category
     * "Use Case View". Both have a property "logical_models" which refers to a list
     * of classes, associations, ... They also have a property "logical_presentations",
     * which contains the views, i.e., diagrams of the model. We start by adding
     * data to the logical model first.
     */
    LogicalCategory cat = model.getLogicalCategory();

    cat.addToModel(person);
    cat.addToModel(prof);
    cat.addToModel(student);
    cat.addToModel(mentor);
    cat.addToModel(address);
    cat.addToModel(money);
    cat.addToModel(uni);
    cat.addToModel(department);
    cat.addToModel(period);

    /* Create (empty) sub-package just for demonstration, it could contain
     * further data and view models.
     */
    cat.addToModel(factory.createLogicalCategory("Foo - Just a test"));

    /* Once we've added the classes to the model we can start adding
     * inheritance relationships and associations between them.
     */
    prof.addSuperClass(person);
    prof.addImplementedInterface(mentor);
    prof.setStereotype("Actor");

    /* Add used class and adapt relationship object, we don't need to create the
     * relationship object ourselves via the factory, but can use the convenience
     * method in "Class".
     */
    UsesRelationship rel = prof.addUsedClass(money);
    rel.setStereotype("needs more");
    rel.setLabel("has much");

    student.addSuperClass(person);

    // Add operations and class attributes aka fields
    person.addOperation("setAddress", "void", "Public", new String[] { "Address" }, new String[] { "addr" });
    person.addClassAttribute("age", "int");
    person.addClassAttribute("name", "String");

    period.addClassAttribute("from", "Date");
    period.addClassAttribute("to", "Date");

    /* Add association "teach": associations contain a "role list" which has exactly
     * two members. The Role objects contain the information about aggregation, the
     * cardinality, etc.
     */
    Association assoc = factory.createAssociation("teach", prof, student);
    cat.addToModel(assoc);

    Role first  = assoc.getFirstRole();
    Role second = assoc.getSecondRole();

    first.setRoleName("Talker");
    first.isNavigable(false); // Only goes from professor to student
    second.setRoleName("Listener");
    second.setCardinality("1..n");
    first.setConstraints("None");

    /* Note that the kind of aggregation depends on the containment of the
     * other role. I.e., first (Professor) has an aggregation association of
     * second (Student), but the containment kind has to be set on the latter.
     */
    first.setIsAggregate(true);
    second.setContainment("By reference");

    // Add association between university and department
    Association assoc2 = factory.createAssociation(uni, department);
    cat.addToModel(assoc2);

    first  = assoc2.getFirstRole();
    second = assoc2.getSecondRole();
    first.setCardinality("1");
    second.setCardinality("1..n");
    first.setIsAggregate(true);
    second.setContainment("By value");

    // Add association between professor and department
    Association assoc3 = factory.createAssociation(prof, department);
    cat.addToModel(assoc3);
    assoc3.setAssociationClass(period.getQualifiedName());

    /* Set up views in class diagram, basically this reflects just the
     * structures defined in the data model.Everything related to an
     * entity such as stereotypes should be created automagically via
     * the factory.  It's important to add classes first, since
     * relationships, etc., rely on them.
     * Views also have "tags", i.e., a local numbering scheme they be
     * refered with, the tags, i.e. indexes are set automatically when
     * the views are added.
     */
    ClassDiagram diag = cat.getFirstClassDiagram();

    ClassView studentview, profview; // We'll need these later on

    diag.addToView(factory.createClassView(person));
    diag.addToView(profview = factory.createClassView(prof));
    diag.addToView(studentview = factory.createClassView(student));
    diag.addToView(factory.createClassView(mentor));
    diag.addToView(factory.createClassView(address));
    diag.addToView(factory.createClassView(money));
    diag.addToView(factory.createClassView(uni));
    diag.addToView(factory.createClassView(department));
    diag.addToView(factory.createClassView(period));

    diag.addToView(factory.createAssociationView(assoc));
    diag.addToView(factory.createAssociationView(assoc2));
    diag.addToView(factory.createAssociationView(assoc3));

    /* Create note and attach it to student and profs, notes are not
     * contained in the data model, just in the view.
     */
    NoteView noteview;
    diag.addToView(noteview = factory.createNoteView("This is\nsome note"));
    diag.addAttachView(noteview, profview); // Convenience method
    diag.addAttachView(noteview, studentview);

    /** Ok, now add the inheritance views for the classes, i.e., some arrows.
     */
    for(Iterator i = factory.createInheritViews(prof).iterator(); i.hasNext(); ) {
      InheritView view = (InheritView)i.next();

      diag.addToView(view);
    }

    for(Iterator i = factory.createInheritViews(student).iterator(); i.hasNext(); ) {
      InheritView view = (InheritView)i.next();

      diag.addToView(view);
    }

    /** Do the same for the other kinds of associations.
     */
    for(Iterator i = factory.createRealizeViews(prof).iterator(); i.hasNext(); ) {
      RealizeView view = (RealizeView)i.next();

      diag.addToView(view);
    }

    for(Iterator i = factory.createUsesViews(prof).iterator(); i.hasNext(); ) {
      UsesView view = (UsesView)i.next();

      diag.addToView(view);
    }

    ///////////////// Create data and view for use case diagram /////////////

    UseCase lecture = factory.createUseCase("Lecture");
    UseCase exam    = factory.createUseCase("Examination");
    UseCase exam2   = factory.createUseCase("Hard examination");
    UseCaseCategory cat2 = model.getUseCaseCategory();

    cat2.addToModel(lecture);
    cat2.addToModel(exam);
    cat2.addToModel(exam2);
    exam2.addSuperUseCase(exam);

    lecture.setStereotype("obsolete");

    assoc = factory.createAssociation("depends on", exam, lecture);
    cat2.addToModel(assoc);
    assoc.getFirstRole().isNavigable(false);

    assoc2 = factory.createAssociation(prof, lecture);
    cat2.addToModel(assoc2);
    assoc2.getFirstRole().isNavigable(false);

    assoc3 = factory.createAssociation(prof, exam);
    cat2.addToModel(assoc3);
    assoc3.getFirstRole().isNavigable(false);

    UseCaseDiagram diag2 = cat2.getFirstUseCaseDiagram();

    diag2.addToView(factory.createUseCaseView(lecture));
    diag2.addToView(factory.createUseCaseView(exam));
    diag2.addToView(factory.createUseCaseView(exam2));
    diag2.addToView(factory.createClassView(prof));

    for(Iterator i = factory.createInheritViews(exam2).iterator(); i.hasNext(); ) {
      InheritView view = (InheritView)i.next();

      diag2.addToView(view);
    }

    diag2.addToView(factory.createAssociationView(assoc));
    diag2.addToView(factory.createAssociationView(assoc2));
    diag2.addToView(factory.createAssociationView(assoc3));

    // We're done, print the model.
    model.accept(new PrintVisitor(System.out));
  }
}
