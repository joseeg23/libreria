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
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.repositorios.AutorRepositorio;

@Service
public class AutorService {

    @Autowired
    private AutorRepositorio repositorio;

    @Transactional
    @SuppressWarnings("empty-statement")
    public void registrarAutor(String nombre) throws Exception {

        if (nombre == null) {
            throw new Exception("nombre nulo");
        }
        if (buscarAutorPorNombre(nombre) == null) {
            Autor autor = new Autor();
            autor.setNombre(nombre);

            repositorio.save(autor);
        }else{

            if (buscarAutorPorNombre(nombre).isBaja() == true) {
                Autor autor = buscarAutorPorNombre(nombre);
                autor.setBaja(false);
                repositorio.save(autor);
            } else if (buscarAutorPorNombre(nombre).isBaja() == false) {
                throw new ExceptionService("Ya hay un autor con este nombre");

            }
        }

    }
    @Transactional
    public void modificarAutor(String id, String nombre) throws ExceptionService {
        Optional<Autor> respuesta = repositorio.findById(id);

        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autor.setNombre(nombre);

            repositorio.save(autor);
        } else {
            throw new ExceptionService("el autor no fue encontrado");
        }

    }

    public Autor buscarAutorPorNombre(String nombre) throws ExceptionService {

        try {
            if (nombre == null) {
                throw new Exception("Debes indicar el nombre del autor a buscar");
            }

            Autor autor = repositorio.buscarAutorPorNombre(nombre);

            return autor;

        } catch (Exception e) {
            throw new ExceptionService("Error buscando el autor");
        }
    }

    public void eliminar(String id) throws ExceptionService {

        Optional<Autor> verificar = repositorio.findById(id);
        if (verificar.isPresent()) {

            Autor autor = verificar.get();
            if (autor.isBaja()) {
                throw new ExceptionService(" el autor ya se encuentra eliminado");
            }
            autor.setBaja(true);
            repositorio.save(autor);
        } else {
            throw new ExceptionService("no se encontro el autor con el id indicado");
        }
    }

    public Autor buscarPorId(String id) throws ExceptionService {

        Optional<Autor> verificar = repositorio.findById(id);
        if (verificar.isPresent()) {

            Autor autor = verificar.get();
            return autor;
        } else {
            throw new ExceptionService("no se encontro el autor con el id indicado");
        }
    }

    public List listar() {
        return repositorio.listar();
    }

}
