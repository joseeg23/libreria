/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.egg.libreria.service;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.egg.libreria.entidades.Autor;
import web.egg.libreria.entidades.Editorial;
import web.egg.libreria.entidades.Libro;
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.repositorios.LibroRepositorio;

@Service
public class LibroService {

    @Autowired
    private LibroRepositorio librorepositorio;
    @Autowired
    private AutorService AS;
    @Autowired
    private EditorialService ES;

    @Transactional
    public void registrarLibro(String idEditorial, String idAutor, String titulo, Integer anio, Integer ejemplares) throws ExceptionService, Exception {

        if (titulo == null || titulo.isEmpty()) {
            throw new ExceptionService("titulo nulo");
        }
        if (anio < 1000) {
            throw new ExceptionService("anio incorrecto");
        }
        if (ejemplares < 0) {
            throw new ExceptionService("ejemplares nulo");
        }

        if (idAutor == null || idAutor.isEmpty()) {
            throw new ExceptionService("ingrese un autor valido");
        }
        if (idEditorial == null || idEditorial.isEmpty()) {
            throw new ExceptionService("ingrese un autor valido");
        }

//        busco y seteo la editorial
        Editorial ed = ES.buscarEditorialPorId(idEditorial);

        //busco y seteo el autor
        Autor aut = AS.buscarPorId(idAutor);

        Libro libro = new Libro();
//        long size= librorepositorio.count()+1;
//        //libro.setIsbn(Math.round(Math.random()*99999999));
//       libro.setIsbn(size);// falta isbn
        libro.setTitulo(titulo);
        libro.setAnio(anio);
        libro.setEjemplares(ejemplares);
        libro.setAutor(aut);
        libro.setEditorial(ed);
        libro.setPrestados(0);

        librorepositorio.save(libro);

    }

    public void modificarLibro(Long isbn, String titulo, String idautor, String ideditorial, Integer anio, Integer ejemplares) throws ExceptionService {

        Optional<Libro> verificar = librorepositorio.findById(isbn);
        if (verificar.isPresent()) {

            Libro libro = verificar.get();
       
            Editorial ed = ES.buscarEditorialPorId(ideditorial);

            //busco/creo y seteo el autor
            Autor aut = AS.buscarPorId(idautor);

            if(titulo==null || titulo.isEmpty()){ libro.setTitulo(libro.getTitulo());}else{ libro.setTitulo(titulo);}
           if(anio==null){ libro.setAnio(libro.getAnio());}else { libro.setAnio(anio);}
           if(ejemplares==null){  libro.setEjemplares(libro.getEjemplares());}else{libro.setEjemplares(ejemplares);}

            libro.setAutor(aut);
            libro.setEditorial(ed);

            librorepositorio.save(libro);
        } else {
            throw new ExceptionService("no se encontro el libro con el isbn indicado");
        }

    }

    public Libro buscarLibroPorISBN(Long isbn) throws ExceptionService {
        Optional<Libro> verificar = librorepositorio.findById(isbn);
        if (verificar.isPresent()) {

            Libro libro = verificar.get();
            return libro;
        } else {
            throw new ExceptionService("no se encontro el libro con el isbn indicado");
        }
    }

    public void eliminar(Long isbn) throws ExceptionService {
        Optional<Libro> verificar = librorepositorio.findById(isbn);
        if (verificar.isPresent()) {

            Libro libro = verificar.get();
               if(libro.isBaja()){ throw new ExceptionService("La libro ya se encuentra de baja");}
            //dar de baja
            libro.setBaja(true);
            librorepositorio.save(libro);
        } else {
            throw new ExceptionService("no se encontro el libro con el isbn indicado");
        }
    }

    public List listaLibros() {
        return librorepositorio.listarLibrosActivos();
    }
}
