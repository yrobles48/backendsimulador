package com.prestaclub.prestaclub_backend.persistence.antiguo.mapper;

import java.util.List;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionIDDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.PropietariosDTO;

public interface OperacionesMapper {
    List<OperacionDTO> selectOperacionesXNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);
    List<OperacionDTO> selectOperacionesXNumeroOperacion(@Param("numeroOperacion") Integer numeroOperacion);

    List<OperacionDTO> selectOperacionesXFecha(@Param("fechaini") String fechaini, @Param("fechafin") String fechafin);

    List<OperacionIDDTO> selectOperacionesXNumeroID(@Param("numeroOperacion") Integer numeroOperacion);
    List<OperacionIDDTO> selectOperacionesXNumeroDocumentoID(@Param("numeroDocumento") String numeroDocumento);

    Long selectCodigoAnterior(@Param("numeroOperacion") Integer numeroOperacion);

    @Select("select ms.c_tipo_solicitud,count(1) from mae_solicitud ms  group by ms.c_tipo_solicitud")
    List<String> selectTiposSolicitud();

    List<PropietariosDTO> getPropietariosXNumeroOperacion(@Param("numeroOperacion") Integer numeroOperacion);

    
@Select("""
    SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
    FROM mae_solicitud ms
    JOIN mae_solicitud_garantia msg ON msg.c_mae_solicitud_id = ms.c_mae_solicitud_id
    JOIN mae_garantia mg ON mg.c_mae_garantia_id = msg.c_mae_garantia_id
    WHERE ms.c_mae_solicitud_id_pk = #{numeroOperacion} AND trim(mg.n_partida) = #{pRegistral}
""")
Boolean existPRegistral(@Param("numeroOperacion") Integer numeroOperacion, @Param("pRegistral") String pRegistral);

}
 