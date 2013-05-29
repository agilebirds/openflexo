package org.openflexo.fge.layout;

import org.openflexo.fge.ConnectorGraphicalRepresentation;

public class LayoutedEdge {
	private ConnectorGraphicalRepresentation<?> graphicalRepresentation;
	private LayoutedNode source;
	
	private LayoutedNode target;
	
	public LayoutedEdge(ConnectorGraphicalRepresentation<?> graphicalRepresentation,
			LayoutedNode source, LayoutedNode target) {
		super();
		this.graphicalRepresentation = graphicalRepresentation;
		this.source = source;
		this.target = target;
	}

	public LayoutedNode getSource() {
		return source;
	}

	public void setSource(LayoutedNode source) {
		this.source = source;
	}

	public LayoutedNode getTarget() {
		return target;
	}

	public void setTarget(LayoutedNode target) {
		this.target = target;
	}

	public LayoutedEdge(ConnectorGraphicalRepresentation<?> graphicalRepresentation) {
		super();
		this.graphicalRepresentation = graphicalRepresentation;
	}

	public ConnectorGraphicalRepresentation<?> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(
			ConnectorGraphicalRepresentation<?> graphicalRepresentation) {
		this.graphicalRepresentation = graphicalRepresentation;
	}
	
}
