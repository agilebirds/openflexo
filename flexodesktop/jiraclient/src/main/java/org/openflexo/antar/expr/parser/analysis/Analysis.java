/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.openflexo.antar.expr.parser.analysis;

import org.openflexo.antar.expr.parser.node.*;

public interface Analysis extends Switch
{
    Object getIn(Node node);
    void setIn(Node node, Object o);
    Object getOut(Node node);
    void setOut(Node node, Object o);

    void caseStart(Start node);
    void caseAExpr2Expr(AExpr2Expr node);
    void caseAEqExprExpr(AEqExprExpr node);
    void caseAEq2ExprExpr(AEq2ExprExpr node);
    void caseANeqExprExpr(ANeqExprExpr node);
    void caseALtExprExpr(ALtExprExpr node);
    void caseAGtExprExpr(AGtExprExpr node);
    void caseALteExprExpr(ALteExprExpr node);
    void caseAGteExprExpr(AGteExprExpr node);
    void caseAExpr3Expr2(AExpr3Expr2 node);
    void caseAOrExprExpr2(AOrExprExpr2 node);
    void caseAOr2ExprExpr2(AOr2ExprExpr2 node);
    void caseAPlusExpr2(APlusExpr2 node);
    void caseAMinusExpr2(AMinusExpr2 node);
    void caseATermExpr3(ATermExpr3 node);
    void caseAAndExprExpr3(AAndExprExpr3 node);
    void caseAAnd2ExprExpr3(AAnd2ExprExpr3 node);
    void caseAMultExpr3(AMultExpr3 node);
    void caseADivExpr3(ADivExpr3 node);
    void caseAModExpr3(AModExpr3 node);
    void caseANotExpr3(ANotExpr3 node);
    void caseACall(ACall node);
    void caseAArgList(AArgList node);
    void caseAAdditionalArg(AAdditionalArg node);
    void caseAIdentifierBinding(AIdentifierBinding node);
    void caseACallBinding(ACallBinding node);
    void caseATailBinding(ATailBinding node);
    void caseACosFunction(ACosFunction node);
    void caseASinFunction(ASinFunction node);
    void caseATanFunction(ATanFunction node);
    void caseAAtanFunction(AAtanFunction node);
    void caseATrueConstant(ATrueConstant node);
    void caseAFalseConstant(AFalseConstant node);
    void caseAPiConstant(APiConstant node);
    void caseADecimalNumberNumber(ADecimalNumberNumber node);
    void caseAPreciseNumberNumber(APreciseNumberNumber node);
    void caseAScientificNotationNumberNumber(AScientificNotationNumberNumber node);
    void caseAConstantNumber(AConstantNumber node);
    void caseANegativeTerm(ANegativeTerm node);
    void caseANumberTerm(ANumberTerm node);
    void caseAStringValueTerm(AStringValueTerm node);
    void caseACharsValueTerm(ACharsValueTerm node);
    void caseAFunctionTerm(AFunctionTerm node);
    void caseABindingTerm(ABindingTerm node);
    void caseAExprTerm(AExprTerm node);

    void caseTPlus(TPlus node);
    void caseTMinus(TMinus node);
    void caseTMult(TMult node);
    void caseTDiv(TDiv node);
    void caseTMod(TMod node);
    void caseTLt(TLt node);
    void caseTGt(TGt node);
    void caseTLte(TLte node);
    void caseTGte(TGte node);
    void caseTAnd(TAnd node);
    void caseTAnd2(TAnd2 node);
    void caseTOr(TOr node);
    void caseTOr2(TOr2 node);
    void caseTEq(TEq node);
    void caseTEq2(TEq2 node);
    void caseTNeq(TNeq node);
    void caseTNot(TNot node);
    void caseTLPar(TLPar node);
    void caseTRPar(TRPar node);
    void caseTDot(TDot node);
    void caseTComma(TComma node);
    void caseTPi(TPi node);
    void caseTCos(TCos node);
    void caseTSin(TSin node);
    void caseTTan(TTan node);
    void caseTAtan(TAtan node);
    void caseTTrue(TTrue node);
    void caseTFalse(TFalse node);
    void caseTDecimalNumber(TDecimalNumber node);
    void caseTPreciseNumber(TPreciseNumber node);
    void caseTScientificNotationNumber(TScientificNotationNumber node);
    void caseTStringValue(TStringValue node);
    void caseTCharsValue(TCharsValue node);
    void caseTIdentifier(TIdentifier node);
    void caseTBlank(TBlank node);
    void caseEOF(EOF node);
}
