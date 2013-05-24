package org.openflexo.fge.geom.area;

public class DefaultAreaProvider<O> implements FGEAreaProvider<O> {

	private final FGEArea area;

	public DefaultAreaProvider(FGEArea area) {
		super();
		this.area = area;
	}

	@Override
	public FGEArea getArea(O input) {
		return area;
	}

}
