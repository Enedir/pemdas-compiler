package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectsquaretrinomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRulePerfectSquareTrinomialSumSquare implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        this.source = this.generateSumSquare();

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Identificamos os " +
                "elementos a e b e escrevemos o resultado como o quadrado da diferença, no formato (a \\pm b)^2"));
        return steps;
    }

    private ThreeAddressCode generateSumSquare() {
        ExpandedQuadruple lastQuadruple = this.getLastQuadruple(this.source.getRootQuadruple());
        String argument1 = this.source.findQuadrupleByArgument(lastQuadruple.getResult()).getArgument1();
        String argument2 = lastQuadruple.getArgument1();
        ExpandedQuadruple coreQuadruple = new ExpandedQuadruple(lastQuadruple.getOperator(), argument1, argument2, "T1", 0, 1);

        List<ExpandedQuadruple> newQuadruples = new ArrayList<>();
        newQuadruples.add(coreQuadruple);

        ExpandedQuadruple squareQuadruple = new ExpandedQuadruple("^","T1","2","T2",0,0);
        newQuadruples.add(squareQuadruple);

        return new ThreeAddressCode("T2",newQuadruples);
    }

    private ExpandedQuadruple getLastQuadruple(ExpandedQuadruple iterationQuadruple) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getLastQuadruple(source.findQuadrupleByResult(iterationQuadruple.getArgument1()));
        }

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getLastQuadruple(source.findQuadrupleByResult(iterationQuadruple.getArgument2()));
        }
        return iterationQuadruple;
    }
}
