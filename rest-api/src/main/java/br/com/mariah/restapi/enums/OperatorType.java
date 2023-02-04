package br.com.mariah.restapi.enums;

import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

public enum OperatorType {
    AND("and") {
        @Override
        public Specification exec(Specification origin, Specification toAdd) {
            return origin.and(toAdd);
        }
    },
    OR("or") {
        @Override
        public Specification exec(Specification origin, Specification toAdd) {
            return origin.or(toAdd);
        }
    };

    private final String prefix;

    OperatorType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static OperatorType resolve(String prefix) {
        return Arrays.stream(OperatorType.values())
                .filter(operatorType -> operatorType.getPrefix().equals(prefix))
                .findFirst().orElse(null);
    }

    public abstract Specification exec(Specification origin, Specification toAdd);

}
