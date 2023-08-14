package com.example.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.models.dao.ILibroDao;
import com.example.app.models.entity.Libro;

@Service
public class LibroServiceImple implements ILibroService {

	@Autowired
	private ILibroDao ilibroDao;
	
	
	@Override
	@Transactional(readOnly=true)
	public List<Libro> findALL() {
		
		return (List<Libro>) ilibroDao.findAll();
	}
	
	@Transactional
	@Override
	public void save(Libro libro) {
		
		ilibroDao.save(libro);
		
	}

	@Transactional(readOnly=true)
	@Override
	public Libro findOne(Long id) {
		return ilibroDao.findById(id).orElse(null);
	}
	
	@Transactional
	@Override
	public void delete(Long id) {
		ilibroDao.deleteById(id);
		
	}
	
	@Transactional(readOnly=true)
	@Override
	public Page<Libro> findALL(Pageable pageable) {
		
		return ilibroDao.findAll(pageable);
	}



}
