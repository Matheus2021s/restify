package br.com.mariah.restify;

import br.com.mariah.restify.model.ModelData;
import br.com.mariah.restify.model.ParameterData;
import br.com.mariah.restify.service.generator.ClassGeneratorsService;

import java.time.LocalDateTime;
import java.util.List;

public class RestifyApplication {
    private final ClassGeneratorsService classGeneratorsService;

    public RestifyApplication() {
        this.classGeneratorsService = new ClassGeneratorsService();
    }

    public void generate() {

        System.out.println("Hello world!");


        ModelData modelData = ModelData.builder()
                .name("orcamento")
                .parameters(List.of(
                                ParameterData.builder()
                                        .name("id")
                                        .dataType(Long.class)
                                        .isPrimaryKey(true)
                                        .build()
                                ,
                                ParameterData.builder()
                                        .name("nome")
                                        .dataType(String.class)
                                        .build(),

                                ParameterData.builder()
                                        .name("dataInicio")
                                        .dataType(LocalDateTime.class)
                                        .build(),

                        ParameterData.builder()
                                .name("dataFim")
                                .dataType(LocalDateTime.class)
                                .build()
                        )
                )
                .build();

        this.classGeneratorsService.generate(modelData);
    }
}