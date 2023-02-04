package br.com.mariah.restapi.service;

import br.com.mariah.restapi.domain.OrcamentoEntity;
import br.com.mariah.restapi.repository.OrcamentoRepository;
import br.com.mariah.restapi.service.OrcamentoService;
import br.com.mariah.restapi.utils.creator.OrcamentoCreator;
import java.lang.String;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.data.jpa.domain.Specification.*;

@ExtendWith (
	SpringExtension.class 
)
class OrcamentoServiceTest { 

	@InjectMocks
	public OrcamentoService orcamentoService;
	@Mock
	private OrcamentoRepository orcamentoRepository;
	private OrcamentoEntity orcamentoEntity;
	private Pageable pageable;
	private Page page;
	private Specification<OrcamentoEntity> specification; 

	@BeforeEach
	void setUp() {
		this.orcamentoEntity = OrcamentoCreator.getEntity(); 
		this.pageable = Pageable.ofSize(20); 
		this.page = new PageImpl(List.of(this.orcamentoEntity)); 
		this.specification = where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), 1L)); 
		when(this.orcamentoRepository.findAll(this.pageable)).thenReturn(this.page); 
		when(this.orcamentoRepository.findAll(this.specification, this.pageable)).thenReturn(this.page); 
		when(this.orcamentoRepository.findById(this.orcamentoEntity.getId())).thenReturn(Optional.of(this.orcamentoEntity)); 
		when(this.orcamentoRepository.save(this.orcamentoEntity)).thenReturn(this.orcamentoEntity); 
		doNothing().when(this.orcamentoRepository).delete(this.orcamentoEntity); 
	}
	
	@Test
	void teste_list_retorna_instancia_de_page_com_lista_de_entidades() {
		assertThatCode(()-> this.orcamentoService.list(this.pageable)).doesNotThrowAnyException(); 
		Page<OrcamentoEntity> result = this.orcamentoService.list(this.pageable); 
		assertThat(result).contains(this.orcamentoEntity); 
		assertThat(result.getTotalElements()).isEqualTo(1); 
		assertThat(result.getNumber()).isEqualTo(0); 
		assertThat(result.getTotalPages()).isEqualTo(1); 
		verify(this.orcamentoRepository, times(2)).findAll(this.pageable); 
	}
	
	@Test
	void teste_list_specification_retorna_instancia_de_page_com_lista_de_entidades() {
		assertThatCode(()-> this.orcamentoService.listSpecification(this.pageable, this.specification)).doesNotThrowAnyException(); 
		Page<OrcamentoEntity> result = this.orcamentoService.listSpecification(this.pageable, this.specification); 
		assertThat(result).contains(this.orcamentoEntity); 
		assertThat(result.getTotalElements()).isEqualTo(1); 
		assertThat(result.getNumber()).isEqualTo(0); 
		assertThat(result.getTotalPages()).isEqualTo(1); 
		verify(this.orcamentoRepository, times(2)).findAll(this.specification,this.pageable); 
	}
	
	@Test
	void teste_find_by_id_retorna_instancia_de_entidade() {
		assertThatCode(() -> this.orcamentoService.findById(this.orcamentoEntity.getId())).doesNotThrowAnyException(); 
		OrcamentoEntity result = this.orcamentoService.findById(this.orcamentoEntity.getId()); 
		assertThat(result).isNotNull().isEqualTo(this.orcamentoEntity); 
		verify(this.orcamentoRepository, times(2)).findById(this.orcamentoEntity.getId()); 
	}
	
	@Test
	void teste_create_retorna_instancia_de_entidade_perisitida() {
		assertThatCode(() -> this.orcamentoService.create(this.orcamentoEntity)).doesNotThrowAnyException(); 
		OrcamentoEntity result = this.orcamentoService.create(this.orcamentoEntity); 
		assertThat(result).isNotNull().isEqualTo(this.orcamentoEntity); 
		verify(this.orcamentoRepository, times(2)).save(this.orcamentoEntity); 
	}
	
	@Test
	void teste_update_retorna_instancia_de_entidade_atualizada() {
		OrcamentoEntity newData = new OrcamentoEntity(); 
		newData.setNome("String updated"); 
		newData.setDataInicio(LocalDateTime.now().plus(1,ChronoUnit.DAYS)); 
		newData.setDataFim(LocalDateTime.now().plus(1,ChronoUnit.DAYS)); 
		assertThatCode(() -> this.orcamentoService.update(newData, this.orcamentoEntity.getId())).doesNotThrowAnyException(); 
		OrcamentoEntity result = this.orcamentoService.update(newData, this.orcamentoEntity.getId()); 
		assertThat(result).isNotNull().isEqualTo(this.orcamentoEntity); 
		assertThat(result.getNome()).isEqualTo(newData.getNome()); 
		assertThat(result.getDataInicio()).isEqualTo(newData.getDataInicio()); 
		assertThat(result.getDataFim()).isEqualTo(newData.getDataFim()); 
	}
	
	@Test
	void teste_delete_remove_o_registro() {
		 assertThatCode(() -> this.orcamentoService.delete(this.orcamentoEntity.getId())).doesNotThrowAnyException(); 
		verify(this.orcamentoRepository, atMostOnce()).delete(this.orcamentoEntity); 
	} 

}