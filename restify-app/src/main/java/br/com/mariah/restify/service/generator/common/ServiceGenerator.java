package br.com.mariah.restify.service.generator.common;

import br.com.mariah.restify.enums.ElementType;
import br.com.mariah.restify.model.Element;
import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.service.generator.GeneratorService;
import br.com.mariah.restify.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Modifier;
import java.util.List;

@RequiredArgsConstructor
public class ServiceGenerator implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {


        String servicePackage = String.format("%s.service", resourceUtils.getBasePackage());
        UnitSourceGenerator unitSourceGenerator = UnitSourceGenerator.create(servicePackage);

        String serviceName = String.format("%sService", modelData.getCamelNameFirstLetterUpper());
        ClassSourceGenerator classSourceGenerator = ClassSourceGenerator
                .create(
                        TypeDeclarationSourceGenerator
                                .create(
                                        serviceName
                                )
                )
                .addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator.create(
                                Service.class
                        )
                )
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(RequiredArgsConstructor.class)
                );


        String repositoryName = String.format("%sRepository", modelData.getCamelNameFirstLetterUpper() );
        VariableSourceGenerator repository = VariableSourceGenerator
                .create(
                        TypeDeclarationSourceGenerator
                                .create(
                                        String.format("%s.repository.%s", this.resourceUtils.getBasePackage(), repositoryName),
                                        repositoryName
                                ),
                        "repository"

                )
                .addModifier(Modifier.PRIVATE)
                .addModifier(Modifier.FINAL);

        classSourceGenerator.addField(repository);


        String entityName = String.format("%sEntity", modelData.getCamelNameFirstLetterUpper() );

        FunctionSourceGenerator listMethod = FunctionSourceGenerator.create("list")
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator
                                                .create(
                                                        Pageable.class
                                                ), "pageable"
                                )
                ).addModifier(Modifier.PUBLIC)
                .setReturnType(
                        TypeDeclarationSourceGenerator
                                .create(
                                        Page.class
                                ).addGeneric(
                                        GenericSourceGenerator
                                                .create(
                                                        entityName
                                                )
                                )
                )
                .addBodyCodeLine("return this.repository.findAll(pageable);");





        FunctionSourceGenerator listSpecificationMethod = FunctionSourceGenerator.create("listSpecification")
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator
                                                .create(
                                                        Pageable.class
                                                ), "pageable"
                                )
                )
                .addParameter(
                        VariableSourceGenerator.create(
                                TypeDeclarationSourceGenerator.create(Specification.class),
                                "specification"
                        )
                )
                .addModifier(Modifier.PUBLIC)
                .setReturnType(
                        TypeDeclarationSourceGenerator
                                .create(
                                        Page.class
                                ).addGeneric(
                                        GenericSourceGenerator
                                                .create(
                                                        entityName
                                                )
                                )
                )
                .addBodyCodeLine("return this.repository.findAll(specification, pageable);");

        unitSourceGenerator.addImport(String.format("%s.domain.%s", resourceUtils.getBasePackage(), entityName));


        Boolean isComposePrimaryKey = modelData.getIsComposePrimaryKey();

        FunctionSourceGenerator findByIdMethod = FunctionSourceGenerator
                .create("findById")
                .addModifier(Modifier.PUBLIC)
                .setReturnType(entityName)
                .addBodyCodeLine("return this.repository.findById(id)")
                .addBodyCodeLine(String.format(".orElseThrow(()-> new ResourceNotFoundException(\" %s não encontrado!\"));", modelData.getCamelNameFirstLetterUpper() ));

        unitSourceGenerator.addImport(String.format("%s.exception.ResourceNotFoundException", resourceUtils.getBasePackage()));


        FunctionSourceGenerator deleteMethod = FunctionSourceGenerator
                .create("delete")
                .addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(Transactional.class)
                )
                .setReturnType("void")
                .addBodyCodeLine("this.repository.delete(findById(id));");


        FunctionSourceGenerator updateMethod = FunctionSourceGenerator.create("update")
                .addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(Transactional.class)
                )
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator.create(entityName),
                                        "entity"
                                )
                )
                .setReturnType(
                        TypeDeclarationSourceGenerator.create(entityName)
                )
                .addBodyCodeLine(String.format("%s entityFound = findById(id);", entityName));


        List<FunctionSourceGenerator> commonMethod = List.of(findByIdMethod, deleteMethod, updateMethod);

        if (isComposePrimaryKey) {

            String embeddedName = String.format("%sEmbeddedId", modelData.getCamelNameFirstLetterUpper() );
            String embeddedImport = String.format("%s.domain.%s", resourceUtils.getBasePackage(), embeddedName);

            commonMethod.forEach(functionSourceGenerator -> functionSourceGenerator.addParameter(
                    VariableSourceGenerator
                            .create(
                                    TypeDeclarationSourceGenerator
                                            .create(
                                                    embeddedImport, embeddedName
                                            ), "id"
                            )
            ));

     /*       modelData.getPrimaryKeys().forEach(parameterData -> {
                updateMethod.addBodyCodeLine(String.format("entityFound.getId().set%s(entity.getId().get%s());", parameterData.getCamelNameFirstLetterUpper(), parameterData.getCamelNameFirstLetterUpper()));
            });*/

            modelData.getNonPrimaryKeys().forEach(parameterData -> {
                updateMethod.addBodyCodeLine(String.format("entityFound.set%s(entity.get%s());", parameterData.getCamelNameFirstLetterUpper(), parameterData.getCamelNameFirstLetterUpper()));
            });

        } else {
            modelData.getPrimaryKeys()
                    .forEach(parameterData -> {
                        commonMethod.forEach(functionSourceGenerator -> functionSourceGenerator.addParameter(
                                VariableSourceGenerator
                                        .create(
                                                TypeDeclarationSourceGenerator
                                                        .create(
                                                                parameterData.getDataType()
                                                        ), "id"
                                        )
                        ));
                    });
            modelData.getNonPrimaryKeys().forEach(parameterData -> {
                updateMethod.addBodyCodeLine(String.format("entityFound.set%s(entity.get%s());", parameterData.getCamelNameFirstLetterUpper(), parameterData.getCamelNameFirstLetterUpper()));
            });
        }


        updateMethod.addBodyCodeLine("return entityFound;");


        FunctionSourceGenerator createMethod = FunctionSourceGenerator.create("create")
                .addModifier(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSourceGenerator
                                .create(Transactional.class)
                )
                .addParameter(
                        VariableSourceGenerator
                                .create(
                                        TypeDeclarationSourceGenerator.create(entityName),
                                        "entity"
                                )
                )
                .setReturnType(entityName)
                .addBodyCodeLine("return this.repository.save(entity);");


        classSourceGenerator.addMethod(listMethod);
        classSourceGenerator.addMethod(listSpecificationMethod);
        classSourceGenerator.addMethod(findByIdMethod);
        classSourceGenerator.addMethod(createMethod);
        classSourceGenerator.addMethod(updateMethod);
        classSourceGenerator.addMethod(deleteMethod);
        unitSourceGenerator.addClass(
                classSourceGenerator
        );

        modelData.getDomain().addElement(Element.of(ElementType.SERVICE, servicePackage, serviceName));

        return List.of(unitSourceGenerator);
    }
}
