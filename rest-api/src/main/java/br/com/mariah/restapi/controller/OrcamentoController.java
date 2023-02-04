package br.com.mariah.restapi.controller;

import br.com.mariah.restapi.domain.OrcamentoEntity;
import br.com.mariah.restapi.dto.create.OrcamentoCreateDTO;
import br.com.mariah.restapi.dto.response.OrcamentoResponseDTO;
import br.com.mariah.restapi.dto.search.OrcamentoSearch;
import br.com.mariah.restapi.dto.update.OrcamentoUpdateDTO;
import br.com.mariah.restapi.service.OrcamentoService;
import br.com.mariah.restapi.utils.SpecificationUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.lang.Long;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping (
	value = "/orcamento" 
)
@RequiredArgsConstructor
public class OrcamentoController { 

	private final OrcamentoService orcamentoService; 

	@GetMapping
	public ResponseEntity list(
		Pageable pageable,
		@ModelAttribute
		OrcamentoSearch orcamentoSearch
	) {
		return ResponseEntity.ok(Optional.ofNullable(SpecificationUtils.generateSpecification(orcamentoSearch, OrcamentoEntity.class)) 
		.map(objectSpecification -> OrcamentoResponseDTO.ofPage(this.orcamentoService.listSpecification(pageable, objectSpecification))) 
		.orElse(OrcamentoResponseDTO.ofPage(this.orcamentoService.list(pageable)))); 
	}
	
	@GetMapping (
		value = "/{id}" 
	)
	public ResponseEntity getById(
		@PathVariable
		Long id
	) {
		return ResponseEntity.ok(OrcamentoResponseDTO.of(this.orcamentoService.findById(id))); 
	}
	
	@PostMapping
	public ResponseEntity create(
		@RequestBody
		@Valid
		OrcamentoCreateDTO orcamentoCreateDTO
	) {
		OrcamentoEntity entity = this.orcamentoService.create(orcamentoCreateDTO.toEntity()); 
		return ResponseEntity.created(URI.create("/orcamento")).body(OrcamentoResponseDTO.of(entity)); 
	}
	
	@PutMapping (
		value = "/{id}" 
	)
	public ResponseEntity update(
		@Valid
		@RequestBody
		OrcamentoUpdateDTO orcamentoUpdateDTO,
		@Valid
		@NotNull
		@PathVariable (
			name = "id" 
		)
		Long id
	) {
		return ResponseEntity.ok(OrcamentoResponseDTO.of(this.orcamentoService.update(orcamentoUpdateDTO.toEntity(),id))); 
	}
	
	@DeleteMapping (
		value = "/{id}" 
	)
	public ResponseEntity delete(
		@Valid
		@NotNull
		@PathVariable (
			name = "id" 
		)
		Long id
	) {
		this.orcamentoService.delete(id); 
		return ResponseEntity.noContent().build(); 
	} 

}