/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.node;

import org.openflexo.antar.expr.parser.analysis.*;

@SuppressWarnings("nls")
public final class AGteExprExpr extends PExpr
{
    private PExpr _left_;
    private TGte _gte_;
    private PExpr2 _right_;

    public AGteExprExpr()
    {
        // Constructor
    }

    public AGteExprExpr(
        @SuppressWarnings("hiding") PExpr _left_,
        @SuppressWarnings("hiding") TGte _gte_,
        @SuppressWarnings("hiding") PExpr2 _right_)
    {
        // Constructor
        setLeft(_left_);

        setGte(_gte_);

        setRight(_right_);

    }

    @Override
    public Object clone()
    {
        return new AGteExprExpr(
            cloneNode(this._left_),
            cloneNode(this._gte_),
            cloneNode(this._right_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAGteExprExpr(this);
    }

    public PExpr getLeft()
    {
        return this._left_;
    }

    public void setLeft(PExpr node)
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

    public TGte getGte()
    {
        return this._gte_;
    }

    public void setGte(TGte node)
    {
        if(this._gte_ != null)
        {
            this._gte_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._gte_ = node;
    }

    public PExpr2 getRight()
    {
        return this._right_;
    }

    public void setRight(PExpr2 node)
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
            + toString(this._gte_)
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

        if(this._gte_ == child)
        {
            this._gte_ = null;
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
            setLeft((PExpr) newChild);
            return;
        }

        if(this._gte_ == oldChild)
        {
            setGte((TGte) newChild);
            return;
        }

        if(this._right_ == oldChild)
        {
            setRight((PExpr2) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
