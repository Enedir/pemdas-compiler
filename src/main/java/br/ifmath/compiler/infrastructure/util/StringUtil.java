package br.ifmath.compiler.infrastructure.util;

import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;

/**
 * <p>
 * It provides some methods to manipulate strings</p>
 *
 * @author alexjravila
 */
public class StringUtil {

    /**
     * <p>
     * Private constructor to avoid instantiation of this class</p>
     */
    private StringUtil() {

    }

    /**
     * <p>
     * It verifies if a string is null. If it is null, will return an empty
     * string to avoid NullPointerException</p>
     * <p>
     * All methods should use this method to avoid basic exceptions such as
     * NullPointerException</p>
     *
     * @param source - the string to be analyzed
     * @return a string not null
     */
    private static String validate(String source) {
        if (isNull(source)) {
            return "";
        }

        return source;
    }

    /**
     * <p>
     * It replaces all special characters and spaces in a string</p>
     *
     * @param source - the string to be analyzed
     * @return the string after replace all special characters and spaces
     */
    public static String replaceSpecialAndSpace(String source) {
        source = validate(source);

        source = source.replaceAll("[ÂÀÁÄÃ]", "A");
        source = source.replaceAll("[âãàáä]", "a");
        source = source.replaceAll("[ÊÈÉË]", "E");
        source = source.replaceAll("[êèéë]", "e");
        source = source.replaceAll("[ÎÍÌÏ]", "I");
        source = source.replaceAll("[îíìï]", "i");
        source = source.replaceAll("[ÔÕÒÓÖ]", "O");
        source = source.replaceAll("[ôõòóö]", "o");
        source = source.replaceAll("[ÛÙÚÜ]", "U");
        source = source.replaceAll("[ûúùü]", "u");
        source = source.replaceAll("Ç", "C");
        source = source.replaceAll("ç", "c");
        source = source.replaceAll("[ýÿ]", "y");
        source = source.replaceAll("Ý", "Y");
        source = source.replaceAll("ñ", "n");
        source = source.replaceAll("Ñ", "N");
        source = source.replaceAll("['<>|/]", "");
        source = source.replaceAll(" ", "_");

        return source;
    }

    /**
     * <p>
     * It replaces all special characters in a string</p>
     *
     * @param source - the string to be analyzed
     * @return the string after replace all special characters
     */
    public static String replaceSpecial(String source) {
        source = validate(source);

        source = source.replaceAll("[ÂÀÁÄÃ]", "A");
        source = source.replaceAll("[âãàáä]", "a");
        source = source.replaceAll("[ÊÈÉË]", "E");
        source = source.replaceAll("[êèéë]", "e");
        source = source.replaceAll("[ÎÍÌÏ]", "I");
        source = source.replaceAll("[îíìï]", "i");
        source = source.replaceAll("[ÔÕÒÓÖ]", "O");
        source = source.replaceAll("[ôõòóö]", "o");
        source = source.replaceAll("[ÛÙÚÜ]", "U");
        source = source.replaceAll("[ûúùü]", "u");
        source = source.replaceAll("Ç", "C");
        source = source.replaceAll("ç", "c");
        source = source.replaceAll("[ýÿ]", "y");
        source = source.replaceAll("Ý", "Y");
        source = source.replaceAll("ñ", "n");
        source = source.replaceAll("Ñ", "N");
        source = source.replaceAll("['<>|/]", "");

        return source;
    }

    /**
     * <p>
     * It replaces all matches of pattern for a specific text</p>
     *
     * @param source  - the string to be analyzed
     * @param pattern - the pattern to be replaced. It can be a regular
     *                expression or a literal string
     * @param replace - the string to replace the matched patterns
     * @return the source after replace the matched patterns
     */
    public static String replace(String source, String pattern, String replace) {
        source = validate(source);

        if (isEmpty(pattern)) {
            return source;
        }

        return source.replaceAll(pattern, replace);
    }

    /**
     * <p>
     * It removes all matches of pattern for a specific text</p>
     *
     * @param source  - the string to be analyzed
     * @param pattern - the pattern to be removed. It can be a regular
     *                expression or a literal string
     * @return the source after remove the matched patterns
     */
    public static String remove(String source, String pattern) {
        source = validate(source);

        if (isEmpty(pattern)) {
            return source;
        }

        return source.replaceAll(pattern, "");
    }

    /**
     * <p>
     * It removes all characters that aren't numbers</p>
     *
     * @param source - the string to be analyzed
     * @return the source after remove the matched patterns
     */
    public static String removeNonNumericChars(String source) {
        return replace(source, RegexPattern.NON_NUMERIC.toString(), "");
    }

    /**
     * <p>
     * It removes all characters that are numbers</p>
     *
     * @param source - the string to be analyzed
     * @return the source after remove the matched patterns
     */
    public static String removeNumericChars(String source) {
        return replace(source, RegexPattern.NUMERIC.toString(), "");
    }

    /**
     * <p>
     * It removes all characters that aren't alphanumeric</p>
     *
     * @param source - the string to be analyzed
     * @return the source after remove the matched patterns
     */
    public static String removeNonAlphanumericChars(String source) {
        return replace(source, RegexPattern.NON_ALPHANUMERIC.toString(), "");
    }

    /**
     * <p>
     * It verifies if there are only characters from pattern in source</p>
     *
     * @param source  - the string to be analyzed
     * @param pattern - the pattern to be verified. It can be a regular
     *                expression or a literal string
     * @return the source after remove the matched patterns
     */
    public static boolean containOnyTheseChars(String source, String pattern) {
        if (isEmpty(source)) {
            return isEmpty(pattern);
        }

        return isEmpty(replace(source, pattern, ""));
    }

    /**
     * <p>
     * It verifies if source is a decimal number</p>
     *
     * @param source - the string to be analyzed
     * @return if the source is a decimal number
     */
    public static boolean isDecimalNumber(String source) {
        return isNumeric(source, RegexPattern.DECIMAL_NUMBER.toString());
    }

    /**
     * <p>
     * It verifies if source is a positive decimal number</p>
     *
     * @param source - the string to be analyzed
     * @return if the source is a positive decimal number
     */
    public static boolean isPositiveDecimalNumber(String source) {
        return isNumeric(source, RegexPattern.POSITIVE_DECIMAL_NUMBER.toString());
    }

    /**
     * <p>
     * It verifies if source is a integer number</p>
     *
     * @param source - the string to be analyzed
     * @return if the source is a integer number
     */
    public static boolean isIntegerNumber(String source) {
        return isNumeric(source, RegexPattern.INTEGER_NUMBER.toString());
    }

    /**
     * <p>
     * It verifies if source is a natural number</p>
     *
     * @param source - the string to be analyzed
     * @return if the source is a natural number
     */
    public static boolean isNaturalNumber(String source) {
        return isNumeric(source, RegexPattern.NATURAL_NUMBER.toString());
    }

    /**
     * <p>
     * It verifies if source is a number</p>
     *
     * @param source - the string to be analyzed
     * @return if the source is a decimal number
     */
    private static boolean isNumeric(String source, String pattern) {
        if (isEmpty(source)) {
            return false;
        }

        return isEmpty(remove(source, pattern));
    }

    /**
     * <p>
     * It verifies if source matches with pattern</p>
     *
     * @param source  - the string to be analyzed
     * @param pattern - pattern to search
     * @return if the source matches
     */
    public static boolean match(String source, String pattern) {
        if (isEmpty(source)) {
            return false;
        }

        return isEmpty(remove(source, pattern));
    }


    /**
     * <p>
     * It verifies if source matches with pattern</p>
     *
     * @param source  - the string to be analyzed
     * @param pattern - pattern to search
     * @return if the source matches
     */
    public static boolean contains(String source, String pattern) {
        if (isEmpty(source)) {
            return false;
        }

        return remove(source, pattern).length() != source.length();
    }

    /**
     * <p>
     * It verifies if source matches with any pattern</p>
     *
     * @param source   - the string to be analyzed
     * @param patterns - patterns to search
     * @return if the source matches
     */
    public static boolean matchAny(String source, String... patterns) {
        if (isEmpty(source)) {
            return false;
        }

        for (String pattern : patterns) {
            if (isEmpty(remove(source, pattern)))
                return true;
        }

        return false;
    }

    /**
     * <p>
     * It verifies if source matches with all pattern</p>
     *
     * @param source   - the string to be analyzed
     * @param patterns - patterns to search
     * @return if the source matches
     */
    public static boolean matchAll(String source, String... patterns) {
        if (isEmpty(source)) {
            return false;
        }

        for (String pattern : patterns) {
            if (!isEmpty(remove(source, pattern)))
                return false;
        }

        return true;
    }

    /**
     * <p>
     * It fills a string with a specific length</p>
     *
     * @param source   - string to fill the StringBuilder's length
     * @param quantity - the size of string
     * @return the string with a specific length
     */
    private static String fill(char source, int quantity) {
        if (quantity <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(quantity);
        while (sb.length() < quantity) {
            sb.append(source);
        }

        return sb.toString();
    }

    /**
     * <p>
     * It fills a string with a specific char until the source to have the
     * determined length. The chars will be inserted in before source (in other
     * words, in left)</p>
     * <p>
     * If the length of source is bigger than length passed by parameter, the
     * source will be cut</p>
     *
     * @param source - the original string to be filled
     * @param toFill - char that will be used to fill the source
     * @param length - the length that source needs to have after filling
     * @return the filled string with a specific length
     */
    public static String insertLeftString(String source, char toFill, int length) {
        String str = fill(toFill, length - source.length()) + source;
        return str.substring(0, length);
    }

    /**
     * <p>
     * It fills a string with a specific char until the source to have the
     * determined length. The chars will be inserted in after source (in other
     * words, in right)</p>
     * <p>
     * If the length of source is bigger than length passed by parameter, the
     * source will be cut</p>
     *
     * @param source - the original string to be filled
     * @param toFill - char that will be used to fill the source
     * @param length - the length that source needs to have after filling
     * @return the filled string with a specific length
     */
    public static String insertRightString(String source, char toFill, int length) {
        return (source + fill(toFill, length)).substring(0, length);
    }

    /**
     * <p>
     * It cuts a string between two keys</p>
     *
     * @param source - string to be cut
     * @param begin  - the begin's key
     * @param end    - the end's key
     * @return the cut string
     */
    public static String substringBetween(String source, String begin, String end) {
        return substringBetween(source, begin, end, 0);
    }

    /**
     * <p>
     * It cuts a string between two keys</p>
     *
     * @param source    - string to be cut
     * @param begin     - the begin's key
     * @param end       - the end's key
     * @param fromIndex - the index where the search will start
     * @return the cut string
     */
    public static String substringBetween(String source, String begin, String end, int fromIndex) {
        if (isEmpty(source) || isEmpty(begin) || isEmpty(end)) {
            return null;
        }

        int start = source.indexOf(begin, fromIndex);
        if (start != -1) {
            int endIndex = source.indexOf(end, start + begin.length());
            if (endIndex != -1) {
                return substring(source, start + begin.length(), endIndex);
            }
        }

        return "";
    }

    /**
     * <p>
     * It cuts a string. This method has some validations to avoid common
     * exceptions that happens when is tried to cut a string</p>
     *
     * @param source     - the string that will be cut
     * @param beginIndex - the index where the cut begins
     * @param endIndex   - the index where the cut ends
     * @return the cut string
     */
    public static String substring(String source, int beginIndex, int endIndex) {
        if (isEmpty(source)) {
            return "";
        }

        int length = source.length();
        if (beginIndex < 0) {
            beginIndex = 0;
        }

        if (beginIndex >= length) {
            beginIndex = length;
        }

        if (endIndex > length) {
            endIndex = length;
        }

        if (endIndex < beginIndex) {
            return "";
        }

        return source.substring(beginIndex, endIndex);
    }

    /**
     * <p>
     * It verifies if source is null</p>
     *
     * @param source - the string to be analyzed
     * @return if source is null
     */
    public static boolean isNull(String source) {
        return (source == null);
    }

    /**
     * <p>
     * It verifies if source is empty</p>
     *
     * @param source - the string to be analyzed
     * @return if source is empty
     */
    public static boolean isEmpty(String source) {
        return !(source != null && source.length() > 0);
    }

    /**
     * <p>
     * It verifies if source is not empty</p>
     *
     * @param source - the string to be analyzed
     * @return if source is not empty
     */
    public static boolean isNotEmpty(String source) {
        return (source != null && source.length() > 0);
    }

    /**
     * Obtains the value of the exponent, if the {@code param} is a {@link Monomial}.
     *
     * @param param {@link String} which represents a {@link Monomial}.
     * @return integer value which is the exponent of the {@code param}.
     */
    public static int getPowerValue(String param) {
        Monomial monomial = new Monomial(param);
        return monomial.getLiteralDegree();
    }

    /**
     * Identifies if an argument contains a variable.
     *
     * @param param {@link String} to be identified.
     * @return true if the {@code param} contains a variable and else otherwise.
     */
    public static boolean isVariable(String param) {
        return StringUtil.matchAny(param, RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFFICIENT.toString(), RegexPattern.VARIABLE_WITH_EXPONENT.toString())
                && !StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString());
    }

    /**
     * Identifies if an argument is a {@link Monomial}.
     *
     * @param param {@link String} to be identified.
     * @return true if the {@code param} represents a {@link Monomial} and else otherwise.
     */
    public static boolean isMonomial(String param) {
        return StringUtil.matchAny(param, RegexPattern.VARIABLE_WITH_EXPONENT.toString(),
                RegexPattern.VARIABLE_WITH_COEFFICIENT.toString());
    }


    /**
     * Obtains only the variable within a {@code param}.
     *
     * @param param {@link String} to be obtained the variable.
     * @return {@link String} which contains the varible among the {@code param}.
     */
    public static String getVariable(String param) {
        return StringUtil.removeNumericChars(param);
    }

    public static String getVariable(String a, String b) {
        a = StringUtil.removeNumericChars(a).replace("-", "").replace(",", "").replace(".", "");
        if (StringUtil.isNotEmpty(a))
            return a;

        b = StringUtil.removeNumericChars(b).replace("-", "").replace(",", "").replace(".", "");
        if (StringUtil.isNotEmpty(b))
            return b;

        return "";
    }

    /**
     * <p>
     * It counts which times an specific character repeat into a string</p>
     *
     * @param source      - the string to be analyzed
     * @param charToCount - character to be counted
     * @return the quantity of an character repeats into a string
     */
    public static int countChar(String source, char charToCount) {
        if (isNull(source)) {
            return 0;
        }

        int count = 0;
        int length = source.length();
        for (int i = 0; i < length; i++) {
            if (source.charAt(i) == charToCount) {
                count++;
            }
        }

        return count;
    }

    /**
     * <p>
     * It concatenates some string, using a specific separator</p>
     *
     * @param separator - the string that will separate each union
     * @param source    - the strings that will be concatenates
     * @return the concatenated string
     */
    public static String concat(String separator, String... source) {
        StringBuilder sb = new StringBuilder();
        for (String str : source) {
            if (!StringUtil.isEmpty(str)) {
                if (sb.length() > 0) {
                    sb.append(separator);
                }
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * <p>
     * It transforms an string into a camel's case string<p>
     *
     * @param source - string to be transformed
     * @return the transformed string
     */
    public static String capitalize(String source) {
        boolean afterSpace = true;
        for (int index = 0; index < source.length(); index++) {
            String prefix = source.substring(0, index);
            String suffix = (source.length() > (index + 1) ? source.substring(index + 1) : "");

            String character;
            if (afterSpace) {
                character = source.substring(index, index + 1).toUpperCase();
            } else {
                character = source.substring(index, index + 1).toLowerCase();
            }

            source = prefix + character + suffix;

            afterSpace = source.charAt(index) == ' ';
        }

        return source;
    }

    /**
     * It formats the string. The method will change reserved characters of
     * regex to //character
     *
     * @param lexeme - expression to be adjusted
     * @return the adjusted lexeme
     */
    public static String getLiteralLexemeToRegex(String lexeme) {
        lexeme = lexeme.replace("(", "\\(");
        lexeme = lexeme.replace(")", "\\)");
        lexeme = lexeme.replace("[", "\\[");
        lexeme = lexeme.replace("]", "\\]");
        lexeme = lexeme.replace("{", "\\{");
        lexeme = lexeme.replace("}", "\\}");
        lexeme = lexeme.replace("+", "\\+");
        lexeme = lexeme.replace("-", "\\-");
        lexeme = lexeme.replace("*", "\\*");
        lexeme = lexeme.replace("^", "\\^");

        return lexeme;
    }

    /**
     * It identifies the index of first character not blank.
     *
     * @param source - string to be analyzed
     * @return the index of first character not blank or -1 (if the string is
     * empty)
     */
    public static int getFirstIndexNotBlank(String source) {
        for (int index = 0; index < source.length(); index++) {
            if (source.charAt(index) != ' ') {
                return index;
            }
        }

        return -1;
    }
}
