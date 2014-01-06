/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.*;

@SuppressWarnings("nls")
public final class TExp extends Token {
	public TExp() {
		super.setText("exp");
	}

	public TExp(int line, int pos) {
		super.setText("exp");
		setLine(line);
		setPos(pos);
	}

	@Override
	public Object clone() {
		return new TExp(getLine(), getPos());
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseTExp(this);
	}

	@Override
	public void setText(@SuppressWarnings("unused") String text) {
		throw new RuntimeException("Cannot change TExp text.");
	}
}
