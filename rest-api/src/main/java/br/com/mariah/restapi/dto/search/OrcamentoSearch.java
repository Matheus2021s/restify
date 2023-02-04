package br.com.mariah.restapi.dto.search;

import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoSearch implements SearchDTO { 

	private String id;
	private String nome;
	private String data_inicio;
	private String data_fim; 

	@Override
	public List<SearchField> getFields() {
		List<SearchField> fields = new ArrayList<>(); 
		SearchField.includeQuery(fields,Long.class, this.id, "id"); 
		SearchField.includeQuery(fields,String.class, this.nome, "nome"); 
		SearchField.includeQuery(fields,LocalDateTime.class, this.data_inicio, "dataInicio"); 
		SearchField.includeQuery(fields,LocalDateTime.class, this.data_fim, "dataFim"); 
		return fields; 
	} 

}