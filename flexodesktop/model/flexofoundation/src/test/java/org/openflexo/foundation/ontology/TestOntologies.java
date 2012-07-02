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
package org.openflexo.foundation.ontology;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.LocalResourceCenterImplementation;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.FileResource;

public class TestOntologies extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static FlexoResourceCenter testResourceCenter;

	public TestOntologies(String name) {
		super(name);
	}

	/**
	 * Instanciate new ResourceCenter
	 */
	public void test0LoadTestResourceCenter() {
		log("test0LoadTestResourceCenter()");
		testResourceCenter = LocalResourceCenterImplementation.instanciateTestLocalResourceCenterImplementation(new FileResource(
				"TestResourceCenter"));
	}

	public void test1AssertRDFOntologyPresentAndLoaded() {
		log("test1AssertRDFOntologyPresentAndLoaded()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		assertTrue(rdfOntology.getImportedOntologies().size() == 1);
		assertTrue(rdfOntology.getImportedOntologies().firstElement() == testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology());
	}

	public void test2AssertRDFSOntologyPresentAndLoaded() {
		log("test2AssertRDFSOntologyPresentAndLoaded()");
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OntologyClass LITERAL = rdfsOntology.getClass(OntologyObject.RDFS_LITERAL_URI);
		assertNotNull(LITERAL);
		OntologyClass RESOURCE = rdfsOntology.getClass(OntologyObject.RDFS_RESOURCE_URI);
		assertNotNull(RESOURCE);
		OntologyClass CLASS = rdfsOntology.getClass(OntologyObject.RDFS_CLASS_URI);
		assertNotNull(CLASS);
		OntologyClass DATATYPE = rdfsOntology.getClass(OntologyObject.RDFS_DATATYPE_URI);
		assertNotNull(DATATYPE);
		OntologyClass CONTAINER = rdfsOntology.getClass(OntologyObject.RDFS_CONTAINER_URI);
		assertNotNull(CONTAINER);

		OntologyProperty DOMAIN = rdfsOntology.getProperty(OntologyObject.RDFS_DOMAIN_URI);
		assertNotNull(DOMAIN);
		assertFalse(DOMAIN.isAnnotationProperty());

		OntologyProperty RANGE = rdfsOntology.getProperty(OntologyObject.RDFS_RANGE_URI);
		assertNotNull(RANGE);
		assertFalse(RANGE.isAnnotationProperty());

		OntologyProperty SUB_CLASS = rdfsOntology.getProperty(OntologyObject.RDFS_SUB_CLASS_URI);
		assertNotNull(SUB_CLASS);
		assertFalse(SUB_CLASS.isAnnotationProperty());

		OntologyProperty SUB_PROPERTY = rdfsOntology.getProperty(OntologyObject.RDFS_SUB_PROPERTY_URI);
		assertNotNull(SUB_PROPERTY);
		assertFalse(SUB_PROPERTY.isAnnotationProperty());

		OntologyProperty MEMBER = rdfsOntology.getProperty(OntologyObject.RDFS_MEMBER_URI);
		assertNotNull(MEMBER);
		assertFalse(MEMBER.isAnnotationProperty());

		OntologyProperty SEE_ALSO = rdfsOntology.getProperty(OntologyObject.RDFS_SEE_ALSO_URI);
		assertNotNull(SEE_ALSO);
		assertTrue(SEE_ALSO.isAnnotationProperty());

		OntologyProperty IS_DEFINED_BY = rdfsOntology.getProperty(OntologyObject.RDFS_IS_DEFINED_BY_URI);
		assertNotNull(IS_DEFINED_BY);
		assertTrue(IS_DEFINED_BY.isAnnotationProperty());

		OntologyProperty LABEL = rdfsOntology.getProperty(OntologyObject.RDFS_LABEL_URI);
		assertNotNull(LABEL);
		assertTrue(LABEL.isAnnotationProperty());

		OntologyProperty COMMENT = rdfsOntology.getProperty(OntologyObject.RDFS_COMMENT_URI);
		assertNotNull(COMMENT);
		assertTrue(COMMENT.isAnnotationProperty());

	}

	public void test3AssertRDFAndRDFSOntologyCorrectImports() {
		log("test3AssertRDFAndRDFSOntologyCorrectImports()");
		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertTrue(rdfOntology.getImportedOntologies().size() == 1);
		assertTrue(rdfOntology.getImportedOntologies().firstElement() == rdfsOntology);
		assertTrue(rdfsOntology.getImportedOntologies().size() == 1);
		assertTrue(rdfsOntology.getImportedOntologies().firstElement() == rdfOntology);
	}

	public void test4AssertOWLOntologyPresentAndLoaded() {
		log("test4AssertOWLOntologyPresentAndLoaded()");
		FlexoOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());
		assertTrue(owlOntology.getImportedOntologies().size() == 2);
		assertTrue(owlOntology.getImportedOntologies().contains(testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology()));
		assertTrue(owlOntology.getImportedOntologies().contains(testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology()));
		assertNotNull(owlOntology.getThingConcept());
	}

	public void test5AssertFlexoConceptsOntologyIsCorrect() {
		log("test5AssertFlexoConceptsOntologyIsCorrect()");
		FlexoOntology flexoConceptsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology();
		assertNotNull(flexoConceptsOntology);
		assertTrue(flexoConceptsOntology.isLoaded());
		assertEquals(10, flexoConceptsOntology.getClasses().size());

		assertNotNull(flexoConceptsOntology.getThingConcept());

		OntologyClass flexoConcept = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoConcept");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoModelObject = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoModelObject");
		assertNotNull(flexoModelObject);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcessFolder = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessFolder");
		assertNotNull(flexoProcessFolder);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcess = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoProcess");
		assertNotNull(flexoProcess);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoRole = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoRole");
		assertNotNull(flexoRole);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoProcessElement = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessElement");
		assertNotNull(flexoProcessElement);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoActivity = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoActivity);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoOperation = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoOperation");
		assertNotNull(flexoOperation);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OntologyClass flexoEvent = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoEvent");
		assertNotNull(flexoEvent);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));

		OntologyClass classConcept = flexoConceptsOntology.getClass(OWL2URIDefinitions.OWL_CLASS_URI);
		assertNotNull(classConcept);

		assertFalse(flexoConcept.redefinesOriginalDefinition());
		assertFalse(flexoModelObject.redefinesOriginalDefinition());

		assertTrue(flexoConcept.getSuperClasses().size() == 1);
		assertSameList(flexoConcept.getSuperClasses(), flexoConceptsOntology.getThingConcept());

		assertTrue(flexoModelObject.getSuperClasses().size() == 1);
		assertSameList(flexoModelObject.getSuperClasses(), flexoConceptsOntology.getThingConcept());

		assertSameList(flexoProcess.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoModelObject);

		assertSameList(flexoProcessFolder.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoModelObject);

		assertSameList(flexoRole.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoModelObject);

		assertSameList(flexoProcessElement.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoModelObject);

		assertSameList(flexoActivity.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoProcessElement);

		assertSameList(flexoEvent.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoProcessElement);

		assertSameList(flexoOperation.getSuperClasses(), flexoConceptsOntology.getThingConcept(), flexoProcessElement);

		OntologyObjectProperty inRelationWithProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "inRelationWith");
		assertNotNull(inRelationWithProperty);
		OntologyObjectProperty linkedToModelProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "linkedToModel");
		assertNotNull(linkedToModelProperty);
		OntologyObjectProperty linkedToConceptProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "linkedToConcept");
		assertNotNull(linkedToConceptProperty);
		OntologyDataProperty resourceNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "resourceName");
		assertNotNull(resourceNameProperty);
		OntologyDataProperty classNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "className");
		assertNotNull(classNameProperty);
		OntologyDataProperty flexoIDProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "flexoID");
		assertNotNull(flexoIDProperty);

		assertFalse(inRelationWithProperty.redefinesOriginalDefinition());
		assertFalse(linkedToModelProperty.redefinesOriginalDefinition());
		assertFalse(linkedToConceptProperty.redefinesOriginalDefinition());
		assertFalse(resourceNameProperty.redefinesOriginalDefinition());
		assertFalse(classNameProperty.redefinesOriginalDefinition());
		assertFalse(flexoIDProperty.redefinesOriginalDefinition());

		assertEquals(1, flexoModelObject.getAnnotationStatements().size());

	}

	public void test6TestMultiReferencesO3() {
		log("test6TestMultiReferencesO3()");
		FlexoOntology o1 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		FlexoOntology o2 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		FlexoOntology o3 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getThingConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getThingConcept(), o3.getThingConcept().getOriginalDefinition());

		OntologyClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OntologyClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OntologyClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getThingConcept(), a2, b2, c2);

		OntologyClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OntologyClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OntologyClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getThingConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

	}

	public void test7TestMultiReferencesO4() {
		log("test7TestMultiReferencesO4()");
		FlexoOntology o1 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		FlexoOntology o2 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		FlexoOntology o3 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);
		FlexoOntology o5 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O4.owl");
		assertNotNull(o5);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getThingConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getThingConcept(), o3.getThingConcept().getOriginalDefinition());

		OntologyClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OntologyClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OntologyClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getThingConcept(), a2, b2, c2);

		OntologyClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OntologyClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OntologyClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getThingConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

		o5.loadWhenUnloaded();
		assertTrue(o5.isLoaded());

		OntologyClass a1fromO4 = o5.getClass(o1.getURI() + "#A1");
		assertNotNull(a1fromO4);
		assertEquals(a1, a1fromO4.getOriginalDefinition());

		OntologyClass a2fromO4 = o5.getClass(o2.getURI() + "#A2");
		assertNotNull(a2fromO4);
		assertEquals(a2, a2fromO4.getOriginalDefinition());

		assertEquals(3, o5.getClasses().size());
		assertSameList(o5.getClasses(), o5.getThingConcept(), a1fromO4, a2fromO4);

		assertTrue(a1fromO4.isSuperConceptOf(a2fromO4));

	}

	public void test8TestMultiReferencesO5() {
		log("test8TestMultiReferencesO5()");
		FlexoOntology o1 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		FlexoOntology o2 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		FlexoOntology o3 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);
		FlexoOntology o5 = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O5.owl");
		assertNotNull(o5);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getThingConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getThingConcept(), o3.getThingConcept().getOriginalDefinition());

		OntologyClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OntologyClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OntologyClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getThingConcept(), a2, b2, c2);

		OntologyClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OntologyClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OntologyClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getThingConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

		o5.loadWhenUnloaded();
		assertTrue(o5.isLoaded());

		OntologyClass a1fromO5 = o5.getClass(o1.getURI() + "#A1");
		assertNotNull(a1fromO5);
		assertEquals(a1, a1fromO5.getOriginalDefinition());

		OntologyClass a2fromO5 = o5.getClass(o2.getURI() + "#A2");
		assertNotNull(a2fromO5);
		assertEquals(a2, a2fromO5.getOriginalDefinition());

		assertEquals(3, o5.getClasses().size());
		assertSameList(o5.getClasses(), o5.getThingConcept(), a1fromO5, a2fromO5);

		assertTrue(a2fromO5.isSuperConceptOf(a1fromO5));

	}

	public void test9TestInstances() {
		log("test9TestInstances()");
		FlexoOntology ontology = testResourceCenter.retrieveBaseOntologyLibrary().getOntology(
				"http://www.openflexo.org/test/TestInstances.owl");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());

		assertEquals(4, ontology.getIndividuals().size());

		OntologyIndividual activity1 = ontology.getIndividual(ontology.getURI() + "#Activity1");
		assertNotNull(activity1);
		OntologyIndividual activity2 = ontology.getIndividual(ontology.getURI() + "#Activity2");
		assertNotNull(activity2);
		OntologyIndividual activity3 = ontology.getIndividual(ontology.getURI() + "#Activity3");
		assertNotNull(activity3);
		OntologyIndividual untypedIndividual = ontology.getIndividual(ontology.getURI() + "#UntypedIndividual");
		assertNotNull(untypedIndividual);

		assertSameList(ontology.getIndividuals(), activity1, activity2, activity3, untypedIndividual);

		OntologyClass redefinedFlexoActivity = ontology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(redefinedFlexoActivity);
		OntologyClass flexoActivity = ontology.getOntologyLibrary().getFlexoConceptOntology()
				.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoActivity);
		assertTrue(redefinedFlexoActivity.redefinesOriginalDefinition());
		assertEquals(flexoActivity, redefinedFlexoActivity.getOriginalDefinition());

		assertTrue(redefinedFlexoActivity.isSuperConceptOf(activity1));
		assertTrue(redefinedFlexoActivity.isSuperConceptOf(activity2));
		assertTrue(redefinedFlexoActivity.isSuperConceptOf(activity3));

		assertTrue(ontology.getThingConcept().isSuperConceptOf(untypedIndividual));
		assertTrue(ontology.getThingConcept().getOriginalDefinition().isSuperConceptOf(untypedIndividual));

		OntologyDataProperty resourceNameProperty = ontology.getOntologyLibrary().getFlexoConceptOntology()
				.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "resourceName");
		assertNotNull(resourceNameProperty);

		assertEquals("Process1", activity1.getPropertyValue(resourceNameProperty));

		assertEquals(35, ontology.getAccessibleClasses().size());

	}

	public void test10TestAnnotations() {
		log("test10TestAnnotations()");
		FlexoOntology ontology = testResourceCenter.retrieveBaseOntologyLibrary().getOntology(
				"http://www.openflexo.org/test/TestInstances.owl");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());

		assertEquals(4, ontology.getIndividuals().size());

		OntologyIndividual activity1 = ontology.getIndividual(ontology.getURI() + "#Activity1");
		assertNotNull(activity1);
		OntologyIndividual activity2 = ontology.getIndividual(ontology.getURI() + "#Activity2");
		assertNotNull(activity2);
		OntologyIndividual activity3 = ontology.getIndividual(ontology.getURI() + "#Activity3");
		assertNotNull(activity3);

		assertEquals(4, activity1.getAnnotationStatements().size());

		OntologyDataProperty COMMENT = ontology.getDataProperty(OntologyObject.RDFS_COMMENT_URI);
		assertNotNull(COMMENT);
		assertTrue(COMMENT.isAnnotationProperty());

		assertEquals(activity1.getAnnotationValue(COMMENT, Language.ENGLISH), "A comment in english");
		assertEquals(activity1.getAnnotationValue(COMMENT, Language.FRENCH), "Un commentaire en francais");

		FlexoLocalization.setCurrentLanguage(Language.ENGLISH);
		// System.out.println("english value = " + activity1.getPropertyValue(COMMENT));
		assertEquals(activity1.getPropertyValue(COMMENT), "A comment in english");

		FlexoLocalization.setCurrentLanguage(Language.FRENCH);
		// System.out.println("french value = " + activity1.getPropertyValue(COMMENT));
		assertEquals(activity1.getPropertyValue(COMMENT), "Un commentaire en francais");

		// System.out.println("les annotations pour activity2=" + activity2.getAnnotationStatements());
		assertEquals(activity2.getPropertyValue(COMMENT), "Activity2 is only commented in english");

		// System.out.println("les annotations pour activity3=" + activity3.getAnnotationStatements());
		assertEquals(activity3.getPropertyValue(COMMENT), "Cette activite est commentee en francais uniquement");

		assertEquals(1, activity1.getAnnotationObjectStatements().size());

		OntologyObjectProperty SEE_ALSO = ontology.getObjectProperty(OntologyObject.RDFS_SEE_ALSO_URI);
		assertNotNull(SEE_ALSO);
		assertTrue(SEE_ALSO.isAnnotationProperty());

		assertEquals(activity3, activity1.getAnnotationObjectValue(SEE_ALSO));

	}

	public void test11TestLoadArchimateOntology() {
		log("test11TestLoadArchimateOntology()");
		FlexoOntology ontology = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.bolton.ac.uk/archimate");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());
	}

	public void test12TestLoadCPMFInstanceOntology() {
		log("test12TestLoadCPMFInstanceOntology()");
		FlexoOntology ontology = testResourceCenter.retrieveBaseOntologyLibrary()
				.getOntology("http://www.cpmf.org/ontologies/cpmfInstance");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());
	}

	public void test13TestLoadBPMNOntology() {
		log("test13TestLoadBPMNOntology()");
		FlexoOntology ontology = testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://dkm.fbk.eu/index.php/BPMN_Ontology");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());
	}

	public void test14TestBasicOntologEditor() {
		log("test14TestBasicOntologEditor()");
		FlexoOntology ontology = testResourceCenter.retrieveBaseOntologyLibrary().getOntology(
				"http://www.agilebirds.com/openflexo/ViewPoints/BasicOntology.owl");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());
	}

	public void test15TestLoadSKOS() {
		log("test15TestLoadSKOS()");
		String SKOS_URI = "http://www.w3.org/2004/02/skos/core";
		FlexoOntology skosOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOntology(SKOS_URI);
		assertNotNull(skosOntology);

		skosOntology.loadWhenUnloaded();
		assertTrue(skosOntology.isLoaded());

		OntologyObjectProperty hasTopConceptProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "hasTopConcept");
		assertNotNull(hasTopConceptProperty);
		OntologyObjectProperty inSchemeProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "inScheme");
		assertNotNull(inSchemeProperty);
		OntologyObjectProperty topConceptOfProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "topConceptOf");
		assertNotNull(topConceptOfProperty);
		OntologyObjectProperty skosMemberProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "member");
		assertNotNull(skosMemberProperty);
		OntologyObjectProperty memberListProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "memberList");
		assertNotNull(memberListProperty);
		OntologyObjectProperty semanticRelationProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "semanticRelation");
		assertNotNull(semanticRelationProperty);
		OntologyObjectProperty relatedProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "related");
		assertNotNull(relatedProperty);
		OntologyObjectProperty relatedMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "relatedMatch");
		assertNotNull(relatedMatchProperty);
		OntologyObjectProperty mappingRelationProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "mappingRelation");
		assertNotNull(mappingRelationProperty);
		OntologyObjectProperty closeMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "closeMatch");
		assertNotNull(closeMatchProperty);
		OntologyObjectProperty exactMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "exactMatch");
		assertNotNull(exactMatchProperty);
		OntologyObjectProperty narrowMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrowMatch");
		assertNotNull(narrowMatchProperty);
		OntologyObjectProperty broadMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broadMatch");
		assertNotNull(broadMatchProperty);
		OntologyObjectProperty broaderTransitiveProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broaderTransitive");
		assertNotNull(broaderTransitiveProperty);
		OntologyObjectProperty broaderProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broader");
		assertNotNull(broaderProperty);
		OntologyObjectProperty narrowerTransitiveProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrowerTransitive");
		assertNotNull(narrowerTransitiveProperty);
		OntologyObjectProperty narrowerProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrower");
		assertNotNull(narrowerProperty);

	}

	public void test16TestAccessibleProperties() {
		log("test16TestAccessibleProperties()");

		FlexoOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		FlexoOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		FlexoOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());

		OntologyObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OntologyObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);

		OntologyDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OntologyDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OntologyObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OntologyObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OntologyObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OntologyObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OntologyObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OntologyObjectProperty subClassOfProperty = rdfOntology
				.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OntologyObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyObjectProperty topObjectProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "topObjectProperty");
		assertNotNull(topObjectProperty);
		OntologyObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OntologyDataProperty bottomDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OntologyDataProperty topDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);

		OntologyObjectProperty annotatedPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OntologyObjectProperty annotatedSourceProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OntologyObjectProperty annotatedTargetProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OntologyObjectProperty datatypeComplementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OntologyObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OntologyObjectProperty equivalentClassProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OntologyObjectProperty equivalentPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OntologyObjectProperty intersectionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OntologyObjectProperty membersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OntologyObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OntologyObjectProperty oneOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OntologyObjectProperty propertyDisjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OntologyObjectProperty unionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OntologyObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "versionInfo");
		assertNotNull(versionInfoProperty);
		OntologyObjectProperty withRestrictionsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OntologyObjectProperty differentFromProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "differentFrom");
		assertNotNull(differentFromProperty);
		OntologyObjectProperty sameAsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OntologyObjectProperty distinctMembersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OntologyObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OntologyObjectProperty disjointUnionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OntologyObjectProperty complementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "complementOf");
		assertNotNull(complementOfProperty);
		OntologyObjectProperty disjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "disjointWith");
		assertNotNull(disjointWithProperty);
		OntologyObjectProperty sourceIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OntologyObjectProperty assertionPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OntologyObjectProperty targetValueProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "targetValue");
		assertNotNull(targetValueProperty);
		OntologyObjectProperty targetIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OntologyObjectProperty propertyChainAxiomProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OntologyObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		String SKOS_URI = "http://www.w3.org/2004/02/skos/core";
		FlexoOntology skosOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOntology(SKOS_URI);
		assertNotNull(skosOntology);

		skosOntology.loadWhenUnloaded();
		assertTrue(skosOntology.isLoaded());

		OntologyObjectProperty hasTopConceptProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "hasTopConcept");
		assertNotNull(hasTopConceptProperty);
		OntologyObjectProperty inSchemeProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "inScheme");
		assertNotNull(inSchemeProperty);
		OntologyObjectProperty topConceptOfProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "topConceptOf");
		assertNotNull(topConceptOfProperty);
		OntologyObjectProperty skosMemberProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "member");
		assertNotNull(skosMemberProperty);
		OntologyObjectProperty memberListProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "memberList");
		assertNotNull(memberListProperty);
		OntologyObjectProperty semanticRelationProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "semanticRelation");
		assertNotNull(semanticRelationProperty);
		OntologyObjectProperty relatedProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "related");
		assertNotNull(relatedProperty);
		OntologyObjectProperty relatedMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "relatedMatch");
		assertNotNull(relatedMatchProperty);
		OntologyObjectProperty mappingRelationProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "mappingRelation");
		assertNotNull(mappingRelationProperty);
		OntologyObjectProperty closeMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "closeMatch");
		assertNotNull(closeMatchProperty);
		OntologyObjectProperty exactMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "exactMatch");
		assertNotNull(exactMatchProperty);
		OntologyObjectProperty narrowMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrowMatch");
		assertNotNull(narrowMatchProperty);
		OntologyObjectProperty broadMatchProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broadMatch");
		assertNotNull(broadMatchProperty);
		OntologyObjectProperty broaderTransitiveProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broaderTransitive");
		assertNotNull(broaderTransitiveProperty);
		OntologyObjectProperty broaderProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "broader");
		assertNotNull(broaderProperty);
		OntologyObjectProperty narrowerTransitiveProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrowerTransitive");
		assertNotNull(narrowerTransitiveProperty);
		OntologyObjectProperty narrowerProperty = skosOntology.getObjectProperty(SKOS_URI + "#" + "narrower");
		assertNotNull(narrowerProperty);

		OntologyClass concept = skosOntology.getClass("http://www.w3.org/2004/02/skos/core#Concept");
		System.out.println("concept super classes = " + concept.getSuperClasses());
		System.out.println("concept super classes = " + concept.getSuperClasses().get(0).getPropertiesTakingMySelfAsDomain());
		System.out.println("accessible properties = " + concept.getPropertiesTakingMySelfAsDomain());

		assertSameList(concept.getPropertiesTakingMySelfAsDomain(), broaderProperty, broaderTransitiveProperty, broadMatchProperty,
				closeMatchProperty, exactMatchProperty, mappingRelationProperty, narrowerProperty, narrowerTransitiveProperty,
				narrowMatchProperty, relatedProperty, relatedMatchProperty, semanticRelationProperty, topConceptOfProperty,
				bottomObjectProperty, bottomDataProperty, differentFromProperty, sameAsProperty, topDataProperty, topObjectProperty,
				hasKeyProperty, complementOfProperty, seeAlsoProperty, memberProperty, disjointUnionOfProperty, disjointWithProperty,
				subClassOfProperty, valueProperty, typeProperty, commentProperty, isDefinedByProperty, labelProperty);

		OntologyClass thingFromOWL = owlOntology.getThingConcept();
		System.out.println("thingFromOWL = " + thingFromOWL.getPropertiesTakingMySelfAsDomain());

		OntologyClass thingFromSKOS = skosOntology.getThingConcept();
		System.out.println("thingFromSKOS = " + thingFromSKOS.getPropertiesTakingMySelfAsDomain());

	}
}
