package org.openflexo.fge;

public abstract class GRProvider<O, GR extends GraphicalRepresentation> {
	public abstract GR provideGR(O drawable, FGEModelFactory factory);

	public static abstract class ContainerGRProvider<O, GR extends ContainerGraphicalRepresentation> extends GRProvider<O, GR> {

	}

	public static abstract class DrawingGRProvider<O> extends ContainerGRProvider<O, DrawingGraphicalRepresentation> {

	}

	public static abstract class ShapeGRProvider<O> extends ContainerGRProvider<O, ShapeGraphicalRepresentation> {

	}

	public static abstract class ConnectorGRProvider<O> extends GRProvider<O, ConnectorGraphicalRepresentation> {

	}

	public static abstract class GeometricGRProvider<O> extends GRProvider<O, GeometricGraphicalRepresentation> {

	}

}
