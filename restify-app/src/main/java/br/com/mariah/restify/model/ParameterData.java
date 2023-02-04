package br.com.mariah.restify.model;

import br.com.mariah.restify.utils.StringUtils;
import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ParameterData {

    private String name;

    @Builder.Default
    private Boolean nullable = true;

    @Builder.Default
    private Boolean isPrimaryKey = false;

    private Class<?> dataType;

    private String defaultValue;

    public String getCamelNameFirstLetterUpper() {
        return StringUtils.getCamelCaseFirstLetterUpper(this.name);
    }

    public String getCamelNameFirstLetterLower() {
        return StringUtils.getCamelCaseFirstLetterLower(this.name);
    }

    public String getNameUpperSeparatedByUnderscore() {
        return StringUtils.getUpperSeparatedByUnderscore(this.name);
    }

    public String getNameLowerSeparatedByUnderscore() {
        return StringUtils.getLowerSeparatedByUnderscore(this.name);
    }

    public String getNameUpperSeparatedByDash() {
        return StringUtils.getUpperSeparatedByDash(this.name);
    }

    public String getNameLowerSeparatedByDash() {
        return StringUtils.getLowerSeparatedByDash(this.name);
    }


}
