package com.example.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.models.dao.IClienteDao;
import com.example.app.models.dao.IFacturaDao;
import com.example.app.models.dao.ILibroDao;
import com.example.app.models.entity.Cliente;
import com.example.app.models.entity.Factura;
import com.example.app.models.entity.Libro;

@Service
public class ClienteServiceImple implements IClienteService {
	

	@Autowired 
	private IClienteDao clienteDao;
	
	@Autowired
	private ILibroDao libroDao;
	
	@Autowired
	private IFacturaDao facturaDao;
	
	@Override
	@Transactional(readOnly=true)
	public List<Cliente> findALL() {
		return (List<Cliente>) clienteDao.findAll();
	}

	@Transactional
	@Override
	public void save(Cliente cliente) {
		clienteDao.save(cliente);
		
	}
	
	@Transactional(readOnly=true) 
	@Override
	public Cliente findOne(Long id) {
		return clienteDao.findById(id).orElse(null);
	}
	
	@Transactional
	@Override
	public void delete(Long id) {
	clienteDao.deleteById(id);
		
	}
	
	@Override
	@Transactional(readOnly=true)
	public Page<Cliente> findALL(Pageable pageable) {	
		return clienteDao.findAll(pageable);
	}

	@Override
	public Cliente findByDni(Integer dni) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Libro findLibroById(Long id) {
		// TODO Auto-generated method stub
		return libroDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public List<Libro> finByNombre(String term) {
		// TODO Auto-generated method stub
		return libroDao.findByNombre(term);
	}

	@Transactional
	@Override
	public void saveFactura(Factura factura) {
		facturaDao.save(factura);
		
	}


}
