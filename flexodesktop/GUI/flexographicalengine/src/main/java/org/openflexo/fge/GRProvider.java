package org.openflexo.fge;

public abstract class GRProvider<R, GR extends GraphicalRepresentation> {
	public abstract GR provideGR(R drawable, FGEModelFactory factory);
}
