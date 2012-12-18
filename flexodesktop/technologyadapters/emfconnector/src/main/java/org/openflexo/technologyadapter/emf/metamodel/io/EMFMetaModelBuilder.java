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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
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
 * EMF MetaModel Builder.
 * 
 * @author gbesancon
 */
public class EMFMetaModelBuilder {
	/** EMF Adapter. */
	protected final EMFTechnologyAdapter adapter;

	/**
	 * Constructor.
	 */
	public EMFMetaModelBuilder(EMFTechnologyAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * Build MetaModel.
	 * 
	 * @param aPackage
	 * @return
	 */
	public EMFMetaModel buildMetaModel(EMFMetaModelConverter converter, EPackage aPackage) {
		return new EMFMetaModel(converter, aPackage, adapter);
	}

	/**
	 * Build Annotation.
	 * 
	 * @param metaModel
	 * @param aAnnotation
	 * @return
	 */
	public EMFAnnotationAnnotation buildAnnotation(EMFMetaModel metaModel, EAnnotation aAnnotation) {
		return new EMFAnnotationAnnotation(metaModel, aAnnotation);
	}

	/**
	 * Build Package Container.
	 * 
	 * @param metaModel
	 * @param aPackage
	 * @return
	 */
	public EMFPackageContainer buildPackage(EMFMetaModel metaModel, EPackage aPackage) {
		return new EMFPackageContainer(metaModel, aPackage);
	}

	/**
	 * Build Class.
	 * 
	 * @param aClass
	 * @return
	 */
	public EMFClassClass buildClass(EMFMetaModel metaModel, EClass aClass) {
		return new EMFClassClass(metaModel, aClass);
	}

	/**
	 * Build DataType.
	 * 
	 * @param aDataType
	 * @return
	 */
	public EMFDataTypeDataType buildDataType(EMFMetaModel metaModel, EDataType aDataType) {
		return new EMFDataTypeDataType(metaModel, aDataType);
	}

	/**
	 * Build Enum.
	 * 
	 * @param aDataType
	 * @return
	 */
	public EMFEnumClass buildEnum(EMFMetaModel metaModel, EEnum aEnum) {
		return new EMFEnumClass(metaModel, aEnum);
	}

	/**
	 * Build Enum Literal.
	 * 
	 * @param aEnumLiteral
	 * @return
	 */
	public EMFEnumIndividual buildEnumLiteral(EMFMetaModel metaModel, EEnumLiteral aEnumLiteral) {
		return new EMFEnumIndividual(metaModel, aEnumLiteral);
	}

	/**
	 * Build Attribute Association.
	 * 
	 * @param aAttribute
	 * @return
	 */
	public EMFAttributeAssociation buildAttributeAssociation(EMFMetaModel metaModel, EAttribute aAttribute) {
		return new EMFAttributeAssociation(metaModel, aAttribute);
	}

	/**
	 * Build Attribute Data Property.
	 * 
	 * @param aReference
	 * @return
	 */
	public EMFAttributeDataProperty buildAttributeDataProperty(EMFMetaModel metaModel, EAttribute aAttribute) {
		return new EMFAttributeDataProperty(metaModel, aAttribute);
	}

	/**
	 * Build Attribute Object Property.
	 * 
	 * @param aReference
	 * @return
	 */
	public EMFAttributeObjectProperty buildAttributeObjectProperty(EMFMetaModel metaModel, EAttribute aAttribute) {
		return new EMFAttributeObjectProperty(metaModel, aAttribute);
	}

	/**
	 * Build Reference Association.
	 * 
	 * @param aReference
	 * @return
	 */
	public EMFReferenceAssociation buildReferenceAssociation(EMFMetaModel metaModel, EReference aReference) {
		return new EMFReferenceAssociation(metaModel, aReference);
	}

	/**
	 * Build Reference Object Property.
	 * 
	 * @param aReference
	 * @return
	 */
	public EMFReferenceObjectProperty buildReferenceObjectProperty(EMFMetaModel metaModel, EReference aReference) {
		return new EMFReferenceObjectProperty(metaModel, aReference);
	}
}
