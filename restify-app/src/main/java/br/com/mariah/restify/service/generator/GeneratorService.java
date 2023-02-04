package br.com.mariah.restify.service.generator;

import br.com.mariah.restify.model.ModelData;
import org.burningwave.core.classes.UnitSourceGenerator;

import java.util.List;

public interface GeneratorService {
    List<UnitSourceGenerator> generate(ModelData modelData);
}
