package org.openflexo.fge;

import java.util.List;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;

public abstract class GRStructureWalker<R> {

	private DrawingTreeNode<R, ?> dtn;

	public void startWalking(DrawingTreeNode<R, ?> dtn) {

	}

	public abstract void walk(R drawable);

	public List<DrawingTreeNode<?, ?>> stopWalking(DrawingTreeNode<R, ?> dtn) {
		return null;
	}

	/*public <O> DrawingTreeNode<O> drawDrawing(DrawingGRBinding<O> binding, O representable) {
		return null;
	}*/

	public <O> ShapeNode<O> drawShape(ShapeGRBinding<O> binding, O representable, Object parentRepresentable) {
		return null;
	}

	public <O, P> ShapeNode<O> drawShape(ShapeGRBinding<O> binding, O representable, ShapeGRBinding<P> parentBinding, P parentRepresentable) {
		return null;
	}

	public <O, P> ShapeNode<O> drawShape(ShapeGRBinding<O> binding, O representable, DrawingGRBinding<P> parentBinding,
			P parentRepresentable) {
		return null;
	}

	public <O> ShapeNode<O> drawShape(ShapeNode<?> parent, ShapeGRBinding<O> binding, O drawable) {
		Drawing<?> drawing = dtn.getDrawing();
		return drawing.drawShape(parent, binding, drawable);
	}

	public <O> ShapeNode<O> drawShape(RootNode<?> parent, ShapeGRBinding<O> binding, O drawable) {
		Drawing<?> drawing = dtn.getDrawing();
		return drawing.drawShape(parent, binding, drawable);
	}

	public <O> ConnectorNode<O> drawConnector(ConnectorGRBinding<O> binding, O drawable, Object fromDrawable, Object toDrawable) {
		return null;
	}

	public <O, F, T> ConnectorNode<O> drawConnector(ConnectorGRBinding<O> binding, O drawable, ShapeGRBinding<F> fromBinding,
			F fromDrawable, ShapeGRBinding<T> toBinding, T toDrawable) {
		return null;
	}
}
