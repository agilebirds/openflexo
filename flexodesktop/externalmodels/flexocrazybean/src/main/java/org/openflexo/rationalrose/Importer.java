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
package org.openflexo.rationalrose;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openflexo.dataimporter.DataImporter;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.AddFlexoProperty;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.dm.DMCardinality;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMPropertyImplementationType;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DMVisibilityType;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.RationalRoseRepository;
import org.openflexo.foundation.dm.DMMethod.DMMethodParameter;
import org.openflexo.foundation.dm.action.CreateDMMethod;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;

import cb.parser.EOModelExtractor;
import cb.parser.PetalParser;
import cb.petal.Attribute;
import cb.petal.Class;
import cb.petal.ClassAttribute;
import cb.petal.List;
import cb.petal.Literal;
import cb.petal.Operation;
import cb.petal.Parameter;
import cb.petal.PetalFile;
import cb.petal.PetalNode;
import cb.petal.PetalObject;
import cb.petal.Role;
import cb.petal.Value;

public class Importer implements DataImporter {

	private static final String ATTRIBUTES_PROPERTY_NAME = "attributes";
	private static final String OPERATIONS_PROPERTY_NAME = "operations";
	private static final String PARAMETERS_PROPERTY_NAME = "parameters";
	private static final String DOCUMENTATION_PROPERTY_NAME = "documentation";
	private static final String CLASS_ATTRIBUTES_PROPERTY_NAME = "class_attributes";
	private static final String SUPER_CLASSES_PROPERTY_NAME = "superclasses";
	private static final String ROLES_PROPERTY_NAME = "roles";
	private static final String QUID_PROPERTY_NAME = "quid";
	private static final String QUIDU_PROPERTY_NAME = "quidu";

	private static final String[] PROPERTIES_TO_IGNORE = new String[] { QUID_PROPERTY_NAME, QUIDU_PROPERTY_NAME, ATTRIBUTES_PROPERTY_NAME,
			OPERATIONS_PROPERTY_NAME, PARAMETERS_PROPERTY_NAME, ROLES_PROPERTY_NAME, DOCUMENTATION_PROPERTY_NAME,
			CLASS_ATTRIBUTES_PROPERTY_NAME, SUPER_CLASSES_PROPERTY_NAME };
	private static final String ORDER_ATTRIBUTE = "Import order";
	private static final String RATIONAL_TYPE_ATTRIBUTE = "Rational type";

	private FlexoProject project;
	private FlexoAction<?, ? extends FlexoModelObject, ? extends FlexoModelObject> flexoAction;
	private Hashtable<String, DMEntity> entities;
	private EOModelExtractor extractor;
	private PetalFile tree;

	@Override
	public Object importInProject(FlexoProject project, File importedFile, Object[] parameters) {
		String repositoryName = (parameters.length >= 1 ? (String) parameters[0] : null);
		String packageName = (parameters.length >= 2 ? (String) parameters[1] : DMPackage.DEFAULT_PACKAGE_NAME);
		flexoAction = (parameters.length >= 3 ? (FlexoAction<?, ? extends FlexoModelObject, ? extends FlexoModelObject>) parameters[2]
				: null);
		this.project = project;

		String[] args = new String[] { importedFile.getAbsolutePath() };
		if (flexoAction != null)
			flexoAction.setProgress(FlexoLocalization.localizedForKey("parsing") + " " + importedFile.getName());
		tree = PetalParser.parse(args);
		extractor = new EOModelExtractor(System.out);
		tree.accept(extractor);
		extractor.arrangeRelationship();
		// data is now stored in extractor
		// proceed to importation in flexo.
		RationalRoseRepository repository = importInFlexo(repositoryName, packageName);
		// Let's nullify the fields to be avoid memory leaks
		project = null;
		entities = null;
		extractor = null;
		flexoAction = null;
		return repository;
	}

	public Importer() {
		super();
	}

	private RationalRoseRepository importInFlexo(String repositoryName, String packageName) {
		RationalRoseRepository repo = RationalRoseRepository.createNewRationalRoseRepository(repositoryName, project.getDataModel());
		repo.setIsReadOnly(false);
		try {
			DMPackage pack = repo.packageWithName(packageName);
			extractClasses(pack);
			extractData();
		} finally {
			repo.setIsReadOnly(true);
		}
		return repo;
	}

	private Hashtable<String, DMEntity> extractClasses(DMPackage pack) {
		if (flexoAction != null) {
			flexoAction.setProgress(FlexoLocalization.localizedForKey("importing_data_structure"));
			flexoAction.resetSecondaryProgress(extractor.classes.size());
		}
		entities = new Hashtable<String, DMEntity>();
		int numberOfDigits = 0;
		numberOfDigits = String.valueOf(extractor.classes.size() + 1).length();
		String entityFormat = "%0" + numberOfDigits + "d";

		Enumeration<cb.petal.Class> en = extractor.orderedClasses.elements();
		int entityOrder = 1;
		while (en.hasMoreElements()) {
			cb.petal.Class currentClass = en.nextElement();
			DMEntity entity = createClass(pack, String.format(entityFormat, entityOrder), currentClass);
			entities.put(entity.getName(), entity);
			entityOrder++;
		}
		return entities;
	}

	/**
	 * @param pack
	 * @param flexoAction
	 * @param entityFormat
	 * @param entityOrder
	 * @param currentClass
	 * @param className
	 * @return
	 */
	private DMEntity createClass(DMPackage pack, String order, cb.petal.Class currentClass) {
		String className = EOModelExtractor.getLabel(currentClass);
		if (flexoAction != null) {
			flexoAction.setSecondaryProgress(FlexoLocalization.localizedForKey("importing") + " " + className);
		}
		DMEntity entity = createDMEntity(className, pack);
		addProperty(RATIONAL_TYPE_ATTRIBUTE, "Class", entity);
		extractProperties(currentClass, entity);
		if (currentClass.getParameters() != null && currentClass.getParameters().size() > 0)
			addProperty("Parameters", currentClass.getParameters().toString(), entity);
		addProperty(ORDER_ATTRIBUTE, order, entity);
		java.util.List<cb.petal.Class> superClasses = currentClass.getSuperclasses();
		if (superClasses != null && superClasses.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (Class klass : superClasses) {
				if (sb.length() > 0)
					sb.append(",");
				sb.append(EOModelExtractor.getLabel(klass));
			}
			addProperty("Super classes", sb.toString(), entity);
		}
		if (currentClass.getDocumentation() != null && currentClass.getDocumentation().trim().length() > 0)
			entity.setDescription(currentClass.getDocumentation());
		return entity;
	}

	private void extractData() {
		if (flexoAction != null) {
			flexoAction.setProgress(FlexoLocalization.localizedForKey("importing_data_structure"));
			flexoAction.resetSecondaryProgress(extractor.classes.size());
		}
		Enumeration en = extractor.classes.elements();
		while (en.hasMoreElements()) {
			try {
				// The class
				cb.petal.Class currentClass = (cb.petal.Class) en.nextElement();
				String className = EOModelExtractor.getLabel(currentClass);
				if (flexoAction != null) {
					flexoAction.setSecondaryProgress(FlexoLocalization
							.localizedForKey("extracting_attributes_operations_and_relationships_for") + " " + className);
				}
				DMEntity currentEntity = entities.get(className);
				String superClassName = EOModelExtractor.getSuperClassLabel(currentClass);
				if (superClassName != null && entities.get(superClassName) != null) {
					currentEntity.setParentBaseEntity(entities.get(superClassName));
				} else {
					currentEntity.setParentBaseEntity(currentEntity.getDMModel().getDMEntity(Object.class));
				}

				// Class attributes
				List classAttributes = currentClass.getClassAttributeList();
				if (classAttributes != null && classAttributes.getChildCount() > 0) {
					int numberOfDigits = String.valueOf(classAttributes.getChildCount() + 1).length();
					String propertyFormat = "%0" + numberOfDigits + "d";
					for (int j = 0; j < classAttributes.getChildCount(); j++) {
						ClassAttribute attrib = (ClassAttribute) classAttributes.get(j);
						createAttribute(currentEntity, String.format(propertyFormat, j + 1), attrib);
					}
				}
				// Class operations
				List classOperations = currentClass.getOperationList();
				if (classOperations != null && classOperations.getChildCount() > 0) {
					int numberOfDigits = String.valueOf(classOperations.getChildCount() + 1).length();
					String propertyFormat = "%0" + numberOfDigits + "d";
					for (int j = 0; j < classOperations.getChildCount(); j++) {
						Operation operation = (Operation) classOperations.get(j);
						createMethod(currentEntity, String.format(propertyFormat, j + 1), operation);
					}
				}
				// Class relations
				if (currentClass.getRelationsAsRole() != null) {
					Enumeration en2 = currentClass.getRelationsAsRole().elements();
					int numberOfDigits = String.valueOf(currentClass.getRelationsAsRole().size() + 1).length();
					String propertyFormat = "%0" + numberOfDigits + "d";
					int order = 1;
					while (en2.hasMoreElements()) {
						Role rel = (Role) en2.nextElement();
						if (EOModelExtractor.getLabel(rel).indexOf("$") == -1) {
							Class klass = null;
							if (rel.hasQuidu())
								klass = tree.getClassByQuid(rel.getQuidu());
							DMEntity to = entities.get(EOModelExtractor.getSupplierShortName(rel));
							DMProperty prop = createRelationship(rel, currentEntity, to);
							addProperty(RATIONAL_TYPE_ATTRIBUTE, "Role", prop);
							prop.setDescription(rel.commentaires);
							addProperty(ORDER_ATTRIBUTE, String.format(propertyFormat, order), prop);
							order++;
						}
					}
				}
			} finally {

			}
		}
	}

	/**
	 * @param flexoAction
	 * @param entity
	 * @param propertyFormat
	 * @param operationOrder
	 * @param operation
	 * @param operationName
	 */
	private void createMethod(DMEntity entity, String order, Operation operation) {
		DMMethod m = createDMMethod(entity, EOModelExtractor.getLabel(operation), flexoAction != null ? flexoAction.getEditor() : null);
		m.setDescription(operation.getDocumentation());
		if (operation.isPrivate()) {
			m.setVisibilityModifier(DMVisibilityType.PRIVATE);
		} else if (operation.isProtected()) {
			m.setVisibilityModifier(DMVisibilityType.PROTECTED);
		} else if (operation.isPublic()) {
			m.setVisibilityModifier(DMVisibilityType.PUBLIC);
		} else
			m.setVisibilityModifier(DMVisibilityType.NONE);
		if (operation.getResult() != null && entities.get(operation.getResult()) != null)
			m.setReturnType(DMType.makeResolvedDMType(entities.get(operation.getResult())), false);
		addProperty(RATIONAL_TYPE_ATTRIBUTE, "Operation", m);
		extractProperties(operation, m);
		addProperty(ORDER_ATTRIBUTE, order, m);
		List params = operation.getParameters();
		if (params != null) {
			for (int k = 0; k < params.getChildCount(); k++) {
				Parameter p = (Parameter) params.get(k);
				String paramName = EOModelExtractor.getLabel(p);
				if (paramName != null) {
					DMMethodParameter methodParam;
					try {
						methodParam = m.createNewParameter();
						methodParam.setName(paramName);
						if (p.getType() != null && entities.get(p.getType()) != null)
							methodParam.setType(DMType.makeResolvedDMType(entities.get(p.getType())), false);
						else
							methodParam.setType(DMType.makeResolvedDMType(project.getDataModel().getDMEntity(Object.class)), false);
					} catch (DuplicateMethodSignatureException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @param flexoAction
	 * @param entity
	 * @param propertyFormat
	 * @param j
	 * @param attrib
	 */
	private void createAttribute(DMEntity entity, String order, ClassAttribute attrib) {
		String attributeName = EOModelExtractor.getLabel(attrib);
		String card = null;
		int start = attributeName.indexOf('[');
		if (start > -1) {
			int end = attributeName.indexOf(']');
			if (end > start)
				card = attributeName.substring(start + 1, end).trim();
			attributeName = attributeName.substring(0, start).trim();
		}
		DMProperty prop = createDMProperty(entity, attributeName);
		if (attrib.getType() != null) {
			DMEntity e = entities.get(attrib.getType());
			if (e != null) {
				prop.setType(DMType.makeResolvedDMType(e), false);
			}
		}
		prop.setDescription(attrib.getDocumentation());
		addProperty(RATIONAL_TYPE_ATTRIBUTE, "ClassAttribute", prop);
		if (card != null)
			addProperty("client_cardinality", card, prop);
		extractProperties(attrib, prop);
		addProperty(ORDER_ATTRIBUTE, order, prop);
	}

	/**
	 * @param flexoAction
	 * @param petalObject
	 * @param modelObject
	 */
	private void extractProperties(cb.petal.PetalObject petalObject, FlexoModelObject modelObject) {
		for (String name : (java.util.List<String>) petalObject.getNames()) {
			if (propertyWithNameShouldBeIgnored(name))
				continue;
			PetalNode value = petalObject.getProperty(name);
			String s = getStringRepresentation(value);
			if (value != null)
				addProperty(name, s, modelObject);
		}
		ArrayList<PetalNode> attributes = petalObject.getProperties(ATTRIBUTES_PROPERTY_NAME);
		if (attributes != null) {
			for (PetalNode n : attributes) {
				if (n instanceof List) {
					List attributeList = (List) n;
					for (Object o : attributeList.getElements()) {
						if (o instanceof Attribute) {
							Attribute a = (Attribute) o;
							PetalNode value = a.getValue();
							String s = getStringRepresentation(value);
							addProperty(a.getAttributeName(), s, modelObject);
						}
					}
				}
			}
		}
	}

	private boolean propertyWithNameShouldBeIgnored(String name) {
		for (String propertiesToIgnore : PROPERTIES_TO_IGNORE) {
			if (propertiesToIgnore.equals(name))
				return true;
		}
		return false;
	}

	private String getStringRepresentation(PetalNode value) {
		String s = value != null ? value.toString() : null;
		if (value instanceof Value) {
			s = ((Value) value).getStringValue();
		} else if (value instanceof Literal) {
			s = ((Literal) value).getLiteralValue().toString();
		}
		return s;
	}

	private DMProperty createRelationship(Role role, DMEntity from, DMEntity to) {
		boolean isRoleToOne = role.getCardinality() != null && role.getCardinality().equals("1");
		DMProperty newProperty = new DMProperty(from.getDMModel(), EOModelExtractor.getLabel(role), DMType.makeResolvedDMType(to), /*isRoleToOne?*/
				DMCardinality.SINGLE/*:DMCardinality.VECTOR*/, from.getIsReadOnly(), true,
				DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
		extractProperties(role, newProperty);
		if (role.getParent() instanceof PetalObject)
			extractProperties((PetalObject) role.getParent(), newProperty);
		from.registerProperty(newProperty, true);
		return newProperty;
	}

	private DMProperty createDMProperty(DMEntity entity, String newPropertyName) {
		DMEntity defaultType = entity.getDMModel().getDMEntity(String.class);
		DMProperty newProperty = new DMProperty(entity.getDMModel(), /*entity,*/newPropertyName, DMType.makeResolvedDMType(defaultType),
				DMCardinality.SINGLE, entity.getIsReadOnly(), true, DMPropertyImplementationType.PUBLIC_ACCESSORS_PRIVATE_FIELD);
		entity.registerProperty(newProperty, true);
		return newProperty;
	}

	private DMMethod createDMMethod(DMEntity entity, String newMethodName, FlexoEditor editor) {
		CreateDMMethod createMethod = CreateDMMethod.actionType.makeNewAction(entity, null, editor);
		createMethod.__setLogActionTime(false);
		createMethod.setNewMethodName(newMethodName);
		createMethod.doAction();
		return createMethod.getNewMethod();
	}

	private DMEntity createDMEntity(String entityName, DMPackage pack) {
		DMEntity newEntity = new DMEntity(pack.getDMModel(), entityName, pack.getName(), entityName, null);
		pack.getRepository().registerEntity(newEntity);
		return newEntity;
	}

	/**
	 * @param flexoAction
	 * @param attrib
	 * @param prop
	 */
	private void addProperty(String name, String value, FlexoModelObject prop) {
		if (value == null || value.length() == 0)
			return;
		AddFlexoProperty add = AddFlexoProperty.actionType.makeNewAction(prop, null, flexoAction != null ? flexoAction.getEditor() : null);
		add.__setLogActionTime(false);// Prevents from leaking
		add.setName(name);
		add.setValue(value);
		add.setInsertSorted(true);
		add.doAction();
	}

}
