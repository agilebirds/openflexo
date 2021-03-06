/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class TEq2 extends Token {
	public TEq2() {
		super.setText("==");
	}

	public TEq2(int line, int pos) {
		super.setText("==");
		setLine(line);
		setPos(pos);
	}

	@Override
	public Object clone() {
		return new TEq2(getLine(), getPos());
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseTEq2(this);
	}

	@Override
	public void setText(@SuppressWarnings("unused") String text) {
		throw new RuntimeException("Cannot change TEq2 text.");
	}
}
