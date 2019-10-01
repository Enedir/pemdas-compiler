package br.ifmath.compiler.infrastructure.util;

import br.ifmath.compiler.infrastructure.props.RegexPattern;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author alex_
 */
public class MathOperatorUtil {

    public static String signalRule(String operator1, String operator2) {
        if (operator1.equals(operator2))
            return "+";

        if (StringUtil.matchAny(operator1, "(MINUS)", "\\-") && StringUtil.matchAny(operator2, "(MINUS)", "\\-"))
            return "+";

        return "-";
    }

    public static double times(String a, String b) {
        double value = 0;

        if (StringUtil.isDecimalNumber(a) && StringUtil.isDecimalNumber(b)) {
            value = Double.parseDouble(a) * Double.parseDouble(b);
        } else if (StringUtil.isDecimalNumber(a)) {
            value = multiplyNumberAndVariable(a, b);
        } else if (StringUtil.isDecimalNumber(b)) {
            value = multiplyNumberAndVariable(b, a);
        } else {
            //TODO multiplicação entre variáveis
        }

        return value;
    }

    private static double multiplyNumberAndVariable(String multiplier, String variable) {
        if (StringUtil.matchAny(variable.replace("-", ""), RegexPattern.TEMPORARY_VARIABLE.toString(), RegexPattern.VARIABLE.toString())) {
            return variable.contains("-") ? -Double.parseDouble(multiplier) : Double.parseDouble(multiplier);
        }

        String coeficient = StringUtil.removeNonNumericChars(variable);
        coeficient = coeficient.replace(",", ".");

        return Double.parseDouble(multiplier) * Double.parseDouble(coeficient);
    }

    public static String replaceReducedDistributive(String source) {
        CharSequence inputStr = source;
        Pattern pattern = Pattern.compile(RegexPattern.REDUCED_DISTRIBUTIVE_OPERATION.toString());
        Matcher matcher = pattern.matcher(inputStr);
        int plusSpace = 0;
        while(matcher.find()){
            source = source.substring(0, matcher.end() + plusSpace - 1 ) + "*" + source.substring(matcher.end() + plusSpace - 1 );
            plusSpace++;
        }
        
        return source;
    }
    
}
