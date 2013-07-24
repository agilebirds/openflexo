package org.openflexo.fge;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.GraphicalRepresentation.GRParameter;

public abstract class GRBinding<O, GR extends GraphicalRepresentation> {

	private GRProvider<O, GR> grProvider;
	private List<GRStructureWalker<O>> walkers;

	public GRBinding() {
		walkers = new ArrayList<GRStructureWalker<O>>();
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

	public void setGRProvider(GRProvider<O, GR> grProvider) {
		this.grProvider = grProvider;
	}

	public void addDynamicPropertyValue(GRParameter parameter, String bindingValue) {
	}

	public static class DrawingGRBinding<R> extends GRBinding<R, DrawingGraphicalRepresentation> {

	}

	public static class ShapeGRBinding<R> extends GRBinding<R, ShapeGraphicalRepresentation> {

	}

	public static class ConnectorGRBinding<R> extends GRBinding<R, ConnectorGraphicalRepresentation> {

	}

}
