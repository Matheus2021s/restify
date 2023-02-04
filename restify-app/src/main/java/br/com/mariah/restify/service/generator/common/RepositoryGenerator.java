package br.com.mariah.restify.service.generator.common;

import br.com.mariah.restify.model.Element;
import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.model.ParameterData;
import br.com.mariah.restify.service.generator.GeneratorService;
import br.com.mariah.restify.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import org.burningwave.core.classes.ClassSourceGenerator;
import org.burningwave.core.classes.GenericSourceGenerator;
import org.burningwave.core.classes.TypeDeclarationSourceGenerator;
import org.burningwave.core.classes.UnitSourceGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.Modifier;
import java.util.List;

import static br.com.mariah.restify.enums.ElementType.*;
import static br.com.mariah.restify.service.helper.ElementHelper.getImport;
import static br.com.mariah.restify.service.helper.ElementHelper.getNameFirstLetterUpper;

@RequiredArgsConstructor
public class RepositoryGenerator implements GeneratorService {

    private final ResourceUtils resourceUtils;

    @Override
    public List<UnitSourceGenerator> generate(ModelData modelData) {

        String repositoryPackage = String.format("%s.repository", resourceUtils.getBasePackage());
        UnitSourceGenerator unitSourceGenerator = UnitSourceGenerator
                .create(repositoryPackage);


        String repositoryName = String.format("%sRepository", modelData.getCamelNameFirstLetterUpper());

        unitSourceGenerator.addClass(
                ClassSourceGenerator
                        .createInterface(
                                TypeDeclarationSourceGenerator
                                        .create(repositoryName)
                        ).expands(
                                TypeDeclarationSourceGenerator
                                        .create(JpaRepository.class)
                                        .addGeneric(
                                                GenericSourceGenerator
                                                        .create(getNameFirstLetterUpper(modelData, ENTITY))
                                        ).addGeneric(
                                                GenericSourceGenerator
                                                        .create(getRepositoryDataType(modelData, unitSourceGenerator))
                                        )
                        ).expands(
                                TypeDeclarationSourceGenerator
                                        .create(JpaSpecificationExecutor.class)
                                        .addGeneric(
                                                GenericSourceGenerator
                                                        .create(getNameFirstLetterUpper(modelData, ENTITY))
                                        )
                        )
                        .addModifier(Modifier.PUBLIC)

        );

        modelData.getDomain().addElement(Element.of(REPOSITORY, repositoryPackage, repositoryName));

        unitSourceGenerator.addImport(getImport(modelData, ENTITY));

        return List.of(unitSourceGenerator);
    }

    private String getRepositoryDataType(ModelData modelData, UnitSourceGenerator unitSourceGenerator) {
        if (modelData.getIsComposePrimaryKey()) {
            String embedded = getNameFirstLetterUpper(modelData, EMBEDDED_ENTITY);
            unitSourceGenerator.addImport(getImport(modelData, EMBEDDED_ENTITY));
            return embedded;
        }

        Class<?> result = null;
        for (ParameterData data : modelData.getParameters()) {
            if (data.getIsPrimaryKey()) {
                result = data.getDataType();
                break;
            }
        }
        unitSourceGenerator.addImport(result);
        return result.getSimpleName();
    }
}
