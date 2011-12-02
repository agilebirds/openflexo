package org.openflexo.foundation.viewpoint;

import java.io.File;
import java.util.Vector;

public class ViewPointFolder extends ViewPointLibraryObject {

	private final ViewPointLibrary viewPointLibrary;
	private final String name;
	private final ViewPointFolder parent;
	private final Vector<ViewPointFolder> children;
	private final Vector<ViewPoint> viewPoints;

	public ViewPointFolder(String name, ViewPointFolder parentFolder, ViewPointLibrary viewPointLibrary) {
		this.viewPointLibrary = viewPointLibrary;
		this.name = name;
		this.parent = parentFolder;
		children = new Vector<ViewPointFolder>();
		viewPoints = new Vector<ViewPoint>();
		if (parentFolder != null) {
			parentFolder.addToChildren(this);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public Vector<ViewPointFolder> getChildren() {
		return children;
	}

	public void addToChildren(ViewPointFolder aFolder) {
		children.add(aFolder);
	}

	public void removeFromChildren(ViewPointFolder aFolder) {
		children.remove(aFolder);
	}

	public Vector<ViewPoint> getViewPoints() {
		return viewPoints;
	}

	public void addToViewPoints(ViewPoint aCalc) {
		viewPoints.add(aCalc);
	}

	public void removeFromViewPoints(ViewPoint aCalc) {
		viewPoints.remove(aCalc);
	}

	@Override
	public ViewPointLibrary getViewPointLibrary() {
		return viewPointLibrary;
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	public File getExpectedPath() {
		if (parent != null) {
			return new File(parent.getExpectedPath(), name);
		}
		return viewPointLibrary.getResourceCenter().getNewCalcSandboxDirectory();
	}
}
