/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.egg.libreria.repositorios;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.egg.libreria.entidades.Libro;

@Repository
public interface LibroRepositorio extends JpaRepository<Libro, Long> {

    @Query("Select a from Libro a where a.titulo = :titulo")
    public Libro buscarPorTitulo(@Param("titulo") String titulo);

    @Query("Select a from Libro a")
    public ArrayList<Libro> listar();

    @Query("Select l from Prestamo p, IN( p.cliente) c, IN(p.libro) l where c.documento = :documento and p.statu like 'en prestamo'")
    public List<Libro> listarLibrosPrestados(@Param("documento") Long documento);
    
     @Query("Select l from Libro l where baja is false")
    public List<Libro> listarLibrosActivos();
    
    
}
