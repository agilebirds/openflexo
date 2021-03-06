/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class ATail2Binding extends PBinding {
	private PCall _call_;
	private TDot _dot_;
	private PBinding _binding_;

	public ATail2Binding() {
		// Constructor
	}

	public ATail2Binding(@SuppressWarnings("hiding") PCall _call_, @SuppressWarnings("hiding") TDot _dot_,
			@SuppressWarnings("hiding") PBinding _binding_) {
		// Constructor
		setCall(_call_);

		setDot(_dot_);

		setBinding(_binding_);

	}

	@Override
	public Object clone() {
		return new ATail2Binding(cloneNode(this._call_), cloneNode(this._dot_), cloneNode(this._binding_));
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseATail2Binding(this);
	}

	public PCall getCall() {
		return this._call_;
	}

	public void setCall(PCall node) {
		if (this._call_ != null) {
			this._call_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._call_ = node;
	}

	public TDot getDot() {
		return this._dot_;
	}

	public void setDot(TDot node) {
		if (this._dot_ != null) {
			this._dot_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._dot_ = node;
	}

	public PBinding getBinding() {
		return this._binding_;
	}

	public void setBinding(PBinding node) {
		if (this._binding_ != null) {
			this._binding_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._binding_ = node;
	}

	@Override
	public String toString() {
		return "" + toString(this._call_) + toString(this._dot_) + toString(this._binding_);
	}

	@Override
	void removeChild(@SuppressWarnings("unused") Node child) {
		// Remove child
		if (this._call_ == child) {
			this._call_ = null;
			return;
		}

		if (this._dot_ == child) {
			this._dot_ = null;
			return;
		}

		if (this._binding_ == child) {
			this._binding_ = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}

	@Override
	void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild) {
		// Replace child
		if (this._call_ == oldChild) {
			setCall((PCall) newChild);
			return;
		}

		if (this._dot_ == oldChild) {
			setDot((TDot) newChild);
			return;
		}

		if (this._binding_ == oldChild) {
			setBinding((PBinding) newChild);
			return;
		}

		throw new RuntimeException("Not a child.");
	}
}
