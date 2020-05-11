package br.ifmath.compiler.domain.expertsystem.polynomial.multiplication;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialMultiplicationRuleDistributive implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return isThereADistributiveCase(sources);
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {
        source = sources.get(0);
        ExpandedQuadruple distributiveQuadruple = this.source.findQuadrupleByResult(this.source.getLeft());

        String reason = this.applyDistributive(distributiveQuadruple);
        this.fixingQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        List<Step> steps = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), reason));

        return steps;
    }

    private void fixingQuadruples() {
        this.source.clearNonUsedQuadruples();
        this.source.removeQuadruplesParentheses(true);
    }

    private void adjustMinusSigns() {
        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {
            if (!expandedQuadruple.getResult().equals(this.source.getLeft()) &&
                    StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple argument1Quadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument1());
                if (argument1Quadruple.isNegative()) {
                    this.handleMinusParentheses(expandedQuadruple);
                }
            }
        }
    }

    /**
     * Faz as alteracoes de sinais e de argumentos em casos de uma quadrupla com MINUS.
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} que contém o operador MINUS
     */
    private void handleMinusParentheses(ExpandedQuadruple expandedQuadruple) {
        ExpandedQuadruple grandfather = this.findDirectFather(expandedQuadruple);
        if (grandfather != null) {
            grandfather.setOperator(MathOperatorUtil.signalRule(grandfather.getOperator(), "-"));
            ExpandedQuadruple minusQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument1());
            expandedQuadruple.setArgument1(minusQuadruple.getArgument1());
        }
    }

    /**
     * Encontra o pai que contém o operador diretamente antes da {@code iterationQuadruple}
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} de onde sera encontrado o pai
     * @return {@link ExpandedQuadruple} que contem o operador anterior relativo a {@code iterationQuadruple}
     */
    private ExpandedQuadruple findDirectFather(ExpandedQuadruple iterationQuadruple) {
        ExpandedQuadruple father = this.source.findQuadrupleByArgument(iterationQuadruple.getResult());
        if (father.getArgument1().equals(iterationQuadruple.getResult())) {
            if (father.getResult().equals(this.source.getLeft()))
                return null;
            return this.findDirectFather(father);
        }
        return father;

    }

    private String applyDistributive(ExpandedQuadruple distributiveQuadruple) {
        if (StringUtil.match(distributiveQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) &&
                StringUtil.match(distributiveQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple leftQuadruple = this.source.findQuadrupleByResult(distributiveQuadruple.getArgument1());
            ExpandedQuadruple rightQuadruple = this.source.findQuadrupleByResult(distributiveQuadruple.getArgument2());
            if (leftQuadruple.isNegative()) {
                return this.applyMonomialDistributive(leftQuadruple.getResult(), rightQuadruple);
            }

            if (rightQuadruple.isNegative()) {
                return this.applyMonomialDistributive(rightQuadruple.getResult(), leftQuadruple);
            }
            ThreeAddressCode newSource = this.createNewSource(this.getQuadruplesWithMinus());
            return polynomialDistributive(leftQuadruple, rightQuadruple, newSource);

        }

        if (!StringUtil.match(distributiveQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.applyMonomialDistributive(distributiveQuadruple.getArgument1(), this.source.findQuadrupleByResult(distributiveQuadruple.getArgument2()));
        }
        return this.applyMonomialDistributive(distributiveQuadruple.getArgument2(), this.source.findQuadrupleByResult(distributiveQuadruple.getArgument1()));

    }

    private List<ExpandedQuadruple> getQuadruplesWithMinus() {
        List<ExpandedQuadruple> minusList = new ArrayList<>();
        int tvIndex = 2;
        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {
            if (expandedQuadruple.isMinusOrNegative()) {
                String argument1;
                if (expandedQuadruple.isMinus()) {
                    if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                        ExpandedQuadruple negativeQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument2());
                        argument1 = negativeQuadruple.getArgument1();
                    } else {
                        argument1 = expandedQuadruple.getArgument2();
                    }
                } else {
                    argument1 = expandedQuadruple.getArgument1();
                }
                ExpandedQuadruple newQuadruple = new ExpandedQuadruple("MINUS", argument1, "T" + tvIndex, expandedQuadruple.getPosition(), expandedQuadruple.getLevel());
                minusList.add(newQuadruple);
                tvIndex++;
            }
        }
        return minusList;
    }

    private ThreeAddressCode createNewSource(List<ExpandedQuadruple> oldList) {
        List<ExpandedQuadruple> newQuadrupleList = new ArrayList<>();
        newQuadrupleList.add(new ExpandedQuadruple("", "", "", "T1", 0, 0));
        newQuadrupleList.addAll(oldList);
        return new ThreeAddressCode("T1", newQuadrupleList);
    }

    private String applyMonomialDistributive(String multiplier, ExpandedQuadruple distributiveQuadruple) {
        this.source.setLeft(distributiveQuadruple.getResult());
        return monomialDistributive(multiplier, distributiveQuadruple);
    }

    private String monomialDistributive(String multiplier, ExpandedQuadruple distributiveQuadruple) {
        if (!distributiveQuadruple.isNegative()) {
            if (StringUtil.match(distributiveQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
                return this.monomialDistributive(multiplier, this.source.findQuadrupleByResult(distributiveQuadruple.getArgument1()));

            this.source.addQuadrupleToList("*", multiplier, distributiveQuadruple.getArgument1(), distributiveQuadruple, true);
        } else {
            ExpandedQuadruple father = this.source.findQuadrupleByArgument(distributiveQuadruple.getResult());
            this.source.addQuadrupleToList("*", multiplier, distributiveQuadruple.getResult(), father, true);
            distributiveQuadruple = father;
        }

        if (StringUtil.match(distributiveQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple nextQuadruple = this.source.findQuadrupleByResult(distributiveQuadruple.getArgument2());

            if (distributiveQuadruple.isMinus())
                this.source.addQuadrupleToList("MINUS", nextQuadruple.getArgument1(), "", nextQuadruple, true);

            return this.monomialDistributive(multiplier, this.source.findQuadrupleByResult(distributiveQuadruple.getArgument2()));
        }

        if (distributiveQuadruple.isMinus())
            this.source.addQuadrupleToList("MINUS", distributiveQuadruple.getArgument2(), "", distributiveQuadruple, false);

        this.source.addQuadrupleToList("*", multiplier, distributiveQuadruple.getArgument2(), distributiveQuadruple, false);
        this.adjustMonomialQuadruples();

        return "Aplicando a propriedade distributiva, onde cada elemento dentro dos parênteses é multiplicado " +
                "pelo elemento do outro termo";
    }

    private void adjustMonomialQuadruples() {
        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {
            if (expandedQuadruple.isNegative())
                expandedQuadruple.setLevel(1);
            if (expandedQuadruple.isMinus())
                expandedQuadruple.setOperator("+");
        }
    }

    private String polynomialDistributive(ExpandedQuadruple leftDistQuadruple, ExpandedQuadruple rightDistQuadruple, ThreeAddressCode newSource) {
        if (StringUtil.match(leftDistQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return this.polynomialDistributive(this.source.findQuadrupleByResult(leftDistQuadruple.getArgument1()), rightDistQuadruple, newSource);

        if (leftDistQuadruple.isNegative()) {
            ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(leftDistQuadruple.getArgument1(), newSource.getExpandedQuadruples(), true);
            this.polynomialRightTermDistributive(minusQuadruple.getResult(), rightDistQuadruple, newSource);
            leftDistQuadruple = this.source.findQuadrupleByArgument(leftDistQuadruple.getResult());
        } else
            this.polynomialRightTermDistributive(leftDistQuadruple.getArgument1(), rightDistQuadruple, newSource);

        if (StringUtil.match(leftDistQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {


            ExpandedQuadruple nextQuadruple = this.source.findQuadrupleByResult(leftDistQuadruple.getArgument2());

            if (leftDistQuadruple.isMinus() && !StringUtil.match(nextQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(nextQuadruple.getArgument1(), newSource.getExpandedQuadruples(), false);
                this.source.addQuadrupleToList(minusQuadruple.getOperator(), minusQuadruple.getArgument1(), minusQuadruple.getArgument2(), nextQuadruple, true);
            }


            return this.polynomialDistributive(nextQuadruple, rightDistQuadruple, newSource);
        }
        String leftQuadrupleMultiplier;
        if (leftDistQuadruple.isMinus()) {
            ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(leftDistQuadruple.getArgument2(), newSource.getExpandedQuadruples(), true);
            leftQuadrupleMultiplier = minusQuadruple.getResult();
        } else
            leftQuadrupleMultiplier = leftDistQuadruple.getArgument2();

        this.polynomialRightTermDistributive(leftQuadrupleMultiplier, rightDistQuadruple, newSource);
        this.adjustLastQuadruple(newSource);
        this.source = newSource;

        return "Aplicando a propriedade distributiva, onde cada elemento do primeiro termo é multiplicado " +
                "por cada um dos elementos do segundo termo.";
    }

    //o booleano é pra procurar de cima para baixo, ou de baixo para cima, no caso tiver 2 numeros negativos iguais nas
    // duas quadruplas
    private ExpandedQuadruple getMinusQuadruple(String argument, List<ExpandedQuadruple> expandedQuadruples, boolean isTopToBottom) {
        if (isTopToBottom) {
            for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
                if (expandedQuadruple.isNegative() && expandedQuadruple.getArgument1().equals(argument))
                    return expandedQuadruple;
            }
        }
        for (int i = expandedQuadruples.size() - 1; i > 0; i--) {
            ExpandedQuadruple expandedQuadruple = expandedQuadruples.get(i);
            if (expandedQuadruple.isNegative() && expandedQuadruple.getArgument1().equals(argument))
                return expandedQuadruple;
        }
        return expandedQuadruples.get(0);
    }

    private void adjustLastQuadruple(ThreeAddressCode newSource) {
        ExpandedQuadruple lastQuadruple = newSource.getExpandedQuadruples().get(newSource.getExpandedQuadruples().size() - 1);
        if (lastQuadruple.getOperator().equals("")) {
            ExpandedQuadruple father = newSource.findQuadrupleByArgument2(lastQuadruple.getResult());
            newSource.replaceFatherArgumentForSons(father, 1);
        }
    }


    //FIXME será usado na proxima regra
    private String findArgument1Operator(ExpandedQuadruple expandedQuadruple) {
        if (expandedQuadruple.getResult().equals(this.source.getLeft()))
            return "+";
        ExpandedQuadruple father = this.source.findQuadrupleByArgument(expandedQuadruple.getResult());
        if (father.getArgument1().equals(expandedQuadruple.getResult()) || father.isTimes()) {
            return this.findArgument1Operator(father);
        }
        return father.getOperator();
    }

    private ThreeAddressCode polynomialRightTermDistributive(String leftQuadrupleMultiplier, ExpandedQuadruple rightQuadruple, ThreeAddressCode newSource) {
        if (StringUtil.match(rightQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return this.polynomialRightTermDistributive(leftQuadrupleMultiplier, this.source.findQuadrupleByResult(rightQuadruple.getArgument1()), newSource);
        if (rightQuadruple.isNegative()) {
            ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(rightQuadruple.getArgument1(), newSource.getExpandedQuadruples(), false);
            this.addNewQuadrupleToSource(leftQuadrupleMultiplier, minusQuadruple.getResult(), newSource);
            rightQuadruple = this.source.findQuadrupleByArgument(rightQuadruple.getResult());
        } else
            this.addNewQuadrupleToSource(leftQuadrupleMultiplier, rightQuadruple.getArgument1(), newSource);

        if (StringUtil.match(rightQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

            ExpandedQuadruple nextQuadruple = this.source.findQuadrupleByResult(rightQuadruple.getArgument2());

            if (rightQuadruple.isMinus() && !StringUtil.match(nextQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(nextQuadruple.getArgument1(), newSource.getExpandedQuadruples(), false);
                this.source.addQuadrupleToList(minusQuadruple.getOperator(), minusQuadruple.getArgument1(), minusQuadruple.getArgument2(), nextQuadruple, true);
            }

            return this.polynomialRightTermDistributive(leftQuadrupleMultiplier, nextQuadruple, newSource);
        }
        String rightQuadrupleArgument;
        if (rightQuadruple.isMinus()) {
            ExpandedQuadruple minusQuadruple = this.getMinusQuadruple(rightQuadruple.getArgument2(), newSource.getExpandedQuadruples(), false);
            rightQuadrupleArgument = minusQuadruple.getResult();
        } else
            rightQuadrupleArgument = rightQuadruple.getArgument2();
        this.addNewQuadrupleToSource(leftQuadrupleMultiplier, rightQuadrupleArgument, newSource);
        return newSource;
    }

    private void addNewQuadrupleToSource(String leftQuadrupleMultiplier, String rightQuadrupleArgument, ThreeAddressCode newSource) {
        ExpandedQuadruple iterationQuadruple = this.findIterationQuadruple(newSource);
        newSource.addQuadrupleToList("*", leftQuadrupleMultiplier, rightQuadrupleArgument, iterationQuadruple, true);
        newSource.addQuadrupleToList("", "", "", iterationQuadruple, false);
        iterationQuadruple.setOperator("+");
    }

    private ExpandedQuadruple findIterationQuadruple(ThreeAddressCode newSource) {
        for (ExpandedQuadruple expandedQuadruple : newSource.getExpandedQuadruples()) {
            if (expandedQuadruple.getArgument1().equals("") &&
                    expandedQuadruple.getArgument2().equals("") && !expandedQuadruple.isNegative())
                return expandedQuadruple;
        }
        return newSource.getExpandedQuadruples().get(0);
    }

    private boolean isThereADistributiveCase(List<ThreeAddressCode> source) {
        for (ExpandedQuadruple expandedQuadruple : source.get(0).getExpandedQuadruples()) {
            if (expandedQuadruple.isTimes()) {
                if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && !StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerOperation = source.get(0).findQuadrupleByResult(expandedQuadruple.getArgument1());

                    if (StringUtil.isVariable(expandedQuadruple.getArgument2()) && !innerOperation.isNegative())
                        return true;

                    if (isThereAVariableInQuadruple(source.get(0), innerOperation)) {
                        return true;
                    }
                }

                if (!StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerOperation = source.get(0).findQuadrupleByResult(expandedQuadruple.getArgument2());

                    if (StringUtil.isVariable(expandedQuadruple.getArgument1()) && !innerOperation.isNegative())
                        return true;

                    if (isThereAVariableInQuadruple(source.get(0), innerOperation)) {
                        return true;
                    }
                }

                if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    ExpandedQuadruple innerOperation1 = source.get(0).findQuadrupleByResult(expandedQuadruple.getArgument1());
                    ExpandedQuadruple innerOperation2 = source.get(0).findQuadrupleByResult(expandedQuadruple.getArgument2());

                    if (!innerOperation1.isNegative() && !innerOperation2.isNegative())
                        return true;

                    if (innerOperation1.isNegative() && isThereAVariableInQuadruple(source.get(0), innerOperation2))
                        return true;

                    if (innerOperation2.isNegative() && isThereAVariableInQuadruple(source.get(0), innerOperation1))
                        return true;
                }
            }
        }

        return false;
    }

    private boolean isThereAVariableInQuadruple(ThreeAddressCode source, ExpandedQuadruple innerOperation) {
        if (StringUtil.match(innerOperation.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return isThereAVariableInQuadruple(source, source.findQuadrupleByResult(innerOperation.getArgument1()));

        if (StringUtil.match(innerOperation.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            return isThereAVariableInQuadruple(source, source.findQuadrupleByResult(innerOperation.getArgument2()));

        return StringUtil.isVariable(innerOperation.getArgument1()) || StringUtil.isVariable(innerOperation.getArgument2());
    }
}
