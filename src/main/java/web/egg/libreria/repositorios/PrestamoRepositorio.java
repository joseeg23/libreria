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
import web.egg.libreria.entidades.Prestamo;

@Repository
public interface PrestamoRepositorio extends JpaRepository<Prestamo, String> {

    @Query("Select a from Prestamo a")
    public ArrayList<Prestamo> listar();

    @Query("Select a from Prestamo a, IN(a.libro) l")
    public ArrayList<Prestamo> join();

    @Query("Select a from Prestamo a, IN(a.cliente) c where c.documento = :documento and a.statu like 'en prestamo'")
    public ArrayList<Prestamo> buscarPrestamosCliente(@Param ("documento") Long documento);
    
     @Query("Select l from Prestamo l where statu like 'en prestamo'")
    public ArrayList<Prestamo> listarPrestamosActivos();

}
