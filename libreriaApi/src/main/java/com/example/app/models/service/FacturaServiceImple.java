package com.example.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.models.dao.IFacturaDao;
import com.example.app.models.entity.Factura;

@Service
public class FacturaServiceImple implements IFacturaService {
	
	@Autowired
	private IFacturaDao facturaDao;

	@Override
	@Transactional(readOnly=true)
	public List<Factura> findAll() {
		return (List<Factura>) facturaDao.findAll();
	}

	@Transactional(readOnly=true)
	@Override
	public Page<Factura> findAll(Pageable pageable) {	
		return facturaDao.findAll(pageable);
	}


	@Transactional(readOnly=true)
	@Override
	public Factura findOne(Long id) {
		return facturaDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public void delete(Long id) {
		facturaDao.deleteById(id);
		
	}

}
