package br.com.mariah.restify.service.generator.common;

import br.com.mariah.restify.model.Element;
import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.model.ParameterData;
import br.com.mariah.restify.service.generator.GeneratorService;
import br.com.mariah.restify.utils.ResourceUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.mariah.restify.enums.ElementType.*;
import static br.com.mariah.restify.service.helper.ElementHelper.*;

@RequiredArgsConstructor
public class ControllerGeneratorService implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {

        String controllerPackage = String.format("%s.controller", resourceUtils.getBasePackage());
        UnitSourceGenerator controllerUnitSource = UnitSourceGenerator.create(controllerPackage);

        String controllerName = String.format("%sController", modelData.getCamelNameFirstLetterUpper());
        ClassSourceGenerator controllerClass = ClassSourceGenerator
                .create(
                        TypeDeclarationSourceGenerator
                                .create(controllerName)
                ).addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(RestController.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(RequestMapping.class)
                                .addParameter(
                                        VariableSourceGenerator
                                                .create("value")
                                                .setValue(String.format("\"/%s\"", modelData.getNameLowerSeparatedByDash()))
                                )
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(RequiredArgsConstructor.class)
                );

        VariableSourceGenerator variableService =

                getParameter(modelData, SERVICE)
                        .addModifier(Modifier.PRIVATE)
                        .addModifier(Modifier.FINAL);

        controllerClass.addField(variableService);


        FunctionSourceGenerator listMethod = FunctionSourceGenerator
                .create("list")
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator
                                                .create(Pageable.class),
                                        "pageable"
                                )
                )
                .addParameter(
                        getParameter(modelData, SEARCH_DTO)
                                .addAnnotation(
                                        AnnotationSourceGenerator.create(ModelAttribute.class)
                                )
                )
                .setReturnType(ResponseEntity.class)
                .addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(GetMapping.class)
                )
                .addBodyCodeLine(String.format("return ResponseEntity.ok(Optional.ofNullable(SpecificationUtils.generateSpecification(%s, %s.class))",
                        getNameFirstLetterLower(modelData, SEARCH_DTO),
                        getNameFirstLetterUpper(modelData, ENTITY)))
                .addBodyCodeLine(String.format(".map(objectSpecification -> %s.ofPage(this.%s.listSpecification(pageable, objectSpecification)))",
                        getNameFirstLetterUpper(modelData, DTO_RESPONSE),
                        getNameFirstLetterLower(modelData, SERVICE)
                ))
                .addBodyCodeLine(String.format(".orElse(%s.ofPage(this.orcamentoService.list(pageable))));",
                        getNameFirstLetterUpper(modelData, DTO_RESPONSE),
                        getNameFirstLetterLower(modelData, SERVICE)
                        )
                );

        controllerUnitSource.addImport(Specification.class);
        controllerUnitSource.addImport(Optional.class);
        controllerUnitSource.addImport(Page.class);
        controllerUnitSource.addImport(String.format("%s.utils.SpecificationUtils", resourceUtils.getBasePackage()));
        controllerUnitSource.addImport(getImport(modelData, SEARCH_DTO));
        controllerUnitSource.addImport(getImport(modelData, ENTITY));
        controllerClass.addMethod(listMethod);

        FunctionSourceGenerator getByIdMethod = FunctionSourceGenerator.create("getById")
                .setReturnType(TypeDeclarationSourceGenerator.create(ResponseEntity.class))
                .addModifier(Modifier.PUBLIC);

        getByIdMethodContent(modelData, controllerUnitSource, getByIdMethod);

        addIdMappingAnnotation(getByIdMethod, modelData, AnnotationSourceGenerator.create(GetMapping.class));

        controllerClass.addMethod(getByIdMethod);


        FunctionSourceGenerator createMethod = FunctionSourceGenerator.create("create")
                .setReturnType(TypeDeclarationSourceGenerator.create(ResponseEntity.class))
                .addAnnotation(AnnotationSourceGenerator.create(PostMapping.class))
                .addModifier(Modifier.PUBLIC)
                .addParameter(
                        getParameter(modelData, DTO_CREATE)
                                .addAnnotation(
                                        AnnotationSourceGenerator
                                                .create(RequestBody.class)
                                ).addAnnotation(
                                        AnnotationSourceGenerator
                                                .create(Valid.class)
                                )
                )
                .addBodyCodeLine(String.format("%sEntity entity = this.%sService.create(%sCreateDTO.toEntity());", modelData.getCamelNameFirstLetterUpper(), modelData.getCamelNameFirstLetterLower(), modelData.getCamelNameFirstLetterLower()))
                .addBodyCodeLine(String.format("return ResponseEntity.created(URI.create(\"/%s\")).body(%sResponseDTO.of(entity));", modelData.getNameLowerSeparatedByDash(), modelData.getCamelNameFirstLetterUpper()));

        ;

        controllerUnitSource.addImport(getImport(modelData, ENTITY));

        controllerUnitSource.addImport(URI.class);

        controllerClass.addMethod(createMethod);


        FunctionSourceGenerator updateMethod = FunctionSourceGenerator.create("update")
                .setReturnType(ResponseEntity.class)
                .addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        generateAnnotationMapping(modelData, PutMapping.class)
                )
                .addParameter(
                        getParameter(modelData, DTO_UPDATE)
                                .addAnnotation(
                                        AnnotationSourceGenerator.create(Valid.class)
                                )
                                .addAnnotation(
                                        AnnotationSourceGenerator.create(RequestBody.class)
                                )
                );

        String idParameter = addMethodIdParameters(modelData, updateMethod);

        updateMethod.addBodyCodeLine(String.format("return ResponseEntity.ok(%s.of(this.%s.update(%s.toEntity(),%s)));",
                getNameFirstLetterUpper(modelData, DTO_RESPONSE),
                getNameFirstLetterLower(modelData, SERVICE),
                getNameFirstLetterLower(modelData, DTO_UPDATE),
                idParameter
        ));

        controllerClass.addMethod(updateMethod);


        FunctionSourceGenerator deleteMethod = FunctionSourceGenerator.create("delete")
                .addModifier(Modifier.PUBLIC)
                .setReturnType(ResponseEntity.class)
                .addAnnotation(
                        generateAnnotationMapping(modelData, DeleteMapping.class)
                );

        String idParameters = addMethodIdParameters(modelData, deleteMethod);

        deleteMethod.addBodyCodeLine(String.format("this.%s.delete(%s);", getNameFirstLetterLower(modelData, SERVICE), idParameters));
        deleteMethod.addBodyCodeLine("return ResponseEntity.noContent().build();");

        controllerClass.addMethod(deleteMethod);

        controllerUnitSource.addClass(controllerClass);

        modelData.getDomain().addElement(Element.of(CONTROLLER, controllerPackage, controllerName));

        return List.of(controllerUnitSource);
    }

    private static String addMethodIdParameters(ModelData modelData, FunctionSourceGenerator method) {
        String idParameter = "";
        if (modelData.getIsComposePrimaryKey()) {
            modelData.getPrimaryKeys().stream()
                    .map(parameterData -> VariableSourceGenerator
                            .create(
                                    TypeDeclarationSourceGenerator
                                            .create(parameterData.getDataType()),
                                    parameterData.getCamelNameFirstLetterLower()
                            ).addAnnotation(
                                    AnnotationSourceGenerator
                                            .create(RequestParam.class)
                                            .addParameter(
                                                    VariableSourceGenerator
                                                            .create("name")
                                                            .setValue(String.format("\"%s\"", parameterData.getCamelNameFirstLetterLower()))
                                            )
                            )
                    )
                    .forEach(method::addParameter);


            String sb = String.format("%sEmbeddedId id = %sEmbeddedId.builder()\n", modelData.getCamelNameFirstLetterUpper(), modelData.getCamelNameFirstLetterUpper()) +
                    modelData.getPrimaryKeys().stream()
                            .map(ParameterData::getCamelNameFirstLetterLower)
                            .map(name -> String.format(".%s(%s)\n", name, name))
                            .collect(Collectors.joining()) +
                    ".build();";

            method.addBodyCodeLine(sb);

            idParameter = "id";

        } else {
            ParameterData parameterData = modelData.getPrimaryKeys()
                    .stream()
                    .findFirst()
                    .orElse(null);

            method.addParameter(
                    VariableSourceGenerator.create(
                            TypeDeclarationSourceGenerator.create(parameterData.getDataType()),
                            parameterData.getCamelNameFirstLetterLower()
                    ).addAnnotation(
                            AnnotationSourceGenerator.create(Valid.class)
                    ).addAnnotation(
                            AnnotationSourceGenerator.create(NotNull.class)
                    ).addAnnotation(
                            AnnotationSourceGenerator.create(PathVariable.class)
                                    .addParameter(
                                            VariableSourceGenerator
                                                    .create("name")
                                                    .setValue(String.format("\"%s\"", modelData.getPrimaryKeys().get(0).getCamelNameFirstLetterLower()))
                                    )
                    )
            );

            idParameter = parameterData.getCamelNameFirstLetterLower();
        }
        return idParameter;
    }

    private static AnnotationSourceGenerator generateAnnotationMapping(ModelData modelData, Class<?> annotationClass) {
        if (modelData.getIsComposePrimaryKey()) {
            return AnnotationSourceGenerator
                    .create(annotationClass);
        } else {
            return AnnotationSourceGenerator
                    .create(annotationClass)
                    .addParameter(
                            VariableSourceGenerator
                                    .create("value")
                                    .setValue(String.format("\"/{%s}\"", modelData.getPrimaryKeys().get(0).getCamelNameFirstLetterLower()))
                    );
        }
    }

    private void getByIdMethodContent(ModelData modelData, UnitSourceGenerator controllerUnitSource, FunctionSourceGenerator getByIdMethod) {
        if (modelData.getIsComposePrimaryKey()) {

            modelData.getPrimaryKeys().stream()
                    .map(parameterData -> VariableSourceGenerator
                            .create(
                                    TypeDeclarationSourceGenerator
                                            .create(parameterData.getDataType()),
                                    parameterData.getCamelNameFirstLetterLower()
                            ).addAnnotation(
                                    AnnotationSourceGenerator
                                            .create(RequestParam.class)
                                            .addParameter(
                                                    VariableSourceGenerator
                                                            .create("value")
                                                            .setValue(String.format("\"%s\"", parameterData.getCamelNameFirstLetterLower()))
                                            )
                            )
                    )
                    .forEach(getByIdMethod::addParameter);


            String sb = String.format("%sEmbeddedId id = %sEmbeddedId.builder()\n", modelData.getCamelNameFirstLetterUpper(), modelData.getCamelNameFirstLetterUpper()) +
                    modelData.getPrimaryKeys().stream()
                            .map(ParameterData::getCamelNameFirstLetterLower)
                            .map(name -> String.format(".%s(%s)\n", name, name))
                            .collect(Collectors.joining()) +
                    ".build();";

            getByIdMethod.addBodyCodeLine(sb);

            String finalString = String.format("return ResponseEntity.ok(%sResponseDTO.of(this.%sService.findById(id)));", modelData.getCamelNameFirstLetterUpper(), modelData.getCamelNameFirstLetterLower());

            getByIdMethod.addBodyCodeLine(finalString);

            controllerUnitSource.addImport(getImport(modelData, EMBEDDED_ENTITY));


        } else {

            modelData.getPrimaryKeys().stream().findFirst()
                    .ifPresent(parameterData -> getByIdMethod.addParameter(
                            VariableSourceGenerator
                                    .create(
                                            TypeDeclarationSourceGenerator
                                                    .create(parameterData.getDataType()),
                                            parameterData.getCamelNameFirstLetterLower()
                                    )
                                    .addAnnotation(
                                            AnnotationSourceGenerator.create(PathVariable.class)
                                    )
                    ));

            String parameterId = modelData.getPrimaryKeys().stream()
                    .map(ParameterData::getCamelNameFirstLetterLower)
                    .collect(Collectors.joining());

            String finalString = String.format("return ResponseEntity.ok(%sResponseDTO.of(this.%sService.findById(%s)));", modelData.getCamelNameFirstLetterUpper(), modelData.getCamelNameFirstLetterLower(), parameterId);

            getByIdMethod.addBodyCodeLine(finalString);

        }

        controllerUnitSource.addImport(getImport(modelData, DTO_RESPONSE));

    }


    private void addIdMappingAnnotation(FunctionSourceGenerator getByIdMethod, ModelData modelData, AnnotationSourceGenerator mapping) {

        if (modelData.getIsComposePrimaryKey()) {
            getByIdMethod.addAnnotation(mapping);
        } else {
            modelData.getPrimaryKeys().stream()
                    .findFirst()
                    .map(ParameterData::getCamelNameFirstLetterLower)
                    .map(string -> String.format("\"/{%s}\"", string))
                    .ifPresent(s -> mapping.addParameter(
                                    VariableSourceGenerator
                                            .create(
                                                    TypeDeclarationSourceGenerator
                                                            .create("value")
                                            ).setValue(s)
                            )
                    );
            getByIdMethod.addAnnotation(mapping);
        }

    }
}
