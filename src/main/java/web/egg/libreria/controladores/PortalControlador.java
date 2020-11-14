/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.egg.libreria.controladores;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.egg.libreria.entidades.Libro;
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.excepciones.Roles;
import web.egg.libreria.repositorios.LibroRepositorio;
import web.egg.libreria.service.ClienteService;
import web.egg.libreria.service.PrestamoService;

@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private ClienteService clienteS;
    @Autowired
    private LibroRepositorio libroR;
    @Autowired
    private PrestamoService prestamoS;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registroCliente.html";
    }

    @PostMapping("/registrar")
    public String registrar(ModelMap modelo, @RequestParam String documento, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String domicilio, @RequestParam String telefono, @RequestParam String clave, @RequestParam String rol) {

        try {
            clienteS.registrarCliente(Long.parseLong(documento), nombre, apellido, domicilio, telefono, clave, rol);
        } catch (ExceptionService ex) {
            modelo.put("error", ex.getMessage());

            modelo.put("documento", documento);
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("domicilio", domicilio);
            modelo.put("rol", rol);
            modelo.put("telefono", telefono);
            modelo.put("clave", clave);
            Roles roles = null;
            modelo.put("roles", roles);
         

            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, ex);
            return "registroCliente.html";
        }
        modelo.put("titulo", "Bienvenido a la libreria EGG");
        modelo.put("descripcion", "Usted ha sido registrado con exito, ya puede acceder a todos los recursos de la libreria mas completa de EGG");
        return "exito.html";
    }

    // Endpoint Uri + HTTP -> /pagina + GET (<a href="#"> a desde href="") \\ /envio + POST  (form desde action="")
    @GetMapping("/iniciarSesion")
    public String iniciarSesion(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap modelo) {
        if (error != null && !error.isEmpty()) {
            modelo.put("error", "Documento o clave incorrecto");
        }
        if (logout != null && !logout.isEmpty()) {
            modelo.put("logout", "Usted ha cerrado la sesion");

        }
        return "login.html";
    }

    @PreAuthorize("hasRole('ROLE_cliente')")
    @GetMapping("/principal")
    public String principal(HttpSession session) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + auth.getAuthorities().toArray()[0].toString());

        return "principalAlterno.html";
    }

}
