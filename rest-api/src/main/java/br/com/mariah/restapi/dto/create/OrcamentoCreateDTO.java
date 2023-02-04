package br.com.mariah.restapi.dto.create;

import br.com.mariah.restapi.domain.OrcamentoEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.Long;
import java.lang.String;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude (
	value = JsonInclude.Include.NON_ABSENT 
)
public class OrcamentoCreateDTO { 

	@JsonProperty (
		value = "ID" 
	)
	private Long id;
	@JsonProperty (
		value = "NOME" 
	)
	private String nome;
	@JsonProperty (
		value = "DATA_INICIO" 
	)
	private LocalDateTime dataInicio;
	@JsonProperty (
		value = "DATA_FIM" 
	)
	private LocalDateTime dataFim; 

	public OrcamentoEntity toEntity() {
		return OrcamentoEntity.builder() 
			.id(this.id) 
			.nome(this.nome) 
			.dataInicio(this.dataInicio) 
			.dataFim(this.dataFim) 
			.build(); 
	} 

}