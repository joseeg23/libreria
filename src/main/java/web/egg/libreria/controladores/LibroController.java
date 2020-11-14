/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.egg.libreria.controladores;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.egg.libreria.entidades.Autor;
import web.egg.libreria.entidades.Editorial;
import web.egg.libreria.entidades.Libro;
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.service.AutorService;
import web.egg.libreria.service.EditorialService;
import web.egg.libreria.service.LibroService;

@Controller
@RequestMapping("/libro")
public class LibroController {

    @Autowired
    private LibroService libroS;
    @Autowired
    private EditorialService editorialS;
    @Autowired
    private AutorService autorS;

    @GetMapping("/")
    public String principal() {
        return "principalLibro.html";

    }

    @GetMapping("/registro-libro")
    public String registroLibro(ModelMap modelo) {
        List<Editorial> editoriales = editorialS.Listar();
        List<Autor> autores = autorS.listar();

        modelo.put("listaEditoriales", editoriales);
        modelo.put("autores", autores);
        return "registroLibro.html";

    }

    @PostMapping("/registrar-libro")
    public String resgitrarLibro(ModelMap modelo, @RequestParam String titulo, @RequestParam String idEdit, @RequestParam String idAutor, @RequestParam(required = true, defaultValue = "1") Integer anio, @RequestParam(required = true, defaultValue = "1") Integer ejemplares) {
        try {

//            Integer year = Integer.parseInt(anio);
//            Integer ejem = Integer.parseInt(ejemplares);
            libroS.registrarLibro(idEdit, idAutor, titulo, anio, ejemplares);
        } catch (Exception ex) {
            Logger.getLogger(LibroController.class.getName()).log(Level.SEVERE, null, ex);
            modelo.put("error", ex.getMessage());

            List<Editorial> editoriales = editorialS.Listar();
            List<Autor> autores = autorS.listar();
            modelo.put("listaEditoriales", editoriales);
            modelo.put("autores", autores);
            return "registroLibro.html";
        }
        modelo.put("descripcion", "el libro ha sido registrado con exito");
        return "principalLibro.html";

    }

    @GetMapping("/modifico-libro")
    public String modificoLibro(ModelMap modelo) {
        List<Libro> libros = libroS.listaLibros();
        List<Editorial> editoriales = editorialS.Listar();
        List<Autor> autores = autorS.listar();

        modelo.put("libros", libros);
        modelo.put("listaEditoriales", editoriales);
        modelo.put("autores", autores);
        return "modificarLibro.html";

    }
    //get mappin de modificar libro desde lista

    @GetMapping("/modifico/{isbn}")
    public String modifico(ModelMap modelo, @PathVariable(name = "isbn") String isbn) {
        Libro libro = null;
        try {
            libro = libroS.buscarLibroPorISBN(Long.parseLong(isbn));
        } catch (ExceptionService ex) {
            Logger.getLogger(LibroController.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Editorial> editoriales = editorialS.Listar();
        List<Autor> autores = autorS.listar();

        modelo.put("isbn", isbn);
        modelo.put("titulo", libro.getTitulo());
        modelo.put("ejemplares", libro.getEjemplares());
        modelo.put("anio", libro.getAnio());

        modelo.put("listaEditoriales", editoriales);
        modelo.put("autores", autores);
        return "modificarLibroLista.html";

    }

    @PostMapping("/modificar-libros")//@RequestParam(required=true,defaultValue="1") Integer veri
    public String modificarLibro(ModelMap modelo, @RequestParam String isbn, @RequestParam String titulo, @RequestParam String idEdit, @RequestParam String idAutor, @RequestParam String anio, @RequestParam String ejemplares) {
        try {

            Integer year = Integer.parseInt(anio);
            Integer ejem = Integer.parseInt(ejemplares);

            libroS.modificarLibro(Long.parseLong(isbn), titulo, idAutor, idEdit, year, ejem);
        } catch (NumberFormatException | ExceptionService ex) {
            Logger.getLogger(LibroController.class.getName()).log(Level.SEVERE, null, ex);
            modelo.put("error", ex.getMessage());
            List<Libro> libros = libroS.listaLibros();
            List<Editorial> editoriales = editorialS.Listar();
            List<Autor> autores = autorS.listar();
            modelo.put("libros", libros);
            modelo.put("listaEditoriales", editoriales);
            modelo.put("autores", autores);
            return "modificarLibro.html";
        }
        modelo.put("descripcion", "el libro ha sido modificado con exito");
        return "principalLibro.html";

    }

    @GetMapping("/elimino")
    public String eliminar(ModelMap model) {
        model.put("libros", libroS.listaLibros());
        return "eliminarLibro.html";
    }

    @PostMapping("/eliminar")
    public String elimino(ModelMap modelo, @RequestParam String isbn) {

        try {
            libroS.eliminar(Long.parseLong(isbn));
        } catch (ExceptionService ex) {
            Logger.getLogger(LibroController.class.getName()).log(Level.SEVERE, null, ex);
            return "eliminarLibro.html";
        }
        modelo.put("descripcion", "el libro ha sido eliminado con exito");
        return "principalLibro.html";
    }
//eliminar desde lista
    @GetMapping("/eliminado/{isbn}")
    public String eliminado(ModelMap modelo,@PathVariable(name="isbn") String isbn) {

        try {
            libroS.eliminar(Long.parseLong(isbn));
        } catch (ExceptionService ex) {
            Logger.getLogger(LibroController.class.getName()).log(Level.SEVERE, null, ex);
            modelo.addAttribute("libros", libroS.listaLibros());
            modelo.put("error", ex.getMessage());
             return "listarLibros.html";
        }
        modelo.addAttribute("libros", libroS.listaLibros());
        modelo.put("descripcion", "el libro ha sido eliminado con exito");
        return "listarLibros.html";
    }
    
    @GetMapping("/listar-libros")
    public String listarLibros(ModelMap model) {
        model.addAttribute("libros", libroS.listaLibros());
        return "listarLibros.html";
    }

    //get mapping de la lista de libros a prestamo
    @GetMapping("/prestoDesdeLibro/{isbn}")
    public String prestamo(ModelMap modelo, @PathVariable(name = "isbn") String isbn) {
          Libro libro=null;
        try {
          libro = libroS.buscarLibroPorISBN(Long.parseLong(isbn));
        } catch (ExceptionService ex) {
            Logger.getLogger(LibroController.class.getName()).log(Level.SEVERE, null, ex);
        }

        modelo.put("libro", libro);
        return "registroPrestamoDesdeLista.html";
    }

}
