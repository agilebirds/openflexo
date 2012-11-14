/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class TPi extends Token {
	public TPi() {
		super.setText("pi");
	}

	public TPi(int line, int pos) {
		super.setText("pi");
		setLine(line);
		setPos(pos);
	}

	@Override
	public Object clone() {
		return new TPi(getLine(), getPos());
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseTPi(this);
	}

	@Override
	public void setText(@SuppressWarnings("unused") String text) {
		throw new RuntimeException("Cannot change TPi text.");
	}
}
