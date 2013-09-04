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
import org.openflexo.fge.GraphicalRepresentation.GRParameter;

public abstract class GRBinding<O, GR extends GraphicalRepresentation> implements Bindable {

	private static BindingFactory BINDING_FACTORY = new JavaBindingFactory();

	private String name;
	private GRProvider<O, GR> grProvider;
	private List<GRStructureWalker<O>> walkers;

	private Map<GRParameter, DataBinding<?>> dynamicPropertyValues;
	protected BindingModel bindingModel;

	protected GRBinding(String name, Class<?> drawableClass, GRProvider<O, GR> grProvider) {
		this.name = name;
		this.grProvider = grProvider;
		walkers = new ArrayList<GRStructureWalker<O>>();
		dynamicPropertyValues = new Hashtable<GRParameter, DataBinding<?>>();
		bindingModel = new BindingModel();
		bindingModel.addToBindingVariables(new BindingVariable("drawable", drawableClass));
	}

	public List<GRStructureWalker<O>> getWalkers() {
		return walkers;
	}

	public void addToWalkers(GRStructureWalker<O> walker) {
		walkers.add(walker);
	}

	public GRProvider<O, GR> getGRProvider() {
		return grProvider;
	}

	public DataBinding<?> getDynamicPropertyValue(GRParameter parameter) {
		return dynamicPropertyValues.get(parameter);
	}

	public void setDynamicPropertyValue(GRParameter parameter, String bindingValue) {
		DataBinding<?> binding = new DataBinding<Object>(bindingValue, this, Object.class, BindingDefinitionType.GET_SET);
		dynamicPropertyValues.put(parameter, binding);
	}

	public boolean hasDynamicPropertyValue(GRParameter parameter) {
		return dynamicPropertyValues.get(parameter) != null;
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
