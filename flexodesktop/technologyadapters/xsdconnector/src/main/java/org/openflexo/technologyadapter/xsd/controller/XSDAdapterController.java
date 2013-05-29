package org.openflexo.technologyadapter.xsd.controller;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.ontology.IFlexoOntologyPropertyValue;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.gui.XMLModelBrowserModel;
import org.openflexo.technologyadapter.xsd.gui.XSDIconLibrary;
import org.openflexo.technologyadapter.xsd.gui.XSDMetaModelBrowserModel;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSOntClass;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.viewpoint.XSClassPatternRole;
import org.openflexo.technologyadapter.xsd.viewpoint.XSIndividualPatternRole;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.AddXSClass;
import org.openflexo.technologyadapter.xsd.viewpoint.editionaction.AddXSIndividual;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.TechnologyAdapterController;

public class XSDAdapterController extends TechnologyAdapterController<XSDTechnologyAdapter> {

	static final Logger logger = Logger.getLogger(XSDAdapterController.class.getPackage().getName());

	@Override
	public Class<XSDTechnologyAdapter> getTechnologyAdapterClass() {
		return XSDTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {

		actionInitializer.getController().getModuleInspectorController()
				.loadDirectory(new FileResource("src/main/resources/Inspectors/XSD"));
	}

	/**
	 * Return icon representing underlying technology, required size is 32x32
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyBigIcon() {
		return XSDIconLibrary.XSD_TECHNOLOGY_BIG_ICON;
	}

	/**
	 * Return icon representing underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getTechnologyIcon() {
		return XSDIconLibrary.XSD_TECHNOLOGY_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getModelIcon() {
		return XSDIconLibrary.XML_FILE_ICON;
	}

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	@Override
	public ImageIcon getMetaModelIcon() {
		return XSDIconLibrary.XSD_FILE_ICON;
	}

	/**
	 * Return icon representing supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForOntologyObject(Class<? extends IFlexoOntologyObject> objectClass) {
		if (AbstractXSOntObject.class.isAssignableFrom(objectClass))
			return XSDIconLibrary.iconForObject((Class<? extends AbstractXSOntObject>) objectClass);
		return null;
	}

	/**
	 * Return icon representing supplied property value
	 * 
	 * @param object
	 * @return
	 */
	@Override
	public ImageIcon getIconForPropertyValue(Class<? extends IFlexoOntologyPropertyValue> objectClass) {
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
		if (XSClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(XSOntClass.class);
		} else if (XSIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForOntologyObject(XSOntIndividual.class);
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
		if (AddXSIndividual.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(XSOntIndividual.class), IconLibrary.DUPLICATE);
		} else if (AddXSClass.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForOntologyObject(XSOntClass.class), IconLibrary.DUPLICATE);
		}
		return super.getIconForEditionAction(editionActionClass);
	}

	@Override
	public OntologyBrowserModel makeOntologyBrowserModel(IFlexoOntology context) {
		if (context instanceof XSDMetaModel) {
			return new XSDMetaModelBrowserModel((XSDMetaModel) context);
		} else if (context instanceof XMLModel) {
			return new XMLModelBrowserModel((XMLModel) context);
		} else {
			logger.warning("Unexpected " + context);
			return null;
		}
	}

}
