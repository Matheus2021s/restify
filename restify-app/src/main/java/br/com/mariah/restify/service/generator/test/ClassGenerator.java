package br.com.mariah.restify.service.generator.test;

import br.com.mariah.restify.enums.DataType;
import br.com.mariah.restify.enums.ElementType;
import br.com.mariah.restify.model.Element;
import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.service.generator.GeneratorService;
import br.com.mariah.restify.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.ClassSourceGenerator;
import org.burningwave.core.classes.FunctionSourceGenerator;
import org.burningwave.core.classes.TypeDeclarationSourceGenerator;
import org.burningwave.core.classes.UnitSourceGenerator;

import java.lang.reflect.Modifier;
import java.util.List;

import static br.com.mariah.restify.enums.ElementType.EMBEDDED_ENTITY;
import static br.com.mariah.restify.enums.ElementType.ENTITY;
import static br.com.mariah.restify.service.helper.ElementHelper.*;

@RequiredArgsConstructor
public class ClassGenerator implements GeneratorService {
    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {

        String destinationPackage = String.format("%s.utils.creator", resourceUtils.getBasePackage());

        UnitSourceGenerator unitSourceGenerator = UnitSourceGenerator.create(destinationPackage);

        String creatorClassName = String.format("%sCreator", modelData.getCamelNameFirstLetterUpper());

        ClassSourceGenerator classGenerator = ClassSourceGenerator.create(
                        TypeDeclarationSourceGenerator.create(creatorClassName)
                )
                .addModifier(Modifier.PUBLIC);


        FunctionSourceGenerator getEntity = FunctionSourceGenerator.create("getEntity")
                .addModifier(Modifier.PUBLIC)
                .addModifier(Modifier.STATIC)
                .setReturnType(getNameFirstLetterUpper(modelData, ENTITY))
                .addBodyCodeLine(
                        String.format("%s %s = new %s();",
                                getNameFirstLetterUpper(modelData, ENTITY),
                                getNameFirstLetterLower(modelData, ENTITY),
                                getNameFirstLetterUpper(modelData, ENTITY)
                        ));

        unitSourceGenerator.addImport(getImport(modelData, ENTITY));

        if (modelData.getIsComposePrimaryKey()) {
            getEntity
                    .addBodyCodeLine(
                            String.format("%s %s = new %s();",
                                    getNameFirstLetterUpper(modelData, EMBEDDED_ENTITY),
                                    getNameFirstLetterLower(modelData, EMBEDDED_ENTITY),
                                    getNameFirstLetterUpper(modelData, EMBEDDED_ENTITY)
                            ));


            modelData.getPrimaryKeys().forEach(
                    parameterData -> {
                        getEntity
                                .addBodyCodeLine(
                                        String.format("%s.getId().set%s(%s);",
                                                getNameFirstLetterLower(modelData, EMBEDDED_ENTITY),
                                                parameterData.getCamelNameFirstLetterUpper(),
                                                DataType.resolveByClass(parameterData.getDataType()).getDefalutValue()

                                        ));
                        unitSourceGenerator.addImport(parameterData.getDataType());

                    });

            getEntity
                    .addBodyCodeLine(String
                            .format("%s.setId(%s);",
                                    getNameFirstLetterLower(modelData, ENTITY),
                                    getNameFirstLetterLower(modelData, EMBEDDED_ENTITY)
                            ));

            modelData.getNonPrimaryKeys().forEach(
                    parameterData -> {
                        getEntity
                                .addBodyCodeLine(
                                        String.format("%s.set%s(%s);",
                                                getNameFirstLetterLower(modelData, ENTITY),
                                                parameterData.getCamelNameFirstLetterUpper(),
                                                DataType.resolveByClass(parameterData.getDataType()).getDefalutValue()

                                        ));
                        unitSourceGenerator.addImport(parameterData.getDataType());
                    });

            unitSourceGenerator.addImport(getImport(modelData, EMBEDDED_ENTITY));

        } else {

            modelData.getParameters().forEach(
                    parameterData -> {
                        getEntity
                                .addBodyCodeLine(
                                        String.format("%s.set%s(%s);",
                                                getNameFirstLetterLower(modelData, ENTITY),
                                                parameterData.getCamelNameFirstLetterUpper(),
                                                DataType.resolveByClass(parameterData.getDataType()).getDefalutValue()

                                        ));
                        unitSourceGenerator.addImport(parameterData.getDataType());

                    });

        }
        getEntity
                .addBodyCodeLine(
                        String.format("return %s;",
                                getNameFirstLetterLower(modelData, ENTITY))
                );


        classGenerator.addMethod(getEntity);

        unitSourceGenerator.addClass(classGenerator);


        modelData.getDomain().addElement(Element.of(ElementType.CREATOR, destinationPackage, creatorClassName));
        return List.of(unitSourceGenerator);
    }
}
