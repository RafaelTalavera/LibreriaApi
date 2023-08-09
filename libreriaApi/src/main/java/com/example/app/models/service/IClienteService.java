package com.example.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.app.models.entity.Cliente;

public interface IClienteService {

	public List<Cliente> findALL();
	
	public Page<Cliente> findALL(Pageable pageable);
	
	public void save(Cliente cliente);
	
	public Cliente findOne(Long id);
	
	public void delete(Long id);

	public Cliente findByDni(Integer dni);


}
