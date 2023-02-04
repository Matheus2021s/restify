package br.com.mariah.restapi.utils.creator;

import br.com.mariah.restapi.domain.OrcamentoEntity;
import java.lang.Long;
import java.lang.String;
import java.time.LocalDateTime;

public class OrcamentoCreator { 

	public static OrcamentoEntity getEntity() {
		OrcamentoEntity orcamentoEntity = new OrcamentoEntity(); 
		orcamentoEntity.setId(1L); 
		orcamentoEntity.setNome("String"); 
		orcamentoEntity.setDataInicio(LocalDateTime.now()); 
		orcamentoEntity.setDataFim(LocalDateTime.now()); 
		return orcamentoEntity; 
	} 

}