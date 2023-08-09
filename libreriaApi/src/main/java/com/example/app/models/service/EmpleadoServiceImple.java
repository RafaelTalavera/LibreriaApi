package com.example.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.models.dao.IEmpleadoDao;

import com.example.app.models.entity.Empleado;



@Service
public class EmpleadoServiceImple implements IEmpleadoService{

	@Autowired
	IEmpleadoDao empleadoDao;
	
	@Override
	@Transactional(readOnly=true)
	public List<Empleado> findALL() {
		return (List<Empleado>) empleadoDao.findAll();
	}

	@Transactional(readOnly=true)
	@Override
	public Page<Empleado> findALL(Pageable pageable) {
		return empleadoDao.findAll(pageable);
	}

	@Transactional
	@Override
	public void save(Empleado empleado) {
		empleadoDao.save(empleado);
		
	}

	@Transactional(readOnly=true) 
	@Override
	public Empleado findOne(Long id) {
		return empleadoDao.findById(id).orElse(null);
	}
	
	@Transactional
	@Override
	public void delete(Long id) {
		empleadoDao.deleteById(id);
		
	}

	@Transactional(readOnly=true) 
	@Override
	public List<Empleado> finByNombre(String term) {
		// TODO Auto-generated method stub
		return empleadoDao.findByNombre(term);
	}



}
