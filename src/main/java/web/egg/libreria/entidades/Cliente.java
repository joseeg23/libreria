
package web.egg.libreria.entidades;

import java.io.Serializable;
import javax.persistence.Entity;

@Entity
public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @javax.persistence.Id
    private Long documento;
     private String nombre;
     private String apellido;
     private String domicilio;
     private String telefono;
     private String clave;
     private String rol;
  
    public Cliente() {
        this.rol="CLIENTE";
        this.baja= false;
    }

    public Cliente(Long documento, String nombre, String apellido, String domicilio, String telefono) {
        this.documento = documento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.domicilio = domicilio;
        this.telefono = telefono;
          this.rol="CLIENTE";
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
  public boolean isBaja() {
        return baja;
    }

    public void setBaja(boolean baja) {
        this.baja = baja;
    }
     private boolean baja;


    public Long getDocumento() {
        return documento;
    }

    public void setDocumento(Long documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String Rol) {
        this.rol = Rol;
    }
    
    

    public Long getdocumento() {
        return documento;
    }

    public void setId(Long documento) {
        this.documento= documento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (documento!= null ? documento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        if ((this.documento == null && other.documento != null) || (this.documento!= null && !this.documento.equals(other.documento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Cliente{" + "documento=" + documento + ", nombre=" + nombre + ", apellido=" + apellido + ", domicilio=" + domicilio + ", telefono=" + telefono + '}';
    }

  

}
