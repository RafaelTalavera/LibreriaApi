package com.example.app.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.app.models.entity.Empleado;
import com.example.app.models.service.IEmpleadoService;
import com.example.app.util.paginator.PageRender;
import jakarta.validation.Valid;


@Controller
@SessionAttributes("empleado")
public class EmpleadoController {

	@Autowired
	private IEmpleadoService empleadoService;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final static String UPLOADS_FOLDER = "uploads";


	@GetMapping(value="/uploads/empleados/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		Path pathFoto = Paths.get("uploads/empleados").resolve(filename).toAbsolutePath();
		log.info("pathFoto: " + pathFoto);
		Resource recurso = null;
		try {
			recurso = new UrlResource(pathFoto.toUri());
			if(!recurso.exists() || !recurso.isReadable()) {
				throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +  recurso.getFilename() +"\"")
				.body(recurso);
	}
	

	@GetMapping(value = "/empleado/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Empleado empleado = empleadoService.findOne(id);
		if (empleado == null) {
			flash.addFlashAttribute("error", "El Empleado no existe en la base de datos");
			return "redirect:/listar_empleado";
		}

		model.put("empleado", empleado);
		model.put("titulo", "Detalle del Empleado: " + empleado.getNombre());
		return "/empleado/ver";
	}

	@RequestMapping(value = "/listar-empleado", method = RequestMethod.GET)
	public String listar(@RequestParam(name="page", defaultValue="0") int page, Model model) {
		
		Pageable pageRequest = PageRequest.of(page, 4);
		
		Page<Empleado> empleados = empleadoService.findALL(pageRequest);
		
		PageRender<Empleado> pageRender = new PageRender<Empleado>("/listar-empleado", empleados);
		model.addAttribute("titulo", "Listado de Empleados");
		model.addAttribute("empleados", empleados);
		model.addAttribute("page", pageRender);
		return "/listar-empleado";
	}

	@RequestMapping(value = "/empleado/form")
	public String crear(Map<String, Object> model) {

		Empleado empleado = new Empleado();
		model.put("empleado", empleado);
		model.put("titulo", "Formulario de Empleado");
		return "empleado/form";
	}
	
	
	@RequestMapping(value = "empleado/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Empleado empleado = null;

		if (id > 0) {
			
			empleado = empleadoService.findOne(id);
			if (empleado == null) {
				flash.addFlashAttribute("error", "El ID del Empleado no existe en la BBDD!");
				return "redirect:/listar-empleado";
			}
		} else {
			flash.addFlashAttribute("error", "El ISBN del libro no puede ser cero!");
			return "redirect:/listar-empleado";
		}
		model.put("Empleado", empleado);
		model.put("titulo", "Editar Empleado");
		return "empleado/form";
	}


	@RequestMapping(value = "empleado/form", method = RequestMethod.POST)
	public String guardar(@Valid Empleado empleado, BindingResult result, Model model, 
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Empleado");
			return "/empleado/form";
		}
		
		if (empleado != null) {
			
		if (!foto.isEmpty()) {
			
			if(empleado.getId() !=null 
					&& empleado.getId() > 0
					&& empleado.getFoto()!=null
					&& empleado.getFoto().length() > 0) {
				
				Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(empleado.getFoto()).toAbsolutePath();
				File archivo = rootPath.toFile();
				
				if(archivo.exists() && archivo.canRead()) {
					archivo.delete();
				}
			}
			
			String uniqueFilename = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
			Path rootPath = Paths.get("uploads").resolve(uniqueFilename);

			Path rootAbsolutPath = rootPath.toAbsolutePath();
			
			log.info("rootPath: " + rootPath);
			log.info("rootAbsolutPath: " + rootAbsolutPath);

			try {

				Files.copy(foto.getInputStream(), rootAbsolutPath);
				
				flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFilename + "'");

				empleado.setFoto(uniqueFilename);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		}
		
		String mensajeFlash = (empleado.getId() != null) ? "Empleado editado con éxito!" : "Empleado creado con éxito!";

		empleadoService.save(empleado);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/listar-empleado";
	}
	
	
	@RequestMapping(value = "/empleado/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

	    if (id > 0) {
	        Empleado empleado = empleadoService.findOne(id);
	        if (empleado == null) {
	            flash.addFlashAttribute("error", "El empleado no existe en la base de datos");
	            return "redirect:/listar-empleado";
	        }

	        // Eliminar el empleado de la base de datos
	        empleadoService.delete(id);
	        flash.addFlashAttribute("success", "Empleado eliminado con éxito!");

	        // Eliminar la foto asociada al empleado si existe
	        if (empleado.getFoto() != null && !empleado.getFoto().isEmpty()) {
	            Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(empleado.getFoto()).toAbsolutePath();
	            File archivo = rootPath.toFile();

	            if (archivo.exists() && archivo.canRead()) {
	                if (archivo.delete()) {
	                    flash.addFlashAttribute("info", "Empleado " + empleado.getFoto() + " eliminada con éxito!");
	                }
	            }
	        }
	    } else {
	        flash.addFlashAttribute("error", "El Id del empleado no puede ser cero!");
	    }
	    return "redirect:/listar-empleado";
	}


}