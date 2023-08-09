package com.example.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.models.dao.IClienteDao;
import com.example.app.models.entity.Cliente;

@Service
public class ClienteServiceImple implements IClienteService {
	

	@Autowired 
	private IClienteDao clienteDao;
	
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
	@Transactional(readOnly=true) 
	public Cliente findByDni(Integer dni) {
		// TODO Auto-generated method stub
		return null;
	}


}
