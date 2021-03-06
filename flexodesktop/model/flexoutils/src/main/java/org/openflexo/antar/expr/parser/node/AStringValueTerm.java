/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class AStringValueTerm extends PTerm {
	private TStringValue _stringValue_;

	public AStringValueTerm() {
		// Constructor
	}

	public AStringValueTerm(@SuppressWarnings("hiding") TStringValue _stringValue_) {
		// Constructor
		setStringValue(_stringValue_);

	}

	@Override
	public Object clone() {
		return new AStringValueTerm(cloneNode(this._stringValue_));
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseAStringValueTerm(this);
	}

	public TStringValue getStringValue() {
		return this._stringValue_;
	}

	public void setStringValue(TStringValue node) {
		if (this._stringValue_ != null) {
			this._stringValue_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._stringValue_ = node;
	}

	@Override
	public String toString() {
		return "" + toString(this._stringValue_);
	}

	@Override
	void removeChild(@SuppressWarnings("unused") Node child) {
		// Remove child
		if (this._stringValue_ == child) {
			this._stringValue_ = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}

	@Override
	void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild) {
		// Replace child
		if (this._stringValue_ == oldChild) {
			setStringValue((TStringValue) newChild);
			return;
		}

		throw new RuntimeException("Not a child.");
	}
}
