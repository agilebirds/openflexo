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

import java.util.ArrayList;
import java.util.List;

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
	
	@Override
	public String produceHtmlLabel(String label){
		//setHtmlLabel(getName());
		StringBuilder sb = new StringBuilder();
		if(getName()!=null){
			if(getReadOnly()){
				sb.append("<html><i>");
				sb.append(label);
				sb.append("</i></html>");
			}
			else{
				sb.append("<html>");
				sb.append(label);
				sb.append("</html>");
			}
		}
		performSuperSetter(NAME, sb.toString().replaceAll("<[^>]*>", ""));
		return sb.toString();
	}
	
	@Override
	public void setHtmlLabel(String label){
		if(getReadOnly()&&(!label.replaceAll("<[^>]*>", "").equals(getName()))){
			return;
		}
		else{
			performSuperSetter(HTML_LABEL, label);
		}
		
		return;
	}
	
	public void removeUnusedProperties(){
		// check if there is one instance with this attribute, otherwise delete it
		List<PropertyDefinition> propertyToRemove = new ArrayList<PropertyDefinition>();
		List<Instance> instanceWithPropertyToRemove = new ArrayList<Instance>();
		for(PropertyDefinition propDef : getProperties()){
			boolean validProperty = false;
			for(Instance instance : getInstanceWithProperty(propDef)){
				if(instance.getPropertyNamed(propDef.getName()).getValue()!=""){
					validProperty = true;
				}
				else{
					instanceWithPropertyToRemove.add(instance);
				}
			}
			if(!validProperty){
				propertyToRemove.add(propDef);
				for(Instance instance : instanceWithPropertyToRemove){
					instance.removeFromPropertyValues(instance.getPropertyNamed(propDef.getName()));
				}
			}
		}
		
		getProperties().removeAll(propertyToRemove);
	}
	
	
	private List<Instance> getInstanceWithProperty(PropertyDefinition propertyDefinition){
		List<Instance> instanceWithProperty = new ArrayList<Instance>();
		for(Instance instance : getInstances()){
			if(instance.getPropertyNamed(propertyDefinition.getName())!=null){
				instanceWithProperty.add(instance);
			}
		}
		return instanceWithProperty;
	}
}
