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
package org.openflexo.fme.model;

/**
 * Default implementation for concept
 * 
 * @author sylvain
 * 
 */
public abstract class ConceptImpl implements Concept {

	public boolean isUsed() {
		if (getName().equals(NONE_CONCEPT)) {
			return true;
		}
		if (getDataModel() != null && getDataModel().getDiagram() != null) {
			for (ConceptGRAssociation association : getDataModel().getDiagram().getAssociations()) {
				if (association.getConcept() == this) {
					return true;
				}
			}
		}
		return false;
	}
}
