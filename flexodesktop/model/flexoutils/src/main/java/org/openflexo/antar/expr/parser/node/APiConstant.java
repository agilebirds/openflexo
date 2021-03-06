/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class APiConstant extends PConstant {
	private TPi _pi_;

	public APiConstant() {
		// Constructor
	}

	public APiConstant(@SuppressWarnings("hiding") TPi _pi_) {
		// Constructor
		setPi(_pi_);

	}

	@Override
	public Object clone() {
		return new APiConstant(cloneNode(this._pi_));
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseAPiConstant(this);
	}

	public TPi getPi() {
		return this._pi_;
	}

	public void setPi(TPi node) {
		if (this._pi_ != null) {
			this._pi_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._pi_ = node;
	}

	@Override
	public String toString() {
		return "" + toString(this._pi_);
	}

	@Override
	void removeChild(@SuppressWarnings("unused") Node child) {
		// Remove child
		if (this._pi_ == child) {
			this._pi_ = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}

	@Override
	void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild) {
		// Replace child
		if (this._pi_ == oldChild) {
			setPi((TPi) newChild);
			return;
		}

		throw new RuntimeException("Not a child.");
	}
}
