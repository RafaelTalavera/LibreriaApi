package com.example.app.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.app.models.entity.Cliente;

import com.example.app.models.entity.Factura;
import com.example.app.models.entity.Libro;
import com.example.app.models.service.IClienteService;
import com.example.app.models.service.ILibroService;


@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private ILibroService libroService;
	
	
	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable(value = "clienteId") Long clienteId, Map<String, Object> model, RedirectAttributes flash ) {
		
		Cliente cliente = clienteService.findOne(clienteId);
		
		if(cliente == null) {
			flash.addAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar_cliente";
			
		}
		
		Factura factura = new Factura();
		factura.setCliente(cliente);
		
		
		model.put("factura", factura);
		model.put("titulo", "Crear Factura");
		
		return "factura/form";
	}

	@GetMapping(value="/cargar-productos/{term}", produces= {"application/json"})
	public @ResponseBody List<Libro> cargarLibros(@PathVariable String term){
		
		return libroService.finByNombre(term);
	}
}
