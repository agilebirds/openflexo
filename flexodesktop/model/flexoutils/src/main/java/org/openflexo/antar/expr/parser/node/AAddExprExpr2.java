/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.*;

@SuppressWarnings("nls")
public final class AAddExprExpr2 extends PExpr2
{
    private PExpr2 _left_;
    private TPlus _plus_;
    private PExpr3 _right_;

    public AAddExprExpr2()
    {
        // Constructor
    }

    public AAddExprExpr2(
        @SuppressWarnings("hiding") PExpr2 _left_,
        @SuppressWarnings("hiding") TPlus _plus_,
        @SuppressWarnings("hiding") PExpr3 _right_)
    {
        // Constructor
        setLeft(_left_);

        setPlus(_plus_);

        setRight(_right_);

    }

    @Override
    public Object clone()
    {
        return new AAddExprExpr2(
            cloneNode(this._left_),
            cloneNode(this._plus_),
            cloneNode(this._right_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAddExprExpr2(this);
    }

    public PExpr2 getLeft()
    {
        return this._left_;
    }

    public void setLeft(PExpr2 node)
    {
        if(this._left_ != null)
        {
            this._left_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._left_ = node;
    }

    public TPlus getPlus()
    {
        return this._plus_;
    }

    public void setPlus(TPlus node)
    {
        if(this._plus_ != null)
        {
            this._plus_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._plus_ = node;
    }

    public PExpr3 getRight()
    {
        return this._right_;
    }

    public void setRight(PExpr3 node)
    {
        if(this._right_ != null)
        {
            this._right_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._right_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._left_)
            + toString(this._plus_)
            + toString(this._right_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._left_ == child)
        {
            this._left_ = null;
            return;
        }

        if(this._plus_ == child)
        {
            this._plus_ = null;
            return;
        }

        if(this._right_ == child)
        {
            this._right_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._left_ == oldChild)
        {
            setLeft((PExpr2) newChild);
            return;
        }

        if(this._plus_ == oldChild)
        {
            setPlus((TPlus) newChild);
            return;
        }

        if(this._right_ == oldChild)
        {
            setRight((PExpr3) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}