/** Copyright (c) 2012, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.metamodel.io;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFAnnotationAnnotation;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeAssociation;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeDataProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeObjectProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFDataTypeDataType;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumIndividual;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFPackageContainer;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceAssociation;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceObjectProperty;

/**
 * EMF MetaModel Converter.
 * 
 * @author gbesancon
 */
public class EMFMetaModelConverter {

	/** Builder. */
	protected EMFMetaModelBuilder builder = new EMFMetaModelBuilder();
	/** Packages from EPackage. */
	protected Map<EPackage, EMFPackageContainer> packages = new HashMap<EPackage, EMFPackageContainer>();
	/** Classes from EClass. */
	protected Map<EClass, EMFClassClass> classes = new HashMap<EClass, EMFClassClass>();
	/** DataType from EDataType. */
	protected Map<EDataType, EMFDataTypeDataType> dataTypes = new HashMap<EDataType, EMFDataTypeDataType>();
	/** Classes from EEnum. */
	protected Map<EEnum, EMFEnumClass> enums = new HashMap<EEnum, EMFEnumClass>();
	/** Individual from EEnumLiteral. */
	protected Map<Enumerator, EMFEnumIndividual> enumLiterals = new HashMap<Enumerator, EMFEnumIndividual>();
	/** Features from EAttribute. */
	protected Map<EAttribute, EMFAttributeObjectProperty> objectAttributes = new HashMap<EAttribute, EMFAttributeObjectProperty>();
	/** Features from EAttribute. */
	protected Map<EAttribute, EMFAttributeDataProperty> dataAttributes = new HashMap<EAttribute, EMFAttributeDataProperty>();
	/** Feature Associations from EAttribute. */
	protected Map<EAttribute, EMFAttributeAssociation> attributeAssociations = new HashMap<EAttribute, EMFAttributeAssociation>();
	/** Features from EReference. */
	protected Map<EReference, EMFReferenceObjectProperty> references = new HashMap<EReference, EMFReferenceObjectProperty>();
	/** Feature Associations from EReference. */
	protected Map<EReference, EMFReferenceAssociation> referenceAssociations = new HashMap<EReference, EMFReferenceAssociation>();
	/** Annotations. */
	protected Map<EAnnotation, EMFAnnotationAnnotation> annotations = new HashMap<EAnnotation, EMFAnnotationAnnotation>();

	/**
	 * Constructor.
	 */
	public EMFMetaModelConverter() {
	}

	/**
	 * Convert an EMF Resource into a MetaModel.
	 * 
	 * @param resource
	 * @return
	 */
	public EMFMetaModel convertMetaModel(Resource aResource) {
		EMFMetaModel metaModel = null;
		if (aResource.getContents().size() == 1 && aResource.getContents().get(0).eClass().getClassifierID() == EcorePackage.EPACKAGE) {
			EPackage aPackage = (EPackage) aResource.getContents().get(0);
			metaModel = convertMetaModel(aPackage);
		}
		return metaModel;
	}

	/**
	 * Convert an EMF Package into a MetaModel.
	 * 
	 * @param aPackage
	 * @return
	 */
	public EMFMetaModel convertMetaModel(EPackage aPackage) {
		EMFMetaModel metaModel = builder.buildMetaModel(this, aPackage);
		convertPackage(metaModel, aPackage);
		return metaModel;
	}

	/**
	 * Convert an EMF Annotation into an EMF Annotation.
	 * 
	 * @param metaModel
	 * @param aAnnotation
	 * @return
	 */
	public EMFAnnotationAnnotation convertAnnotation(EMFMetaModel metaModel, EAnnotation aAnnotation) {
		EMFAnnotationAnnotation emfAnnotation = null;
		if (annotations.get(aAnnotation) == null) {
			emfAnnotation = builder.buildAnnotation(metaModel, aAnnotation);
		} else {
			emfAnnotation = annotations.get(aAnnotation);
		}
		return emfAnnotation;
	}

	/**
	 * Convert an EMF Package into an EMF Package container.
	 * 
	 * @param metaModel
	 * @param aPackage
	 * @return
	 */
	public EMFPackageContainer convertPackage(EMFMetaModel metaModel, EPackage aPackage) {
		EMFPackageContainer emfPackageContainer = null;
		if (packages.get(aPackage) == null) {
			// Super Package.
			if (aPackage.getESuperPackage() != null) {
				EMFPackageContainer emfSuperPackageContainer = convertPackage(metaModel, aPackage.getESuperPackage());
			}

			emfPackageContainer = builder.buildPackage(metaModel, aPackage);
			packages.put(aPackage, emfPackageContainer);

			// DataTypes
			for (EClassifier aClassifier : aPackage.getEClassifiers()) {
				if (aClassifier.eClass().getClassifierID() == EcorePackage.EDATA_TYPE) {
					EMFDataTypeDataType emfDataType = convertDataType(metaModel, (EDataType) aClassifier);
				}
			}
			// Enum
			for (EClassifier aClassifier : aPackage.getEClassifiers()) {
				if (aClassifier.eClass().getClassifierID() == EcorePackage.EENUM) {
					EMFEnumClass emfEnum = convertEnum(metaModel, (EEnum) aClassifier);
				}
			}
			// Classes
			for (EClassifier aClassifier : aPackage.getEClassifiers()) {
				if (aClassifier.eClass().getClassifierID() == EcorePackage.ECLASS) {
					EMFClassClass emfClass = convertClass(metaModel, (EClass) aClassifier);
				}
			}

			// Sub Packages
			for (EPackage aSubPackage : aPackage.getESubpackages()) {
				EMFPackageContainer emfSubPackageContainer = convertPackage(metaModel, aSubPackage);
			}
		} else {
			emfPackageContainer = packages.get(aPackage);
		}

		return emfPackageContainer;
	}

	/**
	 * Convert an EMF DataType into an EMF DataType.
	 * 
	 * @param metaModel
	 * @param aDataType
	 * @return
	 */
	public EMFDataTypeDataType convertDataType(EMFMetaModel metaModel, EDataType aDataType) {
		EMFDataTypeDataType emfDataType = null;
		if (dataTypes.get(aDataType) == null) {
			if (aDataType.getEPackage() != null) {
				EMFPackageContainer emfPackageContainer = convertPackage(metaModel, aDataType.getEPackage());
			}

			emfDataType = builder.buildDataType(metaModel, aDataType);
			dataTypes.put(aDataType, emfDataType);
		} else {
			emfDataType = dataTypes.get(aDataType);
		}
		return emfDataType;
	}

	/**
	 * Convert an EMF Enum into an EMF Enum Class Concept.
	 * 
	 * @param metaModel
	 * @param aEnum
	 * @return
	 */
	public EMFEnumClass convertEnum(EMFMetaModel metaModel, EEnum aEnum) {
		EMFEnumClass emfEnum = null;
		if (enums.get(aEnum) == null) {
			if (aEnum.getEPackage() != null) {
				EMFPackageContainer emfPackageContainer = convertPackage(metaModel, aEnum.getEPackage());
			}

			emfEnum = builder.buildEnum(metaModel, aEnum);
			enums.put(aEnum, emfEnum);
			for (EEnumLiteral aEnumLiteral : aEnum.getELiterals()) {
				EMFEnumIndividual emfEnumIndividual = convertEnumLiteral(metaModel, aEnumLiteral);
			}
		} else {
			emfEnum = enums.get(aEnum);
		}
		return emfEnum;
	}

	/**
	 * Convert an EMF Enum Literal into an EMF Enum Individual Concept.
	 * 
	 * @param metaModel
	 * @param aEnumLiteral
	 * @return
	 */
	public EMFEnumIndividual convertEnumLiteral(EMFMetaModel metaModel, EEnumLiteral aEnumLiteral) {
		EMFEnumIndividual emfEnumLiteral = null;
		if (enumLiterals.get(aEnumLiteral.getInstance()) == null) {
			if (aEnumLiteral.getEEnum() != null) {
				EMFEnumClass emfEnum = convertEnum(metaModel, aEnumLiteral.getEEnum());
			}

			emfEnumLiteral = builder.buildEnumLiteral(metaModel, aEnumLiteral);
			enumLiterals.put(aEnumLiteral.getInstance(), emfEnumLiteral);
		} else {
			emfEnumLiteral = enumLiterals.get(aEnumLiteral.getInstance());
		}
		return emfEnumLiteral;
	}

	/**
	 * Convert an EMF Class into an EMF Class Concept.
	 * 
	 * @param metaModel
	 * @param aClass
	 * @return
	 */
	public EMFClassClass convertClass(EMFMetaModel metaModel, EClass aClass) {
		EMFClassClass emfClass = null;
		if (classes.get(aClass) == null) {
			if (aClass.getEPackage() != null) {
				EMFPackageContainer emfPackageContainer = convertPackage(metaModel, aClass.getEPackage());
			}

			emfClass = builder.buildClass(metaModel, aClass);
			classes.put(aClass, emfClass);

			for (EClass eSuperClass : aClass.getESuperTypes()) {
				EMFClassClass emfSuperClass = convertClass(metaModel, eSuperClass);
			}

			for (EStructuralFeature eStructuralFeature : aClass.getEStructuralFeatures()) {
				if (eStructuralFeature.eClass().getClassifierID() == EcorePackage.EREFERENCE) {
					EMFReferenceAssociation emfReferenceAssociation = convertReferenceAssociation(metaModel,
							(EReference) eStructuralFeature);
				} else if (eStructuralFeature.eClass().getClassifierID() == EcorePackage.EATTRIBUTE) {
					EMFAttributeAssociation emfAttributeAssociation = convertAttributeAssociation(metaModel,
							(EAttribute) eStructuralFeature);
				}
			}

		} else {
			emfClass = classes.get(aClass);
		}
		return emfClass;
	}

	/**
	 * Convert an EMF Attribute into an EMF Feature Association.
	 * 
	 * @param metaModel
	 * @param aAttribute
	 * @return
	 */
	public EMFAttributeAssociation convertAttributeAssociation(EMFMetaModel metaModel, EAttribute aAttribute) {
		EMFAttributeAssociation emfAttributeAssociation = null;
		if (attributeAssociations.get(aAttribute) == null) {
			if (aAttribute.getEContainingClass() != null) {
				EMFClassClass emfClass = convertClass(metaModel, aAttribute.getEContainingClass());
			}

			emfAttributeAssociation = builder.buildAttributeAssociation(metaModel, aAttribute);
			attributeAssociations.put(aAttribute, emfAttributeAssociation);
			IFlexoOntologyStructuralProperty emfAttributeProperty = convertAttributeProperty(metaModel, aAttribute);
		} else {
			emfAttributeAssociation = attributeAssociations.get(aAttribute);
		}
		return emfAttributeAssociation;
	}

	/**
	 * Convert an EMF Reference into an EMF Feature Association.
	 * 
	 * @param metaModel
	 * @param aReference
	 * @return
	 */
	public EMFReferenceAssociation convertReferenceAssociation(EMFMetaModel metaModel, EReference aReference) {
		EMFReferenceAssociation emfReferenceAssociation = null;
		if (referenceAssociations.get(aReference) == null) {
			if (aReference.getEContainingClass() != null) {
				EMFClassClass emfClass = convertClass(metaModel, aReference.getEContainingClass());
			}

			emfReferenceAssociation = builder.buildReferenceAssociation(metaModel, aReference);
			referenceAssociations.put(aReference, emfReferenceAssociation);
			EMFReferenceObjectProperty emfReferenceObjectProperty = convertReferenceObjectProperty(metaModel, aReference);
		} else {
			emfReferenceAssociation = referenceAssociations.get(aReference);
		}
		return emfReferenceAssociation;
	}

	/**
	 * Convert an EMF Attribute into an EMF Structural Property.
	 * 
	 * @param metaModel
	 * @param aAttribute
	 * @return
	 */
	public IFlexoOntologyStructuralProperty convertAttributeProperty(EMFMetaModel metaModel, EAttribute aAttribute) {
		IFlexoOntologyStructuralProperty structuralProperty = null;
		if (aAttribute.getEAttributeType().eClass().getClassifierID() == EcorePackage.EDATA_TYPE) {
			structuralProperty = convertAttributeDataProperty(metaModel, aAttribute);
		} else if (aAttribute.getEAttributeType().eClass().getClassifierID() == EcorePackage.EENUM) {
			structuralProperty = convertAttributeObjectProperty(metaModel, aAttribute);
		}
		return structuralProperty;
	}

	/**
	 * Convert an EMF Attribute into an EMF Attribute Data Property.
	 * 
	 * @param metaModel
	 * @param aAttribute
	 * @return
	 */
	public EMFAttributeDataProperty convertAttributeDataProperty(EMFMetaModel metaModel, EAttribute aAttribute) {
		EMFAttributeDataProperty dataProperty = null;
		if (dataAttributes.get(aAttribute) == null) {
			if (aAttribute.getEContainingClass() != null) {
				EMFClassClass emfClass = convertClass(metaModel, aAttribute.getEContainingClass());
			}

			dataProperty = builder.buildAttributeDataProperty(metaModel, aAttribute);
			dataAttributes.put(aAttribute, dataProperty);

			if (aAttribute.getEAttributeType().eClass().getClassifierID() == EcorePackage.EDATA_TYPE) {
				EMFDataTypeDataType emfDataType = convertDataType(metaModel, aAttribute.getEAttributeType());
			} else if (aAttribute.getEAttributeType().eClass().getClassifierID() == EcorePackage.EENUM) {
				EMFEnumClass emfEnum = convertEnum(metaModel, (EEnum) aAttribute.getEAttributeType());
			}
		} else {
			dataProperty = dataAttributes.get(aAttribute);
		}
		return dataProperty;
	}

	/**
	 * Convert an EMF Attribute into an EMF Attribute Object Property.
	 * 
	 * @param metaModel
	 * @param aAttribute
	 * @return
	 */
	public EMFAttributeObjectProperty convertAttributeObjectProperty(EMFMetaModel metaModel, EAttribute aAttribute) {
		EMFAttributeObjectProperty objectProperty = null;
		if (objectAttributes.get(aAttribute) == null) {
			if (aAttribute.getEContainingClass() != null) {
				EMFClassClass emfClass = convertClass(metaModel, aAttribute.getEContainingClass());
			}

			objectProperty = builder.buildAttributeObjectProperty(metaModel, aAttribute);
			objectAttributes.put(aAttribute, objectProperty);
		} else {
			objectProperty = objectAttributes.get(aAttribute);
		}
		return objectProperty;
	}

	/**
	 * Convert an EMF Reference into an EMF Object Property.
	 * 
	 * @param metaModel
	 * @param aReference
	 * @return
	 */
	public EMFReferenceObjectProperty convertReferenceObjectProperty(EMFMetaModel metaModel, EReference aReference) {
		EMFReferenceObjectProperty objectProperty = null;
		if (references.get(aReference) == null) {
			if (aReference.getEContainingClass() != null) {
				EMFClassClass emfClass = convertClass(metaModel, aReference.getEContainingClass());
			}

			objectProperty = builder.buildReferenceObjectProperty(metaModel, aReference);
			references.put(aReference, objectProperty);
		} else {
			objectProperty = references.get(aReference);
		}
		return objectProperty;
	}

	/**
	 * Getter of packages.
	 * 
	 * @return the packages value
	 */
	public Map<EPackage, EMFPackageContainer> getPackages() {
		return packages;
	}

	/**
	 * Getter of classes.
	 * 
	 * @return the classes value
	 */
	public Map<EClass, EMFClassClass> getClasses() {
		return classes;
	}

	/**
	 * Getter of dataTypes.
	 * 
	 * @return the dataTypes value
	 */
	public Map<EDataType, EMFDataTypeDataType> getDataTypes() {
		return dataTypes;
	}

	/**
	 * Getter of enums.
	 * 
	 * @return the enums value
	 */
	public Map<EEnum, EMFEnumClass> getEnums() {
		return enums;
	}

	/**
	 * Getter of enumLiterals.
	 * 
	 * @return the enumLiterals value
	 */
	public Map<Enumerator, EMFEnumIndividual> getEnumLiterals() {
		return enumLiterals;
	}

	/**
	 * Getter of objectAttributes.
	 * 
	 * @return the objectAttributes value
	 */
	public Map<EAttribute, EMFAttributeObjectProperty> getObjectAttributes() {
		return objectAttributes;
	}

	/**
	 * Getter of dataAttributes.
	 * 
	 * @return the dataAttributes value
	 */
	public Map<EAttribute, EMFAttributeDataProperty> getDataAttributes() {
		return dataAttributes;
	}

	/**
	 * Getter of attributeAssociations.
	 * 
	 * @return the attributeAssociations value
	 */
	public Map<EAttribute, EMFAttributeAssociation> getAttributeAssociations() {
		return attributeAssociations;
	}

	/**
	 * Getter of references.
	 * 
	 * @return the references value
	 */
	public Map<EReference, EMFReferenceObjectProperty> getReferences() {
		return references;
	}

	/**
	 * Getter of referenceAssociations.
	 * 
	 * @return the referenceAssociations value
	 */
	public Map<EReference, EMFReferenceAssociation> getReferenceAssociations() {
		return referenceAssociations;
	}
}
