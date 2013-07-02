package org.openflexo.technologyadapter.xsd.controller;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.components.widget.OntologyBrowserModel;
import org.openflexo.components.widget.OntologyView;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
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
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.IFlexoOntologyTechnologyAdapterController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class XSDAdapterController extends TechnologyAdapterController<XSDTechnologyAdapter> implements
		IFlexoOntologyTechnologyAdapterController {

	static final Logger logger = Logger.getLogger(XSDAdapterController.class.getPackage().getName());

	@Override
	public Class<XSDTechnologyAdapter> getTechnologyAdapterClass() {
		return XSDTechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {

		actionInitializer.getController().getModuleInspectorController().loadDirectory(new FileResource("Inspectors/XSD"));
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
	public ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject> objectClass) {
		if (AbstractXSOntObject.class.isAssignableFrom(objectClass)) {
			return XSDIconLibrary.iconForObject((Class<? extends AbstractXSOntObject>) objectClass);
		}
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
			return getIconForTechnologyObject(XSOntClass.class);
		} else if (XSIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return getIconForTechnologyObject(XSOntIndividual.class);
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
			return IconFactory.getImageIcon(getIconForTechnologyObject(XSOntIndividual.class), IconLibrary.DUPLICATE);
		} else if (AddXSClass.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(getIconForTechnologyObject(XSOntClass.class), IconLibrary.DUPLICATE);
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

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return object instanceof XSDMetaModel || object instanceof XMLModel;
	}

	@Override
	public <T extends FlexoObject> ModuleView<T> createModuleViewForObject(T object, FlexoController controller,
			FlexoPerspective perspective) {
		if (object instanceof XMLModel) {
			OntologyView<XMLModel> returned = new OntologyView<XMLModel>((XMLModel) object, controller, perspective);
			returned.setShowClasses(false);
			returned.setShowDataProperties(false);
			returned.setShowObjectProperties(false);
			returned.setShowAnnotationProperties(false);
			return (ModuleView<T>) returned;
		} else if (object instanceof XSDMetaModel) {
			OntologyView<XSDMetaModel> returned = new OntologyView<XSDMetaModel>((XSDMetaModel) object, controller, perspective);
			returned.setShowClasses(true);
			returned.setShowDataProperties(true);
			returned.setShowObjectProperties(true);
			returned.setShowAnnotationProperties(true);
			return (ModuleView<T>) returned;
		}
		return new EmptyPanel<T>(controller, perspective, object);
	}

}
