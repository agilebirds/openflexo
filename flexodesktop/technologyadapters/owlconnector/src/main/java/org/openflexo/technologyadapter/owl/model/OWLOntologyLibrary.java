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
package org.openflexo.technologyadapter.owl.model;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.OntologyObjectConverter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

import com.hp.hpl.jena.graph.GraphMaker;
import com.hp.hpl.jena.graph.impl.SimpleGraphMaker;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.shared.AlreadyExistsException;
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * The {@link OWLOntologyLibrary} works in conjunction with a {@link FlexoResourceCenterService}. It provides the mechanism to keep
 * reference to all {@link OWLOntology} known in the scope provided by the {@link FlexoResourceCenterService}
 * 
 * @author sylvain
 * 
 */
public class OWLOntologyLibrary extends TechnologyContextManager<OWLOntology, OWLOntology> implements ModelMaker {

	private static final Logger logger = Logger.getLogger(OWLOntologyLibrary.class.getPackage().getName());

	public static final String FLEXO_CONCEPT_ONTOLOGY_URI = "http://www.agilebirds.com/openflexo/ontologies/FlexoConceptsOntology.owl";

	public static final String OPENFLEXO_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#openflexoDescription";
	public static final String BUSINESS_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#businessDescription";
	public static final String TECHNICAL_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#technicalDescription";
	public static final String USER_MANUAL_DESCRIPTION_URI = FLEXO_CONCEPT_ONTOLOGY_URI + "#userManualDescription";

	private SimpleGraphMaker graphMaker;

	private Map<String, FlexoResource<OWLOntology>> ontologies;
	private final Map<String, OWLDataType> dataTypes;

	private OntologyObjectConverter ontologyObjectConverter;

	protected Hashtable<IFlexoOntologyStructuralProperty, StatementWithProperty> statementsWithProperty;

	public StatementWithProperty getStatementWithProperty(IFlexoOntologyStructuralProperty aProperty) {
		if (statementsWithProperty.get(aProperty) != null) {
			return statementsWithProperty.get(aProperty);
		} else {
			StatementWithProperty returned = new StatementWithProperty(aProperty);
			statementsWithProperty.put(aProperty, returned);
			return returned;
		}
	}

	public OWLOntologyLibrary(OWLTechnologyAdapter adapter, FlexoResourceCenterService resourceCenterService) {
		super(adapter, resourceCenterService);

		ontologyObjectConverter = new OntologyObjectConverter(null/*this*/);
		graphMaker = new SimpleGraphMaker();

		ontologies = new HashMap<String, FlexoResource<OWLOntology>>();
		dataTypes = new HashMap<String, OWLDataType>();

		statementsWithProperty = new Hashtable<IFlexoOntologyStructuralProperty, StatementWithProperty>();

	}

	private boolean defaultOntologiesLoaded = false;

	public void init() {
		if (defaultOntologiesLoaded) {
			return;
		}
		logger.info("Instantiating OWLOntologyLibrary Done. Trying to load some ontologies...");

		logger.info("ontologies=" + ontologies);

		// logger.info("getRDFSOntology()=" + getRDFSOntology());
		// logger.info("getRDFOntology()=" + getRDFOntology());
		// logger.info("getOWLOntology()=" + getOWLOntology());
		// logger.info("getFlexoConceptOntology()=" + getFlexoConceptOntology());

		FlexoResource<OWLOntology> rdfsOntologyResource = ontologies.get(RDFSURIDefinitions.RDFS_ONTOLOGY_URI);
		logger.info("rdfsOntologyResource=" + rdfsOntologyResource);

		if (getRDFSOntology() != null && getRDFOntology() != null && getOWLOntology() != null && getFlexoConceptOntology() != null) {
			logger.info("Loading some ontologies...");
			getRDFSOntology().loadWhenUnloaded();
			getRDFOntology().loadWhenUnloaded();
			getOWLOntology().loadWhenUnloaded();
			// Because some ontologies have cross reference, we update again concept and properties to setup cross references
			getRDFSOntology().updateConceptsAndProperties();
			getRDFOntology().updateConceptsAndProperties();
			getOWLOntology().updateConceptsAndProperties();
			// Because we have updated again, we have to clear the modification stamp
			getRDFSOntology().clearIsModified();
			getRDFOntology().clearIsModified();
			getOWLOntology().clearIsModified();
			getFlexoConceptOntology().loadWhenUnloaded();
			defaultOntologiesLoaded = true;
		}
	}

	@Override
	public OWLTechnologyAdapter getTechnologyAdapter() {
		return (OWLTechnologyAdapter) super.getTechnologyAdapter();
	}

	public OWLDataType getDataType(String dataTypeURI) {
		OWLDataType returned = dataTypes.get(dataTypeURI);
		if (returned == null) {
			returned = new OWLDataType(dataTypeURI, getTechnologyAdapter());
			dataTypes.put(dataTypeURI, returned);
		}
		return returned;

	}

	public List<OWLDataType> getDataTypes() {
		ArrayList<OWLDataType> returned = new ArrayList<OWLDataType>();
		for (OWLDataType dt : dataTypes.values()) {
			returned.add(dt);
		}
		return returned;
	}

	public OntologyObjectConverter getOntologyObjectConverter() {
		return ontologyObjectConverter;
	}

	public void registerOntology(FlexoResource<OWLOntology> ontologyResource) {
		ontologies.put(ontologyResource.getURI(), ontologyResource);
	}

	/**
	 * Return ontology with supplied URI, look in all repositories of all known resource centers
	 * 
	 * @param ontologyUri
	 * @return
	 */
	public OWLOntology getOntology(String ontologyURI) {
		/*for (FlexoResourceCenter rc : resourceCenterService.getResourceCenters()) {
			MetaModelRepository<? extends OWLOntologyResource, OWLOntology, OWLOntology, OWLTechnologyAdapter> mmRep = rc
					.getMetaModelRepository(adapter);
			OWLOntologyResource mmResource = mmRep.getResource(ontologyURI);
			if (mmResource != null) {
				return mmResource.getResourceData();
			}
			ModelRepository<? extends OWLOntologyResource, OWLOntology, OWLOntology, OWLTechnologyAdapter> mRep = rc
					.getModelRepository(adapter);
			OWLOntologyResource mResource = mmRep.getResource(ontologyURI);
			if (mResource != null) {
				return mResource.getResourceData();
			}
		}
		logger.warning("Not found ontology: " + ontologyURI);
		return null;*/
		FlexoResource<OWLOntology> ontologyResource = ontologies.get(ontologyURI);
		if (ontologyResource != null) {
			try {
				return ontologyResource.getResourceData(null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceDependencyLoopException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.warning("Not found ontology: " + ontologyURI);
		return null;
	}

	public OWLOntology getFlexoConceptOntology() {
		return getOntology(FLEXO_CONCEPT_ONTOLOGY_URI);
	}

	public OWLOntology getRDFOntology() {
		return getOntology(RDFURIDefinitions.RDF_ONTOLOGY_URI);
	}

	public OWLOntology getRDFSOntology() {
		return getOntology(RDFSURIDefinitions.RDFS_ONTOLOGY_URI);
	}

	public OWLOntology getOWLOntology() {
		return getOntology(OWL2URIDefinitions.OWL_ONTOLOGY_URI);
	}

	@Override
	public GraphMaker getGraphMaker() {
		return graphMaker;
	}

	@Override
	public void close() {
		getGraphMaker().close();
	}

	public Model openModel() {
		return new ModelCom(getGraphMaker().openGraph());
	}

	@Override
	public OntModel openModelIfPresent(String name) {
		return getGraphMaker().hasGraph(name) ? openModel(name) : null;
	}

	@Override
	public OntModel openModel(String name, boolean strict) {
		getGraphMaker().openGraph(name, strict);
		OWLOntology ont = getOntology(name);
		if (ont != null) {
			ont.loadWhenUnloaded();
			return ont.getOntModel();
		}
		if (!strict) {
			/*OWLMetaModel newOntology = new OWLMetaModel(name, null, this);
			newOntology.setOntModel(createFreshModel());
			ontologies.put(name, newOntology);
			setChanged();
			notifyObservers(new OntologyImported(newOntology));
			return newOntology.getOntModel();*/
			logger.warning("Not implemented yet !!!");
			return null;
		} else {
			throw new DoesNotExistException(name);
		}
	}

	@Override
	public OntModel openModel(String name) {
		return openModel(name, false);
	}

	@Override
	public OntModel createModel(String name, boolean strict) {
		getGraphMaker().createGraph(name, strict);
		OWLOntology ont = getOntology(name);
		if (ont != null) {
			if (strict) {
				throw new AlreadyExistsException(name);
			}
			return createDefaultModel();
		}
		/*OWLMetaModel newOntology = new OWLMetaModel(name, null, this);
		newOntology.setOntModel(createFreshModel());
		ontologies.put(name, newOntology);
		setChanged();
		notifyObservers(new OntologyImported(newOntology));
		return newOntology.getOntModel();*/
		logger.warning("Not implemented yet !!!");
		return null;
	}

	@Override
	public OntModel createModel(String name) {
		return createModel(name, false);
	}

	public OntModel createModelOver(String name) {
		return createModel(name);
	}

	@Override
	public OntModel createFreshModel() {
		return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, this, null);
	}

	@Override
	public OntModel createDefaultModel() {
		return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, this, null);
	}

	@Override
	public void removeModel(String name) {
		getGraphMaker().removeGraph(name);
	}

	@Override
	public boolean hasModel(String name) {
		if (getOntology(name) != null) {
			return true;
		}
		return getGraphMaker().hasGraph(name);
	}

	@Override
	public ExtendedIterator listModels() {
		return getGraphMaker().listGraphs();
	}

	/**
	 * ModelGetter implementation component.
	 */
	@Override
	public Model getModel(String URL) {
		return hasModel(URL) ? openModel(URL) : null;
	}

	@Override
	public Model getModel(String URL, ModelReader loadIfAbsent) {
		Model already = getModel(URL);
		return already == null ? loadIfAbsent.readModel(createModel(URL), URL) : already;
	}

	/**
	 * Return true if URI is well formed and valid regarding its unicity (no one other object has same URI)
	 * 
	 * @param uri
	 * @return
	 */
	public boolean testValidURI(String ontologyURI, String conceptURI) {
		if (StringUtils.isEmpty(conceptURI)) {
			return false;
		}
		if (StringUtils.isEmpty(conceptURI.trim())) {
			return false;
		}
		return conceptURI.equals(ToolBox.getJavaName(conceptURI, true, false)) && !isDuplicatedURI(ontologyURI, conceptURI);
	}

	/**
	 * Return true if URI is duplicated
	 * 
	 * @param uri
	 * @return
	 */
	public boolean isDuplicatedURI(String ontologyURI, String conceptURI) {
		OWLOntology o = getOntology(ontologyURI);
		if (o != null) {
			return o.getOntologyObject(ontologyURI + "#" + conceptURI) != null;
		}
		return false;
	}

	@Override
	public void registerMetaModel(FlexoMetaModelResource<OWLOntology, OWLOntology> ontologyResource) {
		super.registerMetaModel(ontologyResource);
		registerOntology(ontologyResource);
	}

	@Override
	public void registerModel(FlexoModelResource<OWLOntology, OWLOntology> ontologyResource) {
		super.registerModel(ontologyResource);
		registerOntology(ontologyResource);
	}

}
