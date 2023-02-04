package br.com.mariah.restapi.repository;

import br.com.mariah.restapi.domain.OrcamentoEntity;
import java.lang.Long;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrcamentoRepository extends JpaRepository<OrcamentoEntity, Long>, JpaSpecificationExecutor<OrcamentoEntity> { 

}