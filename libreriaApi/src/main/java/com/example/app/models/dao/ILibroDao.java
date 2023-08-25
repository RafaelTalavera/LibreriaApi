package com.example.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.example.app.models.entity.Libro;

public interface ILibroDao extends PagingAndSortingRepository<Libro, Long>, CrudRepository<Libro, Long>{
	
	@Query("select a from Libro a where a.titulo like %?1%")
	public List<Libro> findByNombre(String term);
	
}

