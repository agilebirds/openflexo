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
package org.openflexo.components.widget;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.ontology.OntologyUtils;
import org.openflexo.foundation.ontology.owl.OWL2URIDefinitions;
import org.openflexo.foundation.ontology.owl.OWLClass;
import org.openflexo.foundation.ontology.owl.OWLDataProperty;
import org.openflexo.foundation.ontology.owl.OWLObjectProperty;
import org.openflexo.foundation.ontology.owl.OWLOntology;
import org.openflexo.foundation.ontology.owl.RDFSURIDefinitions;
import org.openflexo.foundation.ontology.owl.RDFURIDefinitions;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.LocalResourceCenterImplementation;
import org.openflexo.toolbox.FileResource;

public class TestOntologyBrowserModel extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static FlexoResourceCenter testResourceCenter;

	public TestOntologyBrowserModel(String name) {
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

	public void test1AssertRDFOntologyNoHierarchy() {
		log("test1AssertRDFOntologyNoHierarchy()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(false);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), rdfOntology);

		assertEquals(6, obm.getChildren(rdfOntology).size());
		assertSameList(obm.getChildren(rdfOntology), listConcept, propertyConcept, statementConcept, rdfsOntology, typeProperty,
				valueProperty);
		assertSameList(obm.getChildren(listConcept), restProperty, firstProperty);
		assertSameList(obm.getChildren(statementConcept), subjectProperty, predicateProperty, objectProperty);

		assertEquals(4, obm.getChildren(rdfsOntology).size());
		assertSameList(obm.getChildren(rdfsOntology), resourceConcept, domainProperty, rangeProperty, subPropertyOfProperty);
		assertEquals(8, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), classConcept, containerConcept, literalConcept, labelProperty,
				isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty);
		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);

		obm.setDisplayPropertiesInClasses(false);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), rdfOntology);

		assertEquals(11, obm.getChildren(rdfOntology).size());
		assertSameList(obm.getChildren(rdfOntology), listConcept, propertyConcept, statementConcept, rdfsOntology, typeProperty,
				valueProperty, restProperty, firstProperty, subjectProperty, predicateProperty, objectProperty);

	}

	public void test2AssertRDFOntologyHierarchy() {
		log("test2AssertRDFOntologyHierarchy()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), resourceConcept);

		assertEquals(13, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), listConcept, propertyConcept, statementConcept, classConcept, containerConcept,
				literalConcept, labelProperty, isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty, valueProperty,
				typeProperty);

		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);
	}

	public void test3AssertRDFOntologyStrictMode() {
		log("test3AssertRDFOntologyStrictMode()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfOntology);
		obm.setStrictMode(true);
		obm.setHierarchicalMode(false);
		obm.recomputeStructure();

		assertEquals(5, obm.getRoots().size());
		assertSameList(obm.getRoots(), listConcept, propertyConcept, statementConcept, typeProperty, valueProperty);

	}

	public void test4AssertRDFSOntologyNoHierarchy() {
		log("test4AssertRDFSOntologyNoHierarchy()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfsOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(false);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), rdfsOntology);

		assertEquals(5, obm.getChildren(rdfsOntology).size());
		assertSameList(obm.getChildren(rdfsOntology), resourceConcept, domainProperty, rangeProperty, subPropertyOfProperty, rdfOntology);

		assertEquals(8, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), classConcept, containerConcept, literalConcept, labelProperty, commentProperty,
				isDefinedByProperty, seeAlsoProperty, memberProperty);
		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);

		assertEquals(5, obm.getChildren(rdfOntology).size());
		assertSameList(obm.getChildren(rdfOntology), listConcept, propertyConcept, statementConcept, typeProperty, valueProperty);
	}

	public void test5AssertRDFSOntologyHierarchy() {
		log("test5AssertRDFSOntologyHierarchy()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfsOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), resourceConcept);

		assertEquals(13, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), listConcept, propertyConcept, statementConcept, classConcept, containerConcept,
				literalConcept, labelProperty, isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty, valueProperty,
				typeProperty);

		assertEquals(2, obm.getChildren(classConcept).size());
		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);
	}

	public void test6AssertRDFSOntologyStrictMode() {
		log("test6AssertRDFSOntologyStrictMode()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(rdfsOntology);
		obm.setStrictMode(true);
		obm.setHierarchicalMode(false);
		obm.recomputeStructure();

		assertEquals(4, obm.getRoots().size());
		assertSameList(obm.getRoots(), resourceConcept, domainProperty, rangeProperty, subPropertyOfProperty);

		assertEquals(8, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), classConcept, containerConcept, literalConcept, labelProperty,
				isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty);

		assertEquals(2, obm.getChildren(classConcept).size());
		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);

	}

	public void test7AssertOWLOntologyNoHierarchy() {
		log("test7AssertOWLOntologyNoHierarchy()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		OWLOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OWLClass thingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OWLClass allDifferentConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OWLClass annotationPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OWLClass owlClassConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OWLClass restrictionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OWLClass datatypePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OWLClass namedIndividualConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OWLClass negativePropertyAssertionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OWLClass nothingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OWLClass objectPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OWLClass asymmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OWLClass inverseFunctionalPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OWLClass irreflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OWLClass reflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OWLClass symmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OWLClass transitivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OWLClass ontologyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OWLClass ontologyPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IFlexoOntologyStructuralProperty");
		assertNotNull(ontologyPropertyConcept);
		OWLObjectProperty topObjectProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topObjectProperty");
		assertNotNull(topObjectProperty);
		OWLObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OWLDataProperty bottomDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OWLDataProperty topDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);

		OWLObjectProperty annotatedPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OWLObjectProperty annotatedSourceProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OWLObjectProperty annotatedTargetProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OWLObjectProperty datatypeComplementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OWLObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OWLObjectProperty equivalentClassProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OWLObjectProperty equivalentPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OWLObjectProperty intersectionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OWLObjectProperty membersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OWLObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OWLObjectProperty oneOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OWLObjectProperty propertyDisjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OWLObjectProperty unionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OWLObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "versionInfo");
		assertNotNull(versionInfoProperty);
		OWLObjectProperty withRestrictionsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OWLObjectProperty differentFromProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "differentFrom");
		assertNotNull(differentFromProperty);
		OWLObjectProperty sameAsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OWLObjectProperty distinctMembersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OWLObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OWLObjectProperty disjointUnionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OWLObjectProperty complementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "complementOf");
		assertNotNull(complementOfProperty);
		OWLObjectProperty disjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "disjointWith");
		assertNotNull(disjointWithProperty);
		OWLObjectProperty sourceIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OWLObjectProperty assertionPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OWLObjectProperty targetValueProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "targetValue");
		assertNotNull(targetValueProperty);
		OWLObjectProperty targetIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OWLObjectProperty propertyChainAxiomProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OWLObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(owlOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(false);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), owlOntology);

		assertEquals(18, obm.getChildren(owlOntology).size());
		assertSameList(obm.getChildren(owlOntology), thingConcept, rdfsOntology, rdfOntology, annotatedPropertyProperty,
				annotatedSourceProperty, annotatedTargetProperty, datatypeComplementOfProperty, deprecatedProperty,
				equivalentClassProperty, equivalentPropertyProperty, intersectionOfProperty, membersProperty, onDatatypeProperty,
				oneOfProperty, propertyDisjointWithProperty, unionOfProperty, versionInfoProperty, withRestrictionsProperty);

		assertSameList(obm.getChildren(thingConcept), allDifferentConcept, annotationPropertyConcept, owlClassConcept,
				datatypePropertyConcept, namedIndividualConcept, negativePropertyAssertionConcept, nothingConcept, objectPropertyConcept,
				ontologyConcept, ontologyPropertyConcept, bottomDataProperty, topObjectProperty, topDataProperty, bottomObjectProperty,
				sameAsProperty, differentFromProperty);

		assertSameList(obm.getChildren(allDifferentConcept), distinctMembersProperty);
		assertSameList(obm.getChildren(owlClassConcept), restrictionConcept, hasKeyProperty, disjointUnionOfProperty, complementOfProperty,
				disjointWithProperty);
		assertSameList(obm.getChildren(negativePropertyAssertionConcept), sourceIndividualProperty, assertionPropertyProperty,
				targetValueProperty, targetIndividualProperty);

		assertSameList(obm.getChildren(objectPropertyConcept), asymmetricPropertyConcept, inverseFunctionalPropertyConcept,
				irreflexivePropertyConcept, reflexivePropertyConcept, symmetricPropertyConcept, transitivePropertyConcept,
				propertyChainAxiomProperty, inverseOfProperty);

		assertEquals(4, obm.getChildren(rdfsOntology).size());
		assertSameList(obm.getChildren(rdfsOntology), resourceConcept, domainProperty, rangeProperty, subPropertyOfProperty);

		assertSameList(obm.getChildren(resourceConcept), classConcept, containerConcept, literalConcept, labelProperty,
				isDefinedByProperty, seeAlsoProperty, memberProperty, commentProperty);

		assertSameList(obm.getChildren(classConcept), datatypeConcept, subClassOfProperty);

		assertSameList(obm.getChildren(rdfOntology), listConcept, propertyConcept, statementConcept, typeProperty, valueProperty);
	}

	public void test8AssertOWLOntologyHierarchy() {
		log("test8AssertOWLOntologyHierarchy()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		OWLOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OWLClass thingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OWLClass allDifferentConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OWLClass annotationPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OWLClass owlClassConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OWLClass restrictionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OWLClass datatypePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OWLClass namedIndividualConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OWLClass negativePropertyAssertionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OWLClass nothingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OWLClass objectPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OWLClass asymmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OWLClass inverseFunctionalPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OWLClass irreflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OWLClass reflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OWLClass symmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OWLClass transitivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OWLClass ontologyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OWLClass ontologyPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IFlexoOntologyStructuralProperty");
		assertNotNull(ontologyPropertyConcept);
		OWLObjectProperty topObjectProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topObjectProperty");
		assertNotNull(topObjectProperty);
		OWLObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OWLDataProperty bottomDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OWLDataProperty topDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);
		OWLObjectProperty annotatedPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OWLObjectProperty annotatedSourceProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OWLObjectProperty annotatedTargetProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OWLObjectProperty datatypeComplementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OWLObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OWLObjectProperty equivalentClassProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OWLObjectProperty equivalentPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OWLObjectProperty intersectionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OWLObjectProperty membersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OWLObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OWLObjectProperty oneOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OWLObjectProperty propertyDisjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OWLObjectProperty unionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OWLObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "versionInfo");
		assertNotNull(versionInfoProperty);
		OWLObjectProperty withRestrictionsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OWLObjectProperty differentFromProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "differentFrom");
		assertNotNull(differentFromProperty);
		OWLObjectProperty sameAsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OWLObjectProperty distinctMembersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OWLObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OWLObjectProperty disjointUnionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OWLObjectProperty complementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "complementOf");
		assertNotNull(complementOfProperty);
		OWLObjectProperty disjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "disjointWith");
		assertNotNull(disjointWithProperty);
		OWLObjectProperty sourceIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OWLObjectProperty assertionPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OWLObjectProperty targetValueProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "targetValue");
		assertNotNull(targetValueProperty);
		OWLObjectProperty targetIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OWLObjectProperty propertyChainAxiomProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OWLObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(owlOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), thingConcept);

		assertEquals(9, obm.getChildren(thingConcept).size());
		assertSameList(obm.getChildren(thingConcept), namedIndividualConcept, nothingConcept, resourceConcept, bottomDataProperty,
				topObjectProperty, topDataProperty, bottomObjectProperty, sameAsProperty, differentFromProperty);

		assertEquals(22, obm.getChildren(resourceConcept).size());
		assertSameList(obm.getChildren(resourceConcept), allDifferentConcept, negativePropertyAssertionConcept, ontologyConcept,
				classConcept, containerConcept, literalConcept, listConcept, propertyConcept, statementConcept, commentProperty,
				annotatedTargetProperty, typeProperty, versionInfoProperty, memberProperty, seeAlsoProperty, membersProperty,
				annotatedPropertyProperty, valueProperty, isDefinedByProperty, annotatedSourceProperty, labelProperty, deprecatedProperty);

		assertEquals(7, obm.getChildren(classConcept).size());
		assertSameList(obm.getChildren(classConcept), owlClassConcept, datatypeConcept, unionOfProperty, oneOfProperty, subClassOfProperty,
				equivalentClassProperty, intersectionOfProperty);

		assertEquals(5, obm.getChildren(owlClassConcept).size());
		assertSameList(obm.getChildren(owlClassConcept), restrictionConcept, complementOfProperty, disjointUnionOfProperty, hasKeyProperty,
				disjointWithProperty);

		assertEquals(9, obm.getChildren(propertyConcept).size());
		assertSameList(obm.getChildren(propertyConcept), annotationPropertyConcept, datatypePropertyConcept, objectPropertyConcept,
				ontologyPropertyConcept, domainProperty, subPropertyOfProperty, propertyDisjointWithProperty, equivalentPropertyProperty,
				rangeProperty);

		assertEquals(8, obm.getChildren(objectPropertyConcept).size());
		assertSameList(obm.getChildren(objectPropertyConcept), asymmetricPropertyConcept, inverseFunctionalPropertyConcept,
				irreflexivePropertyConcept, reflexivePropertyConcept, symmetricPropertyConcept, transitivePropertyConcept,
				inverseOfProperty, propertyChainAxiomProperty);

	}

	public void test9AssertOWLOntologyStrictMode() {
		log("test9AssertOWLOntologyStrictMode()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		OWLOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OWLClass thingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OWLClass allDifferentConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OWLClass annotationPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OWLClass owlClassConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OWLClass restrictionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OWLClass datatypePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OWLClass namedIndividualConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OWLClass negativePropertyAssertionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OWLClass nothingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OWLClass objectPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OWLClass asymmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OWLClass inverseFunctionalPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OWLClass irreflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OWLClass reflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OWLClass symmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OWLClass transitivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OWLClass ontologyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OWLClass ontologyPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IFlexoOntologyStructuralProperty");
		assertNotNull(ontologyPropertyConcept);
		OWLObjectProperty topObjectProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topObjectProperty");
		assertNotNull(topObjectProperty);
		OWLObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OWLDataProperty bottomDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OWLDataProperty topDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);
		OWLObjectProperty annotatedPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OWLObjectProperty annotatedSourceProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OWLObjectProperty annotatedTargetProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OWLObjectProperty datatypeComplementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OWLObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OWLObjectProperty equivalentClassProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OWLObjectProperty equivalentPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OWLObjectProperty intersectionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OWLObjectProperty membersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OWLObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OWLObjectProperty oneOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OWLObjectProperty propertyDisjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OWLObjectProperty unionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OWLObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "versionInfo");
		assertNotNull(versionInfoProperty);
		OWLObjectProperty withRestrictionsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OWLObjectProperty differentFromProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "differentFrom");
		assertNotNull(differentFromProperty);
		OWLObjectProperty sameAsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OWLObjectProperty distinctMembersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OWLObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OWLObjectProperty disjointUnionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OWLObjectProperty complementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "complementOf");
		assertNotNull(complementOfProperty);
		OWLObjectProperty disjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "disjointWith");
		assertNotNull(disjointWithProperty);
		OWLObjectProperty sourceIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OWLObjectProperty assertionPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OWLObjectProperty targetValueProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "targetValue");
		assertNotNull(targetValueProperty);
		OWLObjectProperty targetIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OWLObjectProperty propertyChainAxiomProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OWLObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(owlOntology);
		obm.setStrictMode(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), thingConcept);

		assertSameList(obm.getChildren(thingConcept), allDifferentConcept, annotationPropertyConcept, owlClassConcept,
				datatypePropertyConcept, namedIndividualConcept, negativePropertyAssertionConcept, nothingConcept, objectPropertyConcept,
				ontologyConcept, ontologyPropertyConcept, bottomDataProperty, topObjectProperty, topDataProperty, bottomObjectProperty,
				sameAsProperty, differentFromProperty);

		assertEquals(5, obm.getChildren(owlClassConcept).size());
		assertSameList(obm.getChildren(owlClassConcept), restrictionConcept, complementOfProperty, disjointUnionOfProperty, hasKeyProperty,
				disjointWithProperty);

		assertEquals(8, obm.getChildren(objectPropertyConcept).size());
		assertSameList(obm.getChildren(objectPropertyConcept), asymmetricPropertyConcept, inverseFunctionalPropertyConcept,
				irreflexivePropertyConcept, reflexivePropertyConcept, symmetricPropertyConcept, transitivePropertyConcept,
				inverseOfProperty, propertyChainAxiomProperty);

	}

	public void test10AssertFlexoConceptOntologyNoHierarchy() {
		log("test10AssertFlexoConceptOntologyNoHierarchy()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		OWLOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());
		OWLOntology flexoConceptsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology();
		assertNotNull(flexoConceptsOntology);

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OWLClass thingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OWLClass allDifferentConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OWLClass annotationPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OWLClass owlClassConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OWLClass restrictionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OWLClass datatypePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OWLClass namedIndividualConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OWLClass negativePropertyAssertionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OWLClass nothingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OWLClass objectPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OWLClass asymmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OWLClass inverseFunctionalPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OWLClass irreflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OWLClass reflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OWLClass symmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OWLClass transitivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OWLClass ontologyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OWLClass ontologyPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IFlexoOntologyStructuralProperty");
		assertNotNull(ontologyPropertyConcept);
		OWLObjectProperty topObjectProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topObjectProperty");
		assertNotNull(topObjectProperty);
		OWLObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OWLDataProperty bottomDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OWLDataProperty topDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);

		OWLClass flexoConcept = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoConcept");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoModelObject = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoModelObject");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoProcessFolder = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessFolder");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoProcess = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoProcess");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoRole = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoRole");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoProcessElement = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessElement");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoActivity = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoOperation = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoOperation");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoEvent = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoEvent");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLObjectProperty inRelationWithProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "inRelationWith");
		assertNotNull(inRelationWithProperty);
		OWLObjectProperty linkedToModelProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "linkedToModel");
		assertNotNull(linkedToModelProperty);
		OWLObjectProperty linkedToConceptProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "linkedToConcept");
		assertNotNull(linkedToConceptProperty);
		OWLDataProperty resourceNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "resourceName");
		assertNotNull(resourceNameProperty);
		OWLDataProperty classNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "className");
		assertNotNull(classNameProperty);
		OWLDataProperty flexoIDProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "flexoID");
		assertNotNull(flexoIDProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(flexoConceptsOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(false);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), flexoConceptsOntology);

		assertEquals(4, obm.getChildren(flexoConceptsOntology).size());
		assertSameList(obm.getChildren(flexoConceptsOntology), flexoConceptsOntology.getThingConcept(), owlOntology, rdfOntology,
				rdfsOntology);

		assertEquals(2, obm.getChildren(flexoConceptsOntology.getThingConcept()).size());
		assertSameList(obm.getChildren(flexoConceptsOntology.getThingConcept()), flexoConcept, flexoModelObject);

		assertEquals(2, obm.getChildren(flexoConcept).size());
		assertSameList(obm.getChildren(flexoConcept), inRelationWithProperty, linkedToModelProperty);

		assertEquals(8, obm.getChildren(flexoModelObject).size());
		assertSameList(obm.getChildren(flexoModelObject), flexoProcess, flexoProcessElement, flexoProcessFolder, flexoRole,
				resourceNameProperty, classNameProperty, linkedToConceptProperty, flexoIDProperty);

		assertEquals(3, obm.getChildren(flexoProcessElement).size());
		assertSameList(obm.getChildren(flexoProcessElement), flexoActivity, flexoEvent, flexoOperation);

	}

	public void test11AssertFlexoConceptOntologyHierarchy() {
		log("test11AssertFlexoConceptOntologyHierarchy()");
		OWLOntology rdfOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFOntology();
		assertNotNull(rdfOntology);
		assertTrue(rdfOntology.isLoaded());
		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		assertNotNull(rdfsOntology);
		assertTrue(rdfsOntology.isLoaded());
		OWLOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();
		assertNotNull(owlOntology);
		assertTrue(owlOntology.isLoaded());
		OWLOntology flexoConceptsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getFlexoConceptOntology();
		assertNotNull(flexoConceptsOntology);

		OWLClass listConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "List");
		assertNotNull(listConcept);
		OWLClass propertyConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Property");
		assertNotNull(propertyConcept);
		OWLClass statementConcept = rdfOntology.getClass(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "Statement");
		assertNotNull(statementConcept);
		OWLObjectProperty typeProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "type");
		assertNotNull(typeProperty);
		OWLObjectProperty valueProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "value");
		assertNotNull(valueProperty);
		OWLObjectProperty restProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "rest");
		assertNotNull(restProperty);
		OWLObjectProperty firstProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "first");
		assertNotNull(firstProperty);
		OWLObjectProperty subjectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "subject");
		assertNotNull(subjectProperty);
		OWLObjectProperty predicateProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "predicate");
		assertNotNull(predicateProperty);
		OWLObjectProperty objectProperty = rdfOntology.getObjectProperty(RDFURIDefinitions.RDF_ONTOLOGY_URI + "#" + "object");
		assertNotNull(objectProperty);

		OWLClass resourceConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);
		OWLClass classConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(classConcept);
		OWLClass datatypeConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Datatype");
		assertNotNull(datatypeConcept);
		OWLClass containerConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Container");
		assertNotNull(containerConcept);
		OWLClass literalConcept = rdfOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Literal");
		assertNotNull(literalConcept);
		OWLDataProperty labelProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "label");
		assertNotNull(labelProperty);
		OWLDataProperty commentProperty = rdfOntology.getDataProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "comment");
		assertNotNull(commentProperty);
		OWLObjectProperty isDefinedByProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "isDefinedBy");
		assertNotNull(isDefinedByProperty);
		OWLObjectProperty seeAlsoProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "seeAlso");
		assertNotNull(seeAlsoProperty);
		OWLObjectProperty memberProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "member");
		assertNotNull(memberProperty);
		OWLObjectProperty domainProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "domain");
		assertNotNull(domainProperty);
		OWLObjectProperty rangeProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "range");
		assertNotNull(rangeProperty);
		OWLObjectProperty subClassOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "subClassOf");
		assertNotNull(subClassOfProperty);
		OWLObjectProperty subPropertyOfProperty = rdfOntology.getObjectProperty(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#"
				+ "subPropertyOf");
		assertNotNull(subPropertyOfProperty);

		OWLClass thingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Thing");
		assertNotNull(thingConcept);
		OWLClass allDifferentConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AllDifferent");
		assertNotNull(allDifferentConcept);
		OWLClass annotationPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AnnotationProperty");
		assertNotNull(annotationPropertyConcept);
		OWLClass owlClassConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Class");
		assertNotNull(owlClassConcept);
		OWLClass restrictionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Restriction");
		assertNotNull(restrictionConcept);
		OWLClass datatypePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "DatatypeProperty");
		assertNotNull(datatypePropertyConcept);
		OWLClass namedIndividualConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OWLClass negativePropertyAssertionConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "NegativePropertyAssertion");
		assertNotNull(negativePropertyAssertionConcept);
		OWLClass nothingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);
		OWLClass objectPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ObjectProperty");
		assertNotNull(objectPropertyConcept);
		OWLClass asymmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "AsymmetricProperty");
		assertNotNull(asymmetricPropertyConcept);
		OWLClass inverseFunctionalPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "InverseFunctionalProperty");
		assertNotNull(inverseFunctionalPropertyConcept);
		OWLClass irreflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IrreflexiveProperty");
		assertNotNull(irreflexivePropertyConcept);
		OWLClass reflexivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "ReflexiveProperty");
		assertNotNull(reflexivePropertyConcept);
		OWLClass symmetricPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "SymmetricProperty");
		assertNotNull(symmetricPropertyConcept);
		OWLClass transitivePropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "TransitiveProperty");
		assertNotNull(transitivePropertyConcept);
		OWLClass ontologyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Ontology");
		assertNotNull(ontologyConcept);
		OWLClass ontologyPropertyConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "IFlexoOntologyStructuralProperty");
		assertNotNull(ontologyPropertyConcept);
		OWLObjectProperty topObjectProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topObjectProperty");
		assertNotNull(topObjectProperty);
		OWLObjectProperty bottomObjectProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "bottomObjectProperty");
		assertNotNull(bottomObjectProperty);
		OWLDataProperty bottomDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "bottomDataProperty");
		assertNotNull(bottomDataProperty);
		OWLDataProperty topDataProperty = owlOntology.getDataProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "topDataProperty");
		assertNotNull(topDataProperty);

		OWLObjectProperty annotatedPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedProperty");
		assertNotNull(annotatedPropertyProperty);
		OWLObjectProperty annotatedSourceProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedSource");
		assertNotNull(annotatedSourceProperty);
		OWLObjectProperty annotatedTargetProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "annotatedTarget");
		assertNotNull(annotatedTargetProperty);
		OWLObjectProperty datatypeComplementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "datatypeComplementOf");
		assertNotNull(datatypeComplementOfProperty);
		OWLObjectProperty deprecatedProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "deprecated");
		assertNotNull(deprecatedProperty);
		OWLObjectProperty equivalentClassProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentClass");
		assertNotNull(equivalentClassProperty);
		OWLObjectProperty equivalentPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "equivalentProperty");
		assertNotNull(equivalentPropertyProperty);
		OWLObjectProperty intersectionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "intersectionOf");
		assertNotNull(intersectionOfProperty);
		OWLObjectProperty membersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "members");
		assertNotNull(membersProperty);
		OWLObjectProperty onDatatypeProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "onDatatype");
		assertNotNull(onDatatypeProperty);
		OWLObjectProperty oneOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "oneOf");
		assertNotNull(oneOfProperty);
		OWLObjectProperty propertyDisjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyDisjointWith");
		assertNotNull(propertyDisjointWithProperty);
		OWLObjectProperty unionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "unionOf");
		assertNotNull(unionOfProperty);
		OWLObjectProperty versionInfoProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "versionInfo");
		assertNotNull(versionInfoProperty);
		OWLObjectProperty withRestrictionsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "withRestrictions");
		assertNotNull(withRestrictionsProperty);
		OWLObjectProperty differentFromProperty = owlOntology
				.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "differentFrom");
		assertNotNull(differentFromProperty);
		OWLObjectProperty sameAsProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "sameAs");
		assertNotNull(sameAsProperty);
		OWLObjectProperty distinctMembersProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "distinctMembers");
		assertNotNull(distinctMembersProperty);
		OWLObjectProperty hasKeyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "hasKey");
		assertNotNull(hasKeyProperty);
		OWLObjectProperty disjointUnionOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "disjointUnionOf");
		assertNotNull(disjointUnionOfProperty);
		OWLObjectProperty complementOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "complementOf");
		assertNotNull(complementOfProperty);
		OWLObjectProperty disjointWithProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "disjointWith");
		assertNotNull(disjointWithProperty);
		OWLObjectProperty sourceIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "sourceIndividual");
		assertNotNull(sourceIndividualProperty);
		OWLObjectProperty assertionPropertyProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "assertionProperty");
		assertNotNull(assertionPropertyProperty);
		OWLObjectProperty targetValueProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "targetValue");
		assertNotNull(targetValueProperty);
		OWLObjectProperty targetIndividualProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "targetIndividual");
		assertNotNull(targetIndividualProperty);
		OWLObjectProperty propertyChainAxiomProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#"
				+ "propertyChainAxiom");
		assertNotNull(propertyChainAxiomProperty);
		OWLObjectProperty inverseOfProperty = owlOntology.getObjectProperty(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "inverseOf");
		assertNotNull(inverseOfProperty);

		OWLClass flexoConcept = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoConcept");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoModelObject = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoModelObject");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoProcessFolder = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessFolder");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoProcess = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoProcess");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoRole = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoRole");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoProcessElement = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "FlexoProcessElement");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoActivity = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoActivity");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoOperation = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoOperation");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLClass flexoEvent = flexoConceptsOntology.getClass(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoEvent");
		assertNotNull(flexoConcept);
		assertTrue(flexoConceptsOntology.getClasses().contains(flexoConcept));
		OWLObjectProperty inRelationWithProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "inRelationWith");
		assertNotNull(inRelationWithProperty);
		OWLObjectProperty linkedToModelProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "linkedToModel");
		assertNotNull(linkedToModelProperty);
		OWLObjectProperty linkedToConceptProperty = flexoConceptsOntology.getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI
				+ "#" + "linkedToConcept");
		assertNotNull(linkedToConceptProperty);
		OWLDataProperty resourceNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "resourceName");
		assertNotNull(resourceNameProperty);
		OWLDataProperty classNameProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "className");
		assertNotNull(classNameProperty);
		OWLDataProperty flexoIDProperty = flexoConceptsOntology.getDataProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#"
				+ "flexoID");
		assertNotNull(flexoIDProperty);

		OntologyBrowserModel obm = new OntologyBrowserModel(flexoConceptsOntology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		OWLClass thingConceptSeenFromFlexoConceptOntology = flexoConceptsOntology.getThingConcept();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), thingConceptSeenFromFlexoConceptOntology);

		assertEquals(11, obm.getChildren(thingConceptSeenFromFlexoConceptOntology).size());
		assertSameList(obm.getChildren(thingConceptSeenFromFlexoConceptOntology), flexoConcept, flexoModelObject, namedIndividualConcept,
				nothingConcept, resourceConcept, topDataProperty, topObjectProperty, bottomDataProperty, bottomObjectProperty,
				sameAsProperty, differentFromProperty);

		assertEquals(2, obm.getChildren(flexoConcept).size());
		assertSameList(obm.getChildren(flexoConcept), inRelationWithProperty, linkedToModelProperty);

		assertEquals(8, obm.getChildren(flexoModelObject).size());
		assertSameList(obm.getChildren(flexoModelObject), flexoProcess, flexoProcessElement, flexoProcessFolder, flexoRole,
				resourceNameProperty, classNameProperty, linkedToConceptProperty, flexoIDProperty);

		assertEquals(3, obm.getChildren(flexoProcessElement).size());
		assertSameList(obm.getChildren(flexoProcessElement), flexoActivity, flexoEvent, flexoOperation);

	}

	public void test12AssertO5Ontology() {
		log("test12AssertO5Ontology()");
		OWLOntology o1 = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O1.owl");
		assertNotNull(o1);
		OWLOntology o2 = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O2.owl");
		assertNotNull(o2);
		OWLOntology o3 = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O3.owl");
		assertNotNull(o3);
		OWLOntology o5 = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology("http://www.openflexo.org/test/O5.owl");
		assertNotNull(o5);

		o3.loadWhenUnloaded();
		assertTrue(o3.isLoaded());
		assertTrue(o1.isLoaded());
		assertTrue(o2.isLoaded());

		OWLOntology rdfsOntology = testResourceCenter.retrieveBaseOntologyLibrary().getRDFSOntology();
		OWLOntology owlOntology = testResourceCenter.retrieveBaseOntologyLibrary().getOWLOntology();

		OWLClass resourceConcept = rdfsOntology.getClass(RDFSURIDefinitions.RDFS_ONTOLOGY_URI + "#" + "Resource");
		assertNotNull(resourceConcept);

		OWLClass namedIndividualConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "NamedIndividual");
		assertNotNull(namedIndividualConcept);
		OWLClass nothingConcept = owlOntology.getClass(OWL2URIDefinitions.OWL_ONTOLOGY_URI + "#" + "Nothing");
		assertNotNull(nothingConcept);

		assertEquals(1, o3.getClasses().size());
		assertSameList(o3.getClasses(), o3.getThingConcept());
		assertEquals(o3.getOntologyLibrary().getOWLOntology().getThingConcept(), o3.getThingConcept().getOriginalDefinition());

		OWLClass a2 = o2.getClass(o2.getURI() + "#A2");
		assertNotNull(a2);
		OWLClass b2 = o2.getClass(o2.getURI() + "#B2");
		assertNotNull(b2);
		OWLClass c2 = o2.getClass(o2.getURI() + "#C2");
		assertNotNull(c2);
		assertEquals(4, o2.getClasses().size());
		assertSameList(o2.getClasses(), o2.getThingConcept(), a2, b2, c2);

		OWLClass a1 = o1.getClass(o1.getURI() + "#A1");
		assertNotNull(a1);
		OWLClass b1 = o1.getClass(o1.getURI() + "#B1");
		assertNotNull(b1);
		OWLClass c1 = o1.getClass(o1.getURI() + "#C1");
		assertNotNull(c1);
		assertEquals(4, o1.getClasses().size());
		assertSameList(o1.getClasses(), o1.getThingConcept(), a1, b1, c1);
		assertTrue(b1.isSuperConceptOf(c1));

		o5.loadWhenUnloaded();
		assertTrue(o5.isLoaded());

		OWLClass a1fromO5 = o5.getClass(o1.getURI() + "#A1");
		assertNotNull(a1fromO5);
		assertEquals(a1, a1fromO5.getOriginalDefinition());

		OWLClass a2fromO5 = o5.getClass(o2.getURI() + "#A2");
		assertNotNull(a2fromO5);
		assertEquals(a2, a2fromO5.getOriginalDefinition());

		assertEquals(3, o5.getClasses().size());
		assertSameList(o5.getClasses(), o5.getThingConcept(), a1fromO5, a2fromO5);

		assertTrue(a2fromO5.isSuperConceptOf(a1fromO5));

		OntologyBrowserModel obm = new OntologyBrowserModel(o5);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setDisplayPropertiesInClasses(true);
		obm.setShowOWLAndRDFConcepts(false);
		obm.setShowClasses(true);
		obm.setShowIndividuals(false);
		obm.setShowObjectProperties(false);
		obm.setShowDataProperties(false);
		obm.setShowAnnotationProperties(false);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), o5.getThingConcept());

		assertSameList(obm.getChildren(o5.getThingConcept()), a2fromO5, b2, c2, b1);

		// Showing OWL, RDF and RDFS concepts should not change hierarchy
		obm.setShowOWLAndRDFConcepts(true);
		obm.recomputeStructure();

		assertEquals(1, obm.getRoots().size());
		assertEquals(obm.getRoots().get(0), o5.getThingConcept());

		assertSameList(obm.getChildren(o5.getThingConcept()), a2fromO5, b2, c2, b1, namedIndividualConcept, nothingConcept, resourceConcept);

	}

	public void test13AssertTestPropertiesOntology() {
		log("test13AssertTestPropertiesOntology()");
		OWLOntology ontology = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology(
				"http://www.openflexo.org/test/TestProperties.owl");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());

		OWLClass conceptA = ontology.getClass(ontology.getURI() + "#ConceptA");
		assertNotNull(conceptA);
		OWLClass conceptB = ontology.getClass(ontology.getURI() + "#ConceptB");
		assertNotNull(conceptB);
		OWLClass conceptC = ontology.getClass(ontology.getURI() + "#ConceptC");
		assertNotNull(conceptC);
		OWLClass conceptD = ontology.getClass(ontology.getURI() + "#ConceptD");
		assertNotNull(conceptD);
		OWLClass conceptE = ontology.getClass(ontology.getURI() + "#ConceptE");
		assertNotNull(conceptE);
		OWLClass conceptF = ontology.getClass(ontology.getURI() + "#ConceptF");
		assertNotNull(conceptF);
		OWLClass conceptG = ontology.getClass(ontology.getURI() + "#ConceptG");
		assertNotNull(conceptG);
		OWLClass conceptH = ontology.getClass(ontology.getURI() + "#ConceptH");
		assertNotNull(conceptH);
		OWLClass conceptX = ontology.getClass(ontology.getURI() + "#ConceptX");
		assertNotNull(conceptX);

		OWLObjectProperty property1 = ontology.getObjectProperty(ontology.getURI() + "#property1");
		assertNotNull(property1);
		OWLObjectProperty property2 = ontology.getObjectProperty(ontology.getURI() + "#property2");
		assertNotNull(property2);
		OWLObjectProperty property3 = ontology.getObjectProperty(ontology.getURI() + "#property3");
		assertNotNull(property3);
		OWLObjectProperty property4 = ontology.getObjectProperty(ontology.getURI() + "#property4");
		assertNotNull(property4);

		// Some tests on ancestors computing
		assertEquals(conceptB, OntologyUtils.getFirstCommonAncestor(conceptF, conceptE));
		assertEquals(conceptC, OntologyUtils.getFirstCommonAncestor(conceptH, conceptG));
		assertEquals(conceptA, OntologyUtils.getFirstCommonAncestor(conceptF, conceptG));
		assertEquals(conceptA, OntologyUtils.getFirstCommonAncestor(conceptC, conceptE));
		assertEquals(conceptC, OntologyUtils.getFirstCommonAncestor(conceptC, conceptH));
		assertEquals(ontology.getThingConcept(), OntologyUtils.getFirstCommonAncestor(conceptA, conceptX));

		OntologyBrowserModel obm = new OntologyBrowserModel(ontology);
		obm.setStrictMode(true);
		obm.setHierarchicalMode(true);
		obm.setDisplayPropertiesInClasses(true);
		obm.setShowOWLAndRDFConcepts(false);
		obm.setShowClasses(false);
		obm.setShowIndividuals(false);
		obm.setShowObjectProperties(true);
		obm.setShowDataProperties(true);
		obm.setShowAnnotationProperties(true);
		obm.recomputeStructure();

	}

	public void test14AssertSepelMappingOntology() {
		log("test14AssertSepelMappingOntology()");

		String MAPPING_SPECS_URI = "http://www.thalesgroup.com/ontologies/sepel-ng/MappingSpecifications.owl";
		String INPUT_MODEL_1_URI = "http://www.thalesgroup.com/ontologies/sepel-ng/SEPELInputModel1.owl";
		String INPUT_MODEL_2_URI = "http://www.thalesgroup.com/ontologies/sepel-ng/SEPELInputModel2.owl";
		String OUTPUT_MODEL_1_URI = "http://www.thalesgroup.com/ontologies/sepel-ng/SEPELOutputModel1.owl";
		String OUTPUT_MODEL_2_URI = "http://www.thalesgroup.com/ontologies/sepel-ng/SEPELOutputModel2.owl";
		String OUTPUT_MODEL_3_URI = "http://www.thalesgroup.com/ontologies/sepel-ng/SEPELOutputModel3.owl";

		OWLOntology ontology = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology(
				"http://www.thalesgroup.com/ViewPoints/sepel-ng/MappingSpecification.owl");
		assertNotNull(ontology);

		ontology.loadWhenUnloaded();
		assertTrue(ontology.isLoaded());

		OWLOntology mappingSpecsOntology = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology(MAPPING_SPECS_URI);
		assertNotNull(mappingSpecsOntology);
		assertTrue(mappingSpecsOntology.isLoaded());

		OWLOntology inputModel1Ontology = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology(INPUT_MODEL_1_URI);
		assertNotNull(inputModel1Ontology);
		assertTrue(inputModel1Ontology.isLoaded());

		OWLOntology inputModel2Ontology = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology(INPUT_MODEL_2_URI);
		assertNotNull(inputModel2Ontology);
		assertTrue(inputModel2Ontology.isLoaded());

		OWLOntology outputModel1Ontology = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology(OUTPUT_MODEL_1_URI);
		assertNotNull(outputModel1Ontology);
		assertTrue(outputModel1Ontology.isLoaded());

		OWLOntology outputModel2Ontology = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology(OUTPUT_MODEL_2_URI);
		assertNotNull(outputModel2Ontology);
		assertTrue(outputModel2Ontology.isLoaded());

		OWLOntology outputModel3Ontology = (OWLOntology) testResourceCenter.retrieveBaseOntologyLibrary().getOntology(OUTPUT_MODEL_3_URI);
		assertNotNull(outputModel3Ontology);
		assertTrue(outputModel3Ontology.isLoaded());

		OWLClass rootClassForInputModel1 = ontology.getClass(inputModel1Ontology.getURI() + "#RootClassForInputModel1");
		assertNotNull(rootClassForInputModel1);

		OWLClass rootClassForInputModel2 = ontology.getClass(inputModel2Ontology.getURI() + "#RootClassForInputModel2");
		assertNotNull(rootClassForInputModel2);

		OWLClass rootClassForOutputModel1 = ontology.getClass(outputModel1Ontology.getURI() + "#RootClassForOutputModel1");
		assertNotNull(rootClassForOutputModel1);

		OWLClass rootClassForOutputModel2 = ontology.getClass(outputModel2Ontology.getURI() + "#RootClassForOutputModel2");
		assertNotNull(rootClassForOutputModel2);

		OWLClass rootClassForOutputModel3 = ontology.getClass(outputModel3Ontology.getURI() + "#RootClassForOutputModel3");
		assertNotNull(rootClassForOutputModel3);

		OWLClass inputModelObject = ontology.getClass(mappingSpecsOntology.getURI() + "#InputModelObject");
		assertNotNull(inputModelObject);

		OWLClass outputModelObject = ontology.getClass(mappingSpecsOntology.getURI() + "#OutputModelObject");
		assertNotNull(outputModelObject);

		OWLClass mappingSpecificationObject = ontology.getClass(mappingSpecsOntology.getURI() + "#MappingSpecificationObject");
		assertNotNull(mappingSpecificationObject);

		assertTrue(inputModelObject.redefinesOriginalDefinition());
		assertEquals(mappingSpecsOntology.getClass(mappingSpecsOntology.getURI() + "#InputModelObject"),
				inputModelObject.getOriginalDefinition());

		assertTrue(outputModelObject.redefinesOriginalDefinition());
		assertEquals(mappingSpecsOntology.getClass(mappingSpecsOntology.getURI() + "#OutputModelObject"),
				outputModelObject.getOriginalDefinition());

		assertTrue(rootClassForInputModel1.redefinesOriginalDefinition());
		assertEquals(inputModel1Ontology.getClass(inputModel1Ontology.getURI() + "#RootClassForInputModel1"),
				rootClassForInputModel1.getOriginalDefinition());

		assertTrue(rootClassForInputModel2.redefinesOriginalDefinition());
		assertEquals(inputModel2Ontology.getClass(inputModel2Ontology.getURI() + "#RootClassForInputModel2"),
				rootClassForInputModel2.getOriginalDefinition());

		assertTrue(rootClassForOutputModel1.redefinesOriginalDefinition());
		assertEquals(outputModel1Ontology.getClass(outputModel1Ontology.getURI() + "#RootClassForOutputModel1"),
				rootClassForOutputModel1.getOriginalDefinition());

		assertTrue(rootClassForOutputModel2.redefinesOriginalDefinition());
		assertEquals(outputModel2Ontology.getClass(outputModel2Ontology.getURI() + "#RootClassForOutputModel2"),
				rootClassForOutputModel2.getOriginalDefinition());

		assertTrue(rootClassForOutputModel3.redefinesOriginalDefinition());
		assertEquals(outputModel3Ontology.getClass(outputModel3Ontology.getURI() + "#RootClassForOutputModel3"),
				rootClassForOutputModel3.getOriginalDefinition());

		assertTrue(inputModelObject.isSuperConceptOf(rootClassForInputModel1));
		assertTrue(inputModelObject.isSuperConceptOf(rootClassForInputModel2));
		assertTrue(outputModelObject.isSuperConceptOf(rootClassForOutputModel1));
		assertTrue(outputModelObject.isSuperConceptOf(rootClassForOutputModel2));
		assertTrue(outputModelObject.isSuperConceptOf(rootClassForOutputModel3));

		OntologyBrowserModel obm = new OntologyBrowserModel(ontology);
		obm.setStrictMode(false);
		obm.setHierarchicalMode(true);
		obm.setDisplayPropertiesInClasses(true);
		obm.setShowOWLAndRDFConcepts(false);
		obm.setShowClasses(true);
		obm.setShowIndividuals(false);
		obm.setShowObjectProperties(true);
		obm.setShowDataProperties(true);
		obm.setShowAnnotationProperties(true);
		obm.recomputeStructure();

		assertSameList(obm.getChildren(ontology.getThingConcept()), inputModelObject, outputModelObject, mappingSpecificationObject);

		// We want now to see only properties, lets look at the infered class hierarchy
		obm.setShowClasses(false);
		obm.recomputeStructure();

		assertSameList(obm.getRoots(), ontology.getThingConcept());
		assertSameList(obm.getChildren(ontology.getThingConcept()), rootClassForInputModel1, rootClassForOutputModel1,
				mappingSpecificationObject);

		// Now we want to see only properties below rootClass
		obm.setRootClass(rootClassForInputModel1);
		obm.recomputeStructure();

		OWLClass etat = ontology.getClass(inputModel1Ontology.getURI() + "#Etat");
		assertNotNull(etat);
		OWLClass descriptionSpatiale = ontology.getClass(inputModel1Ontology.getURI() + "#DescriptionSpatiale");
		assertNotNull(descriptionSpatiale);
		OWLClass emetteur = ontology.getClass(inputModel1Ontology.getURI() + "#Emetteur");
		assertNotNull(emetteur);
		OWLClass descriptionSpectrale = ontology.getClass(inputModel1Ontology.getURI() + "#DescriptionSpectrale");
		assertNotNull(descriptionSpectrale);
		OWLClass descriptionTemporelle = ontology.getClass(inputModel1Ontology.getURI() + "#DescriptionTemporelle");
		assertNotNull(descriptionTemporelle);
		OWLClass intervalleComplexe = ontology.getClass(inputModel1Ontology.getURI() + "#IntervalleComplexe");
		assertNotNull(intervalleComplexe);
		OWLClass frequence = ontology.getClass(inputModel1Ontology.getURI() + "#Frequence");
		assertNotNull(frequence);
		OWLClass contenu = ontology.getClass(inputModel1Ontology.getURI() + "#Contenu");
		assertNotNull(contenu);

		assertSameList(obm.getRoots(), rootClassForInputModel1);
		assertSameList(obm.getChildren(rootClassForInputModel1), etat, descriptionSpatiale, emetteur, descriptionSpectrale,
				descriptionTemporelle, intervalleComplexe, frequence, contenu);

	}
}
