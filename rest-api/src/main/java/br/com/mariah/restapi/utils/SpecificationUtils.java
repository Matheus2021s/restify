package br.com.mariah.restapi.utils;

import br.com.mariah.restapi.dto.search.SearchDTO;
import br.com.mariah.restapi.dto.search.SearchField;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SpecificationUtils {

    public static <T> Specification<T> generateSpecification(SearchDTO searchDTO, Class<?> specification) {
        List<SearchField> fields = searchDTO.getFields();
        if (fields.size() > 1) {
            Specification query = generateWhere(specification, fields);
            for (int i = 1; i < fields.size(); i++) {
                SearchField field = fields.get(i);
                query = field.getOperatorType().exec(query, getSpecification(field, specification));

            }
            return query;
        } else if (fields.size() == 1) {
            return generateWhere(specification, fields);
        } else {
            return null;
        }
    }

    private static Specification generateWhere(Class<?> specification, List<SearchField> fields) {
        return Specification.where(getSpecification(fields.get(0), specification));
    }

    private static Specification getSpecification(SearchField searchField, Class<?> specification) {
        return searchField.getFieldComparatorType().exec(specification, searchField.getName(), searchField.getValue());
    }
}
