package com.example.app.models.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.app.models.entity.Empleado;


public interface IEmpleadoService {
	
    public List<Empleado> findALL();
    
    public Page<Empleado> findALL(Pageable pageable);
	
	public void save(Empleado empleado);
	
	public Empleado findOne(Long id);
	
	public void delete(Long id);
	
	public List<Empleado> finByNombre(String term);

}
