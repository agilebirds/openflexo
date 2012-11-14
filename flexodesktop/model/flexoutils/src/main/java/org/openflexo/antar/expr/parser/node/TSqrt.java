/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class TSqrt extends Token {
	public TSqrt() {
		super.setText("sqrt");
	}

	public TSqrt(int line, int pos) {
		super.setText("sqrt");
		setLine(line);
		setPos(pos);
	}

	@Override
	public Object clone() {
		return new TSqrt(getLine(), getPos());
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseTSqrt(this);
	}

	@Override
	public void setText(@SuppressWarnings("unused") String text) {
		throw new RuntimeException("Cannot change TSqrt text.");
	}
}