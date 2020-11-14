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
import web.egg.libreria.entidades.Editorial;
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.repositorios.EditorialRepositorio;

@Service
public class EditorialService {

    @Autowired
    private EditorialRepositorio repositorio;

    @Transactional
    public void registrarEdit(String nombre) throws ExceptionService {

        if (nombre == null || nombre.isEmpty()) {
            throw new ExceptionService("nombre nulo");
        }

        if (buscarEditorialPorNombre(nombre) == null) {
            Editorial edit = new Editorial();
            edit.setNombre(nombre);

            repositorio.save(edit);
        }else{

            if (buscarEditorialPorNombre(nombre).isBaja() == true) {
                Editorial edit = buscarEditorialPorNombre(nombre);
                edit.setBaja(false);
                repositorio.save(edit);
            } else if (buscarEditorialPorNombre(nombre).isBaja() == false) {
                throw new ExceptionService("Ya hay una editorial con este nombre");
            }

        }

    }

    @Transactional
    public void modificarEditorial(String id, String nombre) throws ExceptionService {
        Optional<Editorial> respuesta = repositorio.findById(id);

        if (respuesta.isPresent()) {
            Editorial autor = respuesta.get();
            autor.setNombre(nombre);

            repositorio.save(autor);
        } else {
            throw new ExceptionService("el aauto no fue encontrado");
        }

    }

    public Editorial buscarEditorialPorNombre(String nombre) throws ExceptionService {

        try {

            //Validamos
            if (nombre == null) {
                throw new Exception("Debes indicar el nombre de la editorial a buscar");
            }

            Editorial edit = repositorio.buscarEditorialPorNombre(nombre);

            return edit;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionService("Error buscando la editorial");
        }
    }

    public Editorial buscarEditorialPorId(String id) throws ExceptionService {
        //buscamos la editorial, este metodo trae un objeto de tipo optional
        Optional<Editorial> verificar = repositorio.findById(id);
        if (verificar.isPresent()) { //verificamos que traiga un resultado
            Editorial edit = verificar.get(); //con el get obtenemos del optional el objeto en este caso de tipo editorial
            return edit;
        } else {
            throw new ExceptionService("no se encontro la editorial con el id indicado");
        }
    }

    public void eliminar(String id) throws ExceptionService {

        Optional<Editorial> verificar = repositorio.findById(id);
        if (verificar.isPresent()) {

            Editorial editorial = verificar.get();
            if (editorial.isBaja()) {
                throw new ExceptionService("La editorial ya se encuentra de baja");
            }
            editorial.setBaja(true);
            repositorio.save(editorial);
        } else {
            throw new ExceptionService("no se encontro la editorial con el id indicado");
        }
    }

    public List Listar() {
        return repositorio.listar();

    }

}
