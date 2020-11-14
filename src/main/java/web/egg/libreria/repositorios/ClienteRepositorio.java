/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.egg.libreria.repositorios;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.egg.libreria.entidades.Cliente;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
    
     @Query("Select A from Cliente A where A.nombre = :nombre")
    public Cliente buscarClientePorNombre(@Param ("nombre") String nombre);
    
     @Query("Select l from Cliente l where baja is false")
    public ArrayList<Cliente> listar();
    
  
}
