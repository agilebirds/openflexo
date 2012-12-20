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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.metamodel;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * EMF Uri Builder from Object.
 * 
 * @author gbesancon
 */
public class EMFMetaModelURIBuilder {

	/**
	 * URI of package.
	 * 
	 * @param aPackage
	 * @return
	 */
	public static String getUri(EPackage aPackage) {
		return aPackage.getNsURI();
	}

	/**
	 * URI of class.
	 * 
	 * @param aClass
	 * @return
	 */
	public static String getUri(EClass aClass) {
		return aClass.getEPackage().getNsURI() + '/' + aClass.getName();
	}

	/**
	 * URI of datatype.
	 * 
	 * @param aDataType
	 * @return
	 */
	public static String getUri(EDataType aDataType) {
		return aDataType.getEPackage().getNsURI() + '/' + aDataType.getName();
	}

	/**
	 * 
	 * URI of enum literal.
	 * 
	 * @param aEnumLiteral
	 * @return
	 */
	public static String getUri(EEnumLiteral aEnumLiteral) {
		return aEnumLiteral.getEEnum().getEPackage().getNsURI() + '/' + aEnumLiteral.getEEnum().getName() + '/' + aEnumLiteral.getName();
	}

	/**
	 * URI of StruturalFeature.
	 * 
	 * @param aClass
	 * @return
	 */
	public static String getUri(EStructuralFeature aStructuralFeature) {
		return aStructuralFeature.getEContainingClass().getEPackage().getNsURI() + '/' + aStructuralFeature.getEContainingClass().getName()
				+ '/' + aStructuralFeature.getName();
	}
}
