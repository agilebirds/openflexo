/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class AOr2ExprExpr2 extends PExpr2 {
	private PExpr2 _left_;
	private TOr2 _or2_;
	private PExpr3 _right_;

	public AOr2ExprExpr2() {
		// Constructor
	}

	public AOr2ExprExpr2(@SuppressWarnings("hiding") PExpr2 _left_, @SuppressWarnings("hiding") TOr2 _or2_,
			@SuppressWarnings("hiding") PExpr3 _right_) {
		// Constructor
		setLeft(_left_);

		setOr2(_or2_);

		setRight(_right_);

	}

	@Override
	public Object clone() {
		return new AOr2ExprExpr2(cloneNode(this._left_), cloneNode(this._or2_), cloneNode(this._right_));
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseAOr2ExprExpr2(this);
	}

	public PExpr2 getLeft() {
		return this._left_;
	}

	public void setLeft(PExpr2 node) {
		if (this._left_ != null) {
			this._left_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._left_ = node;
	}

	public TOr2 getOr2() {
		return this._or2_;
	}

	public void setOr2(TOr2 node) {
		if (this._or2_ != null) {
			this._or2_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._or2_ = node;
	}

	public PExpr3 getRight() {
		return this._right_;
	}

	public void setRight(PExpr3 node) {
		if (this._right_ != null) {
			this._right_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._right_ = node;
	}

	@Override
	public String toString() {
		return "" + toString(this._left_) + toString(this._or2_) + toString(this._right_);
	}

	@Override
	void removeChild(@SuppressWarnings("unused") Node child) {
		// Remove child
		if (this._left_ == child) {
			this._left_ = null;
			return;
		}

		if (this._or2_ == child) {
			this._or2_ = null;
			return;
		}

		if (this._right_ == child) {
			this._right_ = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}

	@Override
	void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild) {
		// Replace child
		if (this._left_ == oldChild) {
			setLeft((PExpr2) newChild);
			return;
		}

		if (this._or2_ == oldChild) {
			setOr2((TOr2) newChild);
			return;
		}

		if (this._right_ == oldChild) {
			setRight((PExpr3) newChild);
			return;
		}

		throw new RuntimeException("Not a child.");
	}
}
