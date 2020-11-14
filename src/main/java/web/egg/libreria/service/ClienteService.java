/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.egg.libreria.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import web.egg.libreria.entidades.Cliente;
import web.egg.libreria.excepciones.ExceptionService;
import web.egg.libreria.repositorios.ClienteRepositorio;

@Service
public class ClienteService implements UserDetailsService {

    @Autowired
    private ClienteRepositorio repositorio;

    @Transactional
    public void registrarCliente(Long documento, String nombre, String apellido, String domicilio, String telefono, String clave, String rol) throws ExceptionService {
        try {
            if (documento < 0) {
                throw new ExceptionService("documento incorrecto");
            }
        } catch (NumberFormatException d) {
            throw new ExceptionService(d.getMessage());
        }
        if (nombre == null || nombre.isEmpty()) {
            throw new ExceptionService("nombre nulo");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ExceptionService("apellido nulo");
        }
        if (domicilio == null || domicilio.isEmpty()) {
            throw new ExceptionService("domicilio nulo");
        }
        if (rol == null || rol.isEmpty()) {
            throw new ExceptionService("rol nulo");
        }
        if (telefono == null || telefono.isEmpty()) {
            throw new ExceptionService("telefono nulo");
        }
        if (clave == null || clave.isEmpty() || clave.length() < 6) {
            throw new ExceptionService("La clave no debe estar vacia, y debe contener mas de 6 caracteres");
        }
        if (repositorio.findById(documento).isPresent()) {
            throw new ExceptionService("Este documento ya se encuentra registrado");
        }
        try {
            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setDocumento(documento);
            cliente.setDomicilio(domicilio);
            cliente.setTelefono(telefono);
            cliente.setRol(rol);
            String claveEncriptada = new BCryptPasswordEncoder().encode(clave);
            cliente.setClave(claveEncriptada);

            repositorio.save(cliente);
        } catch (Exception e) {
            throw new ExceptionService(e.getMessage());
        }
    }

    public Cliente buscarClientePorDocumento(String id) throws ExceptionService {
        //buscamos la cliente, este metodo trae un objeto de tipo optional
        Optional<Cliente> verificar = repositorio.findById(Long.parseLong(id));
        if (verificar.isPresent()) { //verificamos que traiga un resultado
            Cliente cliente = verificar.get(); //con el get obtenemos del optional el objeto en este caso de tipo cliente
            return cliente;
        } else {
            throw new ExceptionService("no se encontro el cliente con el documento indicado");
        }
    }

    public Cliente buscarClientePorNombre(String nombre) throws ExceptionService {

        try {

            //Validamos
            if (nombre == null) {
                throw new Exception("Debes indicar el nombre del cliente a buscar");
            }

            Cliente cliente = repositorio.buscarClientePorNombre(nombre);

            return cliente;

        } catch (Exception e) {
            throw new ExceptionService("Error buscando el cliente");
        }
    }
     public void eliminar(String id) throws ExceptionService{
        
        Optional<Cliente> verificar = repositorio.findById(Long.parseLong(id));
        if (verificar.isPresent()) {

            Cliente cliente = verificar.get();
            if(cliente.isBaja()){throw new ExceptionService("cliente ya se encuentra de baja");}
            cliente.setBaja(true);
            repositorio.save(cliente);
        } else {
            throw new ExceptionService("no se encontro el cliente con el documento indicado");
        }
    }

    public List Clientes() {
        return repositorio.listar();

    }

    @Transactional
    public void modificar(Long documento, String nombre, String apellido, String domicilio, String telefono, String clave, String rol) throws ExceptionService {
      
     
        if (nombre == null || nombre.isEmpty()) {
            throw new ExceptionService("nombre nulo");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ExceptionService("apellido nulo");
        }
        if (domicilio == null || domicilio.isEmpty()) {
            throw new ExceptionService("domicilio nulo");
        }
        if (telefono == null || telefono.isEmpty()) {
            throw new ExceptionService("telefono nulo");
        }
        if (clave == null || clave.isEmpty() || clave.length() < 6) {
            throw new ExceptionService("La clave no debe estar vacia, y debe contener mas de 6 caracteres");
        }
       try{
            Optional<Cliente> present = repositorio.findById(documento);
            if(present.isPresent()){
                Cliente cliente= present.get();
            
            cliente.setNombre(nombre);
            cliente.setApellido(apellido);
            cliente.setDomicilio(domicilio);
            cliente.setTelefono(telefono);
            cliente.setRol(rol);

            String claveEncriptada = new BCryptPasswordEncoder().encode(clave);
            cliente.setClave(claveEncriptada);

            repositorio.save(cliente);
           
        } else{
                throw new ExceptionService("error modificando el cliente");
        } }catch(ExceptionService ex){
                    throw new ExceptionService(ex.getMessage());
                    }
    }
    
    

    @Override
    public UserDetails loadUserByUsername(String documento) {

        try {
            Cliente cliente = repositorio.findById(Long.parseLong(documento)).get();

            System.out.println(cliente.toString());
            if (cliente != null) {
                List<GrantedAuthority> permisos = new ArrayList();

                GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + cliente.getRol());
                permisos.add(p1);

                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attr.getRequest().getSession(true);
                session.setAttribute("clientesession", cliente);
                User user = new User(cliente.getDocumento().toString(), cliente.getClave(), permisos);

                return user;
            } else {
                return null;

            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
