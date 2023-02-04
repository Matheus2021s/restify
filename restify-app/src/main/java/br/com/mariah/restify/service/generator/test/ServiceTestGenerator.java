package br.com.mariah.restify.service.generator.test;

import br.com.mariah.restify.enums.DataType;
import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.service.generator.GeneratorService;
import br.com.mariah.restify.utils.ResourceUtils;
import br.com.mariah.restify.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Modifier;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static br.com.mariah.restify.enums.ElementType.*;
import static br.com.mariah.restify.service.helper.ElementHelper.*;

@RequiredArgsConstructor
public class ServiceTestGenerator implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {
        String serviceTestPackageName = String.format("%s.service", resourceUtils.getBasePackage());
        UnitSourceGenerator serviceTestPackage = UnitSourceGenerator.create(serviceTestPackageName);

        String testServiceClassName = String.format("%sServiceTest", modelData.getCamelNameFirstLetterUpper());

        ClassSourceGenerator testServiceClass = ClassSourceGenerator.create(TypeDeclarationSourceGenerator.create(testServiceClassName))
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(
                                        ExtendWith.class
                                )
                                .addParameter(
                                        VariableSourceGenerator.create("SpringExtension.class")
                                )
                );

        serviceTestPackage.addImport("org.springframework.test.context.junit.jupiter.SpringExtension");


        testServiceClass.addField(
                getParameter(modelData, SERVICE)
                        .addModifier(Modifier.PUBLIC)
                        .addAnnotation(
                                AnnotationSourceGenerator.create(
                                        InjectMocks.class
                                )
                        )

        );


        testServiceClass.addField(
                getParameter(modelData, REPOSITORY)
                        .addModifier(Modifier.PRIVATE)
                        .addAnnotation(
                                AnnotationSourceGenerator.create(
                                        Mock.class
                                )
                        )
        );

        testServiceClass.addField(
                getParameter(modelData, ENTITY)
                        .addModifier(Modifier.PRIVATE)
        );

        testServiceClass.addField(
                VariableSourceGenerator.create(
                        TypeDeclarationSourceGenerator.create(Pageable.class),
                        "pageable"
                ).addModifier(Modifier.PRIVATE)
        );
        testServiceClass.addField(
                VariableSourceGenerator.create(
                                TypeDeclarationSourceGenerator.create(Page.class),
                                "page"
                        )
                        .addModifier(Modifier.PRIVATE)
        );

        testServiceClass.addField(
                VariableSourceGenerator.create(
                        TypeDeclarationSourceGenerator.create(Specification.class)
                                .addGeneric(GenericSourceGenerator.create(getNameFirstLetterUpper(modelData, ENTITY))),
                        "specification"
                ).addModifier(Modifier.PRIVATE)
        );


        FunctionSourceGenerator setupMethod = FunctionSourceGenerator.create("setUp")
                .setReturnType("void")
                .addAnnotation(
                        AnnotationSourceGenerator.create(
                                BeforeEach.class
                        )
                )
                .addBodyCodeLine(
                        String.format("this.%s = %s.getEntity();",
                                getNameFirstLetterLower(modelData, ENTITY),
                                getNameFirstLetterUpper(modelData, CREATOR)
                        )
                )
                .addBodyCodeLine("this.pageable = Pageable.ofSize(20);")
                .addBodyCodeLine(String.format("this.page = new PageImpl(List.of(this.%s));",
                        getNameFirstLetterLower(modelData, ENTITY))
                );

        if (modelData.getIsComposePrimaryKey()) {
            setupMethod.addBodyCodeLine(
                    String.format("this.specification = where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(\"id\"), this.%s.getId()));",
                            getNameFirstLetterLower(modelData, ENTITY))
            );
        } else {
            modelData.getPrimaryKeys().stream().findFirst().ifPresent(parameterData ->
                    setupMethod.addBodyCodeLine(String.format("this.specification = where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(\"%s\"), %s));",
                            parameterData.getCamelNameFirstLetterLower(),
                            DataType.resolveByClass(parameterData.getDataType()).getDefalutValue()))
            );
        }

        setupMethod.addBodyCodeLine(String.format(
                "when(this.%s.findAll(this.pageable)).thenReturn(this.page);",
                getNameFirstLetterLower(modelData, REPOSITORY)
        )).addBodyCodeLine(String.format(
                        "when(this.%s.findAll(this.specification, this.pageable)).thenReturn(this.page);",
                        getNameFirstLetterLower(modelData, REPOSITORY)
                )
        ).addBodyCodeLine(String.format(
                "when(this.%s.findById(this.%s.getId())).thenReturn(Optional.of(this.%s));",
                getNameFirstLetterLower(modelData, REPOSITORY),
                getNameFirstLetterLower(modelData, ENTITY),
                getNameFirstLetterLower(modelData, ENTITY)
        )).addBodyCodeLine(String.format(
                "when(this.%s.save(this.%s)).thenReturn(this.%s);",
                getNameFirstLetterLower(modelData, REPOSITORY),
                getNameFirstLetterLower(modelData, ENTITY),
                getNameFirstLetterLower(modelData, ENTITY)
        )).addBodyCodeLine(String.format(
                "doNothing().when(this.%s).delete(this.%s);",
                getNameFirstLetterLower(modelData, REPOSITORY),
                getNameFirstLetterLower(modelData, ENTITY)
        ));


        serviceTestPackage.addImport(getImport(modelData, CREATOR));
        serviceTestPackage.addImport(PageImpl.class);
        serviceTestPackage.addImport(Optional.class);
        serviceTestPackage.addImport(List.class);
        serviceTestPackage.addStaticImport("org.mockito.BDDMockito.*");
        serviceTestPackage.addStaticImport("org.springframework.data.jpa.domain.Specification.*");

        testServiceClass.addMethod(setupMethod);


        FunctionSourceGenerator testFindAll = FunctionSourceGenerator.create(StringUtils.getLowerSeparatedByUnderscore("teste list retorna instancia de page com lista de entidades"))
                .setReturnType("void")
                .addAnnotation(AnnotationSourceGenerator.create(Test.class))
                .addBodyCodeLine(String.format(
                        "assertThatCode(()-> this.%s.list(this.pageable)).doesNotThrowAnyException();",
                        getNameFirstLetterLower(modelData, SERVICE)
                ))
                .addBodyCodeLine(String.format(
                        "Page<%s> result = this.%s.list(this.pageable);",
                        getNameFirstLetterUpper(modelData, ENTITY),
                        getNameFirstLetterLower(modelData, SERVICE)
                )).addBodyCodeLine(String.format(
                        "assertThat(result).contains(this.orcamentoEntity);",
                        getNameFirstLetterLower(modelData, ENTITY)
                )).addBodyCodeLine(
                        "assertThat(result.getTotalElements()).isEqualTo(1);"
                ).addBodyCodeLine(
                        "assertThat(result.getNumber()).isEqualTo(0);"
                ).addBodyCodeLine(
                        "assertThat(result.getTotalPages()).isEqualTo(1);"
                ).addBodyCodeLine(String.format(
                        "verify(this.%s, times(2)).findAll(this.pageable);",
                        getNameFirstLetterLower(modelData, REPOSITORY)
                ));

        serviceTestPackage.addStaticImport("org.assertj.core.api.Assertions.*");

        testServiceClass.addMethod(testFindAll);


        FunctionSourceGenerator listSpecification = FunctionSourceGenerator.create(StringUtils.getLowerSeparatedByUnderscore("teste listSpecification retorna instancia de page com lista de entidades"))
                .setReturnType("void")
                .addAnnotation(AnnotationSourceGenerator.create(Test.class))
                .addBodyCodeLine(String.format(
                        "assertThatCode(()-> this.%s.listSpecification(this.pageable, this.specification)).doesNotThrowAnyException();",
                        getNameFirstLetterLower(modelData, SERVICE)
                ))
                .addBodyCodeLine(String.format(
                        "Page<%s> result = this.%s.listSpecification(this.pageable, this.specification);",
                        getNameFirstLetterUpper(modelData, ENTITY),
                        getNameFirstLetterLower(modelData, SERVICE)
                )).addBodyCodeLine(String.format(
                        "assertThat(result).contains(this.orcamentoEntity);",
                        getNameFirstLetterLower(modelData, ENTITY)
                )).addBodyCodeLine(
                        "assertThat(result.getTotalElements()).isEqualTo(1);"
                ).addBodyCodeLine(
                        "assertThat(result.getNumber()).isEqualTo(0);"
                ).addBodyCodeLine(
                        "assertThat(result.getTotalPages()).isEqualTo(1);"
                )
                .addBodyCodeLine(String.format(
                        "verify(this.%s, times(2)).findAll(this.specification,this.pageable);",
                        getNameFirstLetterLower(modelData, REPOSITORY)
                ));


        testServiceClass.addMethod(listSpecification);


        FunctionSourceGenerator findById = FunctionSourceGenerator.create(StringUtils.getLowerSeparatedByUnderscore("teste findById retorna instancia de entidade"))
                .setReturnType("void")
                .addAnnotation(AnnotationSourceGenerator.create(Test.class))
                .addBodyCodeLine(String.format(
                        "assertThatCode(() -> this.%s.findById(this.%s.getId())).doesNotThrowAnyException();",
                        getNameFirstLetterLower(modelData, SERVICE),
                        getNameFirstLetterLower(modelData, ENTITY)

                ))
                .addBodyCodeLine(String.format(
                        "%s result = this.%s.findById(this.%s.getId());",
                        getNameFirstLetterUpper(modelData, ENTITY),
                        getNameFirstLetterLower(modelData, SERVICE),
                        getNameFirstLetterLower(modelData, ENTITY)
                ))
                .addBodyCodeLine(String.format(
                        "assertThat(result).isNotNull().isEqualTo(this.%s);",
                        getNameFirstLetterLower(modelData, ENTITY)

                ))
                .addBodyCodeLine(String.format(
                        "verify(this.%s, times(2)).findById(this.%s.getId());",
                        getNameFirstLetterLower(modelData, REPOSITORY),
                        getNameFirstLetterLower(modelData, ENTITY)
                ));


        testServiceClass.addMethod(findById);


        FunctionSourceGenerator create = FunctionSourceGenerator.create(StringUtils.getLowerSeparatedByUnderscore("teste create retorna instancia de entidade perisitida"))
                .setReturnType("void")
                .addAnnotation(AnnotationSourceGenerator.create(Test.class))
                .addBodyCodeLine(String.format(
                        "assertThatCode(() -> this.%s.create(this.%s)).doesNotThrowAnyException();",
                        getNameFirstLetterLower(modelData, SERVICE),
                        getNameFirstLetterLower(modelData, ENTITY)

                ))
                .addBodyCodeLine(String.format(
                        "%s result = this.%s.create(this.%s);",
                        getNameFirstLetterUpper(modelData, ENTITY),
                        getNameFirstLetterLower(modelData, SERVICE),
                        getNameFirstLetterLower(modelData, ENTITY)
                ))
                .addBodyCodeLine(String.format(
                        "assertThat(result).isNotNull().isEqualTo(this.%s);",
                        getNameFirstLetterLower(modelData, ENTITY)

                ))
                .addBodyCodeLine(String.format(
                        "verify(this.%s, times(2)).save(this.%s);",
                        getNameFirstLetterLower(modelData, REPOSITORY),
                        getNameFirstLetterLower(modelData, ENTITY)
                ));


        testServiceClass.addMethod(create);


        FunctionSourceGenerator update = FunctionSourceGenerator.create(StringUtils.getLowerSeparatedByUnderscore("teste update retorna instancia de entidade atualizada"))
                .setReturnType("void")
                .addAnnotation(AnnotationSourceGenerator.create(Test.class))
                .addBodyCodeLine(String.format(
                        "%s newData = new %s();",
                        getNameFirstLetterUpper(modelData, ENTITY),
                        getNameFirstLetterUpper(modelData, ENTITY)
                ));

        modelData.getNonPrimaryKeys().forEach(parameterData -> {
            update.addBodyCodeLine(String.format(
                    "newData.set%s(%s);",
                    parameterData.getCamelNameFirstLetterUpper(),
                    DataType.resolveByClass(parameterData.getDataType()).getUpdatedDefaultValue()
            ));

        });
        if (modelData.getIsComposePrimaryKey()) {
            update.addBodyCodeLine(String.format(
                            "assertThatCode(() -> this.%s.update(newData,this.%s.getId())).doesNotThrowAnyException();",
                            getNameFirstLetterLower(modelData, SERVICE),
                            getNameFirstLetterLower(modelData, ENTITY)

                    ))
                    .addBodyCodeLine(String.format(
                            "%s result = this.%s.update(newData, this.%s.getId());",
                            getNameFirstLetterUpper(modelData, ENTITY),
                            getNameFirstLetterLower(modelData, SERVICE),
                            getNameFirstLetterLower(modelData, ENTITY)
                    ));
        } else {

            modelData.getPrimaryKeys().stream().findFirst().ifPresent(parameterData -> {
                update.addBodyCodeLine(String.format(
                                "assertThatCode(() -> this.%s.update(newData, this.%s.get%s())).doesNotThrowAnyException();",
                                getNameFirstLetterLower(modelData, SERVICE),
                                getNameFirstLetterLower(modelData, ENTITY),
                                parameterData.getCamelNameFirstLetterUpper()

                        ))
                        .addBodyCodeLine(String.format(
                                "%s result = this.%s.update(newData, this.%s.get%s());",
                                getNameFirstLetterUpper(modelData, ENTITY),
                                getNameFirstLetterLower(modelData, SERVICE),
                                getNameFirstLetterLower(modelData, ENTITY),
                                parameterData.getCamelNameFirstLetterUpper()
                        ));
            });
        }

        update.addBodyCodeLine(String.format(
                "assertThat(result).isNotNull().isEqualTo(this.%s);",
                getNameFirstLetterLower(modelData, ENTITY)

        ));

        modelData.getNonPrimaryKeys().forEach(parameterData -> {
            update.addBodyCodeLine(String.format(
                    "assertThat(result.get%s()).isEqualTo(newData.get%s());",
                    parameterData.getCamelNameFirstLetterUpper(),
                    parameterData.getCamelNameFirstLetterUpper()
            ));

            serviceTestPackage.addImport(parameterData.getDataType());

        });

        serviceTestPackage.addImport(ChronoUnit.class);

        testServiceClass.addMethod(update);


        FunctionSourceGenerator delete = FunctionSourceGenerator.create(StringUtils.getLowerSeparatedByUnderscore("teste delete remove o registro"))
                .setReturnType("void")
                .addAnnotation(AnnotationSourceGenerator.create(Test.class))
                .addBodyCodeLine(String.format(
                        " assertThatCode(() -> this.%s.delete(this.%s.getId())).doesNotThrowAnyException();",
                        getNameFirstLetterLower(modelData, SERVICE),
                        getNameFirstLetterLower(modelData, ENTITY)
                )).addBodyCodeLine(String.format(
                                "verify(this.%s, atMostOnce()).delete(this.%s);",
                                getNameFirstLetterLower(modelData, REPOSITORY),
                                getNameFirstLetterLower(modelData, ENTITY)

                        )
                );

        testServiceClass.addMethod(delete);

        serviceTestPackage.addClass(testServiceClass);

        return List.of(serviceTestPackage);
    }
}
