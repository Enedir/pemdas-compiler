package br.ifmath.compiler.infrastructure.util;

import br.ifmath.compiler.infrastructure.props.RegexPattern;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author alex_
 */
public class NumberUtil {
    private NumberUtil() {

    }

    public static boolean isInteger(double value) {
        return (value == Math.floor(value)) && !Double.isInfinite(value);
    }

    public static int parseInt(double value) {
        return (int) value;
    }

    public static boolean isIntegerDivision(double a, double b) {
        return a % b == 0;
    }

    public static double greatestCommonDenominator(double a, double b) {
        return b == 0 ? a : greatestCommonDenominator(b, a % b);
    }

    public static boolean canBeSimplified(double a, double b) {
        return greatestCommonDenominator(a, b) != 1 || a % b == 0;
    }

    public static double getVariableCoeficient(String param) {
        String coeficient = StringUtil.removeNonNumericChars(param).replace(",", ".");

        if (StringUtil.isDecimalNumber(coeficient))
            return Double.parseDouble(coeficient);

        return 1d;
    }

    public static String removeVariable(String source) {
        return StringUtil.replace(source, RegexPattern.VARIABLE.toString(), "");

    }

    public static double formatDouble(double param) {
        DecimalFormat decimalFormat = new DecimalFormat("#.0000");
        return Double.parseDouble(decimalFormat.format(param).replace(",", "."));
    }

    public static long leastCommonMultiple(List<Integer> elements) {
        long leastCommonMultiple = 1;
        int divisor = 2;

        while (true) {
            int counter = 0;
            boolean divisible = false;

            for (int i = 0; i < elements.size(); i++) {

                if (elements.get(i) == 0) {
                    return 0;
                } else if (elements.get(i) < 0) {
                    elements.set(i, elements.get(i) * (-1));
                }

                if (elements.get(i) == 1) {
                    counter++;
                }

                if (elements.get(i) % divisor == 0) {
                    divisible = true;
                    elements.set(i, elements.get(i) / divisor);
                }
            }

            if (divisible) {
                leastCommonMultiple = leastCommonMultiple * divisor;
            } else {
                divisor++;
            }

            if (counter == elements.size()) {
                return leastCommonMultiple;
            }
        }
    }
}
