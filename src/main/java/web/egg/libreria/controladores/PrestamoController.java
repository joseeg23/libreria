/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.egg.libreria.controladores;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.egg.libreria.entidades.Cliente;
import web.egg.libreria.entidades.Libro;
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.excepciones.Fecha;
import web.egg.libreria.repositorios.LibroRepositorio;
import web.egg.libreria.service.LibroService;
import web.egg.libreria.service.PrestamoService;

@Controller
@RequestMapping("/prestamo")
public class PrestamoController {

    @Autowired
    private LibroService libroS;
    @Autowired
    private PrestamoService prestamoS;

    @GetMapping("/")
    public String principal() {
        return "principalPrestamo.html";

    }

    @GetMapping("/presto")
    public String prestamo(ModelMap modelo) {
        List<Libro> libros = libroS.listaLibros();
        modelo.put("libros", libros);
        return "registroPrestamo.html";
    }

    @PostMapping("/prestar")
    public String prestar(ModelMap modelo, @RequestParam String documento, @RequestParam String isbn, @RequestParam String devolucion) {

        try {

            Date devolucion1 = Fecha.parseFechaGuiones(devolucion);

            prestamoS.registrarPrestamo(Long.parseLong(documento), isbn, devolucion1);
            modelo.put("descripcion", "Prestamo registrado con exito");
            return "principalPrestamo.html";
        } catch (ExceptionService ex) {
            List<Libro> libros = libroS.listaLibros();
            modelo.put("error", ex.getMessage());
            modelo.put("libros", libros);
            modelo.put("documento", documento);

            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, ex);
            return "registroPrestamo.html";
        }

    }

    @GetMapping("/devolucion")
    public String devolver(ModelMap modelo, HttpSession session) {

        Cliente c = (Cliente) session.getAttribute("clientesession");

        List<Libro> libros = prestamoS.librosPorCliente(c.getDocumento().toString());
        if (libros.isEmpty()) {
            modelo.put("error", "no tiene libros prestados");
        } else {
            modelo.put("libros", libros);
        }

        return "registroDevolucion.html";
    }

    @PostMapping("/devolver")
    public String devolver(ModelMap modelo, @RequestParam String isbn, @RequestParam String documento) {

        try {
            prestamoS.devolverBuscandoPrestamosDelCliente(Long.parseLong(isbn), Long.parseLong(documento));
            modelo.put("descripcion", "Devolucion realizada con exito");
            return "principalPrestamo.html";
        } catch (ExceptionService ex) {
            modelo.put("error", ex.getMessage());
            List<Libro> libros =libroS.listaLibros();
            modelo.put("libros", libros);
            modelo.put("documento", documento);

            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, ex);
            return "registroDevolucion.html";
        }

    }

    @GetMapping("/listar-prestamos")
    public String listarPrestamos(ModelMap model, HttpSession session) {
         Cliente c = (Cliente) session.getAttribute("clientesession");
        model.addAttribute("prestamos", prestamoS.prestamosPorCliente(c.getDocumento()));
        
        return "prestamos.html";

    }

    @GetMapping("/elimino")
    public String elimino(ModelMap model) {
        model.put("prestamos", prestamoS.prestamos());
        return "eliminarPrestamo.html";

    }

    @PostMapping("/eliminar")
    public String elimnar(ModelMap model, @RequestParam String id) {
        try {
            prestamoS.eliminar(id);
            model.put("descripcion", "Prestamo eliminado con exito");
            return "principalPrestamo.html";
        } catch (ExceptionService ex) {
            Logger.getLogger(PrestamoController.class.getName()).log(Level.SEVERE, null, ex);
            model.put("error", ex.getMessage());
            return "eliminarPrestamo.html";
        }

    }
//
//    @PostMapping("/devuelvo")
//    public String devolucion(ModelMap model, @RequestParam String id, HttpSession session) {
//        try {
//
//            Cliente c = (Cliente) session.getAttribute("clientesession");
//            prestamoS.devolverDeLista(id, c.getDocumento());
//            model.put("prestamos", prestamoS.prestamos());
//            model.put("descripcion", "prestamo devuelto con exito");
//            return "prestamos.html";
//
//        } catch (ExceptionService ex) {
//            Logger.getLogger(PrestamoController.class.getName()).log(Level.SEVERE, null, ex);
//            model.put("error", ex.getMessage());
//            model.put("prestamos", prestamoS.prestamos());
//            return "prestamos.html";
//        }
//
//    }

    @PostMapping("/prestarLibro")
    public String prestarLibro(ModelMap modelo, @RequestParam String documento, @RequestParam String isbn, @RequestParam String devolucion) {

        try {

            Date devolucion1 = Fecha.parseFechaGuiones(devolucion);

            prestamoS.registrarPrestamo(Long.parseLong(documento), isbn, devolucion1);
            modelo.put("descripcion", "Prestamo registrado con exito");
            modelo.addAttribute("libros", libroS.listaLibros());
            return "listarLibros.html";
        } catch (ExceptionService ex) {
            Libro libro = null;
            try {
                libro = libroS.buscarLibroPorISBN(Long.parseLong(isbn));
            } catch (ExceptionService ex1) {
                 modelo.put("error", ex1.getMessage());
                Logger.getLogger(PrestamoController.class.getName()).log(Level.SEVERE, null, ex1);
            }

            modelo.put("libro", libro);
            modelo.put("error", ex.getMessage());
            modelo.put("documento", documento);

            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, ex);
            return "registroPrestamoDesdeLista.html";
        }

    }
    
    @GetMapping("/eliminolista/{id}")
    public String eliminar(ModelMap model, @PathVariable String id) {
        try {
            prestamoS.eliminar(id);
            model.put("descripcion", "Prestamo eliminado con exito");
            return "principalPrestamo.html";
        } catch (ExceptionService ex) {
            Logger.getLogger(PrestamoController.class.getName()).log(Level.SEVERE, null, ex);
            model.put("error", ex.getMessage());
            return "eliminarPrestamo.html";
        }

    }

    @GetMapping("/devuelvolista/{id}")
    public String devuelvo(ModelMap model, @PathVariable String id, HttpSession session) {
        try {

            Cliente c = (Cliente) session.getAttribute("clientesession");
            prestamoS.devolverDeLista(id, c.getDocumento());
            model.put("prestamos", prestamoS.prestamos());
            model.put("descripcion", "prestamo devuelto con exito");
            return "prestamos.html";

        } catch (ExceptionService ex) {
            Logger.getLogger(PrestamoController.class.getName()).log(Level.SEVERE, null, ex);
            model.put("error", ex.getMessage());
            model.put("prestamos", prestamoS.prestamos());
            return "prestamos.html";
        }

    }
}
