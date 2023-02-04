package br.com.mariah.restapi.enums;

import br.com.mariah.restapi.exception.ResourceNotFoundException;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;

public enum FieldComparatorType {

    EQUALS("eq") {
        @Override
        public <Y, T extends Comparable> Specification<Y> exec(Class<Y> specification, String parameterName, T objectValue) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(parameterName), objectValue);
        }
    },
    NOT_EQUALS("neq") {
        @Override
        public <Y, T extends Comparable> Specification<Y> exec(Class<Y> specification, String parameterName, T objectValue) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(parameterName), objectValue);
        }
    },
    LIKE("lk") {
        @Override
        public <Y, T extends Comparable> Specification<Y> exec(Class<Y> specification, String parameterName, T objectValue) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(parameterName), "%" + objectValue + "%");
        }
    },
    NOT_LIKE("nlk") {
        @Override
        public <Y, T extends Comparable> Specification<Y> exec(Class<Y> specification, String parameterName, T objectValue) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.notLike(root.get(parameterName), "%" + objectValue + "%");
        }
    },
    GREATER_THAN("gt") {
        @Override
        public <Y, T extends Comparable> Specification<Y> exec(Class<Y> specification, String parameterName, T objectValue) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(parameterName), objectValue );
        }
    },
    GREATER_THAN_OR_EQUALS("gte") {
        @Override
        public <Y, T extends Comparable> Specification<Y> exec(Class<Y> specification, String parameterName, T objectValue) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(parameterName), objectValue );
        }
    },
    LESS_THAN("lt") {
        @Override
        public <Y, T extends Comparable> Specification<Y> exec(Class<Y> specification, String parameterName, T objectValue) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(parameterName), objectValue );
        }
    },
    LESS_THAN_OR_EQUALS("lte") {
        @Override
        public <Y, T extends Comparable> Specification<Y> exec(Class<Y> specification, String parameterName, T objectValue) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(parameterName), objectValue );
        }
    };

    private final String prefix;

    FieldComparatorType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public abstract <Y, T extends Comparable> Specification<Y> exec(Class<Y> specification, String parameterName, T objectValue);

    public static FieldComparatorType resolve(String prefix) {
        return Arrays.stream(FieldComparatorType.values())
                .filter(fieldComparatorType -> fieldComparatorType.getPrefix().equals(prefix))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException(String.format("Tipo de campo de pesquisa %s não encontrado!", prefix)));
    }
}
