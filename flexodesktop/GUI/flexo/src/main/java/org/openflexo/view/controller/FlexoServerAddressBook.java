package org.openflexo.view.controller;

import java.util.List;

import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@XMLElement
public interface FlexoServerAddressBook {

	public static final String INSTANCES = "instances";

	@Getter(value = INSTANCES, cardinality = Cardinality.LIST)
	@XMLElement
	public List<FlexoServerInstance> getInstances();

	@Setter(INSTANCES)
	public void setInstances(List<FlexoServerInstance> instances);

	@Adder(INSTANCES)
	public void addToInstances(FlexoServerInstance instance);

	@Remover(INSTANCES)
	public void removeFromInstances(FlexoServerInstance instance);

	@Finder(attribute = FlexoServerInstance.ID, collection = INSTANCES)
	public FlexoServerInstance getInstanceWithID(String id);
}
