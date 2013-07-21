package org.openflexo.fge;

import java.util.List;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
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

	public <O> ShapeNode<O> drawShape(ShapeGRBinding<O> binding, O drawable, Object parentDrawable) {
		return null;
	}

	public <O, P> ShapeNode<O> drawShape(ShapeGRBinding<O> binding, O drawable, ShapeGRBinding<P> parentBinding, P parentDrawable) {
		return null;
	}

	public <O, P> ShapeNode<O> drawShape(ShapeGRBinding<O> binding, O drawable, DrawingGRBinding<P> parentBinding, P parentDrawable) {
		return null;
	}

	public <O> ShapeNode<O> drawShape(ContainerNode<O, ?> parent, ShapeGRBinding<O> binding, O drawable) {
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
