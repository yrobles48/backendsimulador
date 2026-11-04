package com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.prestaclub.prestaclub_backend.dto.LcrTotalResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.OperacionesAntiguoService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitud;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTCronograma;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosTransaccionRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRSolicitudService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LiquidacionCostosRealesService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService;

@Service
public class PdfServiceImpl {
    @Autowired
    private OperacionesAntiguoService operacionesAntiguoService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private LiquidacionCostosTransaccionRepository liquidacionCostosTransaccionRepository;
    @Autowired
    private LiquidacionCostosRealesService liquidacionCostosRealesService;
    @Autowired
    private LCRSolicitudRepository lcrSolicitudRepository; ;

    public byte[] generatePdfLiquidacionCostosTransaccion(LiquidacionCostosTransaccion liquidacionCostosTransaccion) { 
        try {
            String inversionistaName = "";
            String fondoName = "";
            String inversionistaRuc = ""; 
            String tipoContratoName = "";
            String tipoValorizacionName = "";
            String nombreAsesor = "";
            Context context = new Context(); 
            Long numOperacion = liquidacionCostosTransaccion.getNumOperacion();

            List<OperacionDTO> operacionesXNumeroOperacion = operacionesAntiguoService.obtenerOperacionesXNumeroOperacion(numOperacion.intValue());
            if (operacionesXNumeroOperacion.isEmpty()) {
                return null;
            } 
            OperacionDTO operacion = operacionesXNumeroOperacion.get(0); 

            switch (liquidacionCostosTransaccion.getInversionistaId().intValue()) {
                case 1:
                    inversionistaName = "FONDO CAPITAL EMPRENDEDOR";
                    inversionistaRuc = "20544837835";
                    break;
                case 2:
                    inversionistaName = "FONDO POPULAR 1";
                    inversionistaRuc = "20516816733";
                    break;
                case 3:
                    inversionistaName = "FONDO MYPE";
                    inversionistaRuc = "20519075581";
                    break;
                case 4:
                    inversionistaName = "FONDO PEREZ HIDALGO";
                    inversionistaRuc = "20553718067";
                    break; 
                default:
                    inversionistaName = "-";
                    inversionistaRuc = "-";
                    break;
            } 

            switch (liquidacionCostosTransaccion.getFondoId().intValue()) {
                case 1:
                    fondoName = "FONDO CAPITAL EMPRENDEDOR";
                    break;
                case 2:
                    fondoName = "FONDO POPULAR 1";
                    break;
                case 3:
                    fondoName = "FONDO MYPE";
                    break;
                case 4:
                    fondoName = "FONDO PEREZ HIDALGO";
                    break; 
                default:
                    fondoName = "-";
                    break;
            }

            switch (liquidacionCostosTransaccion.getTipoValorizacion().intValue()) {
                case 1:
                tipoValorizacionName = "Tasación";
                break;
                case 2:
                tipoValorizacionName = "Actualización";
                break;
                case 3:
                tipoValorizacionName = "Sin peritaje";
                break;
                default:
                tipoValorizacionName = "-";
                break;
            }

            tipoContratoName = liquidacionCostosTransaccion.getTipoContratoId().intValue() == 1 ? "TCHN" : "TCHN C/ levantamiento";
            Usuario asesor = usuarioService.findById(liquidacionCostosTransaccion.getAsesorId());
            nombreAsesor = 
            (asesor.getEmpleado().getNombre() != null ? asesor.getEmpleado().getNombre() : "") + " " +
            (asesor.getEmpleado().getApellidoPaterno() != null ? asesor.getEmpleado().getApellidoPaterno() : "") + " " +
            (asesor.getEmpleado().getApellidoMaterno() != null ? asesor.getEmpleado().getApellidoMaterno() : "");

            // tipoValorizacionName = liquidacionCostosTransaccion.getTipoValorizacion().intValue() == 1 ? "Tasación" : "-";
            
            context.setVariable("liquidacionCostosTransaccion",liquidacionCostosTransaccion);  
            context.setVariable("operacion", operacion);
            context.setVariable("inversionistaName", inversionistaName);
            context.setVariable("fondoName", fondoName);
            context.setVariable("inversionistaRuc", inversionistaRuc);
            context.setVariable("tipoContratoName", tipoContratoName);
            context.setVariable("tipoValorizacionName", tipoValorizacionName);
            context.setVariable("nombreAsesor", nombreAsesor);
            String htmlContent = templateEngine.process("template-liquidacion-costos-transaccion.html", context);  
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            return pdfBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generatePdfLctCronogramaPagos(LiquidacionCostosTransaccion liquidacionCostosTransaccion, List<LCTCronograma> lctCronograma) {
        try {
            String nombreAsesor = "";
            //List<LCTCronograma> lctCronograma = entity.getLctCronograma();  
            Context context = new Context(); 
            Long numOperacion = liquidacionCostosTransaccion.getNumOperacion();

            List<OperacionDTO> operacionesXNumeroOperacion = operacionesAntiguoService.obtenerOperacionesXNumeroOperacion(numOperacion.intValue());
            if (operacionesXNumeroOperacion.isEmpty()) {
                return null;
            } 
            OperacionDTO operacion = operacionesXNumeroOperacion.get(0); 
            Usuario asesor = usuarioService.findById(liquidacionCostosTransaccion.getCreatedBy());
            nombreAsesor = 
            (asesor.getEmpleado().getNombre() != null ? asesor.getEmpleado().getNombre() : "") + " " +
            (asesor.getEmpleado().getApellidoPaterno() != null ? asesor.getEmpleado().getApellidoPaterno() : "") + " " +
            (asesor.getEmpleado().getApellidoMaterno() != null ? asesor.getEmpleado().getApellidoMaterno() : "");

            
            context.setVariable("liquidacionCostosTransaccion",liquidacionCostosTransaccion);  
            context.setVariable("listaCronograma", lctCronograma);
            context.setVariable("operacion", operacion);
            context.setVariable("nombreAsesor", nombreAsesor);
            String htmlContent = templateEngine.process("template-lct-cronograma-pagos.html", context);  
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            return pdfBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generatePdfLcrSolicitudAprobada(Long numOperacion){
        try {
            String inversionistaName = "";
            String inversionistaRuc = ""; 
            String tipoValorizacionName = "";
            String tipoContratoName = "";
            String nombreAsesor = ""; 
            LiquidacionCostosTransaccion lct = liquidacionCostosTransaccionRepository.findEsDesembolsoByNumOperacion(numOperacion); 
            List<LcrTotalResponse> listaTotalesLcr = liquidacionCostosRealesService.listaLcrAllByNumOperacion(numOperacion,lct.isEsDolares());
            LCRSolicitud lcrSolicitud = lcrSolicitudRepository.findByNumOperacion(numOperacion);

            List<OperacionDTO> operacionesXNumeroOperacion = operacionesAntiguoService.obtenerOperacionesXNumeroOperacion(numOperacion.intValue());
            if (operacionesXNumeroOperacion.isEmpty()) {
                return null;
            } 
            OperacionDTO operacion = operacionesXNumeroOperacion.get(0);
            //String templateName = "template-lct-cronograma-pagos.html"; // Reemplaza con el nombre de tu template
            switch (lct.getInversionistaId().intValue()) {
                case 1:
                    inversionistaName = "FONDO CAPITAL EMPRENDEDOR";
                    inversionistaRuc = "20544837835";
                case 2:
                    inversionistaName = "FONDO POPULAR 1";
                    inversionistaRuc = "20516816733";
                    break;
                case 3:
                    inversionistaName = "FONDO MYPE";
                    inversionistaRuc = "20519075581";
                    break;
                case 4:
                    inversionistaName = "FONDO PEREZ HIDALGO";
                    inversionistaRuc = "20553718067";
                    break; 
                default:
                    inversionistaName = "-";
                    inversionistaRuc = "-";
                    break;
            }
            BigDecimal cri = listaTotalesLcr.stream().filter(item -> "cri-y-copia-literal".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal bloqueoRegistral = listaTotalesLcr.stream().filter(item -> "bloqueo-registral".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal peritaje = listaTotalesLcr.stream().filter(item -> "peritaje".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal costoInspeccion = listaTotalesLcr.stream().filter(item -> "costo-de-inspeccion".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal totalCRI = cri.add(bloqueoRegistral).add(peritaje).add(costoInspeccion);

            BigDecimal derechosNotariales = listaTotalesLcr.stream().filter(item -> "derechos-notariales".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal derechosRegistrales = listaTotalesLcr.stream().filter(item -> "derechos-registrales".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal asesoriaPrestaclub = listaTotalesLcr.stream().filter(item -> "asesoria-prestaclub".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal pagosDescontadosCheque = derechosNotariales.add(derechosRegistrales).add(asesoriaPrestaclub);

            BigDecimal registralesLevantamiento = listaTotalesLcr.stream().filter(item -> "registrales-de-levantamiento".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal tomaFirmasBiometrico = listaTotalesLcr.stream().filter(item -> "toma-de-firmas-con-lector-biometrico".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal otorgamientoPoder = listaTotalesLcr.stream().filter(item -> "otorgamiento-de-poder".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal chequeGerenciaExtra = listaTotalesLcr.stream().filter(item -> "cheque-de-gerencia-extra".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal totalCostosAdicionales = registralesLevantamiento.add(tomaFirmasBiometrico).add(otorgamientoPoder).add(chequeGerenciaExtra);

            BigDecimal financiamientoTotal  = totalCRI.add(pagosDescontadosCheque).add(totalCostosAdicionales);

            tipoContratoName = lct.getTipoContratoId().intValue() == 2 ? "TCHN C/ levantamiento" : "TCHN"  ;
            tipoValorizacionName = lct.getTipoValorizacion().intValue() == 1 ? "Tasación" : "-";
            nombreAsesor = usuarioService.findById(8L).getEmpleado().getNombre() ; 
            Context context = new Context(); 
            context.setVariable("lct",lct);  
            context.setVariable("operacion", operacion);
            context.setVariable("inversionistaName", inversionistaName);
            context.setVariable("inversionistaRuc", inversionistaRuc);
            context.setVariable("tipoContratoName", tipoContratoName);
            context.setVariable("tipoValorizacionName", tipoValorizacionName);
            context.setVariable("nombreAsesor", nombreAsesor);
            context.setVariable("cri", cri);
            context.setVariable("bloqueoRegistral", bloqueoRegistral);
            context.setVariable("peritaje", peritaje);
            context.setVariable("costoInspeccion", costoInspeccion);
            context.setVariable("totalCRI", totalCRI);
            context.setVariable("derechosNotariales", derechosNotariales);
            context.setVariable("derechosRegistrales", derechosRegistrales);
            context.setVariable("asesoriaPrestaclub", asesoriaPrestaclub);
            context.setVariable("pagosDescontadosCheque", pagosDescontadosCheque);
            context.setVariable("registralesLevantamiento", registralesLevantamiento);
            context.setVariable("tomaFirmasBiometrico", tomaFirmasBiometrico);
            context.setVariable("otorgamientoPoder", otorgamientoPoder);
            context.setVariable("chequeGerenciaExtra", chequeGerenciaExtra);
            context.setVariable("totalCostosAdicionales", totalCostosAdicionales);
            context.setVariable("financiamientoTotal", financiamientoTotal);
            context.setVariable("saldoFavor",(lct.getTotalCostosTransaccion().subtract(financiamientoTotal)));
            // context.setVariable("cuentaBancaria", lcrSolicitud.getCuentaBancaria() != null ? lcrSolicitud.getCuentaBancaria():"-");
            // context.setVariable("cci", lcrSolicitud.getCci() != null ? lcrSolicitud.getCci():"-");
            
            String htmlContent = templateEngine.process("template-lcr-solicitud-aprobada.html", context);  
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            return pdfBytes;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
