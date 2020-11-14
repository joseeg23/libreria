/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import web.egg.libreria.entidades.Editorial;
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.service.EditorialService;

@Controller
@RequestMapping("/editorial")
public class EditorialController {

    @Autowired
    private EditorialService service;

    @GetMapping("/")
    public String principal() {
        return "principalEditorial.html";

    }

    @GetMapping("/registro-editorial")
    public String registroEditorial() {
        return "registroEditorial.html";

    }

    @PostMapping("/registrar-editorial")
    public String resgitrarEditorial(ModelMap modelo, @RequestParam String nombre) {
        try {
            service.registrarEdit(nombre);
        } catch (ExceptionService ex) {
            Logger.getLogger(EditorialController.class.getName()).log(Level.SEVERE, null, ex);
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);

            return "registroEditorial.html";
        }
        modelo.put("descripcion", "La Editorial ha sido registrada con exito");
        return "principalEditorial.html";

    }

    @GetMapping("/modifico-editorial") //
    public String modificoAutor(ModelMap modelo) {
        modelo.put("editoriales", service.Listar());
        return "modificarEditorial.html";

    }
    //get mappin para lista

    @GetMapping("/modifico/{id}") //
    public String modifico(ModelMap modelo, @PathVariable(name="id") String id) {
        Editorial editorial = null;
        try {
            editorial = service.buscarEditorialPorId(id);
        } catch (ExceptionService ex) {
            Logger.getLogger(EditorialController.class.getName()).log(Level.SEVERE, null, ex);
        }
        modelo.put("nombre", editorial.getNombre());
        modelo.put("id", id);
        return "modificarEditorialLista.html";
    }

    @PostMapping("/modificar-editorial")
    public String modificarAutor(ModelMap modelo, @RequestParam String id, @RequestParam String nombre) {

        try {
            Editorial editorial = service.buscarEditorialPorId(id);
            service.modificarEditorial(editorial.getId(), nombre);
        } catch (ExceptionService ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            Logger.getLogger(EditorialController.class.getName()).log(Level.SEVERE, null, ex);
            return "modificarEditorial.html";
        }

        modelo.put("descripcion", "La Editorial ha sido modificado con exito");
        return "principalEditorial.html";
    }

    @GetMapping("/elimino")
    public String elimino(ModelMap modelo) {
        modelo.put("editoriales", service.Listar());
        return "eliminarEditorial.html";

    }

    @PostMapping("/eliminar")
    public String elimnar(ModelMap model, @RequestParam String id) {

        try {
            service.eliminar(id);
        } catch (ExceptionService ex) {
            Logger.getLogger(EditorialController.class.getName()).log(Level.SEVERE, null, ex);
            return "eliminarEditorial.html";
        }

        model.put("descripcion", "Editorial eliminada con exito");
        return "principalEditorial.html";
    }

    @GetMapping("/listar-editoriales")
    public String listarEditoriales(ModelMap model) {
        model.addAttribute("editoriales", service.Listar());
        return "listaEditoriales.html";
    }

    //eliminar de lista
    @GetMapping("/eliminado/{id}")
    public String eliminoLista(ModelMap modelo, @PathVariable String id) {

        try {
            service.eliminar(id);
        } catch (ExceptionService ex) {
            Logger.getLogger(EditorialController.class.getName()).log(Level.SEVERE, null, ex);
            modelo.put("error", ex.getMessage());
            modelo.addAttribute("editoriales", service.Listar());
            return "listaEditoriales.html";
        }
        modelo.addAttribute("editoriales", service.Listar());
        modelo.put("descripcion", "la editorial ha sido eliminada con exito");
        return "listaEditoriales.html";
    }
}
