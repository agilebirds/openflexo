package org.openflexo.fge;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.ContainerGRProvider;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.GeometricGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;

public abstract class GRBinding<O, GR extends GraphicalRepresentation> {

	private GRProvider<O, GR> grProvider;
	private List<GRStructureWalker<O>> walkers;

	protected GRBinding(GRProvider<O, GR> grProvider) {
		this.grProvider = grProvider;
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

	public void addDynamicPropertyValue(GRParameter parameter, String bindingValue) {
	}

	public static abstract class ContainerGRBinding<O, GR extends ContainerGraphicalRepresentation> extends GRBinding<O, GR> {

		public ContainerGRBinding(ContainerGRProvider<O, GR> grProvider) {
			super(grProvider);
		}
	}

	public static class DrawingGRBinding<M> extends ContainerGRBinding<M, DrawingGraphicalRepresentation> {

		public DrawingGRBinding(DrawingGRProvider<M> grProvider) {
			super(grProvider);
		}

	}

	public static class ShapeGRBinding<O> extends ContainerGRBinding<O, ShapeGraphicalRepresentation> {

		public ShapeGRBinding(ShapeGRProvider<O> grProvider) {
			super(grProvider);
		}

	}

	public static class ConnectorGRBinding<O> extends GRBinding<O, ConnectorGraphicalRepresentation> {

		public ConnectorGRBinding(ConnectorGRProvider<O> grProvider) {
			super(grProvider);
		}
	}

	public static class GeometricGRBinding<O> extends GRBinding<O, GeometricGraphicalRepresentation> {

		public GeometricGRBinding(GeometricGRProvider<O> grProvider) {
			super(grProvider);
		}
	}

}
