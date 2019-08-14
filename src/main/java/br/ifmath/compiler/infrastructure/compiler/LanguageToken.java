package br.ifmath.compiler.infrastructure.compiler;

import br.ifmath.compiler.domain.grammar.terminal.Semicolon;
import br.ifmath.compiler.domain.grammar.terminal.Success;
import br.ifmath.compiler.domain.grammar.terminal.Terminal;
import br.ifmath.compiler.domain.grammar.terminal.comparison.*;
import br.ifmath.compiler.domain.grammar.terminal.function.*;
import br.ifmath.compiler.domain.grammar.terminal.id.Id;
import br.ifmath.compiler.domain.grammar.terminal.id.IdWithCoefficient;
import br.ifmath.compiler.domain.grammar.terminal.number.DecimalNumber;
import br.ifmath.compiler.domain.grammar.terminal.number.NaturalNumber;
import br.ifmath.compiler.domain.grammar.terminal.operator.*;
import br.ifmath.compiler.domain.grammar.terminal.precedence.*;
import br.ifmath.compiler.infrastructure.compiler.iface.ILanguageToken;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

/**
 * It contains all tokens recognized by language and provides method to recognize
 *
 * @author alexjravila
 */
public enum LanguageToken implements ILanguageToken {

    PLUS {

        @Override
        public String toString() {
            return "+";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Plus();
        }

    },

    MINUS {

        @Override
        public String toString() {
            return "-";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Minus();
        }

    },

    TIMES {

        @Override
        public String toString() {
            return "*";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Times();
        }

    },

    FRACTION {

        @Override
        public String toString() {
            return "/";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Fraction();
        }

    },

    POW {

        @Override
        public String toString() {
            return "^";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Pow();
        }

    },

    SEMICOLON {

        @Override
        public String toString() {
            return ";";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Semicolon();
        }

    },

    EQUAL {

        @Override
        public String toString() {
            return "=";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Equal();
        }

    },

    DIFFERENT {

        @Override
        public String toString() {
            return "<>";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Different();
        }

    },

    GREATER {

        @Override
        public String toString() {
            return ">";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Greater();
        }

    },

    GREATER_OR_EQUAL {

        @Override
        public String toString() {
            return ">=";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new GreaterOrEqual();
        }

    },

    LOWER {

        @Override
        public String toString() {
            return "<";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Lower();
        }

    },

    LOWER_OR_EQUAL {

        @Override
        public String toString() {
            return "<=";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new LowerOrEqual();
        }

    },

    SQRT {

        @Override
        public String toString() {
            return "raizq";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new SquareRoot();
        }

        @Override
        public boolean possibleMatch(String pattern) {
            return toString().startsWith(pattern);
        }
    },

    ROOT {

        @Override
        public String toString() {
            return "raiz";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Root();
        }

        @Override
        public boolean possibleMatch(String pattern) {
            return toString().startsWith(pattern);
        }
    },

    LOG {

        @Override
        public String toString() {
            return "log";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Log();
        }

        @Override
        public boolean possibleMatch(String pattern) {
            return toString().startsWith(pattern);
        }
    },

    LOG10 {

        @Override
        public String toString() {
            return "log10";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Log10();
        }

        @Override
        public boolean possibleMatch(String pattern) {
            return toString().startsWith(pattern);
        }
    },

    COS {

        @Override
        public String toString() {
            return "cos";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Cosine();
        }

        @Override
        public boolean possibleMatch(String pattern) {
            return toString().startsWith(pattern);
        }
    },

    SIN {

        @Override
        public String toString() {
            return "sen";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Sine();
        }

        @Override
        public boolean possibleMatch(String pattern) {
            return toString().startsWith(pattern);
        }
    },

    TAN {

        @Override
        public String toString() {
            return "tg";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Tangent();
        }

        @Override
        public boolean possibleMatch(String pattern) {
            return toString().startsWith(pattern);
        }
    },

    BEGIN_PARENTHESES {

        @Override
        public String toString() {
            return "(";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new BeginParentheses();
        }

    },

    END_PARENTHESES {

        @Override
        public String toString() {
            return ")";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new EndParentheses();
        }

    },

    BEGIN_BRACKETS {

        @Override
        public String toString() {
            return "[";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new BeginBracket();
        }

    },

    END_BRACKETS {

        @Override
        public String toString() {
            return "]";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new EndBracket();
        }

    },

    BEGIN_KEYS {

        @Override
        public String toString() {
            return "{";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new BeginKey();
        }

    },

    END_KEYS {

        @Override
        public String toString() {
            return "}";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new EndKey();
        }

    },

    ID {

        @Override
        public String toString() {
            return "ID";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Id();
        }

        @Override
        public boolean matchPattern(String pattern) {
            return StringUtil.match(pattern, RegexPattern.VARIABLE.toString());
        }

    },

    ID_WITH_COEFFICIENT {

        @Override
        public String toString() {
            return "ID_WITH_COEFFICIENT";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new IdWithCoefficient();
        }

        @Override
        public boolean matchPattern(String pattern) {
            return StringUtil.match(pattern, RegexPattern.VARIABLE_WITH_COEFICIENT.toString());
        }

    },

    NATURAL_NUMBER {

        @Override
        public String toString() {
            return "NATURAL_NUMBER";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new NaturalNumber();
        }

        @Override
        public boolean matchPattern(String pattern) {
            return StringUtil.isNaturalNumber(pattern);
        }

    },

    DECIMAL_NUMBER {

        @Override
        public String toString() {
            return "DECIMAL_NUMBER";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new DecimalNumber();
        }

        @Override
        public boolean matchPattern(String pattern) {
            return StringUtil.isPositiveDecimalNumber(pattern);
        }

        @Override
        public boolean possibleMatch(String pattern) {
            return StringUtil.match(pattern, RegexPattern.POSSIBLE_NUMBER.toString());
        }

    },

    SUCCESS {

        @Override
        public String toString() {
            return "$";
        }

        @Override
        public Terminal getMatchingTerminal() {
            return new Success();
        }

        @Override
        public boolean matchPattern(String pattern) {
            return false;
        }

        @Override
        public boolean possibleMatch(String pattern) {
            return false;
        }
    };


    /**
     * It verifies if token's pattern match with pattern
     *
     * @param pattern - pattern to be compared
     * @return if match the patterns
     */
    @Override
    public boolean matchPattern(String pattern) {
        return this.toString().toUpperCase().equals(pattern.toUpperCase());
    }

    @Override
    public boolean possibleMatch(String pattern) {
        return false;
    }

    /**
     * It recognizes a token using an expression
     *
     * @param expression - pattern to be recognized
     * @return the recognized token or null
     */
    public static LanguageToken get(String expression) {
        for (LanguageToken token : LanguageToken.values()) {
            if (token.matchPattern(expression)) {
                return token;
            }
        }

        for (LanguageToken token : LanguageToken.values()) {
            if (token.possibleMatch(expression)) {
                return token;
            }
        }

        return null;
    }

}
