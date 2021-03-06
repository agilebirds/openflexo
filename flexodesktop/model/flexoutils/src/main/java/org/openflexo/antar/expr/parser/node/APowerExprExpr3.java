/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class APowerExprExpr3 extends PExpr3 {
	private PExpr3 _left_;
	private TPower _power_;
	private PTerm _right_;

	public APowerExprExpr3() {
		// Constructor
	}

	public APowerExprExpr3(@SuppressWarnings("hiding") PExpr3 _left_, @SuppressWarnings("hiding") TPower _power_,
			@SuppressWarnings("hiding") PTerm _right_) {
		// Constructor
		setLeft(_left_);

		setPower(_power_);

		setRight(_right_);

	}

	@Override
	public Object clone() {
		return new APowerExprExpr3(cloneNode(this._left_), cloneNode(this._power_), cloneNode(this._right_));
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseAPowerExprExpr3(this);
	}

	public PExpr3 getLeft() {
		return this._left_;
	}

	public void setLeft(PExpr3 node) {
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

	public TPower getPower() {
		return this._power_;
	}

	public void setPower(TPower node) {
		if (this._power_ != null) {
			this._power_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._power_ = node;
	}

	public PTerm getRight() {
		return this._right_;
	}

	public void setRight(PTerm node) {
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
		return "" + toString(this._left_) + toString(this._power_) + toString(this._right_);
	}

	@Override
	void removeChild(@SuppressWarnings("unused") Node child) {
		// Remove child
		if (this._left_ == child) {
			this._left_ = null;
			return;
		}

		if (this._power_ == child) {
			this._power_ = null;
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
			setLeft((PExpr3) newChild);
			return;
		}

		if (this._power_ == oldChild) {
			setPower((TPower) newChild);
			return;
		}

		if (this._right_ == oldChild) {
			setRight((PTerm) newChild);
			return;
		}

		throw new RuntimeException("Not a child.");
	}
}
