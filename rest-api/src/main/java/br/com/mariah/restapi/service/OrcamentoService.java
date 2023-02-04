package br.com.mariah.restapi.service;

import br.com.mariah.restapi.domain.OrcamentoEntity;
import br.com.mariah.restapi.exception.ResourceNotFoundException;
import br.com.mariah.restapi.repository.OrcamentoRepository;
import java.lang.Long;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrcamentoService { 

	private final OrcamentoRepository repository; 

	public Page<OrcamentoEntity> list(
		Pageable pageable
	) {
		return this.repository.findAll(pageable); 
	}
	
	public Page<OrcamentoEntity> listSpecification(
		Pageable pageable,
		Specification specification
	) {
		return this.repository.findAll(specification, pageable); 
	}
	
	public OrcamentoEntity findById(
		Long id
	) {
		return this.repository.findById(id) 
		.orElseThrow(()-> new ResourceNotFoundException(" Orcamento não encontrado!")); 
	}
	
	@Transactional
	public OrcamentoEntity create(
		OrcamentoEntity entity
	) {
		return this.repository.save(entity); 
	}
	
	@Transactional
	public OrcamentoEntity update(
		OrcamentoEntity entity,
		Long id
	) {
		OrcamentoEntity entityFound = findById(id); 
		entityFound.setNome(entity.getNome()); 
		entityFound.setDataInicio(entity.getDataInicio()); 
		entityFound.setDataFim(entity.getDataFim()); 
		return entityFound; 
	}
	
	@Transactional
	public void delete(
		Long id
	) {
		this.repository.delete(findById(id)); 
	} 

}