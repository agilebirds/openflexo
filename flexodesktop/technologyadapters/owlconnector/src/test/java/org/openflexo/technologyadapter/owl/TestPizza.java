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
package org.openflexo.technologyadapter.owl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.openflexo.foundation.OpenflexoRunTimeTestCase;
import org.openflexo.foundation.TestFlexoServiceManager;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;
import org.openflexo.toolbox.FileResource;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestPizza extends OpenflexoRunTimeTestCase {

	private static TestFlexoServiceManager testServiceManager;
	private static OWLTechnologyAdapter owlAdapter;
	private static OWLOntologyLibrary ontologyLibrary;

	/**
	 * Instanciate test ResourceCenter
	 */
	public void test0LoadTestResourceCenter() {
		log("test0LoadTestResourceCenter()");
		testServiceManager = new TestFlexoServiceManager(new FileResource("src/test/resources/Ontologies"));
		owlAdapter = testServiceManager.getTechnologyAdapterService().getTechnologyAdapter(OWLTechnologyAdapter.class);
		ontologyLibrary = (OWLOntologyLibrary) testServiceManager.getTechnologyAdapterService().getTechnologyContextManager(owlAdapter);
	}

	/**
	 * Load an ontology
	 */
	public void test1LoadTestResourceCenter() {

		File myOntology = new FileResource("MyOntologies/MyPizza.owl");

		System.out.println("Found: " + myOntology);
		OWLOntology hop = new OWLOntology(OWLOntology.findOntologyURI(myOntology), myOntology, ontologyLibrary, owlAdapter);

		// importedOntologyLibraries.debug();

		hop.loadWhenUnloaded();

		hop.describe();

		// FlexoProject project;
		// project.getURI();

		File createdFile = null;

		try {
			File f = File.createTempFile("MyPizza", ".owl");
			createdFile = new File(myOntology.getParent(), f.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Try to create ontology: " + createdFile);

		String URI = "http://my-pizza.com";

		Model base = ModelFactory.createDefaultModel();
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, ontologyLibrary, base);
		ontModel.createOntology(URI);
		ontModel.setDynamicImports(true);
		OntClass cacaClass = ontModel.createClass(URI + "#" + "caca");
		OntClass pipiClass = ontModel.createClass(URI + "#" + "pipi");
		pipiClass.addSuperClass(cacaClass);

		OWLOntology flexoConceptsOntology = ontologyLibrary.getOntology(OWLOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI);

		ontModel.getDocumentManager().loadImport(flexoConceptsOntology.getOntModel(), OWLOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI);
		ontModel.getDocumentManager().addModel(OWLOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI, flexoConceptsOntology.getOntModel(), true);
		ontModel.loadImports();
		ontModel.getDocumentManager().loadImports(ontModel);

		OntClass flexoConceptClass = flexoConceptsOntology.getOntModel().createClass(
				OWLOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI + "#" + "FlexoConcept");

		for (Iterator i = flexoConceptClass.listSuperClasses(); i.hasNext();) {
			OntClass unParent = (OntClass) i.next();
			System.out.println("FlexoConcept, comme parent j'ai: " + unParent);
		}
		pipiClass.addSuperClass(flexoConceptClass);

		System.out.println("Dynamic imports= " + ontModel.getDynamicImports());

		for (Object o : ontModel.listImportedOntologyURIs()) {
			System.out.println("J'importe " + (String) o);
		}

		for (Iterator i = ontModel.listSubModels(true); i.hasNext();) {
			OntModel unModele = (OntModel) i.next();
			System.out.println("Comme sub-model j'ai: " + unModele);
		}

		for (Iterator i = ontModel.getDocumentManager().listDocuments(); i.hasNext();) {
			System.out.println("Comme document j'ai: " + i.next());
		}

		String ONTOLOGY_C = URI;
		String ONTOLOGY_A = OWLOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI;
		String ONTOLOGY_B = "http://www.denali.be/flexo/ontologies/Calcs/PizzaEditor.owl";
		String ONTOLOGY_D = "http://www.denali.be/flexo/ontologies/Calcs/PizzaIngredientEditor.owl";

		String SOURCE = "@prefix rdf:         <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n"
				+ "@prefix rdfs:        <http://www.w3.org/2000/01/rdf-schema#>.\n"
				+ "@prefix owl:         <http://www.w3.org/2002/07/owl#>.\n" + "<" + ONTOLOGY_C + "> a owl:Ontology \n"
				+ "   ; owl:imports <" + ONTOLOGY_A + ">\n" + "   ; owl:imports <" + ONTOLOGY_B + ">.\n";

		System.out.println("About to load source ontology:");
		System.out.println(SOURCE);

		// create an ont model spec that uses a custom document manager to
		// look for imports in the database
		/*OntModelSpec oms = getMaker();
		oms.setDocumentManager( new DbAwareDocumentManager( m_maker ) );*/

		// create the ontology model
		/*Model base = m_maker.createModel( ONTOLOGY_C );
		OntModel om = ModelFactory.createOntologyModel( oms, base );*/

		// read in some content which does importing
		ontModel.read(new StringReader(SOURCE), ONTOLOGY_C, "N3");

		// as a test, write everything
		// System.out.println( "Combined model contents:" );
		// ontModel.writeAll( System.out, "N3", null );

		// Model base = importedOntologyLibrary.createModel("http://my-pizza.com");
		/*Model base = null; //new ModelCom(importedOntologyLibrary.getGraphMaker().createGraph("http://my-pizza.com"));
		
		
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, importedOntologyLibrary, base);

		ontModel.createOntology("http://my-pizza.com");
		
		//ontModel.addSubModel(importedOntologyLibrary.getOntology(ImportedOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI).getOntModel());
		
		//ontModel.read(importedOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI);
		
		//ontModel.getDocumentManager().addModel(importedOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI, importedOntologyLibrary.getOntology(ImportedOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI).getOntModel());
		
		//		 URI declarations
		String familyUri = "http://my-pizza.com";
		String relationshipUri = "http://purl.org/vocab/relationship/";

		//		 Create an empty Model
		//	Model model = ModelFactory.createDefaultModel();

		//		 Create a Resource for each family member, identified by their URI
		OntClass adam = ontModel.createClass(familyUri+"adam");
		OntClass beth = ontModel.createClass(familyUri+"beth");
		OntClass chuck = ontModel.createClass(familyUri+"chuck");
		OntClass dotty = ontModel.createClass(familyUri+"dotty");
		//		 and so on for other family members

		//		 Create properties for the different types of relationship to represent
		OntProperty childOf = ontModel.createOntProperty(relationshipUri+"childOf");
		OntProperty parentOf = ontModel.createOntProperty(relationshipUri+"parentOf");
		OntProperty siblingOf = ontModel.createOntProperty(relationshipUri+"siblingOf");
		OntProperty spouseOf = ontModel.createOntProperty(relationshipUri+"spouseOf");

		//		 Add properties to adam describing relationships to other family members
		adam.addProperty(siblingOf,beth);
		adam.addProperty(spouseOf,dotty);
		adam.addProperty(parentOf,chuck);

		//		 Can also create statements directly . . .
		Statement statement = ontModel.createStatement(adam,parentOf,dotty);

		//		 but remember to add the created statement to the model
		ontModel.add(statement);

		//ontModel.addLoadedImport(importedOntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI);
		*/

		FileOutputStream out;
		try {
			out = new FileOutputStream(createdFile);
			ontModel.write(out, null/*,"http://my-pizza.com"*/);
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Wrote " + createdFile.getName());

		String SOURCE2 = "@prefix rdf:         <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n"
				+ "@prefix rdfs:        <http://www.w3.org/2000/01/rdf-schema#>.\n"
				+ "@prefix owl:         <http://www.w3.org/2002/07/owl#>.\n" + "<" + ONTOLOGY_C + "> a owl:Ontology \n"
				+ "   ; owl:imports <" + ONTOLOGY_D + ">.\n";

		System.out.println("About to load source ontology:");
		System.out.println(SOURCE2);
		ontModel.read(new StringReader(SOURCE2), ONTOLOGY_C, "N3");

		try {
			out = new FileOutputStream(createdFile);
			ontModel.write(out, null/*,"http://my-pizza.com"*/);
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Wrote " + createdFile.getName());
	}

}
