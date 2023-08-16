package com.example.app.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.app.models.entity.Cliente;
import com.example.app.models.service.IClienteService;
import com.example.app.util.paginator.PageRender;
import jakarta.validation.Valid;

/***/

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;

	@RequestMapping(value = "/listar-cliente", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		Pageable pageRequest = PageRequest.of(page, 4);
		Page<Cliente> clientes = clienteService.findALL(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<>("/listar-cliente", clientes);

		model.addAttribute("titulo", "Litado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);

		return "/listar-cliente";
	}

	@GetMapping(value = "cliente/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = clienteService.findOne(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar-cliente";
		}

		model.put("cliente", cliente);
		model.put("titulo", "Detalle del Cliente: " + cliente.getNombre() + " " + cliente.getApellido());
		return "cliente/ver";
	}

	@RequestMapping(value = "cliente/form")
	public String crear(Map<String, Object> model) {

		Cliente cliente = new Cliente();

		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");

		return "cliente/form";
	}

	@RequestMapping(value = "cliente/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = null;
		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD!");
				return "redirect:listar-cliente";
			}
		} else {
			flash.addFlashAttribute("error", "El id del libro no puede ser 0 ");
			return "redirect:listar-cliente";
		}

		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");

		return "cliente/form";

	}

	@RequestMapping(value = "cliente/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flash,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "cliente/form";
		}

		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", "Cliente creado con éxito");
		return "redirect:/listar-cliente";
	}

	@RequestMapping(value = "cliente/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito");
		}

		return "redirect:listar-cliente";
	}

	/*@GetMapping(value = "/cliente/buscar-por-dni/{term}, produces = { "application/json" }")
	@ResponseBody
	public List<Cliente> buscarClientesPorDNI(@PathVariable String term) {
		List<Cliente> clientes = clienteService.findDniSuggestions(term);; // Cambia el método según tu servicio
		return clientes;
	}
	*/
	
	@GetMapping("/cliente/buscar-por-dni-autocompletado")
	@ResponseBody
	public List<String> buscarClientesPorDNIAutocompletado(@RequestParam("term") String term) {
	    List<String> dniSuggestions = clienteService.findDniSuggestions(term); // Cambia el nombre del método según tu servicio
	    return dniSuggestions;
	}

}

