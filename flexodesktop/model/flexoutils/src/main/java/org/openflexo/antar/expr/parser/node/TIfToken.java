/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.Analysis;

@SuppressWarnings("nls")
public final class TIfToken extends Token {
	public TIfToken() {
		super.setText("?");
	}

	public TIfToken(int line, int pos) {
		super.setText("?");
		setLine(line);
		setPos(pos);
	}

	@Override
	public Object clone() {
		return new TIfToken(getLine(), getPos());
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseTIfToken(this);
	}

	@Override
	public void setText(@SuppressWarnings("unused") String text) {
		throw new RuntimeException("Cannot change TIfToken text.");
	}
}
