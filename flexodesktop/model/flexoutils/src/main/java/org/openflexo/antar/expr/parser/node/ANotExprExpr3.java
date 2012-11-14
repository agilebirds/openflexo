/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class ANotExprExpr3 extends PExpr3 {
	private TNot _not_;
	private PTerm _term_;

	public ANotExprExpr3() {
		// Constructor
	}

	public ANotExprExpr3(@SuppressWarnings("hiding") TNot _not_, @SuppressWarnings("hiding") PTerm _term_) {
		// Constructor
		setNot(_not_);

		setTerm(_term_);

	}

	@Override
	public Object clone() {
		return new ANotExprExpr3(cloneNode(this._not_), cloneNode(this._term_));
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseANotExprExpr3(this);
	}

	public TNot getNot() {
		return this._not_;
	}

	public void setNot(TNot node) {
		if (this._not_ != null) {
			this._not_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._not_ = node;
	}

	public PTerm getTerm() {
		return this._term_;
	}

	public void setTerm(PTerm node) {
		if (this._term_ != null) {
			this._term_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._term_ = node;
	}

	@Override
	public String toString() {
		return "" + toString(this._not_) + toString(this._term_);
	}

	@Override
	void removeChild(@SuppressWarnings("unused") Node child) {
		// Remove child
		if (this._not_ == child) {
			this._not_ = null;
			return;
		}

		if (this._term_ == child) {
			this._term_ = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}

	@Override
	void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild) {
		// Replace child
		if (this._not_ == oldChild) {
			setNot((TNot) newChild);
			return;
		}

		if (this._term_ == oldChild) {
			setTerm((PTerm) newChild);
			return;
		}

		throw new RuntimeException("Not a child.");
	}
}
