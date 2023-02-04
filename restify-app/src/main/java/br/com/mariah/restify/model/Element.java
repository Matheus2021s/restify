package br.com.mariah.restify.model;

import br.com.mariah.restify.enums.ElementType;
import lombok.Getter;

@Getter
public class Element {


    private final ElementType elementType;

    private final String textImport;
    private final String elementName;

    protected Element(ElementType elementType, String textImport, String elementName) {
        this.elementType = elementType;
        this.textImport = textImport;
        this.elementName = elementName;
    }

    public static Element of(ElementType elementType, String textImport, String elementName) {
        return new Element(elementType, textImport, elementName);
    }

    public String getImportAndElement() {
        return String.format("%s.%s", getTextImport(), getElementName());
    }
}
