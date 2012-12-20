package org.openflexo.technologyadapter.owl.controller;

import java.io.File;

import javax.swing.ImageIcon;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.gui.OWLIconLibrary;
import org.openflexo.technologyadapter.owl.model.DataPropertyStatement;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLDataProperty;
import org.openflexo.technologyadapter.owl.model.OWLIndividual;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.OWLObjectProperty;
import org.openflexo.technologyadapter.owl.model.OWLProperty;
import org.openflexo.technologyadapter.owl.model.ObjectPropertyStatement;
import org.openflexo.technologyadapter.owl.model.PropertyStatement;
import org.openflexo.technologyadapter.owl.model.SubClassStatement;
import org.openflexo.technologyadapter.owl.viewpoint.DataPropertyStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLClassPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLDataPropertyPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLIndividualPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLObjectPropertyPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLPropertyPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.ObjectPropertyStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.RestrictionStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.SubClassStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.editionaction.AddOWLClass;
import org.openflexo.technologyadapter.owl.viewpoint.editionaction.AddOWLIndividual;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class OWLAdapterController extends TechnologyAdapterController<OWLTechnologyAdapter> {

	// Ontology edition
	public static File CREATE_ONTOLOGY_CLASS_DIALOG_FIB = new FileResource("src/main/resources/Fib/Dialog/CreateOntologyClassDialog.fib");
	public static File CREATE_ONTOLOGY_INDIVIDUAL_FIB = new FileResource("src/main/resources/Fib/Dialog/CreateOntologyIndividualDialog.fib");
	public static File DELETE_ONTOLOGY_OBJECTS_DIALOG_FIB = new FileResource(
			"src/main/resources/Fib/Dialog/DeleteOntologyObjectsDialog.fib");
	public static File CREATE_DATA_PROPERTY_DIALOG_FIB = new FileResource("src/main/resources/Fib/Dialog/CreateDataPropertyDialog.fib");
	public static File CREATE_OBJECT_PROPERTY_DIALOG_FIB = new FileResource("src/main/resources/Fib/Dialog/CreateObjectPropertyDialog.fib");

	@Override
	public Class<OWLTechnologyAdapter> getTechnologyAdapterClass() {
		return OWLTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {

		actionInitializer.getController().getModuleInspectorController()
				.loadDirectory(new FileResource("src/main/resources/Inspectors/OWL"));

		new CreateOntologyClassInitializer(actionInitializer);
		new CreateOntologyIndividualInitializer(actionInitializer);
		new CreateObjectPropertyInitializer(actionInitializer);
		new CreateDataPropertyInitializer(actionInitializer);
		new DeleteOntologyObjectsInitializer(actionInitializer);
		new AddAnnotationStatementInitializer(actionInitializer);
	}

	/**
	 * Return icon representing underlying technology, required size is 32x32
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyBigIcon() {
		return OWLIconLibrary.ONTOLOGY_LIBRARY_BIG_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return OWLIconLibrary.ONTOLOGY_LIBRARY_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return OWLIconLibrary.ONTOLOGY_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return OWLIconLibrary.ONTOLOGY_ICON;
	}

	/**
	 * Return icon representing supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass) {
		if (OWLObject.class.isAssignableFrom(objectClass))
			return OWLIconLibrary.iconForObject((Class<? extends OWLObject>) objectClass);
		return null;
	}

	/**
	 * Return icon representing supplied pattern role
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPatternRole(Class<? extends PatternRole> patternRoleClass) {
		if (OWLClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(OWLClass.class);
		} else if (OWLIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(OWLIndividual.class);
		} else if (OWLDataPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(OWLDataProperty.class);
		} else if (OWLObjectPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(OWLObjectProperty.class);
		} else if (OWLPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(OWLProperty.class);
		} else if (DataPropertyStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(DataPropertyStatement.class);
		} else if (ObjectPropertyStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(ObjectPropertyStatement.class);
		} else if (RestrictionStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(PropertyStatement.class);
		} else if (SubClassStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(SubClassStatement.class);
		}
		return null;
	}

	/**
	 * Return icon representing supplied edition action
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {
		if (AddOWLIndividual.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(OWLIndividual.class), IconLibrary.DUPLICATE);
		} else if (AddOWLClass.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(OWLClass.class), IconLibrary.DUPLICATE);
		}
		return null;
	}
}
