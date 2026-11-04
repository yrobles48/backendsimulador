package com.prestaclub.prestaclub_backend.persistence.antiguo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.UsuarioDTO;

public interface UsuarioMapper {
    @Select("SELECT idusuario AS idUsuario, usuario, fecharegistro AS fechaRegistro, idrol AS idRol " +
            "FROM usuario " +
            "WHERE esactivo = true")
    List<UsuarioDTO> obtenerUsuariosActivos();
}
