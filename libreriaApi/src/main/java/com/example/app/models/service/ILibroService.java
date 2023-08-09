package com.example.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.app.models.entity.Libro;

public interface ILibroService {

    public List<Libro> findALL();
    
    public Page<Libro> findALL(Pageable pageable);
	
	public void save(Libro libro);
	
	public Libro findOne(Long id);
	
	public void delete(Long id);
	
	public List<Libro> finByNombre(String term);
}

