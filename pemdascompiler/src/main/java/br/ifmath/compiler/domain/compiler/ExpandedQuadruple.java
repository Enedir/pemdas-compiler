package br.ifmath.compiler.domain.compiler;

import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.Objects;

public class ExpandedQuadruple {

    private String operator;
    private String argument1;
    private String argument2;
    private String result;
    private int position;
    private int level;

    public ExpandedQuadruple(String operator, String argument1, String argument2, String result, int position, int level) {
        this.operator = operator;
        this.argument1 = argument1;
        this.argument2 = argument2;
        this.result = result;
        this.position = position;
        this.level = level;
    }

    public ExpandedQuadruple(String argument1, String result, int position, int level) {
        this.argument1 = argument1;
        this.result = result;
        this.position = position;
        this.level = level;
    }

    public ExpandedQuadruple(String operator, String argument1, String result, int position, int level) {
        this.operator = operator;
        this.argument1 = argument1;
        this.result = result;
        this.position = position;
        this.level = level;
    }

    public ExpandedQuadruple(ExpandedQuadruple quintuple) {
        this.operator = quintuple.getOperator();
        this.argument1 = quintuple.getArgument1();
        this.argument2 = quintuple.getArgument2();
        this.result = quintuple.getResult();
        this.position = quintuple.getPosition();
        this.level = quintuple.getLevel();
    }

    public ExpandedQuadruple(ExpandedQuadruple quintuple, int level) {
        this.operator = quintuple.getOperator();
        this.argument1 = quintuple.getArgument1();
        this.argument2 = quintuple.getArgument2();
        this.result = quintuple.getResult();
        this.position = quintuple.getPosition();
        this.level = level;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getArgument1() {
        return argument1;
    }

    public void setArgument1(String argument1) {
        this.argument1 = argument1;
    }

    public String getArgument2() {
        return argument2;
    }

    public void setArgument2(String argument2) {
        this.argument2 = argument2;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isPlus() {
        return operator.equals("+");
    }

    public boolean isPlusOrMinus() {
        return operator.equals("+") || operator.equals("-") || operator.equals("MINUS");
    }

    public boolean isMinusOrNegative() {
        return operator.equals("-") || operator.equals("MINUS");
    }

    public boolean isMinus() {
        return operator.equals("-");
    }

    public boolean isFraction() {
        return operator.equals("/");
    }

    public boolean isTimes() {
        return operator.equals("*");
    }

    public boolean isNegative() {
        return operator != null && operator.equals("MINUS");
    }

    public boolean isArgument1(String argument) {
        return argument1.equals(argument);
    }

    public boolean isArgument2(String argument) {
        return argument2 != null && argument2.equals(argument);
    }

    @Override
    public String toString() {
        if (StringUtil.isNotEmpty(this.argument2))
            return String.format("%s = %s %s %s       || nível: %d", this.result, this.argument1, this.operator, this.argument2, this.level);

        if (StringUtil.isNotEmpty(this.operator))
            return String.format("%s = %s %s       || nível: %d", this.result, this.operator, this.argument1, this.level);

        return String.format("%s = %s       || nível: %d", this.result, this.argument1, this.level);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.result);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExpandedQuadruple other = (ExpandedQuadruple) obj;
        return Objects.equals(this.result, other.result);
    }
}
