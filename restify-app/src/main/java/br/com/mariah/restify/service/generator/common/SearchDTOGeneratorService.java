package br.com.mariah.restify.service.generator.common;


import br.com.mariah.restify.enums.ElementType;
import br.com.mariah.restify.model.Element;
import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.service.generator.GeneratorService;
import br.com.mariah.restify.utils.ResourceUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.burningwave.core.classes.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SearchDTOGeneratorService implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {
        String searchPackage = String.format("%s.dto.search", resourceUtils.getBasePackage());

        UnitSourceGenerator unitSourceGenerator = UnitSourceGenerator.create(searchPackage);

        String searchClassName = String.format("%sSearch", modelData.getCamelNameFirstLetterUpper());

        ClassSourceGenerator searchClass = ClassSourceGenerator.create(
                        TypeDeclarationSourceGenerator.create(searchClassName)
                ).addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator.create(Getter.class)
                )
                .addAnnotation(
                        AnnotationSourceGenerator.create(Setter.class)
                )
                .addConcretizedType(TypeDeclarationSourceGenerator.create("SearchDTO"));


        modelData.getParameters()
                .forEach(parameterData -> {
                            searchClass.addField(
                                    VariableSourceGenerator.create(
                                                    TypeDeclarationSourceGenerator.create(String.class),
                                                    parameterData.getNameLowerSeparatedByUnderscore())
                                            .addModifier(Modifier.PRIVATE)

                            );
                            unitSourceGenerator.addImport(parameterData.getDataType());
                        }
                );


        FunctionSourceGenerator getFields = FunctionSourceGenerator.create("getFields")
                .addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator.create(Override.class)
                )
                .setReturnType("List<SearchField>")
                .addBodyCodeLine("List<SearchField> fields = new ArrayList<>();");
        unitSourceGenerator.addImport(List.class);
        unitSourceGenerator.addImport(ArrayList.class);

        modelData.getParameters().forEach(parameterData -> {
            getFields.addBodyCodeLine(
                    String.format("SearchField.includeQuery(fields,%s.class, this.%s, \"%s\");",
                            parameterData.getDataType().getSimpleName(),
                            parameterData.getNameLowerSeparatedByUnderscore(),
                            parameterData.getCamelNameFirstLetterLower()

                    )
            );

        });

        getFields.addBodyCodeLine("return fields;");


        searchClass.addMethod(getFields);


        unitSourceGenerator.addClass(searchClass);

        modelData.getDomain().addElement(Element.of(ElementType.SEARCH_DTO, searchPackage, searchClassName));

        return List.of(unitSourceGenerator);
    }
}
