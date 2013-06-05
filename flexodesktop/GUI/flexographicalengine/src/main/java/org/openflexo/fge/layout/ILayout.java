package org.openflexo.fge.layout;

public interface ILayout {
	
	// The possible type of layout
	public static enum LayoutStatus {
		COMPLETE, PROGRESS
	}
	
	public LayoutStatus runLayout();
	
	public LayoutStatus getStatus();
}
