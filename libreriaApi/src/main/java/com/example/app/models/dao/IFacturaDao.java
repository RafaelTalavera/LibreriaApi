package com.example.app.models.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.example.app.models.entity.Factura;

public interface IFacturaDao extends PagingAndSortingRepository<Factura, Long>, CrudRepository<Factura, Long>{
	

}
