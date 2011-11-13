package org.openflexo.foundation.ontology;

import java.util.Vector;

import org.openflexo.foundation.TemporaryFlexoModelObject;

public class OntologyFolder extends TemporaryFlexoModelObject {

	private final OntologyLibrary ontologyLibrary;
	private final String name;
	private final OntologyFolder parent;
	private final Vector<OntologyFolder> children;
	private final Vector<FlexoOntology> ontologies;

	public OntologyFolder(String name, OntologyFolder parentFolder, OntologyLibrary ontologyLibrary) {
		this.ontologyLibrary = ontologyLibrary;
		this.name = name;
		this.parent = parentFolder;
		children = new Vector<OntologyFolder>();
		ontologies = new Vector<FlexoOntology>();
		if (parentFolder != null) {
			parentFolder.addToChildren(this);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public Vector<OntologyFolder> getChildren() {
		return children;
	}

	public void addToChildren(OntologyFolder aFolder) {
		children.add(aFolder);
	}

	public void removeFromChildren(OntologyFolder aFolder) {
		children.remove(aFolder);
	}

	public Vector<FlexoOntology> getOntologies() {
		return ontologies;
	}

	public void addToOntologies(FlexoOntology anOntology) {
		ontologies.add(anOntology);
	}

	public void removeFromOntologies(FlexoOntology anOntology) {
		ontologies.remove(anOntology);
	}

	public OntologyLibrary getOntologyLibrary() {
		return ontologyLibrary;
	}

}
