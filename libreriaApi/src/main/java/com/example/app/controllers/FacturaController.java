package com.example.app.controllers;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
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
import com.example.app.models.service.ILibroService;
import com.example.app.util.paginator.PageRender;


@Secured("ROL_ADMIN")
@Controller
@SessionAttributes("factura")
public class FacturaController {
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IFacturaService facturaService;
	
	@Autowired
	private ILibroService libroService;
	
	@Autowired
	private MessageSource messageSource;

	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	
	@GetMapping("/factura/ver/{id}")
	public String ver(@PathVariable(value="id") Long id, 
			Model model,
			RedirectAttributes flash, Locale locale) {
		Factura factura = facturaService.findOne(id);
		
		if(factura == null) {
			flash.addFlashAttribute("error", messageSource.getMessage("text.compra.flash.db.error", null, locale));
			return "redirect:/listar-factura";
		}
		
		model.addAttribute("factura", factura);
		model.addAttribute("titulo", String.format(messageSource.getMessage("text.compra.ver.titulo", null, locale), factura.getDescripcion()));
		model.addAttribute("items", factura.getItems());
		
		return "factura/ver";
	}


	
	@GetMapping("/factura/form/{clienteId}")
	public String crear(@PathVariable("clienteId") Long clienteId, Model model,
	        RedirectAttributes flash, Locale locale) {

	    Cliente cliente = clienteService.findOne(clienteId);

	    if (cliente == null) {
	    	flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.flash.db.error", null, locale));
	        return "redirect:/listar-cliente";
	    }

	    Factura factura = new Factura();
	    factura.setCliente(cliente);

	    model.addAttribute("factura", factura);
	    model.addAttribute("titulo", messageSource.getMessage("text.compra.form.titulo", null, locale));

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

	    // Restar el stock de libros
	    for (ItemFactura item : factura.getItems()) {
	        Libro libro = item.getLibro();
	        libro.setStock(libro.getStock() - item.getCantidad());
	        libroService.save(libro); // Actualizar el libro en la base de datos
	    }

	    clienteService.saveFactura(factura);

	    status.setComplete();

	    flash.addFlashAttribute("success", "Factura creada con éxito");

	    return "redirect:/cliente/ver/" + factura.getCliente().getId();
	}

	
	@RequestMapping(value = "/listar-factura", method = RequestMethod.GET)
	public String listar(@RequestParam(name="page", defaultValue="0") int page, Model model, Locale locale) {
		
		Pageable pageRequest = PageRequest.of(page, 4);
		
		Page<Factura> facturas = facturaService.findAll(pageRequest);
		
		PageRender<Factura> pageRender = new PageRender<>("/listar-factura", facturas);
		model.addAttribute("titulo", messageSource.getMessage("text.compra.listar.titulo", null, locale));
		model.addAttribute("facturas", facturas);
		model.addAttribute("page", pageRender);
		return "listar-factura";
	}
	
	
	@GetMapping("/factura/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id, RedirectAttributes flash) {
		
		Factura factura = facturaService.findOne(id);
		
		if(factura != null) {
			facturaService.delete(id);
			flash.addFlashAttribute("success", "Factura eliminada con éxito!");
			return "redirect:/factura/ver/" + factura.getCliente().getId();
		}
		
		flash.addFlashAttribute("error", "La fctura no se encuenta en la base de datos");
		
		return"redirect:/listar-factura";
	}
	
}
