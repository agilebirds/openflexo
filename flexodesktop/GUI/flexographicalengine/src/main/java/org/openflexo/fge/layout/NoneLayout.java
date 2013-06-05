package org.openflexo.fge.layout;

import org.openflexo.fge.GraphicalRepresentation;

public class NoneLayout extends Layout{

	public NoneLayout(GraphicalRepresentation<?> graphicalRp) {
		super(graphicalRp);
	}

	@Override
	public LayoutStatus runLayout() {
		return getStatus();
	}

	@Override
	public LayoutType getLayoutType() {
		return LayoutType.NONE;
	}

	@Override
	public LayoutStatus getStatus() {
		return LayoutStatus.COMPLETE;
	}

}
