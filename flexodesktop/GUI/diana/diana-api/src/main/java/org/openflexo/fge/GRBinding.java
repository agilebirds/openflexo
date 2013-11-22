/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
package org.openflexo.fge;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.binding.JavaBindingFactory;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.ContainerGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.GeometricGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;

public abstract class GRBinding<O, GR extends GraphicalRepresentation> implements Bindable {

	private static BindingFactory BINDING_FACTORY = new JavaBindingFactory();

	private String name;
	private GRProvider<O, GR> grProvider;
	private List<GRStructureVisitor<O>> walkers;

	private Map<GRParameter, DataBinding<?>> dynamicPropertyValues;
	protected BindingModel bindingModel;

	protected GRBinding(String name, Class<?> drawableClass, GRProvider<O, GR> grProvider) {
		this.name = name;
		this.grProvider = grProvider;
		walkers = new ArrayList<GRStructureVisitor<O>>();
		dynamicPropertyValues = new Hashtable<GRParameter, DataBinding<?>>();
		bindingModel = new BindingModel();
		bindingModel.addToBindingVariables(new BindingVariable("drawable", drawableClass));
	}

	public List<GRStructureVisitor<O>> getWalkers() {
		return walkers;
	}

	public void addToWalkers(GRStructureVisitor<O> walker) {
		walkers.add(walker);
	}

	public GRProvider<O, GR> getGRProvider() {
		return grProvider;
	}

	public <T> DataBinding<T> getDynamicPropertyValue(GRParameter<T> parameter) {
		return (DataBinding<T>) dynamicPropertyValues.get(parameter);
	}

	public <T> void setDynamicPropertyValue(GRParameter<T> parameter, DataBinding<T> bindingValue) {
		setDynamicPropertyValue(parameter, bindingValue, false);
	}

	public <T> void setDynamicPropertyValue(GRParameter<T> parameter, DataBinding<T> bindingValue, boolean settable) {
		bindingValue.setOwner(this);
		bindingValue.setBindingName(parameter.getName());
		bindingValue.setDeclaredType(parameter.getType());
		bindingValue.setBindingDefinitionType(settable ? BindingDefinitionType.GET_SET : BindingDefinitionType.GET);
		dynamicPropertyValues.put(parameter, bindingValue);
	}

	public boolean hasDynamicPropertyValue(GRParameter<?> parameter) {
		return dynamicPropertyValues.get(parameter) != null;
	}

	public Map<GRParameter, DataBinding<?>> getDynamicPropertyValues() {
		return dynamicPropertyValues;
	}

	@Override
	public BindingFactory getBindingFactory() {
		return BINDING_FACTORY;
	}

	@Override
	public BindingModel getBindingModel() {
		return bindingModel;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		// TODO Auto-generated method stub

	}

	public static abstract class ContainerGRBinding<O, GR extends ContainerGraphicalRepresentation> extends GRBinding<O, GR> {

		public ContainerGRBinding(String name, Class<?> drawableClass, ContainerGRProvider<O, GR> grProvider) {
			super(name, drawableClass, grProvider);
		}
	}

	public static class DrawingGRBinding<M> extends ContainerGRBinding<M, DrawingGraphicalRepresentation> {

		public DrawingGRBinding(String name, Class<?> drawableClass, DrawingGRProvider<M> grProvider) {
			super(name, drawableClass, grProvider);
			bindingModel.addToBindingVariables(new BindingVariable("gr", DrawingGraphicalRepresentation.class));
		}

	}

	public static class ShapeGRBinding<O> extends ContainerGRBinding<O, ShapeGraphicalRepresentation> {

		public ShapeGRBinding(String name, Class<?> drawableClass, ShapeGRProvider<O> grProvider) {
			super(name, drawableClass, grProvider);
			bindingModel.addToBindingVariables(new BindingVariable("gr", ShapeGraphicalRepresentation.class));
		}

	}

	public static class ConnectorGRBinding<O> extends GRBinding<O, ConnectorGraphicalRepresentation> {

		public ConnectorGRBinding(String name, Class<?> drawableClass, ConnectorGRProvider<O> grProvider) {
			super(name, drawableClass, grProvider);
			bindingModel.addToBindingVariables(new BindingVariable("gr", ConnectorGraphicalRepresentation.class));
		}
	}

	public static class GeometricGRBinding<O> extends GRBinding<O, GeometricGraphicalRepresentation> {

		public GeometricGRBinding(String name, Class<?> drawableClass, GeometricGRProvider<O> grProvider) {
			super(name, drawableClass, grProvider);
			bindingModel.addToBindingVariables(new BindingVariable("gr", GeometricGraphicalRepresentation.class));
		}
	}

}
