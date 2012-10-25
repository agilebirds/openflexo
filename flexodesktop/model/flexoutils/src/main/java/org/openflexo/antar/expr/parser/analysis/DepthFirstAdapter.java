/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.analysis;

import java.util.*;
import org.openflexo.antar.expr.parser.node.*;

public class DepthFirstAdapter extends AnalysisAdapter
{
    public void inStart(Start node)
    {
        defaultIn(node);
    }

    public void outStart(Start node)
    {
        defaultOut(node);
    }

    public void defaultIn(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    public void defaultOut(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    @Override
    public void caseStart(Start node)
    {
        inStart(node);
        node.getPExpr().apply(this);
        node.getEOF().apply(this);
        outStart(node);
    }

    public void inAExpr2Expr(AExpr2Expr node)
    {
        defaultIn(node);
    }

    public void outAExpr2Expr(AExpr2Expr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAExpr2Expr(AExpr2Expr node)
    {
        inAExpr2Expr(node);
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        outAExpr2Expr(node);
    }

    public void inACondExprExpr(ACondExprExpr node)
    {
        defaultIn(node);
    }

    public void outACondExprExpr(ACondExprExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseACondExprExpr(ACondExprExpr node)
    {
        inACondExprExpr(node);
        if(node.getCondition() != null)
        {
            node.getCondition().apply(this);
        }
        if(node.getIfToken() != null)
        {
            node.getIfToken().apply(this);
        }
        if(node.getThen() != null)
        {
            node.getThen().apply(this);
        }
        if(node.getElseToken() != null)
        {
            node.getElseToken().apply(this);
        }
        if(node.getElse() != null)
        {
            node.getElse().apply(this);
        }
        outACondExprExpr(node);
    }

    public void inAEqExprExpr(AEqExprExpr node)
    {
        defaultIn(node);
    }

    public void outAEqExprExpr(AEqExprExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAEqExprExpr(AEqExprExpr node)
    {
        inAEqExprExpr(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getEq() != null)
        {
            node.getEq().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAEqExprExpr(node);
    }

    public void inAEq2ExprExpr(AEq2ExprExpr node)
    {
        defaultIn(node);
    }

    public void outAEq2ExprExpr(AEq2ExprExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAEq2ExprExpr(AEq2ExprExpr node)
    {
        inAEq2ExprExpr(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getEq2() != null)
        {
            node.getEq2().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAEq2ExprExpr(node);
    }

    public void inANeqExprExpr(ANeqExprExpr node)
    {
        defaultIn(node);
    }

    public void outANeqExprExpr(ANeqExprExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANeqExprExpr(ANeqExprExpr node)
    {
        inANeqExprExpr(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getNeq() != null)
        {
            node.getNeq().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outANeqExprExpr(node);
    }

    public void inALtExprExpr(ALtExprExpr node)
    {
        defaultIn(node);
    }

    public void outALtExprExpr(ALtExprExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseALtExprExpr(ALtExprExpr node)
    {
        inALtExprExpr(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getLt() != null)
        {
            node.getLt().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outALtExprExpr(node);
    }

    public void inAGtExprExpr(AGtExprExpr node)
    {
        defaultIn(node);
    }

    public void outAGtExprExpr(AGtExprExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAGtExprExpr(AGtExprExpr node)
    {
        inAGtExprExpr(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getGt() != null)
        {
            node.getGt().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAGtExprExpr(node);
    }

    public void inALteExprExpr(ALteExprExpr node)
    {
        defaultIn(node);
    }

    public void outALteExprExpr(ALteExprExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseALteExprExpr(ALteExprExpr node)
    {
        inALteExprExpr(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getLte() != null)
        {
            node.getLte().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outALteExprExpr(node);
    }

    public void inAGteExprExpr(AGteExprExpr node)
    {
        defaultIn(node);
    }

    public void outAGteExprExpr(AGteExprExpr node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAGteExprExpr(AGteExprExpr node)
    {
        inAGteExprExpr(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getGte() != null)
        {
            node.getGte().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAGteExprExpr(node);
    }

    public void inAExpr3Expr2(AExpr3Expr2 node)
    {
        defaultIn(node);
    }

    public void outAExpr3Expr2(AExpr3Expr2 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAExpr3Expr2(AExpr3Expr2 node)
    {
        inAExpr3Expr2(node);
        if(node.getExpr3() != null)
        {
            node.getExpr3().apply(this);
        }
        outAExpr3Expr2(node);
    }

    public void inAOrExprExpr2(AOrExprExpr2 node)
    {
        defaultIn(node);
    }

    public void outAOrExprExpr2(AOrExprExpr2 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAOrExprExpr2(AOrExprExpr2 node)
    {
        inAOrExprExpr2(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getOr() != null)
        {
            node.getOr().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAOrExprExpr2(node);
    }

    public void inAOr2ExprExpr2(AOr2ExprExpr2 node)
    {
        defaultIn(node);
    }

    public void outAOr2ExprExpr2(AOr2ExprExpr2 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAOr2ExprExpr2(AOr2ExprExpr2 node)
    {
        inAOr2ExprExpr2(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getOr2() != null)
        {
            node.getOr2().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAOr2ExprExpr2(node);
    }

    public void inAAddExprExpr2(AAddExprExpr2 node)
    {
        defaultIn(node);
    }

    public void outAAddExprExpr2(AAddExprExpr2 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAddExprExpr2(AAddExprExpr2 node)
    {
        inAAddExprExpr2(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getPlus() != null)
        {
            node.getPlus().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAAddExprExpr2(node);
    }

    public void inASubExprExpr2(ASubExprExpr2 node)
    {
        defaultIn(node);
    }

    public void outASubExprExpr2(ASubExprExpr2 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseASubExprExpr2(ASubExprExpr2 node)
    {
        inASubExprExpr2(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getMinus() != null)
        {
            node.getMinus().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outASubExprExpr2(node);
    }

    public void inATermExpr3(ATermExpr3 node)
    {
        defaultIn(node);
    }

    public void outATermExpr3(ATermExpr3 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseATermExpr3(ATermExpr3 node)
    {
        inATermExpr3(node);
        if(node.getTerm() != null)
        {
            node.getTerm().apply(this);
        }
        outATermExpr3(node);
    }

    public void inAAndExprExpr3(AAndExprExpr3 node)
    {
        defaultIn(node);
    }

    public void outAAndExprExpr3(AAndExprExpr3 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAndExprExpr3(AAndExprExpr3 node)
    {
        inAAndExprExpr3(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getAnd() != null)
        {
            node.getAnd().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAAndExprExpr3(node);
    }

    public void inAAnd2ExprExpr3(AAnd2ExprExpr3 node)
    {
        defaultIn(node);
    }

    public void outAAnd2ExprExpr3(AAnd2ExprExpr3 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAnd2ExprExpr3(AAnd2ExprExpr3 node)
    {
        inAAnd2ExprExpr3(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getAnd2() != null)
        {
            node.getAnd2().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAAnd2ExprExpr3(node);
    }

    public void inAMultExprExpr3(AMultExprExpr3 node)
    {
        defaultIn(node);
    }

    public void outAMultExprExpr3(AMultExprExpr3 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAMultExprExpr3(AMultExprExpr3 node)
    {
        inAMultExprExpr3(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getMult() != null)
        {
            node.getMult().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAMultExprExpr3(node);
    }

    public void inADivExprExpr3(ADivExprExpr3 node)
    {
        defaultIn(node);
    }

    public void outADivExprExpr3(ADivExprExpr3 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseADivExprExpr3(ADivExprExpr3 node)
    {
        inADivExprExpr3(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getDiv() != null)
        {
            node.getDiv().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outADivExprExpr3(node);
    }

    public void inAModExprExpr3(AModExprExpr3 node)
    {
        defaultIn(node);
    }

    public void outAModExprExpr3(AModExprExpr3 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAModExprExpr3(AModExprExpr3 node)
    {
        inAModExprExpr3(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getMod() != null)
        {
            node.getMod().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAModExprExpr3(node);
    }

    public void inAPowerExprExpr3(APowerExprExpr3 node)
    {
        defaultIn(node);
    }

    public void outAPowerExprExpr3(APowerExprExpr3 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPowerExprExpr3(APowerExprExpr3 node)
    {
        inAPowerExprExpr3(node);
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        if(node.getPower() != null)
        {
            node.getPower().apply(this);
        }
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
        outAPowerExprExpr3(node);
    }

    public void inANotExprExpr3(ANotExprExpr3 node)
    {
        defaultIn(node);
    }

    public void outANotExprExpr3(ANotExprExpr3 node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANotExprExpr3(ANotExprExpr3 node)
    {
        inANotExprExpr3(node);
        if(node.getNot() != null)
        {
            node.getNot().apply(this);
        }
        if(node.getTerm() != null)
        {
            node.getTerm().apply(this);
        }
        outANotExprExpr3(node);
    }

    public void inACall(ACall node)
    {
        defaultIn(node);
    }

    public void outACall(ACall node)
    {
        defaultOut(node);
    }

    @Override
    public void caseACall(ACall node)
    {
        inACall(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getArgList() != null)
        {
            node.getArgList().apply(this);
        }
        outACall(node);
    }

    public void inAEmptyListArgList(AEmptyListArgList node)
    {
        defaultIn(node);
    }

    public void outAEmptyListArgList(AEmptyListArgList node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAEmptyListArgList(AEmptyListArgList node)
    {
        inAEmptyListArgList(node);
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outAEmptyListArgList(node);
    }

    public void inANonEmptyListArgList(ANonEmptyListArgList node)
    {
        defaultIn(node);
    }

    public void outANonEmptyListArgList(ANonEmptyListArgList node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANonEmptyListArgList(ANonEmptyListArgList node)
    {
        inANonEmptyListArgList(node);
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
        {
            List<PAdditionalArg> copy = new ArrayList<PAdditionalArg>(node.getAdditionalArgs());
            for(PAdditionalArg e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outANonEmptyListArgList(node);
    }

    public void inAAdditionalArg(AAdditionalArg node)
    {
        defaultIn(node);
    }

    public void outAAdditionalArg(AAdditionalArg node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAdditionalArg(AAdditionalArg node)
    {
        inAAdditionalArg(node);
        if(node.getComma() != null)
        {
            node.getComma().apply(this);
        }
        if(node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
        outAAdditionalArg(node);
    }

    public void inAIdentifierBinding(AIdentifierBinding node)
    {
        defaultIn(node);
    }

    public void outAIdentifierBinding(AIdentifierBinding node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIdentifierBinding(AIdentifierBinding node)
    {
        inAIdentifierBinding(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAIdentifierBinding(node);
    }

    public void inACallBinding(ACallBinding node)
    {
        defaultIn(node);
    }

    public void outACallBinding(ACallBinding node)
    {
        defaultOut(node);
    }

    @Override
    public void caseACallBinding(ACallBinding node)
    {
        inACallBinding(node);
        if(node.getCall() != null)
        {
            node.getCall().apply(this);
        }
        outACallBinding(node);
    }

    public void inATail1Binding(ATail1Binding node)
    {
        defaultIn(node);
    }

    public void outATail1Binding(ATail1Binding node)
    {
        defaultOut(node);
    }

    @Override
    public void caseATail1Binding(ATail1Binding node)
    {
        inATail1Binding(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getDot() != null)
        {
            node.getDot().apply(this);
        }
        if(node.getBinding() != null)
        {
            node.getBinding().apply(this);
        }
        outATail1Binding(node);
    }

    public void inATail2Binding(ATail2Binding node)
    {
        defaultIn(node);
    }

    public void outATail2Binding(ATail2Binding node)
    {
        defaultOut(node);
    }

    @Override
    public void caseATail2Binding(ATail2Binding node)
    {
        inATail2Binding(node);
        if(node.getCall() != null)
        {
            node.getCall().apply(this);
        }
        if(node.getDot() != null)
        {
            node.getDot().apply(this);
        }
        if(node.getBinding() != null)
        {
            node.getBinding().apply(this);
        }
        outATail2Binding(node);
    }

    public void inACosFuncFunction(ACosFuncFunction node)
    {
        defaultIn(node);
    }

    public void outACosFuncFunction(ACosFuncFunction node)
    {
        defaultOut(node);
    }

    @Override
    public void caseACosFuncFunction(ACosFuncFunction node)
    {
        inACosFuncFunction(node);
        if(node.getCos() != null)
        {
            node.getCos().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outACosFuncFunction(node);
    }

    public void inAAcosFuncFunction(AAcosFuncFunction node)
    {
        defaultIn(node);
    }

    public void outAAcosFuncFunction(AAcosFuncFunction node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAcosFuncFunction(AAcosFuncFunction node)
    {
        inAAcosFuncFunction(node);
        if(node.getAcos() != null)
        {
            node.getAcos().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outAAcosFuncFunction(node);
    }

    public void inASinFuncFunction(ASinFuncFunction node)
    {
        defaultIn(node);
    }

    public void outASinFuncFunction(ASinFuncFunction node)
    {
        defaultOut(node);
    }

    @Override
    public void caseASinFuncFunction(ASinFuncFunction node)
    {
        inASinFuncFunction(node);
        if(node.getSin() != null)
        {
            node.getSin().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outASinFuncFunction(node);
    }

    public void inAAsinFuncFunction(AAsinFuncFunction node)
    {
        defaultIn(node);
    }

    public void outAAsinFuncFunction(AAsinFuncFunction node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAsinFuncFunction(AAsinFuncFunction node)
    {
        inAAsinFuncFunction(node);
        if(node.getAsin() != null)
        {
            node.getAsin().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outAAsinFuncFunction(node);
    }

    public void inATanFuncFunction(ATanFuncFunction node)
    {
        defaultIn(node);
    }

    public void outATanFuncFunction(ATanFuncFunction node)
    {
        defaultOut(node);
    }

    @Override
    public void caseATanFuncFunction(ATanFuncFunction node)
    {
        inATanFuncFunction(node);
        if(node.getTan() != null)
        {
            node.getTan().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outATanFuncFunction(node);
    }

    public void inAAtanFuncFunction(AAtanFuncFunction node)
    {
        defaultIn(node);
    }

    public void outAAtanFuncFunction(AAtanFuncFunction node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAtanFuncFunction(AAtanFuncFunction node)
    {
        inAAtanFuncFunction(node);
        if(node.getAtan() != null)
        {
            node.getAtan().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outAAtanFuncFunction(node);
    }

    public void inAExpFuncFunction(AExpFuncFunction node)
    {
        defaultIn(node);
    }

    public void outAExpFuncFunction(AExpFuncFunction node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAExpFuncFunction(AExpFuncFunction node)
    {
        inAExpFuncFunction(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outAExpFuncFunction(node);
    }

    public void inALogFuncFunction(ALogFuncFunction node)
    {
        defaultIn(node);
    }

    public void outALogFuncFunction(ALogFuncFunction node)
    {
        defaultOut(node);
    }

    @Override
    public void caseALogFuncFunction(ALogFuncFunction node)
    {
        inALogFuncFunction(node);
        if(node.getLog() != null)
        {
            node.getLog().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outALogFuncFunction(node);
    }

    public void inASqrtFuncFunction(ASqrtFuncFunction node)
    {
        defaultIn(node);
    }

    public void outASqrtFuncFunction(ASqrtFuncFunction node)
    {
        defaultOut(node);
    }

    @Override
    public void caseASqrtFuncFunction(ASqrtFuncFunction node)
    {
        inASqrtFuncFunction(node);
        if(node.getSqrt() != null)
        {
            node.getSqrt().apply(this);
        }
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr2() != null)
        {
            node.getExpr2().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outASqrtFuncFunction(node);
    }

    public void inATrueConstant(ATrueConstant node)
    {
        defaultIn(node);
    }

    public void outATrueConstant(ATrueConstant node)
    {
        defaultOut(node);
    }

    @Override
    public void caseATrueConstant(ATrueConstant node)
    {
        inATrueConstant(node);
        if(node.getTrue() != null)
        {
            node.getTrue().apply(this);
        }
        outATrueConstant(node);
    }

    public void inAFalseConstant(AFalseConstant node)
    {
        defaultIn(node);
    }

    public void outAFalseConstant(AFalseConstant node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAFalseConstant(AFalseConstant node)
    {
        inAFalseConstant(node);
        if(node.getFalse() != null)
        {
            node.getFalse().apply(this);
        }
        outAFalseConstant(node);
    }

    public void inAPiConstant(APiConstant node)
    {
        defaultIn(node);
    }

    public void outAPiConstant(APiConstant node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPiConstant(APiConstant node)
    {
        inAPiConstant(node);
        if(node.getPi() != null)
        {
            node.getPi().apply(this);
        }
        outAPiConstant(node);
    }

    public void inADecimalNumberNumber(ADecimalNumberNumber node)
    {
        defaultIn(node);
    }

    public void outADecimalNumberNumber(ADecimalNumberNumber node)
    {
        defaultOut(node);
    }

    @Override
    public void caseADecimalNumberNumber(ADecimalNumberNumber node)
    {
        inADecimalNumberNumber(node);
        if(node.getDecimalNumber() != null)
        {
            node.getDecimalNumber().apply(this);
        }
        outADecimalNumberNumber(node);
    }

    public void inAPreciseNumberNumber(APreciseNumberNumber node)
    {
        defaultIn(node);
    }

    public void outAPreciseNumberNumber(APreciseNumberNumber node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPreciseNumberNumber(APreciseNumberNumber node)
    {
        inAPreciseNumberNumber(node);
        if(node.getPreciseNumber() != null)
        {
            node.getPreciseNumber().apply(this);
        }
        outAPreciseNumberNumber(node);
    }

    public void inAScientificNotationNumberNumber(AScientificNotationNumberNumber node)
    {
        defaultIn(node);
    }

    public void outAScientificNotationNumberNumber(AScientificNotationNumberNumber node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAScientificNotationNumberNumber(AScientificNotationNumberNumber node)
    {
        inAScientificNotationNumberNumber(node);
        if(node.getScientificNotationNumber() != null)
        {
            node.getScientificNotationNumber().apply(this);
        }
        outAScientificNotationNumberNumber(node);
    }

    public void inAConstantNumber(AConstantNumber node)
    {
        defaultIn(node);
    }

    public void outAConstantNumber(AConstantNumber node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAConstantNumber(AConstantNumber node)
    {
        inAConstantNumber(node);
        if(node.getConstant() != null)
        {
            node.getConstant().apply(this);
        }
        outAConstantNumber(node);
    }

    public void inANegativeTerm(ANegativeTerm node)
    {
        defaultIn(node);
    }

    public void outANegativeTerm(ANegativeTerm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANegativeTerm(ANegativeTerm node)
    {
        inANegativeTerm(node);
        if(node.getMinus() != null)
        {
            node.getMinus().apply(this);
        }
        if(node.getTerm() != null)
        {
            node.getTerm().apply(this);
        }
        outANegativeTerm(node);
    }

    public void inANumberTerm(ANumberTerm node)
    {
        defaultIn(node);
    }

    public void outANumberTerm(ANumberTerm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANumberTerm(ANumberTerm node)
    {
        inANumberTerm(node);
        if(node.getNumber() != null)
        {
            node.getNumber().apply(this);
        }
        outANumberTerm(node);
    }

    public void inAStringValueTerm(AStringValueTerm node)
    {
        defaultIn(node);
    }

    public void outAStringValueTerm(AStringValueTerm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAStringValueTerm(AStringValueTerm node)
    {
        inAStringValueTerm(node);
        if(node.getStringValue() != null)
        {
            node.getStringValue().apply(this);
        }
        outAStringValueTerm(node);
    }

    public void inACharsValueTerm(ACharsValueTerm node)
    {
        defaultIn(node);
    }

    public void outACharsValueTerm(ACharsValueTerm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseACharsValueTerm(ACharsValueTerm node)
    {
        inACharsValueTerm(node);
        if(node.getCharsValue() != null)
        {
            node.getCharsValue().apply(this);
        }
        outACharsValueTerm(node);
    }

    public void inAFunctionTerm(AFunctionTerm node)
    {
        defaultIn(node);
    }

    public void outAFunctionTerm(AFunctionTerm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAFunctionTerm(AFunctionTerm node)
    {
        inAFunctionTerm(node);
        if(node.getFunction() != null)
        {
            node.getFunction().apply(this);
        }
        outAFunctionTerm(node);
    }

    public void inABindingTerm(ABindingTerm node)
    {
        defaultIn(node);
    }

    public void outABindingTerm(ABindingTerm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseABindingTerm(ABindingTerm node)
    {
        inABindingTerm(node);
        if(node.getBinding() != null)
        {
            node.getBinding().apply(this);
        }
        outABindingTerm(node);
    }

    public void inAExprTerm(AExprTerm node)
    {
        defaultIn(node);
    }

    public void outAExprTerm(AExprTerm node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAExprTerm(AExprTerm node)
    {
        inAExprTerm(node);
        if(node.getLPar() != null)
        {
            node.getLPar().apply(this);
        }
        if(node.getExpr() != null)
        {
            node.getExpr().apply(this);
        }
        if(node.getRPar() != null)
        {
            node.getRPar().apply(this);
        }
        outAExprTerm(node);
    }
}
