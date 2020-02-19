package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialAddAndSubRuleRemoveParenthesis implements IRule {


    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereParenthesisToReturn(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {

        String left = getHigherLevelQuadruples(sources, sources.get(0).getLeft());

        List<Step> steps = new ArrayList<>();

        ThreeAddressCode step = new ThreeAddressCode(left, "", "", sources.get(0).getExpandedQuadruples());

        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Removendo os parenteses"));

        return steps;
    }


    private String getHigherLevelQuadruples(List<ThreeAddressCode> source, String result) {
        ExpandedQuadruple expandedQuadruple = source.get(0).findQuadrupleByResult(result);

        if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.getHigherLevelQuadruples(source, expandedQuadruple.getArgument1());
        }

        if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.getHigherLevelQuadruples(source, expandedQuadruple.getArgument2());
        }

        if (expandedQuadruple.getLevel() > 0) {
            if (expandedQuadruple.isNegative()) {
                ExpandedQuadruple parent = source.get(0).findQuadrupleByArgument(expandedQuadruple.getResult());
                ExpandedQuadruple grandParent = source.get(0).findQuadrupleByArgument(parent.getResult());
                if (!grandParent.getArgument1().equals(parent.getResult())) {
                    grandParent.setOperator("-");
                    parent.setArgument1(expandedQuadruple.getArgument1());
                }
            }
            expandedQuadruple.setLevel(0);

        }

        return expandedQuadruple.getResult();
    }

    private boolean isThereParenthesisToReturn(List<ExpandedQuadruple> source) {
        for (ExpandedQuadruple expandedQuadruple : source) {
            if (expandedQuadruple.getLevel() > 0)
                return true;
        }
        return false;
    }


}
