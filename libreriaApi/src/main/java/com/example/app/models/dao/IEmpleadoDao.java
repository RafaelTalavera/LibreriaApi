package com.example.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.example.app.models.entity.Empleado;

public interface IEmpleadoDao extends PagingAndSortingRepository<Empleado, Long>, CrudRepository<Empleado, Long>{

	@Query("select e from Empleado e where e.nombre like %?1%")
	public List<Empleado> findByNombre(String term);
}
