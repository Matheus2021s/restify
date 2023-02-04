package br.com.mariah.restify.service.generator;

import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.service.generator.common.*;
import br.com.mariah.restify.service.generator.test.ClassGenerator;
import br.com.mariah.restify.service.generator.test.ServiceTestGenerator;
import br.com.mariah.restify.service.storage.StorageService;
import br.com.mariah.restify.utils.ResourceUtils;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ClassGeneratorsService {

    private final EntityGenerator entityGenerator;
    private final DtoGeneratorService dtoGeneratorService;
    private final SearchDTOGeneratorService searchDTOGeneratorService;
    private final RepositoryGenerator repositoryGenerator;
    private final ServiceGenerator serviceGenerator;
    private final ControllerGeneratorService controllerGeneratorService;
    private final StorageService storageService;

    private final ServiceTestGenerator serviceTestGenerator;

    private final ClassGenerator classGenerator;


    public ClassGeneratorsService() {
        ResourceUtils resourceUtils = new ResourceUtils();
        this.entityGenerator = new EntityGenerator(resourceUtils);
        this.dtoGeneratorService = new DtoGeneratorService(resourceUtils);
        this.searchDTOGeneratorService = new SearchDTOGeneratorService(resourceUtils);
        this.repositoryGenerator = new RepositoryGenerator(resourceUtils);
        this.serviceGenerator = new ServiceGenerator(resourceUtils);
        this.controllerGeneratorService = new ControllerGeneratorService(resourceUtils);
        this.storageService = new StorageService(resourceUtils);

        this.classGenerator = new ClassGenerator(resourceUtils);
        this.serviceTestGenerator = new ServiceTestGenerator(resourceUtils);
    }


    public void generate(ModelData modelData) {
        List<GeneratorService> generators = new ArrayList<>();
        generators.add(entityGenerator);
        generators.add(dtoGeneratorService);
        generators.add(searchDTOGeneratorService);
        generators.add(repositoryGenerator);
        generators.add(serviceGenerator);
        generators.add(controllerGeneratorService);

        generators.forEach(generatorService ->
                Optional.ofNullable(generatorService.generate(modelData))
                        .ifPresent(unitSourceGenerators -> storageService.store(unitSourceGenerators))
        );


        List<GeneratorService> testGenerators = new ArrayList<>();

        testGenerators.add(classGenerator);
        testGenerators.add(serviceTestGenerator);

        testGenerators.forEach(generatorService ->
                Optional.ofNullable(generatorService.generate(modelData))
                        .ifPresent(unitSourceGenerators -> storageService.storeTest(unitSourceGenerators))
        );

    }
}
