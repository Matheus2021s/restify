package br.com.mariah.restify.service.generator.common;

import br.com.mariah.restify.enums.ElementType;
import br.com.mariah.restify.model.Element;
import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.service.generator.GeneratorService;
import br.com.mariah.restify.utils.ResourceUtils;
import jakarta.persistence.*;
import lombok.*;
import org.burningwave.core.classes.*;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.List;

@RequiredArgsConstructor
public class EntityGenerator implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {

        String domainPackage = String.format("%s.domain", resourceUtils.getBasePackage());

        UnitSourceGenerator unitSourceGenerator = UnitSourceGenerator
                .create(domainPackage);

        String entityClassName = String.format("%sEntity", modelData.getCamelNameFirstLetterUpper());

        ClassSourceGenerator classSource = ClassSourceGenerator
                .create(TypeDeclarationSourceGenerator
                        .create(entityClassName)
                ).addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(Entity.class)
                )
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(Getter.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(Setter.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(AllArgsConstructor.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(NoArgsConstructor.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(Builder.class)
                ).addAnnotation(
                        AnnotationSourceGenerator
                                .create(Table.class)
                                .addParameter(
                                        VariableSourceGenerator
                                                .create("name")
                                                .setValue(String.format("\"%s\"", modelData.getNameUpperSeparatedByUnderscore()))
                                )
                )
                .addConcretizedType(Serializable.class);


        modelData.getDomain().addElement(Element.of(ElementType.ENTITY, domainPackage, entityClassName));


        if (modelData.getIsComposePrimaryKey()) {

            String embeddedName = String.format("%sEmbeddedId", modelData.getCamelNameFirstLetterUpper());

            UnitSourceGenerator embeddedUnitSource = UnitSourceGenerator
                    .create(domainPackage);

            ClassSourceGenerator embeddedEntityClass = ClassSourceGenerator.create(
                            TypeDeclarationSourceGenerator
                                    .create(
                                            embeddedName
                                    )
                    )
                    .addModifier(Modifier.PUBLIC)
                    .addAnnotation(
                            AnnotationSourceGenerator
                                    .create(Embeddable.class)
                    ).addAnnotation(
                            AnnotationSourceGenerator
                                    .create(Getter.class)
                    ).addAnnotation(
                            AnnotationSourceGenerator
                                    .create(Setter.class)
                    ).addAnnotation(
                            AnnotationSourceGenerator
                                    .create(AllArgsConstructor.class)
                    ).addAnnotation(
                            AnnotationSourceGenerator
                                    .create(NoArgsConstructor.class)
                    ).addAnnotation(
                            AnnotationSourceGenerator
                                    .create(Builder.class)
                    )
                    .addConcretizedType(Serializable.class);


            modelData.getPrimaryKeys().forEach(parameterData -> {
                embeddedEntityClass.addField(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator
                                                .create(parameterData.getDataType()),
                                        parameterData.getName()
                                )
                                .addModifier(Modifier.PRIVATE)
                                .addAnnotation(
                                        AnnotationSourceGenerator
                                                .create(Column.class)
                                                .addParameter(
                                                        VariableSourceGenerator
                                                                .create("name")
                                                                .setValue(String.format("\"%s\"", parameterData.getNameUpperSeparatedByUnderscore()))
                                                )
                                )
                );
            });

            modelData.getNonPrimaryKeys().forEach(parameterData -> {
                classSource.addField(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator
                                                .create(parameterData.getDataType()),
                                        parameterData.getName()
                                )
                                .addModifier(Modifier.PRIVATE)
                                .addAnnotation(
                                        AnnotationSourceGenerator
                                                .create(Column.class)
                                                .addParameter(
                                                        VariableSourceGenerator
                                                                .create("name")
                                                                .setValue(String.format("\"%s\"", parameterData.getNameUpperSeparatedByUnderscore()))
                                                )
                                )
                );
            });

            classSource.addField(
                    VariableSourceGenerator
                            .create(
                                    TypeDeclarationSourceGenerator
                                            .create(
                                                    embeddedName
                                            ),
                                    "id"
                            )
                            .addModifier(Modifier.PRIVATE)
                            .addAnnotation(
                                    AnnotationSourceGenerator
                                            .create(EmbeddedId.class)
                            )
            );

            unitSourceGenerator.addClass(classSource);

            embeddedUnitSource.addClass(embeddedEntityClass);

            modelData.getDomain().addElement(Element.of(ElementType.EMBEDDED_ENTITY, domainPackage, embeddedName));

            return List.of(unitSourceGenerator, embeddedUnitSource);
        } else {
            modelData.getParameters().forEach(parameterData -> {
                        VariableSourceGenerator parameter = VariableSourceGenerator
                                .create(TypeDeclarationSourceGenerator
                                        .create(parameterData.getDataType()), parameterData.getName())
                                .addModifier(Modifier.PRIVATE);

                        if (parameterData.getIsPrimaryKey()) {
                            parameter
                                    .addAnnotation(
                                            AnnotationSourceGenerator
                                                    .create(Id.class)
                                    )
                                    .addAnnotation(
                                            AnnotationSourceGenerator
                                                    .create(GeneratedValue.class)
                                                    .addParameter(VariableSourceGenerator.create("strategy")
                                                            .setValue("GenerationType.IDENTITY"))
                                    );
                            unitSourceGenerator.addImport("jakarta.persistence.GenerationType");
                        }

                        parameter
                                .addAnnotation(
                                        AnnotationSourceGenerator
                                                .create(Column.class)
                                                .addParameter(
                                                        VariableSourceGenerator
                                                                .create("name")
                                                                .setValue(String.format("\"%s\"", parameterData.getNameUpperSeparatedByUnderscore()))
                                                )
                                );
                        classSource.addField(parameter);
                    }
            );

        }

        unitSourceGenerator.addClass(classSource);

        return List.of(unitSourceGenerator);
    }
}
