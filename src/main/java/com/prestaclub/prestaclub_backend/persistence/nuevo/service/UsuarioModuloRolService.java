package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Modulo;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Rol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.UsuarioModuloRol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.UsuarioModuloRolRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.UsuarioRepository;

@Service    
public class UsuarioModuloRolService {
    @Autowired
    private UsuarioModuloRolRepository usuarioModuloRolRepository;

    @Autowired
    private RolService rolService;

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void guardarUsuarioModuloRol(UsuarioModuloRol usuarioModuloRol) {
        usuarioModuloRolRepository.save(usuarioModuloRol);
    }

    public List<UsuarioModuloRol> findByUsuarioId(Long id) {
        return usuarioModuloRolRepository.findByUsuarioId(id);
    }

    public ApiResponse guardarNuevoPrivilegio(Long userId, Long moduloId, Long rolId) {
        ApiResponse apiResponse = new ApiResponse();
        UsuarioModuloRol usuarioModuloRol = new UsuarioModuloRol();
        try {
            Rol rol = rolService.obtenerRol(rolId);
            Modulo modulo = moduloService.findById(moduloId);
            Usuario usuario = usuarioRepository.findById(userId).get(); 

            usuarioModuloRol.setRol(rol);
            usuarioModuloRol.setModulo(modulo);
            usuarioModuloRol.setUsuario(usuario);

            usuarioModuloRolRepository.save(usuarioModuloRol);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Privilegio guardado con exito");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse eliminarPrivilegio(Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            UsuarioModuloRol usuarioModuloRol = usuarioModuloRolRepository.findById(id).get();
            if (usuarioModuloRol == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Privilegio no encontrado");
                return apiResponse; 
            } 
            usuarioModuloRolRepository.deleteById(id);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Privilegio eliminado con exito");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al eliminar " + e.getMessage());
            return apiResponse;
        }
    }

}
