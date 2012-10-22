/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.*;

@SuppressWarnings("nls")
public final class ADecimalNumberNumber extends PNumber
{
    private TDecimalNumber _decimalNumber_;

    public ADecimalNumberNumber()
    {
        // Constructor
    }

    public ADecimalNumberNumber(
        @SuppressWarnings("hiding") TDecimalNumber _decimalNumber_)
    {
        // Constructor
        setDecimalNumber(_decimalNumber_);

    }

    @Override
    public Object clone()
    {
        return new ADecimalNumberNumber(
            cloneNode(this._decimalNumber_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADecimalNumberNumber(this);
    }

    public TDecimalNumber getDecimalNumber()
    {
        return this._decimalNumber_;
    }

    public void setDecimalNumber(TDecimalNumber node)
    {
        if(this._decimalNumber_ != null)
        {
            this._decimalNumber_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._decimalNumber_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._decimalNumber_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._decimalNumber_ == child)
        {
            this._decimalNumber_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._decimalNumber_ == oldChild)
        {
            setDecimalNumber((TDecimalNumber) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
