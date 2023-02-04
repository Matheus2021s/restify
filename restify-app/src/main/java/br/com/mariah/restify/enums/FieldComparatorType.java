package br.com.mariah.restify.enums;

import br.com.mariah.restify.exception.ResourceNotFoundException;

import java.util.Arrays;

public enum FieldComparatorType {

    EQUALS("eq", "equal"),
    NOT_EQUALS("neq", "notEqual"),
    LIKE("lk", "like"),
    NOT_LIKE("nlk", "notLike"),
    GREATER_THAN("gt", "greaterThan"),
    GREATER_THAN_OR_EQUALS("gte", "greaterThanOrEquals"),
    LESS_THAN("lt", "lessThan"),
    LESS_THAN_OR_EQUALS("lte", "lessThanOrEquals");

    private final String prefix;

    private final String methodName;

    FieldComparatorType(String prefix, String methodName) {
        this.prefix = prefix;
        this.methodName = methodName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getMethodName() {
        return methodName;
    }

    public static FieldComparatorType resolve(String prefix) {
        return Arrays.stream(FieldComparatorType.values()).filter(fieldComparatorType -> fieldComparatorType.getPrefix().equals(prefix))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException(String.format("Tipo de campo de pesquisa %s não encontrado!", prefix)));
    }


}
