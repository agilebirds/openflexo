/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.*;

@SuppressWarnings("nls")
public final class AConstantNumber extends PNumber
{
    private PConstant _constant_;

    public AConstantNumber()
    {
        // Constructor
    }

    public AConstantNumber(
        @SuppressWarnings("hiding") PConstant _constant_)
    {
        // Constructor
        setConstant(_constant_);

    }

    @Override
    public Object clone()
    {
        return new AConstantNumber(
            cloneNode(this._constant_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAConstantNumber(this);
    }

    public PConstant getConstant()
    {
        return this._constant_;
    }

    public void setConstant(PConstant node)
    {
        if(this._constant_ != null)
        {
            this._constant_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._constant_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._constant_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._constant_ == child)
        {
            this._constant_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._constant_ == oldChild)
        {
            setConstant((PConstant) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
