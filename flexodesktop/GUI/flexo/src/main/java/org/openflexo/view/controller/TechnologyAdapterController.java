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
package org.openflexo.view.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.foundation.viewpoint.AddEditionPatternInstance;
import org.openflexo.foundation.viewpoint.AddToListAction;
import org.openflexo.foundation.viewpoint.ConditionalAction;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.FetchRequestIterationAction;
import org.openflexo.foundation.viewpoint.IterationAction;
import org.openflexo.foundation.viewpoint.MatchEditionPatternInstance;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.RemoveFromListAction;
import org.openflexo.foundation.viewpoint.SelectEditionPatternInstance;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * This class represents a technology-specific controller provided by a {@link TechnologyAdapter}<br>
 * A {@link TechnologyAdapterController} works above conceptual layer provided by a {@link TechnologyAdapter}, and manages all tooling
 * dedicated to technology-specific management of a {@link TechnologyAdapter}<br>
 * This controller makes the bindings between Openflexo controllers/editors layer and the {@link TechnologyAdapter}
 * 
 * @author sylvain
 * 
 */
public abstract class TechnologyAdapterController<TA extends TechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(TechnologyAdapterController.class.getPackage().getName());

	private TechnologyAdapterControllerService technologyAdapterControllerService;

	private final Map<Class<?>, File> fibPanelsForClasses = new HashMap<Class<?>, File>();

	/**
	 * Returns applicable {@link TechnologyAdapterService}
	 * 
	 * @return
	 */
	public TechnologyAdapterControllerService getTechnologyAdapterControllerService() {
		return technologyAdapterControllerService;
	}

	/**
	 * Sets applicable {@link TechnologyAdapterService}
	 * 
	 * @param technologyAdapterService
	 */
	public void setTechnologyAdapterService(TechnologyAdapterControllerService technologyAdapterControllerService) {
		this.technologyAdapterControllerService = technologyAdapterControllerService;
	}

	/**
	 * Return TechnologyAdapter
	 * 
	 * @return
	 */
	public final TA getTechnologyAdapter() {
		return technologyAdapterControllerService.getServiceManager().getService(TechnologyAdapterService.class)
				.getTechnologyAdapter(getTechnologyAdapterClass());
	}

	/**
	 * Return TechnologyAdapter class
	 * 
	 * @return
	 */
	public abstract Class<TA> getTechnologyAdapterClass();

	public abstract void initializeActions(ControllerActionInitializer actionInitializer);

	public void initialize() {

	}

	/**
	 * Return icon representing underlying technology, required size is 32x32
	 * 
	 * @return
	 */
	public abstract ImageIcon getTechnologyBigIcon();

	/**
	 * Return icon representing underlying technology, required size is 16x16
	 * 
	 * @return
	 */
	public abstract ImageIcon getTechnologyIcon();

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	public abstract ImageIcon getModelIcon();

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	public abstract ImageIcon getMetaModelIcon();

	/**
	 * Return icon representing supplied ontology object
	 * 
	 * @param object
	 * @return
	 */
	public abstract ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject> objectClass);

	/**
	 * Return icon representing supplied pattern role
	 * 
	 * @param object
	 * @return
	 */
	public abstract ImageIcon getIconForPatternRole(Class<? extends PatternRole<?>> patternRoleClass);

	/**
	 * Return icon representing supplied edition action
	 * 
	 * @param object
	 * @return
	 */
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction<?, ?>> editionActionClass) {
		if (org.openflexo.foundation.viewpoint.DeclarePatternRole.class.isAssignableFrom(editionActionClass)) {
			return VPMIconLibrary.DECLARE_PATTERN_ROLE_ICON;
		} else if (org.openflexo.foundation.viewpoint.AssignationAction.class.isAssignableFrom(editionActionClass)) {
			return VPMIconLibrary.DECLARE_PATTERN_ROLE_ICON;
		} else if (org.openflexo.foundation.viewpoint.ExecutionAction.class.isAssignableFrom(editionActionClass)) {
			return VPMIconLibrary.ACTION_SCHEME_ICON;
		} else if (AddEditionPatternInstance.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VEIconLibrary.EDITION_PATTERN_INSTANCE_ICON, IconLibrary.DUPLICATE);
		} else if (SelectEditionPatternInstance.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VEIconLibrary.EDITION_PATTERN_INSTANCE_ICON, IconLibrary.IMPORT);
		} else if (MatchEditionPatternInstance.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VPMIconLibrary.EDITION_PATTERN_ICON, IconLibrary.SYNC);
		} else if (AddToListAction.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VPMIconLibrary.LIST_ICON, IconLibrary.POSITIVE_MARKER);
		} else if (RemoveFromListAction.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(VPMIconLibrary.LIST_ICON, IconLibrary.NEGATIVE_MARKER);
		} else if (DeleteAction.class.isAssignableFrom(editionActionClass)) {
			return VPMIconLibrary.DELETE_ICON;
		} else if (ConditionalAction.class.isAssignableFrom(editionActionClass)) {
			return VPMIconLibrary.CONDITIONAL_ACTION_ICON;
		} else if (IterationAction.class.isAssignableFrom(editionActionClass)) {
			return VPMIconLibrary.ITERATION_ACTION_ICON;
		} else if (FetchRequestIterationAction.class.isAssignableFrom(editionActionClass)) {
			return VPMIconLibrary.ITERATION_ACTION_ICON;
		}
		return null;

	}

	/**
	 * Return icon representing supplied edition scheme
	 * 
	 * @param object
	 * @return
	 */
	public ImageIcon getIconForEditionScheme(Class<? extends EditionScheme> editionSchemeClass) {
		return null;
	}

	public abstract boolean hasModuleViewForObject(TechnologyObject object);

	public abstract String getWindowTitleforObject(TechnologyObject object);

	public abstract <T extends FlexoObject> ModuleView<T> createModuleViewForObject(T object, FlexoController controller,
			FlexoPerspective perspective);

	public File getFIBPanelForObject(Object anObject) {
		if (anObject != null) {
			return getFIBPanelForClass(anObject.getClass());
		}
		return null;
	}

	public File getFIBPanelForClass(Class<?> aClass) {
		if (aClass == null) {
			return null;
		}
		File returned = fibPanelsForClasses.get(aClass);
		if (returned == null) {
			if (aClass.getAnnotation(FIBPanel.class) != null) {
				File fibPanel = new FileResource(aClass.getAnnotation(FIBPanel.class).value());
				if (fibPanel.exists()) {
					logger.info("Found " + fibPanel);
					fibPanelsForClasses.put(aClass, fibPanel);
					return fibPanel;
				} else {
					logger.warning("Not found " + fibPanel);
					return null;
				}
			}
			if (aClass.getSuperclass() != null) {
				return getFIBPanelForClass(aClass.getSuperclass());
			}
		}
		return returned;
	}

}
