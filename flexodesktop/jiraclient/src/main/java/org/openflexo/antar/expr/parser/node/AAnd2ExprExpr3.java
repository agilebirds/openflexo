/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.*;

@SuppressWarnings("nls")
public final class AAnd2ExprExpr3 extends PExpr3
{
    private PExpr3 _left_;
    private TAnd2 _and2_;
    private PTerm _right_;

    public AAnd2ExprExpr3()
    {
        // Constructor
    }

    public AAnd2ExprExpr3(
        @SuppressWarnings("hiding") PExpr3 _left_,
        @SuppressWarnings("hiding") TAnd2 _and2_,
        @SuppressWarnings("hiding") PTerm _right_)
    {
        // Constructor
        setLeft(_left_);

        setAnd2(_and2_);

        setRight(_right_);

    }

    @Override
    public Object clone()
    {
        return new AAnd2ExprExpr3(
            cloneNode(this._left_),
            cloneNode(this._and2_),
            cloneNode(this._right_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAnd2ExprExpr3(this);
    }

    public PExpr3 getLeft()
    {
        return this._left_;
    }

    public void setLeft(PExpr3 node)
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

    public TAnd2 getAnd2()
    {
        return this._and2_;
    }

    public void setAnd2(TAnd2 node)
    {
        if(this._and2_ != null)
        {
            this._and2_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._and2_ = node;
    }

    public PTerm getRight()
    {
        return this._right_;
    }

    public void setRight(PTerm node)
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
            + toString(this._and2_)
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

        if(this._and2_ == child)
        {
            this._and2_ = null;
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
            setLeft((PExpr3) newChild);
            return;
        }

        if(this._and2_ == oldChild)
        {
            setAnd2((TAnd2) newChild);
            return;
        }

        if(this._right_ == oldChild)
        {
            setRight((PTerm) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
