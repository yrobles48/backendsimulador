package com.prestaclub.prestaclub_backend.persistence.nuevo.service; 

import java.util.List; 

import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.dto.UsuarioResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.UsuarioDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario; 

@Service
public interface UsuarioService {
    public Usuario guardarUsuario(Usuario usuario);
    public Usuario obtenerUsuario(String username);
    public void EliminarUsuario(Long id);
    public void migrarUsuarios(List<UsuarioDTO> usuariosDTO);
    public List<UsuarioDTO> cargarUsuariosActivos() throws Exception;
    public Usuario findById(Long id);
    public UsuarioMantenimientoDTO obtenerUsuarioMantenimientoById(Long id);
    public ListaResponse listaUsuarios( Integer pageSize, Integer pageNumber, String filterText);
    public UsuarioResponse obtenerUsuarioById(Long id);
    public ApiResponse changeStatus( Boolean status, Long id, Long userId);
    public ApiResponse addUser(String nombres, String apellidPaterno, String apellidMaterno, String email, Long user_id);
    public ApiResponse updateNotificacionCorreo(Long id, Boolean notificacionCorreo);
}
