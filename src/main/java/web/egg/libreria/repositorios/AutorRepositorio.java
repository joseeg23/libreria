package web.egg.libreria.repositorios;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.egg.libreria.entidades.Autor;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor, String> {

    @Query("Select A from Autor A where A.nombre = :nombre")
    public Autor buscarAutorPorNombre(@Param("nombre") String nombre);

      @Query("Select l from Autor l where baja is false")
    public ArrayList<Autor> listar();
    

}
