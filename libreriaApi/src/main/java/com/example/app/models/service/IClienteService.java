package com.example.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.app.models.entity.Cliente;
import com.example.app.models.entity.Factura;
import com.example.app.models.entity.Libro;

public interface IClienteService {

	public List<Cliente> findALL();
	
	public Page<Cliente> findALL(Pageable pageable);
	
	public void save(Cliente cliente);
	
	public Cliente findOne(Long id);
	
	public void delete(Long id);

	public Cliente findByDni(String dni);
	
	public List<Libro> finByNombre(String term);
	
	public Libro findLibroById(Long id);
	
	public void saveFactura(Factura factura);

	public List<String> findDniSuggestions(String term);
	


}
