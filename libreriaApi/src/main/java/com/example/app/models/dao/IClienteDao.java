package com.example.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.example.app.models.entity.Cliente;

public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long>, CrudRepository<Cliente, Long>{

	@Query("SELECT DISTINCT c.dni FROM Cliente c WHERE c.dni LIKE %:term%")
	List<String> findDniSuggestionsByTerm(@Param("term") String term);
}
