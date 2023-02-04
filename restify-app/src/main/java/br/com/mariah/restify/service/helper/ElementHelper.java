package br.com.mariah.restify.service.helper;

import br.com.mariah.restify.enums.ElementType;
import br.com.mariah.restify.model.Element;
import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.utils.StringUtils;
import org.burningwave.core.classes.TypeDeclarationSourceGenerator;
import org.burningwave.core.classes.VariableSourceGenerator;

import java.util.Optional;

public class ElementHelper {
    public static VariableSourceGenerator getParameter(ModelData modelData, ElementType elementType) {

        Optional<Element> elementByType = Optional.ofNullable(modelData.getDomain().getElementByType(elementType));

        if (elementByType.isPresent()) {
            Element element = elementByType.get();

            return VariableSourceGenerator.create(
                    TypeDeclarationSourceGenerator
                            .create(element.getImportAndElement(),
                                    element.getElementName()),
                    StringUtils.getWordWithFirstLetterLower(element.getElementName())
            );
        }
        return null;
    }

    public static String getImport(ModelData modelData, ElementType elementType) {
        return modelData.getDomain().getElementByType(elementType).getImportAndElement();
    }

    public static String getNameFirstLetterUpper(ModelData modelData, ElementType elementType) {
        return StringUtils.getWordWithFirstLetterUpper(
                modelData
                .getDomain()
                .getElementByType(elementType)
                .getElementName());
    }

    public static String getNameFirstLetterLower(ModelData modelData, ElementType elementType) {
        return StringUtils.getWordWithFirstLetterLower(
                modelData
                        .getDomain()
                        .getElementByType(elementType)
                        .getElementName());
    }
}
