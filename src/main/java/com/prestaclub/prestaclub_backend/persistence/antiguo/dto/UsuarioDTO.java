package com.prestaclub.prestaclub_backend.persistence.antiguo.dto;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class UsuarioDTO { 
    private String usuario;
    private String nombre;
    private String apellidos;
    private String apellidoMaterno;
    private LocalDateTime fechaRegistro;
    private String correo;
    private List<Map<String, Object>> modulos;

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Map<String, Object>> getModulos() {
        return modulos;
    }
    public void setModulos(List<Map<String, Object>> modulos) {
        this.modulos = modulos;
    }
     
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public String getApellidoMaterno() {
        return apellidoMaterno;
    }
    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }


}
