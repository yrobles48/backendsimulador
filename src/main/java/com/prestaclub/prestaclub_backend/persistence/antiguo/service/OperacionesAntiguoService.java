package com.prestaclub.prestaclub_backend.persistence.antiguo.service;

import java.util.List;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionIDDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.PropietariosDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.mapper.OperacionesMapper;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudEtapaSeguimientoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosTransaccionRepository;

@Service
public class OperacionesAntiguoService {
    @Autowired
    OperacionesMapper operacionesMapper;

    @Autowired
    LiquidacionCostosTransaccionRepository liquidacionCostosTransaccionRepository;
    @Autowired
    LCRSolicitudRepository lcrSolicitudRepository;

    @Autowired
    LCRSolicitudEtapaSeguimientoRepository lcrSolicitudEtapaSeguimientoRepository;

    public List<OperacionDTO> obtenerOperacionesXNumeroDocumento(String numeroDocumento) {
        List<OperacionDTO> list = operacionesMapper.selectOperacionesXNumeroDocumento(numeroDocumento);
        for (OperacionDTO operacion : list) {
            Long numeroOperacionLong = operacion.getNumero_operacion();
            Integer numeroOperacion = numeroOperacionLong != null ? numeroOperacionLong.intValue() : null;
            Long codAnterior = operacionesMapper.selectCodigoAnterior(numeroOperacion);
            if (codAnterior != null) {
                operacion.setCodigo_anterior(codAnterior);
            }
            boolean esFinalOperacion = liquidacionCostosTransaccionRepository
                    .existsFinal(operacion.getNumero_operacion());
            boolean esDesembolsoOperacion = liquidacionCostosTransaccionRepository
                    .existsDesembolso(operacion.getNumero_operacion());
            operacion.setEs_final_operacion(esFinalOperacion);
            operacion.setEs_desembolso_operacion(esDesembolsoOperacion);

            Boolean status = lcrSolicitudRepository.getExistSolicitudByNumOperacion(operacion.getNumero_operacion());
            operacion.setStatus_lcr_solicitud(status);
            operacion.setLcr_solicitud_etapa_seguimiento(lcrSolicitudEtapaSeguimientoRepository
                    .findByLcrSolicitudNumOperacion(operacion.getNumero_operacion()));
        }
        return list;

    }

    public List<OperacionDTO> obtenerOperacionesXNumeroOperacion(Integer numeroOperacion) {
        List<OperacionDTO> list = operacionesMapper.selectOperacionesXNumeroOperacion(numeroOperacion);
        Long codAnterior = operacionesMapper.selectCodigoAnterior(numeroOperacion);
        for (OperacionDTO operacion : list) {
            if (codAnterior != null) {
                operacion.setCodigo_anterior(codAnterior);
            }
            boolean esFinalOperacion = liquidacionCostosTransaccionRepository.existsFinal(numeroOperacion.longValue());
            boolean esDesembolsoOperacion = liquidacionCostosTransaccionRepository
                    .existsDesembolso(numeroOperacion.longValue());

            operacion.setEs_final_operacion(esFinalOperacion);
            operacion.setEs_desembolso_operacion(esDesembolsoOperacion);

            Boolean status = lcrSolicitudRepository.getExistSolicitudByNumOperacion(operacion.getNumero_operacion());
            operacion.setStatus_lcr_solicitud(status);
            operacion.setLcr_solicitud_etapa_seguimiento(lcrSolicitudEtapaSeguimientoRepository
                    .findByLcrSolicitudNumOperacion(operacion.getNumero_operacion()));
        }
        return list;
    }

    public List<OperacionDTO> obtenerOperacionesXFecha(String fechaini, String fechafin, String estado) {
        List<OperacionDTO> list = operacionesMapper.selectOperacionesXFecha(fechaini, fechafin);
        List<OperacionDTO> listFiltrado = new java.util.ArrayList<>();
        for (OperacionDTO operacion : list) {
            Integer numeroOperacion = operacion.getNumero_operacion().intValue();
            Long codAnterior = operacionesMapper.selectCodigoAnterior(numeroOperacion);
            if (codAnterior != null) {
                operacion.setCodigo_anterior(codAnterior);
            }
            boolean esFinalOperacion = liquidacionCostosTransaccionRepository.existsFinal(numeroOperacion.longValue());
            boolean esDesembolsoOperacion = liquidacionCostosTransaccionRepository
                    .existsDesembolso(numeroOperacion.longValue());

            operacion.setEs_final_operacion(esFinalOperacion);
            operacion.setEs_desembolso_operacion(esDesembolsoOperacion);

            Boolean status = lcrSolicitudRepository.getExistSolicitudByNumOperacion(operacion.getNumero_operacion());
            operacion.setStatus_lcr_solicitud(status);
            operacion.setLcr_solicitud_etapa_seguimiento(lcrSolicitudEtapaSeguimientoRepository
                    .findByLcrSolicitudNumOperacion(operacion.getNumero_operacion()));
            if (operacion.getLcr_solicitud_etapa_seguimiento() != null
                    && !operacion.getLcr_solicitud_etapa_seguimiento().isEmpty()) {
                if (estado.isEmpty()) {
                    listFiltrado.add(operacion);
                } else {
                    if (operacion.getLcr_solicitud_etapa_seguimiento().get(0).getStatus().toString().equals(estado)) {
                        listFiltrado.add(operacion);
                    }
                }
            }
        }
        return listFiltrado;
    }

    public List<OperacionIDDTO> obtenerOperacionesXNumeroDocumentoID(String numeroDocumento) {
        List<OperacionIDDTO> list = operacionesMapper.selectOperacionesXNumeroDocumentoID(numeroDocumento);
        for (OperacionIDDTO operacion : list) {
            Long numeroOperacionLong = operacion.getNumero_operacion();
            Integer numeroOperacion = numeroOperacionLong != null ? numeroOperacionLong.intValue() : null;
            Long codAnterior = operacionesMapper.selectCodigoAnterior(numeroOperacion);
            if (codAnterior != null) {
                operacion.setCodigo_anterior(codAnterior);
            }
            boolean esFinalOperacion = liquidacionCostosTransaccionRepository
                    .existsFinal(operacion.getNumero_operacion());
            boolean esDesembolsoOperacion = liquidacionCostosTransaccionRepository
                    .existsDesembolso(operacion.getNumero_operacion());
            operacion.setEs_final_operacion(esFinalOperacion);
            operacion.setEs_desembolso_operacion(esDesembolsoOperacion);

            Boolean status = lcrSolicitudRepository.getExistSolicitudByNumOperacion(operacion.getNumero_operacion());
            operacion.setStatus_lcr_solicitud(status);
            operacion.setLcr_solicitud_etapa_seguimiento(lcrSolicitudEtapaSeguimientoRepository
                    .findByLcrSolicitudNumOperacion(operacion.getNumero_operacion()));
        }
        return list;

    }

    public List<OperacionIDDTO> obtenerOperacionesXNumeroOperacionID(Integer numeroOperacion) {
        System.out.println("probando error");
        List<OperacionIDDTO> list = operacionesMapper.selectOperacionesXNumeroID(numeroOperacion);
        System.out.println("lalista" + list.size());
        return list;
    }

    public List<PropietariosDTO> getPropietariosXNumeroOperacion(Integer numeroOperacion) {
        return operacionesMapper.getPropietariosXNumeroOperacion(numeroOperacion);
    }

    public OperacionDTO getOperacionXNumeroOperacion(Integer numeroOperacion) {
        List<OperacionDTO> list = operacionesMapper.selectOperacionesXNumeroOperacion(numeroOperacion);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public Boolean existPRegistral(Integer numeroOperacion, String pRegistral) {
        return operacionesMapper.existPRegistral(numeroOperacion, pRegistral);
    }

}
