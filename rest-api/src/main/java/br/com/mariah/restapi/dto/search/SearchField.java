package br.com.mariah.restapi.dto.search;

import br.com.mariah.restapi.enums.DataType;
import br.com.mariah.restapi.enums.FieldComparatorType;
import br.com.mariah.restapi.enums.OperatorType;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Objects;

@Getter
public class SearchField<T extends Comparable> {

    private Class<T> dataType;

    private T value;

    private String name;


    private OperatorType operatorType;
    private FieldComparatorType fieldComparatorType;


    protected SearchField(Class<T> dataType, T value, FieldComparatorType fieldComparatorType, String name, OperatorType operatorType) {
        this.dataType = dataType;
        this.value = value;
        this.name = name;
        this.fieldComparatorType = fieldComparatorType;
        this.operatorType = operatorType;
    }

    public static SearchField of(Class<?> dataType, Comparable value, OperatorType operatorType, FieldComparatorType fieldComparatorType, String name) {
        return new SearchField(dataType, value, fieldComparatorType, name, operatorType);
    }

    public static <T extends Comparable> void includeQuery(List<SearchField> fields, Class<T> datatype, String value, String name) {
        if (Strings.isNotBlank(value)) {
            if (value.contains(",")) {
                for (String query : value.split(",")) {
                    if (query.contains("::")) {
                        String[] strings = query.split("::");
                        if (strings.length == 3) {
                            DataType dataType = DataType.resolveByClass(datatype);
                            if (Strings.isNotBlank(strings[0])
                                    && Strings.isNotBlank(strings[1])
                                    && Strings.isNotBlank(strings[2])
                                    && Objects.nonNull(dataType)
                            ) {
                                fields.add(of(value.getClass(),
                                        dataType.cast(strings[2]),
                                        OperatorType.resolve(strings[0]),
                                        FieldComparatorType.resolve(strings[1]),
                                        name));
                            }
                        }
                    }
                }


            } else {
                if (value.contains("::")) {
                    String[] strings = value.split("::");
                    if (strings.length == 3) {
                        DataType dataType = DataType.resolveByClass(datatype);
                        if (Strings.isNotBlank(strings[0])
                                && Strings.isNotBlank(strings[1])
                                && Strings.isNotBlank(strings[2])
                                && Objects.nonNull(dataType)
                        ) {
                            fields.add(of(value.getClass(),
                                    dataType.cast(strings[2]),
                                    OperatorType.resolve(strings[0]),
                                    FieldComparatorType.resolve(strings[1]),
                                    name));
                        }
                    }
                }
            }

        }
    }

}

