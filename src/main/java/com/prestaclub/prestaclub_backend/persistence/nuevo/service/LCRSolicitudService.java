package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.EmailValues;
import com.prestaclub.prestaclub_backend.dto.LcrResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudHistoryResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolso;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolsoSubEstados;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitud;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudFiles;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudHistory;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTFiles;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosReales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Notificaciones;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.NotificacionesUsuarios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRReembolsoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudEtapaSeguimientoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudFilesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudHistoryRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosTransaccionRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesUsuariosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.ExcelServiceImpl;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.PdfServiceImpl;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.S3ServiceImpl;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.UsuarioServiceImpl;
import com.prestaclub.prestaclub_backend.service.EmailService;

@Service
public class LCRSolicitudService {
    @Autowired
    private LCRSolicitudRepository lcrSolicitudRepository;

    @Autowired
    private LCRSolicitudHistoryRepository lcrSolicitudHistoryRepository;

    @Autowired
    private UsuarioServiceImpl usuarioServiceImpl;

    @Autowired
    private LiquidacionCostosTransaccionRepository liquidacionCostosTransaccionRepository;

    @Autowired
    private PdfServiceImpl pdfServiceImpl;

    @Autowired
    private S3ServiceImpl s3Service;

    @Autowired
    private LCRSolicitudFilesRepository lcrSolicitudFilesRepository;

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificacionesUsuariosRepository notificacionesUsuariosRepository;

    @Autowired
    private ExcelServiceImpl excelService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LCRSolicitudEtapaSeguimientoRepository lcrSolicitudEtapaSeguimientoRepository;

    @Autowired
    private LCRReembolsoRepository lcrReembolsoRepository;

    public void save(LCRSolicitud lcrSolicitud) {
        lcrSolicitudRepository.save(lcrSolicitud);
    }

    public LcrSolicitudResponse findByNumOperacion(Long numOperacion) {
        LCRSolicitud lcrSolicitud = lcrSolicitudRepository.findByNumOperacion(numOperacion);
        if (lcrSolicitud == null) {
            return null;
        }
        return returnResponse(lcrSolicitud);
    }

    public LcrSolicitudResponse returnResponse(LCRSolicitud lcrSolicitud) {
        LcrSolicitudResponse lcrResponse = new LcrSolicitudResponse();
        lcrResponse.setNumOperacion(lcrSolicitud.getNumOperacion());
        // lcrResponse.setStatus(lcrSolicitud.getStatus());
        lcrResponse.setId(lcrSolicitud.getId());
        // lcrResponse.setFechaSolicitud(lcrSolicitud.getCreatedAt());
        lcrResponse.setMontoSimulado(lcrSolicitud.getMontoSimulado());
        lcrResponse.setMontoReal(lcrSolicitud.getMontoReal());
        // lcrResponse.setEsDolares(lcrSolicitud.getEsDolares());
        // lcrResponse.setCuentaBancaria(lcrSolicitud.getCuentaBancaria());
        // if (lcrSolicitud.getStatus() == 2) {
        // LCRSolicitudHistory listaLcrSolicitudHistory =
        // lcrSolicitudHistoryRepository.findTopByUpdatedByIsNull(lcrSolicitud.getId());
        // lcrResponse.setMensaje(listaLcrSolicitudHistory.getMensaje());

        // }

        // UsuarioMantenimientoDTO solicitante =
        // usuarioServiceImpl.obtenerUsuarioMantenimientoById(lcrSolicitud.getCreatedBy().getId());
        // lcrResponse.setSolicitante(solicitante);

        return lcrResponse;
    }

    public ApiResponse aprobar(Long numOperacion, Long userId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Usuario user = usuarioServiceImpl.findById(userId);
            LCRSolicitud lcrSolicitud = lcrSolicitudRepository.findByNumOperacion(numOperacion);
            // lcrSolicitud.setStatus(3);
            lcrSolicitud.setUpdatedBy(user);
            lcrSolicitudRepository.save(lcrSolicitud);

            LCRSolicitudHistory ultimSolicitudHistory = lcrSolicitudHistoryRepository
                    .findTopByUpdatedByIsNull(lcrSolicitud.getId());
            if (ultimSolicitudHistory != null) {
                ultimSolicitudHistory.setUpdatedBy(user);
                ultimSolicitudHistory.setUpdatedDate(LocalDateTime.now());
                lcrSolicitudHistoryRepository.save(ultimSolicitudHistory);
            }
            LCRSolicitudHistory lcrSolicitudHistory = new LCRSolicitudHistory();
            // lcrSolicitudHistory.setMontoRealPrevio(lcrSolicitud.getMontoReal());
            // lcrSolicitudHistory.setMontoRealActual(lcrSolicitud.getMontoReal());
            lcrSolicitudHistory.setLctSolicitud(lcrSolicitud);
            // lcrSolicitudHistory.setStatus(3);
            // lcrSolicitudHistory.setSolicitante(lcrSolicitud.getCreatedBy());
            lcrSolicitudHistory.setCreatedBy(user);
            lcrSolicitudHistory.setCreatedDate(LocalDateTime.now());
            lcrSolicitudHistoryRepository.save(lcrSolicitudHistory);

            byte[] pdfLcrSolicitudAprobada = pdfServiceImpl.generatePdfLcrSolicitudAprobada(numOperacion);
            if (pdfLcrSolicitudAprobada != null) {

                // Subir el archivo combinado a S3
                String fileName = "lcr_solicitud_aprobada_" + numOperacion + "_" + Instant.now().toEpochMilli()
                        + ".pdf";
                String url = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-"
                        + LocalDate.now().getDayOfMonth() + "/lcr/solicitud/" + fileName;

                s3Service.uploadFile(pdfLcrSolicitudAprobada, url);

                // Guardar detalles del archivo en la base de datos
                LCRSolicitudFiles lcrSolicitudFiles = new LCRSolicitudFiles();
                lcrSolicitudFiles.setLcrSolicitud(lcrSolicitud);
                lcrSolicitudFiles.setFileName(fileName);
                lcrSolicitudFiles.setExtension("pdf");
                lcrSolicitudFiles.setUrl(url);
                lcrSolicitudFiles.setTag("lcr-solicitud-aprobada");
                s3Service.uploadFile(pdfLcrSolicitudAprobada, url);
                lcrSolicitudFilesRepository.save(lcrSolicitudFiles);
            }

            apiResponse.setSuccess(true);
            apiResponse.setMessage("La solicitud de la operación " + numOperacion + " ha sido aprobada");
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
        }
        return apiResponse;

    }

    public ApiResponse observar(Long numOperacion, Long userId, String observacion) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Usuario user = usuarioServiceImpl.findById(userId);
            LCRSolicitud lcrSolicitud = lcrSolicitudRepository.findByNumOperacion(numOperacion);
            // lcrSolicitud.setStatus(2);
            lcrSolicitud.setUpdatedBy(user);
            lcrSolicitudRepository.save(lcrSolicitud);
            LCRSolicitudHistory ultimSolicitudHistory = lcrSolicitudHistoryRepository
                    .findTopByUpdatedByIsNull(lcrSolicitud.getId());
            if (ultimSolicitudHistory != null) {
                ultimSolicitudHistory.setUpdatedBy(user);
                ultimSolicitudHistory.setUpdatedDate(LocalDateTime.now());
                lcrSolicitudHistoryRepository.save(ultimSolicitudHistory);
            }
            LCRSolicitudHistory lcrSolicitudHistory = new LCRSolicitudHistory();
            // lcrSolicitudHistory.setMontoRealPrevio(lcrSolicitud.getMontoReal());
            // lcrSolicitudHistory.setMontoRealActual(lcrSolicitud.getMontoReal());
            lcrSolicitudHistory.setLctSolicitud(lcrSolicitud);
            // lcrSolicitudHistory.setStatus(2);
            // lcrSolicitudHistory.setSolicitante(lcrSolicitud.getCreatedBy());
            lcrSolicitudHistory.setMensaje(observacion);
            lcrSolicitudHistory.setCreatedBy(user);
            lcrSolicitudHistory.setCreatedDate(LocalDateTime.now());
            lcrSolicitudHistoryRepository.save(lcrSolicitudHistory);

            String descripcion = "La solicitud de liquidación con el numero de operación "
                    + lcrSolicitud.getNumOperacion() + " ha sido observada";

            Notificaciones notificaciones = new Notificaciones();
            notificaciones.setTitulo("Solicitud Observada - " + lcrSolicitud.getNumOperacion());
            notificaciones.setDescripcion(descripcion);
            notificaciones.setUrl("liquidacion-costos");
            notificaciones.setCreatedBy(user);
            notificaciones.setCreatedAt(LocalDateTime.now());

            Long idNotificacion = notificacionesRepository.save(notificaciones).getId();
            // ver los usuarios que estan en contabilidad y enviarle la notificacion
            Usuario usuario = usuarioServiceImpl.findById(lcrSolicitud.getCreatedBy().getId());
            NotificacionesUsuarios notificacionesUsuario = new NotificacionesUsuarios();
            notificacionesUsuario.setNotificacion(notificacionesRepository.findById(idNotificacion).orElse(null));
            notificacionesUsuario.setUsuario(usuario);
            notificacionesUsuario.setIsView(false);
            notificacionesUsuario.setCreatedAt(LocalDateTime.now());
            notificacionesUsuariosRepository.save(notificacionesUsuario);

            if (usuario.isNotificacionCorreo()) {
                EmailValues emailValues = new EmailValues();
                emailValues.setEmail(usuario.getEmail());
                emailValues.setSubject("NUEVA NOTIFICACION - PRESTACLUB");
                emailValues.setUsername(usuario.getUsername());
                emailService.sendEmailNewNotificacion(emailValues, descripcion);

            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", usuario.getId());
            messagingTemplate.convertAndSend("/topic/global/notificaciones", payload);

            apiResponse.setSuccess(true);
            apiResponse.setMessage("La solicitud de la operación " + numOperacion + " ha sido observada");
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
        }
        return apiResponse;

    }

    public Integer countLcrSolicitudPendiente() {
        // return lcrSolicitudRepository.countLcrSolicitudPendiente();
        Integer count = lcrSolicitudEtapaSeguimientoRepository.countPendienteBySlug("contabilidad");

        return count;
    }

    public byte[] descargarReporteAprobados(String startDate, String endDate, String numOperacion) {
        return excelService.generateExcelSolicitudAprobadasLiqCostos(startDate, endDate, numOperacion);
    }

    /////////////////////////////
    public ApiResponseData<List<LCRSolicitudEtapaSeguimiento>> getLcrSolicitudByNumOperacion(Long numOperacion) {
        ApiResponseData<List<LCRSolicitudEtapaSeguimiento>> apiResponse = new ApiResponseData<>();
        try {
            List<LCRSolicitudEtapaSeguimiento> lista = lcrSolicitudEtapaSeguimientoRepository
                    .findByLcrSolicitudNumOperacion(numOperacion);
            if (lista == null) {
                apiResponse.setData(null);
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No se encontro la solicitud");
                return apiResponse;
            }
            apiResponse.setSuccess(true);
            apiResponse.setData(lista);
            apiResponse.setMessage("ok");
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
        }
        return apiResponse;
    }

    public ListaResponse listaLcrSolicitud(Integer pageSize, Integer pageNumber, String filterText, Integer status) {
        ListaResponse response = new ListaResponse();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<LcrSolicitudResponse> page = lcrSolicitudEtapaSeguimientoRepository.getLcrSolicitudByStatus(status,
                filterText, pageable);
        page.getContent().forEach(item -> {
            LiquidacionCostosTransaccion lcTransaccion = lcrSolicitudEtapaSeguimientoRepository
                    .getLiquidacionCostosTransaccion(item.getNumOperacion());
            BigDecimal montoSimulado = BigDecimal.ZERO;
            List<LiquidacionCostosReales> lcReales = lcrSolicitudEtapaSeguimientoRepository
                    .getLiquidacionCostosReales(item.getNumOperacion());
            if (item.getEsDolares() != null && item.getEsDolares() == true) {
                BigDecimal tc = lcrSolicitudEtapaSeguimientoRepository.getTCSol(item.getNumOperacion());
                // BigDecimal montoSimulado =
                // lcTransaccion.getTotalCostosTransaccion().multiply(tc.subtract(BigDecimal.valueOf(0.2)));
                montoSimulado = lcTransaccion.getCriBloqueoPeritaje()
                        .add(lcTransaccion.getPagosDescontadosCheque())
                        .add(lcTransaccion.getCostosAdicionales())
                        .subtract(lcTransaccion.getPagadoCliente())
                        .multiply(tc.subtract(BigDecimal.valueOf(0.2)));

                item.setMontoSimulado(montoSimulado);
                BigDecimal montoReal = BigDecimal.ZERO;

                for (LiquidacionCostosReales lcReal : lcReales) {
                    if (lcReal.getEsDolares()) {
                        montoReal = montoReal.add(lcReal.getMonto().multiply(lcReal.getTipoCambio()).setScale(2,
                                BigDecimal.ROUND_HALF_UP));
                    } else {
                        montoReal = montoReal.add(lcReal.getMonto().setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }
                System.out.println("montoReal antes de restar pagadoCliente: " + tc.subtract(BigDecimal.valueOf(0.2)));
                montoReal=montoReal.subtract(lcTransaccion.getPagadoCliente().multiply(tc.subtract(BigDecimal.valueOf(0.2))));
                item.setMontoReal(montoReal);
            } else {
                BigDecimal montoReal = BigDecimal.ZERO;

                for (LiquidacionCostosReales lcReal : lcReales) {
                    if (lcReal.getEsDolares()) {
                        montoReal = montoReal.add(lcReal.getMonto().multiply(lcReal.getTipoCambio()).setScale(2,
                                BigDecimal.ROUND_HALF_UP));
                    } else {
                        montoReal = montoReal.add(lcReal.getMonto().setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }
                montoReal =montoReal.subtract(lcTransaccion.getPagadoCliente());
                item.setMontoReal(montoReal);
                montoSimulado = lcTransaccion.getCriBloqueoPeritaje()
                        .add(lcTransaccion.getPagosDescontadosCheque())
                        .add(lcTransaccion.getCostosAdicionales())
                        .subtract(lcTransaccion.getPagadoCliente());
                item.setMontoSimulado(montoSimulado);
            }

        });

        response.setSuccess(true);
        response.setData(page.getContent());
        response.setMessage("ok");
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotal((int) page.getTotalElements());

        return response;
    }

    public ListaResponse historialLcrSolicitudByNumOperacion(Integer pageSize, Integer pageNumber, String filterText,
            Long numOperacion) {
        ListaResponse response = new ListaResponse();
        List<LcrSolicitudHistoryResponse> data = new ArrayList<>();
        List<LCRSolicitudHistory> listaLcrSolicitudHistory = lcrSolicitudHistoryRepository
                .findByNumOperacion(numOperacion);
        for (LCRSolicitudHistory lcrSolicitudHistory : listaLcrSolicitudHistory) {
            if (filterText != null && !filterText.isEmpty()) {
                continue; // Saltar usuarios que no coincidan con el filtro
            }
            LcrSolicitudHistoryResponse lcrResponse = new LcrSolicitudHistoryResponse();
            lcrResponse.setId(lcrSolicitudHistory.getId());
            lcrResponse.setMontoPrevio(lcrSolicitudHistory.getMontoSimulado());
            lcrResponse.setMontoActual(lcrSolicitudHistory.getMontoReal());
            lcrResponse.setStatusPrevio(lcrSolicitudHistory.getStatusPrevio());
            lcrResponse.setStatusActual(lcrSolicitudHistory.getStatusActual());
            // lcrResponse.setSolicitante(usuarioServiceImpl.obtenerUsuarioMantenimientoById(lcrSolicitudHistory.getSolicitante().getId()));
            // lcrResponse.setStatus(lcrSolicitudHistory.getStatus());
            lcrResponse.setLcrSolicitudEtapa(lcrSolicitudHistory.getLctSolicitudEtapa());
            lcrResponse.setMensaje(lcrSolicitudHistory.getMensaje());
            // lcrResponse.setEsDolares(lcrSolicitudHistory.getEsDolares());

            if (lcrSolicitudHistory.getCreatedBy() != null) {
                UsuarioMantenimientoDTO createdBy = usuarioServiceImpl
                        .obtenerUsuarioMantenimientoById(lcrSolicitudHistory.getCreatedBy().getId());
                lcrResponse.setCreatedBy(createdBy);
                lcrResponse.setCreatedDate(lcrSolicitudHistory.getCreatedDate());
            }

            if (lcrSolicitudHistory.getUpdatedBy() != null) {
                UsuarioMantenimientoDTO updatedBy = usuarioServiceImpl
                        .obtenerUsuarioMantenimientoById(lcrSolicitudHistory.getUpdatedBy().getId());
                lcrResponse.setUpdatedBy(updatedBy);
                lcrResponse.setUpdatedDate(lcrSolicitudHistory.getUpdatedDate());
            }

            data.add(lcrResponse);
        }
        int totalSize = data.size();
        int fromIndex = ((pageNumber - 1) * pageSize);
        int toIndex = Math.min(fromIndex + pageSize, totalSize);
        response.setSuccess(true);
        response.setData(data.subList(fromIndex, toIndex));
        response.setMessage("ok");
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotal(totalSize);
        return response;
    }

    public void actualizarEstadoAprobado(Long id) {
        try {
            lcrSolicitudEtapaSeguimientoRepository.updateEstadoAprobado(id);
            // enviando a administracion
            LCRReembolso lcrReembolso = new LCRReembolso();
            lcrReembolso.setNumOperacion(lcrSolicitudRepository.findById(id).orElse(null).getNumOperacion());
            lcrReembolso.setCreatedAt(LocalDateTime.now());
            Usuario user = new Usuario();
            user.setId(1L);
            lcrReembolso.setCreatedBy(user);
            LCRReembolsoSubEstados lcrReembolsoSubEstados = new LCRReembolsoSubEstados();
            lcrReembolsoSubEstados.setId(1L);
            lcrReembolso.setLcrReembolsoSubEstados(lcrReembolsoSubEstados);
            lcrReembolsoRepository.save(lcrReembolso);

        } catch (Exception e) {
            // response.setSuccess(false);
            // response.setMessage("Error al actualizar el estado: " + e.getMessage());
        }
    }

    public byte[] descargarReporteDevolucionesResoluciones(String startDate, String endDate, String tchn,
            String reembolsados, String aplicados) {
        return excelService.generateExcelSolicitudDevolucionesResoluciones(startDate, endDate, tchn, reembolsados, aplicados);
    }

    public byte[] descargarReporteDevolucionesResoluciones(String startDate, String endDate, String tchn,
            Integer estado) {
        return excelService.generateExcelSolicitudDevolucionesResoluciones(startDate, endDate, tchn, estado);
    }

    public byte[] descargarReporteAprobados(String startDate, String endDate, String numOperacion,String estado) {
        return excelService.generateExcelSolicitudAprobadasLiqCostos(startDate, endDate, numOperacion,estado);
    }

}
