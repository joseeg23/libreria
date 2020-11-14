/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.egg.libreria.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.egg.libreria.entidades.Cliente;
import web.egg.libreria.entidades.Libro;
import web.egg.libreria.entidades.Prestamo;
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.repositorios.ClienteRepositorio;
import web.egg.libreria.repositorios.LibroRepositorio;
import web.egg.libreria.repositorios.PrestamoRepositorio;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepositorio repositorio;
    @Autowired
    private ClienteRepositorio repositorioCliente;
    @Autowired
    private LibroRepositorio librorepositorio;

    @Transactional
    public void registrarPrestamo(Long documentoCliente, String isbn, Date devolucion) throws ExceptionService {

        if (isbn == null || isbn.isEmpty()) {
            throw new ExceptionService("isbn nulo");
        }

        if (documentoCliente == null) {
            throw new ExceptionService("Debe ingresar un documento");
        }
        //creo el prestamo;
        Prestamo prestamo = new Prestamo();
        //seteo la fecha del prestamo
        Date fechaPrestamo = new Date();
        prestamo.setFechaDevolucion(devolucion);
        prestamo.setFecha(fechaPrestamo);

        //busco el cliente, verifico y seteo
        Optional<Cliente> buscandoCliente = repositorioCliente.findById(documentoCliente);
        Cliente cliente;
        if (buscandoCliente.isPresent()) {
            cliente = buscandoCliente.get();
        } else {
            throw new ExceptionService("No se consiguio un cliente con el documento ingresado");
        }
        prestamo.setCliente(cliente);

        //busco el libro, verifico y seteo
        Libro libro = librorepositorio.findById(Long.parseLong(isbn)).get();
        if (libro == null) {
            throw new ExceptionService("No se consiguio un libro con el titulo ingresado");
        }
        if (libro.getEjemplares() <= 0) {
            throw new ExceptionService("no hay ejemplares de este libro");
        }
         
        //verifico que la persona tenga un prestamo activo con este libro
        if (librorepositorio.listarLibrosPrestados(documentoCliente).contains(libro)) {
            for (int i = 0; i < repositorio.buscarPrestamosCliente(documentoCliente).size(); i++) {
                if (repositorio.buscarPrestamosCliente(documentoCliente).get(i).getStatus().equals("en prestamo")) {
                    throw new ExceptionService("ya usted tiene prestado este libro");
                }
            }
        }
        
        //modifico el libro para que se registre el prestamo
        if (libro.getPrestados() == null) {
            libro.setPrestados(0);
        }
        int pres = libro.getPrestados() + 1;
        libro.setPrestados(pres);

        int eje = libro.getEjemplares() - 1;
        libro.setEjemplares(eje);
        //guardo el lbro en la bd cambiado y seteo a prestamo
        librorepositorio.save(libro);

        prestamo.setLibro(libro);

        repositorio.save(prestamo);

    }

    public Prestamo buscarPrestamoPorId(String id) throws ExceptionService {

        Optional<Prestamo> verificar = repositorio.findById(id);
        if (verificar.isPresent()) {
            Prestamo prestamo = verificar.get();
            return prestamo;
        } else {
            throw new ExceptionService("no se encontro el prestamo con el id indicado");
        }
    }

    @Transactional
    public void devolver(Long isbn, long documentoCliente) throws ExceptionService {
        boolean ok = true;

        if (librorepositorio.findById(isbn) == null) {
            throw new ExceptionService("Libro no encontrado");
        }
        if (repositorioCliente.findById(documentoCliente).get() == null) {
            throw new ExceptionService("Cliente no encontrado");
        }

        for (Prestamo prestamo : repositorio.findAll()) {
            if (prestamo.getCliente().getDocumento().equals(documentoCliente) && prestamo.getLibro().getIsbn().equals(isbn)) {
                ok = false;
                if (prestamo.getStatus().equals("ELIMINADO")) {
                    throw new ExceptionService("Prestamo no disponible eliminado");
                }
                //registramos la devolucion
                Libro libro = prestamo.getLibro();

                int pres = libro.getPrestados() - 1;
                libro.setPrestados(pres);

                int eje = libro.getEjemplares() + 1;
                libro.setEjemplares(eje);

                librorepositorio.save(libro);

                //calculo multas con las fechas
                Date actual = new Date();

                if (prestamo.getFechaDevolucion().before(actual)) {
                    prestamo.setMulta(100.0);
                }
                prestamo.setStatus("LIBRO DEVUELTO");
                repositorio.save(prestamo);

                break;
            }
        }
        if (ok) {
            throw new ExceptionService("Usted no tiene libros prestados con ese titulo");
        }
    }

    @Transactional
    public void devolverBuscandoPrestamosDelCliente(Long isbn, long documentoCliente) throws ExceptionService {
        boolean ok = true;

        if (librorepositorio.findById(isbn) == null) {
            throw new ExceptionService("Libro no encontrado");
        }
        if (repositorioCliente.findById(documentoCliente).get() == null) {
            throw new ExceptionService("Cliente no encontrado");
        }

        for (Prestamo prestamo : repositorio.buscarPrestamosCliente(documentoCliente)) {
            if (prestamo.getLibro().getIsbn().equals(isbn)) {
                ok = false;
                if (prestamo.getStatus().equals("ELIMINADO")) {
                    throw new ExceptionService("Prestamo ya se ha encuentra eliminado");
                }
                //registramos la devolucion
                Libro libro = prestamo.getLibro();

                int pres = libro.getPrestados() - 1;
                libro.setPrestados(pres);

                int eje = libro.getEjemplares() + 1;
                libro.setEjemplares(eje);

                librorepositorio.save(libro);

                //calculo multas con las fechas
                Date actual = new Date();

                if (prestamo.getFechaDevolucion().before(actual)) {
                    prestamo.setMulta(100.0);
                }
                prestamo.setStatus("LIBRO DEVUELTO");
                repositorio.save(prestamo);

                break;
            }
        }
        if (ok) {
            throw new ExceptionService("Usted no tiene libros prestados con ese titulo");
        }
    }

    public void eliminar(String id) throws ExceptionService {
        Optional<Prestamo> verificar = repositorio.findById(id);
        if (verificar.isPresent()) {

            Prestamo prestamo = verificar.get();
            prestamo.setStatus("ELIMINADO");
            repositorio.save(prestamo);
        } else {
            throw new ExceptionService("no se encontro el prestamo con el id indicado");
        }
    }

    @Transactional
    public void devolverDeLista(String id, long documento) throws ExceptionService {

        Optional<Prestamo> verificar = repositorio.findById(id);
        if (verificar.isPresent()) {
            Prestamo prestamo = verificar.get();

            if (!prestamo.getCliente().getDocumento().equals(documento)) {
                throw new ExceptionService("Usted no tiene prestamos de este libro");
            }
            if (prestamo.getStatus().equals("LIBRO DEVUELTO")) {
                throw new ExceptionService("Prestamo ya devuelto");
            }
            if (prestamo.getStatus().equals("ELIMINADO")) {
                throw new ExceptionService("Prestamo no disponible eliminado");
            }
            //registramos la devolucion
            Libro libro = prestamo.getLibro();

            int pres = libro.getPrestados() - 1;
            libro.setPrestados(pres);

            int eje = libro.getEjemplares() + 1;
            libro.setEjemplares(eje);

            librorepositorio.save(libro);

            //calculo multas con las fechas
            //Date devolucion = new Date(new java.util.Date().getTime());
            //prestamo.setFechaDevolucion(devolucion);
            Date actual = new Date();

            if (prestamo.getFechaDevolucion().before(actual)) {
                prestamo.setMulta(100.0);
            }
            prestamo.setStatus("LIBRO DEVUELTO");
            repositorio.save(prestamo);

        } else {
            throw new ExceptionService("Usted no tiene libros prestados con ese titulo");
        }
    }

    public List<Prestamo> prestamos() {
        return repositorio.listarPrestamosActivos();

    }

    public List<Prestamo> prestamosPorCliente(Long documento) {
        return repositorio.buscarPrestamosCliente(documento);

    }

    public List<Libro> librosPorCliente(String documento) {
        return librorepositorio.listarLibrosPrestados(Long.parseLong(documento));

    }

}
