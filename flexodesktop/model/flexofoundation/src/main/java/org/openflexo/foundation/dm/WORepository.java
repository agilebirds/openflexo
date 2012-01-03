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
package org.openflexo.foundation.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.foundation.dm.DMObject.DataModelObjectNameMustBeValid.CleanDataModelObjectName;
import org.openflexo.foundation.dm.DMObject.DataModelObjectNameMustBeValid.SetCustomName;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.param.DMEntityParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a logical group of objects used for WO scheme
 * 
 * @author sguerin
 * 
 */
public class WORepository extends DMRepository {

	private static final Logger logger = Logger.getLogger(WORepository.class.getPackage().getName());

	private static final String WO_APPSERVER_PACKAGE_NAME = "com.webobjects.appserver";

	private static final String WO_APPLICATION_CLASS_NAME = "WOApplication";
	private static final String WO_SESSION_CLASS_NAME = "WOSession";
	private static final String WO_DIRECT_ACTION_CLASS_NAME = "WODirectAction";
	private static final String WO_COMPONENT_CLASS_NAME = "WOComponent";

	private DMEntity _applicationEntity;
	private DMEntity _sessionEntity;
	private DMEntity _directActionEntity;
	private DMEntity _componentEntity;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public WORepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public WORepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getInternalRepositoryFolder();
	}

	@Override
	public int getOrder() {
		return 2;
	}

	@Override
	public String getName() {
		return "wo_repository";
	}

	@Override
	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	@Override
	public void setName(String name) {
		// Not allowed
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static WORepository createNewWORepository(DMModel dmModel) {
		WORepository newWORepository = new WORepository(dmModel);
		dmModel.setWORepository(newWORepository);
		/*createApplicationEntity(newWORepository);
		createSessionEntity(newWORepository);
		createComponentEntity(newWORepository);
		createDirectActionEntity(newWORepository);*/
		newWORepository.initCustomEntities();
		return newWORepository;
	}

	public void convertFromVersion_2_0() {
		initCustomEntities();
	}

	public void convertFromVersion_2_3_prelude() {
		DMEntity applicationEntityToRemove = getDMEntity(WO_APPSERVER_PACKAGE_NAME, WO_APPLICATION_CLASS_NAME);
		if (applicationEntityToRemove != null) {
			applicationEntityToRemove.delete();
		}
		DMEntity componentEntityToRemove = getDMEntity(WO_APPSERVER_PACKAGE_NAME, WO_COMPONENT_CLASS_NAME);
		if (componentEntityToRemove != null) {
			componentEntityToRemove.delete();
		}
		DMEntity directActionEntityToRemove = getDMEntity(WO_APPSERVER_PACKAGE_NAME, WO_DIRECT_ACTION_CLASS_NAME);
		if (directActionEntityToRemove != null) {
			directActionEntityToRemove.delete();
		}
		DMEntity sessionEntityToRemove = getDMEntity(WO_APPSERVER_PACKAGE_NAME, WO_SESSION_CLASS_NAME);
		if (sessionEntityToRemove != null) {
			sessionEntityToRemove.delete();
		}
	}

	public void convertFromVersion_2_3_postlude() {
		initCustomEntities();
		if (isDenaliCoreAvailable()) {
			if (getCustomApplicationEntity() != null) {
				getCustomApplicationEntity().setParentType(
						DMType.makeResolvedDMType(getDMModel().getEntityNamed("be.denali.core.woapp.WDLApplication")), true);
			}
			if (getCustomSessionEntity() != null) {
				getCustomSessionEntity().setParentType(
						DMType.makeResolvedDMType(getDMModel().getEntityNamed("be.denali.core.woapp.DLSession")), true);
			}
			if (getCustomDirectActionEntity() != null) {
				getCustomDirectActionEntity().setParentType(
						DMType.makeResolvedDMType(getDMModel().getEntityNamed("be.denali.core.woapp.WDLDirectAction")), true);
			}
			if (getCustomComponentEntity() != null) {
				getCustomComponentEntity().setParentType(
						DMType.makeResolvedDMType(getDMModel().getEntityNamed("be.denali.core.woapp.WDLComponent")), true);
			}
		} else if (isWebObjectsAvailable()) {
			if (getCustomApplicationEntity() != null) {
				getCustomApplicationEntity().setParentType(
						DMType.makeResolvedDMType(getDMModel().getEntityNamed("com.webobjects.appserver.WOApplication")), true);
			}
			if (getCustomSessionEntity() != null) {
				getCustomSessionEntity().setParentType(
						DMType.makeResolvedDMType(getDMModel().getEntityNamed("com.webobjects.appserver.WOSession")), true);
			}
			if (getCustomDirectActionEntity() != null) {
				getCustomDirectActionEntity().setParentType(
						DMType.makeResolvedDMType(getDMModel().getEntityNamed("com.webobjects.appserver.WODirectAction")), true);
			}
			if (getCustomComponentEntity() != null) {
				getCustomComponentEntity().setParentType(
						DMType.makeResolvedDMType(getDMModel().getEntityNamed("com.webobjects.appserver.WOComponent")), true);
			}
		}
	}

	private void initCustomEntities() {
		DMModel dmModel = getDMModel();

		try {
			if (getCustomApplicationEntity() == null) {
				DMEntity applicationEntity = new DMEntity(dmModel, "Application", getDefaultPackage().getName(), "Application", null);
				registerEntity(applicationEntity);
				if (isDenaliCoreAvailable()) {
					applicationEntity.setParentType(getWDLApplication(dmModel), true);
				} else if (isWebObjectsAvailable()) {
					applicationEntity.setParentType(getWOApplicationType(dmModel), true);
				}
				setCustomApplicationEntity(applicationEntity);
			}

			if (getCustomDirectActionEntity() == null) {
				DMEntity directActionEntity = new DMEntity(dmModel, "DirectAction", getDefaultPackage().getName(), "DirectAction", null);
				registerEntity(directActionEntity);
				if (isDenaliCoreAvailable()) {
					directActionEntity.setParentType(getWDLDirectAction(dmModel), true);
				} else if (isWebObjectsAvailable()) {
					directActionEntity.setParentType(getWODirectAction(dmModel), true);
				}
				setCustomDirectActionEntity(directActionEntity);
			}

			if (getCustomSessionEntity() == null) {
				DMEntity sessionEntity = new DMEntity(dmModel, "Session", getDefaultPackage().getName(), "Session", null);
				registerEntity(sessionEntity);
				if (isDenaliCoreAvailable()) {
					sessionEntity.setParentType(getDLSession(dmModel), true);
				} else if (isWebObjectsAvailable()) {
					sessionEntity.setParentType(getWOSession(dmModel), true);
				}
				setCustomSessionEntity(sessionEntity);
			}

			if (getCustomComponentEntity() == null) {
				String prefix = getProject().getPrefix() == null ? "Project" : getProject().getPrefix();
				createCustomComponentEntity(prefix + "Component");
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception while initializing custom base entities : " + e.getMessage());
			}
		}

	}

	@Override
	public void finalizeDeserialization(Object builder) {
		logger.fine("Finalizing WORepository deserialization");
		DMModel dmModel = getDMModel();
		DMEntity componentEntity = getCustomComponentEntity();
		if (componentEntity != null) {
			DMProperty session = componentEntity.getProperty("session");
			if (session != null && session.getGetterCode() != null && session.getGetterCode().indexOf("return null") > -1) {
				session.setIsSettable(false);
				try {
					session.getGetterSourceCode().setCode(
							"public Session getSession() {" + StringUtils.LINE_SEPARATOR + "\t return (Session)session();"
									+ StringUtils.LINE_SEPARATOR + "}");
				} catch (ParserNotInstalledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DuplicateMethodSignatureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Fixing session property returning null");
				}
			}
			DMProperty application = componentEntity.getProperty("application");
			if (application != null && application.getGetterCode() != null && application.getGetterCode().indexOf("return null") > -1) {
				application.setIsSettable(false);
				try {
					application.getGetterSourceCode().setCode(
							"public Application getApplication() {" + StringUtils.LINE_SEPARATOR + "\t return (Application)application();"
									+ StringUtils.LINE_SEPARATOR + "}");
				} catch (ParserNotInstalledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DuplicateMethodSignatureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Fixing application property returning null");
				}
			}
		}
		try {
			if (getCustomApplicationEntity() != null) {
				DMEntity applicationEntity = getCustomApplicationEntity();
				if (isDenaliCoreAvailable() && getWOApplicationType(dmModel).equals(applicationEntity.getParentType())) {
					applicationEntity.setParentType(getWDLApplication(dmModel), false);
				}
			}

			if (getCustomDirectActionEntity() != null) {
				DMEntity directActionEntity = getCustomDirectActionEntity();
				if (isDenaliCoreAvailable() && getWODirectAction(dmModel).equals(directActionEntity.getParentType())) {
					directActionEntity.setParentType(getWDLDirectAction(dmModel), false);
				}
			}

			if (getCustomSessionEntity() != null) {
				DMEntity sessionEntity = getCustomSessionEntity();
				if (isDenaliCoreAvailable() && getWOSession(dmModel).equals(sessionEntity.getParentType())) {
					sessionEntity.setParentType(getDLSession(dmModel), false);
				}
			}

			if (componentEntity != null) {
				if (isDenaliCoreAvailable() && getWOComponent(dmModel).equals(componentEntity.getParentType())) {
					componentEntity.setParentType(getWDLComponent(dmModel), false);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception while initializing custom base entities : " + e.getMessage());
			}
		}
		super.finalizeDeserialization(builder);
	}

	public DMType getWDLComponent(DMModel dmModel) {
		return DMType.makeResolvedDMType(dmModel.getEntityNamed("be.denali.core.woapp.WDLComponent"));
	}

	public DMType getWOComponent(DMModel dmModel) {
		return DMType.makeResolvedDMType(dmModel.getEntityNamed("com.webobjects.appserver.WOComponent"));
	}

	public DMType getDLSession(DMModel dmModel) {
		return DMType.makeResolvedDMType(dmModel.getEntityNamed("be.denali.core.woapp.DLSession"));
	}

	public DMType getWOSession(DMModel dmModel) {
		return DMType.makeResolvedDMType(dmModel.getEntityNamed("com.webobjects.appserver.WOSession"));
	}

	public DMType getWDLDirectAction(DMModel dmModel) {
		return DMType.makeResolvedDMType(dmModel.getEntityNamed("be.denali.core.woapp.WDLDirectAction"));
	}

	public DMType getWODirectAction(DMModel dmModel) {
		return DMType.makeResolvedDMType(dmModel.getEntityNamed("com.webobjects.appserver.WODirectAction"));
	}

	public DMType getWDLApplication(DMModel dmModel) {
		return DMType.makeResolvedDMType(dmModel.getEntityNamed("be.denali.core.woapp.WDLApplication"));
	}

	public DMType getWOApplicationType(DMModel dmModel) {
		return DMType.makeResolvedDMType(dmModel.getEntityNamed("com.webobjects.appserver.WOApplication"));
	}

	DMEntity createCustomComponentEntity(String name) {
		DMModel dmModel = getDMModel();
		DMEntity componentEntity = new DMEntity(dmModel, name, getDefaultPackage().getName(), name, null);
		registerEntity(componentEntity);
		if (isDenaliCoreAvailable()) {
			componentEntity.setParentType(getWDLComponent(dmModel), true);
		} else if (isWebObjectsAvailable()) {
			componentEntity.setParentType(getWOComponent(dmModel), true);
		}
		DMProperty sessionProperty = new DMProperty(dmModel, "session", DMType.makeResolvedDMType(getCustomSessionEntity()),
				DMCardinality.SINGLE, true, false, DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY);
		componentEntity.registerProperty(sessionProperty, false);
		try {
			sessionProperty.setGetterCode("public Session getSession() {" + StringUtils.LINE_SEPARATOR + "\t return (Session)session();"
					+ StringUtils.LINE_SEPARATOR + "}");
		} catch (ParserNotInstalledException e) {
			e.printStackTrace();
		} catch (DuplicateMethodSignatureException e) {
			e.printStackTrace();
		}
		DMProperty applicationProperty = new DMProperty(dmModel, "application", DMType.makeResolvedDMType(getCustomApplicationEntity()),
				DMCardinality.SINGLE, true, false, DMPropertyImplementationType.PUBLIC_ACCESSORS_ONLY);
		componentEntity.registerProperty(applicationProperty, false);
		setCustomComponentEntity(componentEntity);
		try {
			applicationProperty.setGetterCode("public Application getApplication() {" + StringUtils.LINE_SEPARATOR
					+ "\t return (Application)application();" + StringUtils.LINE_SEPARATOR + "}");
		} catch (ParserNotInstalledException e) {
			e.printStackTrace();
		} catch (DuplicateMethodSignatureException e) {
			e.printStackTrace();
		}
		return componentEntity;
	}

	public boolean isDenaliCoreAvailable() {
		return getDMModel().getEntityNamed("be.denali.core.woapp.WDLComponent") != null;
	}

	public boolean isWebObjectsAvailable() {
		return getDMModel().getEntityNamed("com.webobjects.appserver.WOApplication") != null;
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".WO";
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public boolean isDeletable() {
		return false;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return getName();
	}

	public DMEntity getCustomApplicationEntity() {
		return _applicationEntity;
	}

	public void setCustomApplicationEntity(DMEntity entity) {
		DMEntity oldValue = _applicationEntity;
		_applicationEntity = entity;
		setChanged();
		notifyObservers(new DMAttributeDataModification(CUSTOM_APPLICATION_ENTITY_KEY, oldValue, entity));
	}

	public DMEntity getCustomDirectActionEntity() {
		return _directActionEntity;
	}

	public void setCustomDirectActionEntity(DMEntity actionEntity) {
		DMEntity oldValue = _directActionEntity;
		_directActionEntity = actionEntity;
		setChanged();
		notifyObservers(new DMAttributeDataModification(CUSTOM_DIRECT_ACTION_ENTITY_KEY, oldValue, actionEntity));
	}

	public DMEntity getCustomSessionEntity() {
		return _sessionEntity;
	}

	public void setCustomSessionEntity(DMEntity entity) {
		DMEntity oldValue = _sessionEntity;
		_sessionEntity = entity;
		setChanged();
		notifyObservers(new DMAttributeDataModification(CUSTOM_SESSION_ENTITY_KEY, oldValue, entity));
	}

	public DMEntity getCustomComponentEntity() {
		return _componentEntity;
	}

	public void setCustomComponentEntity(DMEntity entity) {
		DMEntity oldValue = _componentEntity;
		_componentEntity = entity;
		setChanged();
		notifyObservers(new DMAttributeDataModification(CUSTOM_COMPONENT_ENTITY_KEY, oldValue, entity));
	}

	@Override
	public String getInspectorName() {
		return Inspectors.DM.DM_WOREPOSITORY_INSPECTOR;
	}

	public static final String CUSTOM_COMPONENT_ENTITY_KEY = "customComponentEntity";
	public static final String CUSTOM_SESSION_ENTITY_KEY = "customSessionEntity";
	public static final String CUSTOM_DIRECT_ACTION_ENTITY_KEY = "customDirectActionEntity";
	public static final String CUSTOM_APPLICATION_ENTITY_KEY = "customApplicationEntity";

	DMEntity getDefaultCustomComponentCandidate() {
		for (DMEntity e : getEntities().values()) {
			if (e == getCustomComponentEntity()) {
				return e;
			}
			if (e != getCustomApplicationEntity() && e != getCustomSessionEntity() && e != getCustomDirectActionEntity()) {
				return e;
			}
		}
		if (getEntities().size() > 0) {
			return getEntities().values().iterator().next();
		}
		return null;
	}

	public static class ADefaultComponentEntityMustBeDeclared extends ValidationRule<ADefaultComponentEntityMustBeDeclared, WORepository> {

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public ADefaultComponentEntityMustBeDeclared() {
			super(WORepository.class, "a_default_component_entity_must_be_declared");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue<ADefaultComponentEntityMustBeDeclared, WORepository> applyValidation(WORepository o) {
			if (o.getCustomComponentEntity() == null) {
				ValidationError<ADefaultComponentEntityMustBeDeclared, WORepository> err = new ValidationError<WORepository.ADefaultComponentEntityMustBeDeclared, WORepository>(
						this, o, "no_component_entity_declared");
				err.addToFixProposals(new ChooseEntityProposal());
				err.addToFixProposals(new CreateEntityProposal());
				return err;
			}

			if (!o.isNameValid()) {
				ValidationError err = new ValidationError(this, o,
						"data_model_objects_can_contain_only_alphanumeric_characters_or_underscore_and_must_start_with_a_letter");
				if (o.getName() != null) {
					err.addToFixProposals(new CleanDataModelObjectName(ToolBox.cleanStringForJava(o.getName())));
				}
				err.addToFixProposals(new SetCustomName());
				return err;
			}
			return null;
		}

		public static class ChooseEntityProposal extends ParameteredFixProposal<ADefaultComponentEntityMustBeDeclared, WORepository> {

			private static final ParameterDefinition[] parameters = new ParameterDefinition[] { new DMEntityParameter("componentEntity",
					"component_entity", null) };

			/**
			 * @param aMessage
			 */
			public ChooseEntityProposal() {
				super("choose_component_entity", parameters);
			}

			/**
			 * Overrides fixAction
			 * 
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				DMEntity e = (DMEntity) getValueForParameter("componentEntity");
				if (e != null) {
					SetPropertyAction action = SetPropertyAction.actionType.makeNewAction(getObject(), null);
					action.setKey(CUSTOM_COMPONENT_ENTITY_KEY);
					action.setValue(e);
					action.doAction();
				}
			}

			@Override
			public void updateBeforeApply() {
				WORepository woRep = getObject();
				parameters[0].setValue(woRep.getDefaultCustomComponentCandidate());
			}

		}

		public static class CreateEntityProposal extends ParameteredFixProposal<ADefaultComponentEntityMustBeDeclared, WORepository> {

			private static final ParameterDefinition[] parameters = new ParameterDefinition[] { new TextFieldParameter("name", "name", null) };

			/**
			 * @param aMessage
			 */
			public CreateEntityProposal() {
				super("create_component_entity", parameters);
			}

			/**
			 * Overrides fixAction
			 * 
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				String name = (String) getValueForParameter("name");
				WORepository woRep = getObject();
				DMEntity e = woRep.createCustomComponentEntity(name);
				if (e != null) {
					SetPropertyAction action = SetPropertyAction.actionType.makeNewAction(getObject(), null);
					action.setKey(CUSTOM_COMPONENT_ENTITY_KEY);
					action.setValue(e);
					action.doAction();
				}
			}

			@Override
			public void updateBeforeApply() {
				WORepository woRep = getObject();
				String prefix = getProject().getPrefix() == null ? "Project" : getProject().getPrefix();
				parameters[0]
						.setValue(getProject().getDataModel().getNextDefautEntityName(woRep.getDefaultPackage(), prefix + "Component"));
			}

		}

	}

}
