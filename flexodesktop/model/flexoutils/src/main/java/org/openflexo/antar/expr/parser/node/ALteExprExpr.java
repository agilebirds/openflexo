/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class ALteExprExpr extends PExpr {
	private PExpr _left_;
	private TLte _lte_;
	private PExpr2 _right_;

	public ALteExprExpr() {
		// Constructor
	}

	public ALteExprExpr(@SuppressWarnings("hiding") PExpr _left_, @SuppressWarnings("hiding") TLte _lte_,
			@SuppressWarnings("hiding") PExpr2 _right_) {
		// Constructor
		setLeft(_left_);

		setLte(_lte_);

		setRight(_right_);

	}

	@Override
	public Object clone() {
		return new ALteExprExpr(cloneNode(this._left_), cloneNode(this._lte_), cloneNode(this._right_));
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseALteExprExpr(this);
	}

	public PExpr getLeft() {
		return this._left_;
	}

	public void setLeft(PExpr node) {
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

	public TLte getLte() {
		return this._lte_;
	}

	public void setLte(TLte node) {
		if (this._lte_ != null) {
			this._lte_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._lte_ = node;
	}

	public PExpr2 getRight() {
		return this._right_;
	}

	public void setRight(PExpr2 node) {
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
		return "" + toString(this._left_) + toString(this._lte_) + toString(this._right_);
	}

	@Override
	void removeChild(@SuppressWarnings("unused") Node child) {
		// Remove child
		if (this._left_ == child) {
			this._left_ = null;
			return;
		}

		if (this._lte_ == child) {
			this._lte_ = null;
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
			setLeft((PExpr) newChild);
			return;
		}

		if (this._lte_ == oldChild) {
			setLte((TLte) newChild);
			return;
		}

		if (this._right_ == oldChild) {
			setRight((PExpr2) newChild);
			return;
		}

		throw new RuntimeException("Not a child.");
	}
}
