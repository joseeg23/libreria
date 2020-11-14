package web.egg.libreria.controladores;

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
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.service.AutorService;

@Controller
@RequestMapping("/autor")
public class AutorController {

    @Autowired
    private AutorService service;

    @GetMapping("/")
    public String principal() {
        return "principalAutor.html";

    }

    @GetMapping("/registro-autor")
    public String registroAutor() {
        return "registroAutor.html";

    }

    @PostMapping("/registrar-autor")
    public String resgitrarAutor(ModelMap modelo, @RequestParam String nombre) {
        try {
            service.registrarAutor(nombre);
        } catch (Exception ex) {
            Logger.getLogger(LibroController.class.getName()).log(Level.SEVERE, null, ex);
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            return "registroAutor.html";
        }
        modelo.put("descripcion", "EL AUTOR ha sido registrada con exito");
        return "principalAutor.html";

    }

    @GetMapping("/modifico-autor") //
    public String modificoAutor(ModelMap modelo) {
        modelo.put("autores", service.listar());
        return "modificarAutor.html";

    }

    //get para modificar desde lista
    @GetMapping("/modifico/{id}") //
    public String modifico(ModelMap modelo, @PathVariable(name ="id") String id) {
        Autor autor = null;
        try {
            autor = service.buscarPorId(id);
        } catch (ExceptionService ex) {
            Logger.getLogger(AutorController.class.getName()).log(Level.SEVERE, null, ex);
        }

        modelo.put("id", id);
        modelo.put("nombre", autor.getNombre());
        return "modificarAutorLista.html";

    }

    @PostMapping("/modificar-autor")
    public String modificarAutor(ModelMap modelo, @RequestParam String id, @RequestParam String nombre) {

        try {
            Autor autor = service.buscarPorId(id);
            service.modificarAutor(autor.getId(), nombre);
        } catch (ExceptionService ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, ex);
            return "modificarAutor.html";
        }

        modelo.put("descripcion", "El autor ha sido modificado con exito");
        return "principalAutor.html";
    }

    @GetMapping("/listar-autores")
    public String listarAutores(ModelMap model) {
        model.addAttribute("autores", service.listar());
        return "listaAutores.html";
    }

    @GetMapping("/elimino")
    public String elimino(ModelMap model) {
        model.put("autores", service.listar());
        return "eliminarAutor.html";

    }

    @PostMapping("/eliminar")
    public String elimnar(ModelMap model, @RequestParam String id) {

        try {
            service.eliminar(id);
        } catch (ExceptionService ex) {
            Logger.getLogger(AutorController.class.getName()).log(Level.SEVERE, null, ex);
            return "eliminarAutor.html";
        }

        model.put("descripcion", "Autor eliminado con exito");
        return "principalAutor.html";

    }
//eliminar de lista

    @GetMapping("/eliminado/{id}")
    public String eliminoLista(ModelMap modelo, @PathVariable String id) {

        try {
            service.eliminar(id);
        } catch (ExceptionService ex) {
            Logger.getLogger(LibroController.class.getName()).log(Level.SEVERE, null, ex);
            modelo.put("error", ex.getMessage());
            modelo.addAttribute("autores", service.listar());
            return "listaAutores.html";
        }
        modelo.addAttribute("autores", service.listar());
        modelo.put("descripcion", "el autor ha sido eliminado con exito");
        return "listaAutores.html";
    }
}
