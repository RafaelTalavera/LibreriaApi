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

import com.example.app.models.entity.Libro;
import com.example.app.models.service.ILibroService;
import com.example.app.util.paginator.PageRender;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("libro")
public class LibroController {

	@Autowired
	private ILibroService libroService;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private final static String UPLOADS_FOLDER = "uploads";

	@GetMapping(value="/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		Path pathFoto = Paths.get("uploads").resolve(filename).toAbsolutePath();
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

	@GetMapping(value = "libro/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Libro libro = libroService.findOne(id);
		if (libro == null) {
			flash.addFlashAttribute("error", "El libro no existe en la base de datos");
			return "redirect:libro/listar_libro";
		}

		model.put("libro", libro);
		model.put("titulo", "Detalle libro: " + libro.getTitulo());
		return "libro/ver";
	}

	@RequestMapping(value = "/listar_libro", method = RequestMethod.GET)
	public String listar(@RequestParam(name="page", defaultValue="0") int page, Model model) {
		
		Pageable pageRequest = PageRequest.of(page, 4);
		
		Page<Libro> libros = libroService.findALL(pageRequest);
		
		PageRender<Libro> pageRender = new PageRender<Libro>("/listar_libro", libros);
		model.addAttribute("titulo", "Listado de libros");
		model.addAttribute("libros", libros);
		model.addAttribute("page", pageRender);
		return "/listar_libro";
	}

	@RequestMapping(value = "libro/form")
	public String crear(Map<String, Object> model) {

		Libro libro = new Libro();
		model.put("libro", libro);
		model.put("titulo", "Formulario de Libro");
		return "libro/form";
	}

	@RequestMapping(value = "libro/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Libro libro = null;

		if (id > 0) {
			
			libro = libroService.findOne(id);
			if (libro == null) {
				flash.addFlashAttribute("error", "El ISBN del libro no existe en la BBDD!");
				return "redirect:libro/listar_libro";
			}
		} else {
			flash.addFlashAttribute("error", "El ISBN del libro no puede ser cero!");
			return "redirect:libro/listar_libro";
		}
		model.put("libro", libro);
		model.put("titulo", "Editar Libro");
		return "libro/form";
	}

	@RequestMapping(value = "libro/form", method = RequestMethod.POST)
	public String guardar(@Valid Libro libro, BindingResult result, Model model, 
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Libro");
			return "libro/form";
		}
		
		if (libro != null) {
			
		if (!foto.isEmpty()) {
			
			if(libro.getId() !=null 
					&& libro.getId() > 0
					&& libro.getFoto()!=null
					&& libro.getFoto().length() > 0) {
				
				Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(libro.getFoto()).toAbsolutePath();
				File archivo = rootPath.toFile();
				
				if(archivo.exists() && archivo.canRead()) {
					archivo.delete();
				} else {
		            // El usuario no ha seleccionado ningún archivo, por lo que queremos establecer el campo libro.foto como null
		            libro.setFoto(null);
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

				libro.setFoto(uniqueFilename);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		}
		
		String mensajeFlash = (libro.getId() != null) ? "Libro editado con éxito!" : "Libro creado con éxito!";

		libroService.save(libro);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:libro/listar_libro";
	}

	@RequestMapping(value = "libro/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

	    if (id > 0) {
	        Libro libro = libroService.findOne(id);
	        if (libro == null) {
	            flash.addFlashAttribute("error", "El libro no existe en la base de datos");
	            return "redirect:/listar_libro";
	        }

	        // Eliminar el libro de la base de datos
	        libroService.delete(id);
	        flash.addFlashAttribute("success", "Libro eliminado con éxito!");

	        // Eliminar la foto asociada al libro si existe
	        if (libro.getFoto() != null && !libro.getFoto().isEmpty()) {
	            Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(libro.getFoto()).toAbsolutePath();
	            File archivo = rootPath.toFile();

	            if (archivo.exists() && archivo.canRead()) {
	                if (archivo.delete()) {
	                    flash.addFlashAttribute("info", "Foto " + libro.getFoto() + " eliminada con éxito!");
	                }
	            }
	        }
	    } else {
	        flash.addFlashAttribute("error", "El ISBN del libro no puede ser cero!");
	    }
	    return "redirect:libro/listar_libro";
	}


}
