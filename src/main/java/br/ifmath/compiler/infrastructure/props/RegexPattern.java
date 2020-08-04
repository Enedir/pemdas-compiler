package br.ifmath.compiler.infrastructure.props;

public enum RegexPattern {

    NON_NUMERIC {
        @Override
        public String toString() {
            return "[^0-9]";
        }
    },

    NUMERIC {
        @Override
        public String toString() {
            return "[0-9\\.\\,]";
        }
    },

    NON_ALPHANUMERIC {
        @Override
        public String toString() {
            return "[^A-Za-z0-9]";
        }
    },

    DECIMAL_NUMBER {
        @Override
        public String toString() {
            return "(\\-)?[0-9]+(\\,[0-9]+)?";
        }
    },

    POSITIVE_DECIMAL_NUMBER {
        @Override
        public String toString() {
            return "[0-9]+(\\,[0-9]+)?";
        }
    },

    INTEGER_NUMBER {
        @Override
        public String toString() {
            return "(\\-)?[0-9]+";
        }
    },

    NATURAL_NUMBER {
        @Override
        public String toString() {
            return "[0-9]+";
        }
    },

    NON_INTEGER {
        @Override
        public String toString() {
            return "[^-][^0-9]";
        }
    },

    VARIABLE {
        @Override
        public String toString() {
            return "[a-zA-Z]([0-9]+)?$";
        }
    },

    VARIABLE_WITH_COEFFICIENT {
        @Override
        public String toString() {
            return "[0-9]+(\\,[0-9]+)?[a-zA-Z]([0-9]+)?$";
        }
    },

    VARIABLE_WITH_EXPONENT {
        @Override
        public String toString() {
            return "([0-9]+(\\,[0-9]+)?)?[a-zA-Z][\\^][0-9]+$";
        }
    },

    POSSIBLE_NUMBER {
        @Override
        public String toString() {
            return "([\\d]+\\,)";
        }
    },

    TEMPORARY_VARIABLE {
        @Override
        public String toString() {
            return "T[0-9]([0-9]+)?";
        }
    },

    REDUCED_DISTRIBUTIVE_OPERATION {
        @Override
        public String toString() {
            return "(?<![a-zA-Z0-9])([0-9]+[a-zA-Z]|[a-zA-Z]|[0-9]+)\\(";
        }
    }

}
