package com.example.app.controllers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.app.models.entity.Cliente;

import com.example.app.models.entity.Factura;
import com.example.app.models.entity.ItemFactura;
import com.example.app.models.entity.Libro;
import com.example.app.models.service.IClienteService;
import com.example.app.models.service.IFacturaService;
import com.example.app.util.paginator.PageRender;



@Controller
@SessionAttributes("factura")
public class FacturaController {
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IFacturaService facturaService;
	

	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	
	@GetMapping("/factura/form/{clienteId}")
	public String crear(@PathVariable("clienteId") Long clienteId, Model model,
	        RedirectAttributes flash) {

	    Cliente cliente = clienteService.findOne(clienteId);

	    if (cliente == null) {
	        flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
	        return "redirect:/listar";
	    }

	    Factura factura = new Factura();
	    factura.setCliente(cliente);

	    model.addAttribute("factura", factura);
	    model.addAttribute("titulo", "Crear Factura");

	    return "factura/form";
	}

	@GetMapping(value="/factura/cargar-productos/{term}", produces= {"application/json"})
	public @ResponseBody List<Libro> cargarLibros(@PathVariable String term){
		
		return clienteService.finByNombre(term);
	}
	
	
	@PostMapping("/factura/form")
	public String guardar(@ModelAttribute Factura factura,
	        @RequestParam(name = "item_id[]", required = false) Long[] itemId,
	        @RequestParam(name = "cantidad[]", required = false) Integer[] cantidad,
	        RedirectAttributes flash, SessionStatus status) {

	    for (int i = 0; i < itemId.length; i++) {

	        Libro libro = clienteService.findLibroById(itemId[i]);

	        ItemFactura linea = new ItemFactura();
	        linea.setCantidad(cantidad[i]);
	        linea.setLibro(libro);
	        factura.addItemFactura(linea);

	        log.info("ID:" + itemId[i].toString() + ", cantidad: " + cantidad[i].toString());
	      
	    }

	    clienteService.saveFactura(factura);

	    status.setComplete();

	    flash.addFlashAttribute("success", "Factura creada con éxito");

	    return "redirect:/cliente/ver/" + factura.getCliente().getId();
	}
	
	@RequestMapping(value = "/listar-factura", method = RequestMethod.GET)
	public String listar(@RequestParam(name="page", defaultValue="0") int page, Model model) {
		
		Pageable pageRequest = PageRequest.of(page, 4);
		
		Page<Factura> facturas = facturaService.findAll(pageRequest);
		
		PageRender<Factura> pageRender = new PageRender<>("/listar-factura", facturas);
		model.addAttribute("titulo", "Listado de facturas");
		model.addAttribute("facturas", facturas);
		model.addAttribute("page", pageRender);
		return "listar-factura";
	}
	
}
