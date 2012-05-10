package org.openflexo.generator.action;

import java.io.File;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;

public abstract class GenerateArtefact<A extends GCAction<A, T1>, T1 extends CGObject> extends GCAction<A, T1> {

	protected GenerateArtefact(FlexoActionType<A, T1, CGObject> actionType, T1 focusedObject, Vector<CGObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		// TODO Auto-generated constructor stub
	}

	public abstract File getArtefactFile();

}
