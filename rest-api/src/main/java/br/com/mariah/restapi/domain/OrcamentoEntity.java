package br.com.mariah.restapi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table (
	name = "ORCAMENTO" 
)
public class OrcamentoEntity implements Serializable { 

	@Id
	@GeneratedValue (
		strategy = GenerationType.IDENTITY 
	)
	@Column (
		name = "ID" 
	)
	private Long id;
	@Column (
		name = "NOME" 
	)
	private String nome;
	@Column (
		name = "DATA_INICIO" 
	)
	private LocalDateTime dataInicio;
	@Column (
		name = "DATA_FIM" 
	)
	private LocalDateTime dataFim; 

}