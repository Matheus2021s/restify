package br.com.mariah.restapi.dto.response;

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
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude (
	value = JsonInclude.Include.NON_ABSENT 
)
public class OrcamentoResponseDTO { 

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

	public static OrcamentoResponseDTO of(
		OrcamentoEntity orcamentoEntity
	) {
		return OrcamentoResponseDTO.builder() 
			.id(orcamentoEntity.getId()) 
			.nome(orcamentoEntity.getNome()) 
			.dataInicio(orcamentoEntity.getDataInicio()) 
			.dataFim(orcamentoEntity.getDataFim()) 
		.build(); 
	}
	
	public static Page<OrcamentoResponseDTO> ofPage(
		Page<OrcamentoEntity> page
	) {
		return page.map(OrcamentoResponseDTO::of); 
	} 

}