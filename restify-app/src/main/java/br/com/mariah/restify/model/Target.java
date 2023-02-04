package br.com.mariah.restify.model;

import lombok.Getter;
import org.burningwave.core.classes.UnitSourceGenerator;

import java.util.List;


@Getter
public class Target {
    private final Element element;
    private final List<UnitSourceGenerator> unitSourceGenerators;

    protected Target(Element element, List<UnitSourceGenerator> unitSourceGenerators) {
        this.element = element;
        this.unitSourceGenerators = unitSourceGenerators;
    }

    public static Target of(Element element, List<UnitSourceGenerator> unitSourceGenerators){
        return new Target(element, unitSourceGenerators);
    }


}
