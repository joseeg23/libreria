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
import web.egg.libreria.entidades.Cliente;
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.service.ClienteService;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteS;
   

    @GetMapping("/")
    public String principal() {
        return "principalcliente.html";

    }

    @GetMapping("/registro")
    public String registro() {
        return "registroCliente2.html";
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

            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, ex);
            return "registroCliente2.html";
        }

        modelo.put("descripcion", "El cliente ha sido registrado con exito");
        return "principalcliente.html";
    }

    @GetMapping("/modificar")
    public String modificar(ModelMap modelo) {
        List<Cliente> clientes = clienteS.Clientes();
        modelo.put("clientes", clientes);
        return "modificarPerfil.html";
    }

    //get mappin para modificar desde lista
    @GetMapping("/modifico/{documento}") //
    public String modifico(ModelMap modelo, @PathVariable String documento) {
        Cliente cliente = null;
        try {
            cliente = clienteS.buscarClientePorDocumento(documento);
        } catch (ExceptionService ex) {
            Logger.getLogger(ClienteController.class.getName()).log(Level.SEVERE, null, ex);
        }

        modelo.put("documento", documento);
        modelo.put("nombre", cliente.getNombre());
        modelo.put("apellido", cliente.getApellido());
        modelo.put("domicilio", cliente.getDomicilio());
        modelo.put("telefono", cliente.getTelefono());
        return "modificarClienteLista.html";
    }

    @PostMapping("/modificarPerfil")
    public String modificarPerfil(ModelMap modelo, @RequestParam String documento, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String domicilio, @RequestParam String telefono, @RequestParam String clave, @RequestParam String rol) {

        try {
            clienteS.modificar(Long.parseLong(documento), nombre, apellido, domicilio, telefono, clave, rol);
        } catch (ExceptionService ex) {
            modelo.put("error", ex.getMessage());
            List<Cliente> clientes = clienteS.Clientes();
            modelo.put("clientes", clientes);
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("domicilio", domicilio);
            modelo.put("telefono", telefono);
            modelo.put("clave", clave);

            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, ex);
            return "modificarPerfil.html";
        }
        modelo.put("descripcion", "El perfil ha sigo modificado con exito, ya puede acceder a todos los recursos de la libreria mas completa de EGG");
        return "principalcliente.html";
    }

    @GetMapping("/elimino")
    public String eliminar(ModelMap model) {
        model.put("clientes", clienteS.Clientes());
        return "eliminarCliente.html";
    }

    @PostMapping("/eliminar")
    public String elimino(ModelMap modelo, @RequestParam String documento) {

        try {
            clienteS.eliminar(documento);
        } catch (ExceptionService ex) {
            Logger.getLogger(ClienteController.class.getName()).log(Level.SEVERE, null, ex);
            return "eliminarCliente.html";
        }
        modelo.put("descripcion", "el cliente ha sido eliminado con exito");
        return "principalCliente.html";
    }

    //eliminar de lista
    @GetMapping("/eliminado/{documento}")
    public String eliminoLista(ModelMap modelo, @PathVariable String documento) {
        try {
            clienteS.eliminar(documento);
        } catch (ExceptionService ex) {
            modelo.put("error", ex.getMessage());
            modelo.addAttribute("clientes", clienteS.Clientes());
            Logger.getLogger(ClienteController.class.getName()).log(Level.SEVERE, null, ex);
            return "listaClientes.html";
        }
        modelo.put("descripcion", "el cliente ha sido eliminado con exito");
        modelo.addAttribute("clientes", clienteS.Clientes());
        return "listaClientes.html";
    }

    @GetMapping("/listar-clientes")
    public String listarLibros(ModelMap model) {
        model.addAttribute("clientes", clienteS.Clientes());
        return "listaClientes.html";
    }

}
