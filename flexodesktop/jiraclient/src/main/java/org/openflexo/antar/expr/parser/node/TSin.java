/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.*;

@SuppressWarnings("nls")
public final class TSin extends Token
{
    public TSin()
    {
        super.setText("sin");
    }

    public TSin(int line, int pos)
    {
        super.setText("sin");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TSin(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTSin(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TSin text.");
    }
}
