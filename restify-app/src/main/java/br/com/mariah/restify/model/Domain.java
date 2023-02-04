package br.com.mariah.restify.model;

import br.com.mariah.restify.enums.ElementType;
import br.com.mariah.restify.exception.ElementNotGeneratedException;

import java.util.ArrayList;
import java.util.List;

public class Domain {
    private List<Element> elements = new ArrayList<>();


    public void addElement(Element element){
        this.elements.add(element);
    }

    public Element getElementByType(ElementType elementType) {
        return this.elements.stream().filter(element -> element.getElementType().equals(elementType))
                .findFirst().orElseThrow(() -> new ElementNotGeneratedException(String.format("Elemento %s não criado!", elementType)));
    }

    public Element getElementByName(String elementName) {
        return this.elements.stream().filter(element -> element.getElementName().trim().equalsIgnoreCase(elementName))
                .findFirst().orElseThrow(() -> new ElementNotGeneratedException(String.format("Elemento %s não criado!", elementName)));
    }
}
