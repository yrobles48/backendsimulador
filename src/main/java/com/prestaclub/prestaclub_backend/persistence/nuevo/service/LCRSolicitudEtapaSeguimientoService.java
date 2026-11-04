package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.EmailValues;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.OperacionesAntiguoService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRSolicitudEtapaSeguimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolso;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolsoSubEstados;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitud;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudHistory;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosReales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Modulo;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Notificaciones;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.NotificacionesUsuarios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRReembolsoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRReembolsoSubEstadosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudEtapaSeguimientoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudHistoryRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosRealesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosTransaccionRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.ModuloRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesUsuariosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.UsuarioRepository;
import com.prestaclub.prestaclub_backend.service.EmailService;

@Service
public class LCRSolicitudEtapaSeguimientoService {

    @Autowired
    private LCRSolicitudEtapaSeguimientoRepository lcrSolicitudEtapaSeguimientoRepository;

    @Autowired
    private LCRSolicitudRepository lcrSolicitudRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LCRSolicitudHistoryRepository lcrSolicitudHistoryRepository;

    @Autowired
    private LiquidacionCostosTransaccionRepository liquidacionCostosTransaccionRepository;

    @Autowired
    private LiquidacionCostosRealesRepository liquidacionCostosRealesRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private LCRReembolsoRepository lcrReembolsoRepository;

    @Autowired
    private LCRReembolsoSubEstadosRepository lcrReembolsoSubEstadosRepository;

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Autowired
    private NotificacionesUsuariosRepository notificacionesUsuariosRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OperacionesAntiguoService operacionesAntiguoService;

    public ApiResponseData<LCRSolicitudEtapaSeguimientoDTO> getLcrSolicitudByNumOperacion(Long numOperacion) {
        ApiResponseData<LCRSolicitudEtapaSeguimientoDTO> apiResponse = new ApiResponseData<>();
        try {
            List<LCRSolicitudEtapaSeguimiento> lista = lcrSolicitudEtapaSeguimientoRepository
                    .findByLcrSolicitudNumOperacion(numOperacion);

            LCRSolicitudHistory lcrSolicitudHistory = lcrSolicitudHistoryRepository
                    .findByNumOperacionAndUpdatedByIsNull(numOperacion);

            LCRSolicitudEtapaSeguimientoDTO lcrSolicitudEtapaSeguimientoDTO = new LCRSolicitudEtapaSeguimientoDTO();
            lcrSolicitudEtapaSeguimientoDTO.setEtapas(lista);
            lcrSolicitudEtapaSeguimientoDTO
                    .setMensaje(lcrSolicitudHistory == null ? "" : lcrSolicitudHistory.getMensaje());
            lcrSolicitudEtapaSeguimientoDTO
                    .setStatus(lcrSolicitudHistory == null ? 0 : lcrSolicitudHistory.getStatusActual());
            if (lista == null) {
                apiResponse.setData(null);
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No se encontro la solicitud");
                return apiResponse;
            }
            apiResponse.setSuccess(true);
            apiResponse.setData(lcrSolicitudEtapaSeguimientoDTO);
            apiResponse.setMessage("ok");
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
        }
        return apiResponse;
    }

    public ApiResponse update(Long lcrSolicitudId, Long usuarioId, Integer status, String slug) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            LCRSolicitud lcrSolicitud = lcrSolicitudRepository.findById(lcrSolicitudId).get();
            if (lcrSolicitud == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No se encontro la solicitud");
                return apiResponse;
            }
            Usuario usuario = usuarioRepository.findById(usuarioId).get();
            if (usuario == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No se encontro el usuario");
                return apiResponse;
            }

            if (slug.equals("negocios")) {
                List<OperacionDTO> operacionesXNumeroOperacion = operacionesAntiguoService
                        .obtenerOperacionesXNumeroOperacion(lcrSolicitud.getNumOperacion().intValue());
                if (operacionesXNumeroOperacion.isEmpty()) {
                    apiResponse.setSuccess(false);
                    apiResponse.setMessage("No se encontro la operacion");
                    return apiResponse;
                }
                OperacionDTO operacion = operacionesXNumeroOperacion.get(0);

                // System.out.println("operacion.getTipo_operacion_id(): " +(
                // operacion.getTipo_operacion_id()));

                if (operacion.getTipo_operacion_id() == 1) {
                    Boolean montoValido = verificarMonto(lcrSolicitud.getNumOperacion());
                    // System.out.println("montoValido: " + montoValido);
                    // System.out.println("jejejejej" );
                    if (!montoValido) {
                        apiResponse.setSuccess(false);
                        apiResponse.setMessage("El Peritaje es Obligatorio tener un monto");
                        return apiResponse;
                    }
                }
            }

            BigDecimal montoSimulado = liquidacionCostosTransaccionRepository
                    .getTotalCostosTransaccionByNumOperacion(lcrSolicitud.getNumOperacion());
            BigDecimal montoReal = liquidacionCostosRealesRepository
                    .getTotalRegistroPagosByNumOperacion(lcrSolicitud.getNumOperacion());

            LCRSolicitudEtapaSeguimiento etapaParaActualizar = lcrSolicitudEtapaSeguimientoRepository
                    .findByLcrSolicitudIdAndLcrSolicitudEtapaSlug(lcrSolicitudId, slug);
            Integer statusPrevio = etapaParaActualizar.getStatus();
            Boolean actual = etapaParaActualizar.getActual();
            etapaParaActualizar.setActual(false);
            etapaParaActualizar.setStatus(status);
            etapaParaActualizar.setUpdatedAt(LocalDateTime.now());
            etapaParaActualizar.setUpdatedBy(usuario);
            lcrSolicitudEtapaSeguimientoRepository.save(etapaParaActualizar);

            LCRSolicitudHistory lcrSolicitudHistoryPrevio = lcrSolicitudHistoryRepository
                    .findTopByUpdatedByIsNull(lcrSolicitudId);
            if (lcrSolicitudHistoryPrevio != null) {
                lcrSolicitudHistoryPrevio.setUpdatedBy(usuario);
                lcrSolicitudHistoryPrevio.setUpdatedDate(LocalDateTime.now());
                lcrSolicitudHistoryRepository.save(lcrSolicitudHistoryPrevio);
            }

            LCRSolicitudHistory lcrSolicitudHistory = new LCRSolicitudHistory();
            lcrSolicitudHistory.setCreatedBy(usuario);
            lcrSolicitudHistory.setCreatedDate(LocalDateTime.now());
            lcrSolicitudHistory.setMontoSimulado(montoSimulado);
            lcrSolicitudHistory.setMontoReal(montoReal);
            lcrSolicitudHistory.setLctSolicitud(lcrSolicitud);
            lcrSolicitudHistory.setStatusPrevio(statusPrevio);
            lcrSolicitudHistory.setStatusActual(status);
            lcrSolicitudHistory.setMensaje(null);
            lcrSolicitudHistory.setLctSolicitudEtapa(etapaParaActualizar.getLcrSolicitudEtapa());
            lcrSolicitudHistoryRepository.save(lcrSolicitudHistory);
            // System.out.println("actual: " + actual);
            if (actual) {
                String slugEtapaSiguiente = "";
                if (etapaParaActualizar.getLcrSolicitudEtapa().getSlug().equals("negocios")) {
                    slugEtapaSiguiente = "legal";
                }

                if (etapaParaActualizar.getLcrSolicitudEtapa().getSlug().equals("legal")) {
                    slugEtapaSiguiente = "contabilidad";
                }

                LCRSolicitudEtapaSeguimiento siguienteEtapa = lcrSolicitudEtapaSeguimientoRepository
                        .findByLcrSolicitudIdAndLcrSolicitudEtapaSlug(lcrSolicitudId, slugEtapaSiguiente);
                siguienteEtapa.setActual(true);
                lcrSolicitudEtapaSeguimientoRepository.save(siguienteEtapa);

                lcrSolicitud.setMontoSimulado(montoSimulado);
                lcrSolicitud.setMontoReal(montoReal);
                lcrSolicitudRepository.save(lcrSolicitud);
                if (slugEtapaSiguiente.equals("contabilidad")) {
                    enviarSocketContabilidad(usuario, lcrSolicitud.getNumOperacion(), statusPrevio);
                }

                String titulo = slugEtapaSiguiente.equals("legal") ? "Costos Aprobados por Negocio "
                        : "Solicitud para aprobacion";
                String contenido = slugEtapaSiguiente.equals("legal")
                        ? "Los costos de la Operación " + lcrSolicitud.getNumOperacion()
                                + " han sido aprobados por el Area Comercial"
                        : "Solicitud de Aprobación para la Operación " + lcrSolicitud.getNumOperacion();

                enviarNotificacion(usuario, List.of(slugEtapaSiguiente),
                        "MOVIMIENTO DE ESTADO DE SOLICITUD PARA LA OPERACION "
                                + lcrSolicitud.getNumOperacion(),
                        titulo, contenido, 2L, lcrSolicitud.getNumOperacion());

            }

            if (statusPrevio == 2) {
                String titulo = slug.equals("negocios") ? "Costos Subsanados por Negocio "
                        : "Solicitud para aprobación subsanada";
                String contenido = slug.equals("negocios")
                        ? "Los costos de la Operación " + lcrSolicitud.getNumOperacion()
                                + " han sido subsanados por el Area Comercial"
                        : "Solicitud de Aprobación subsanada para la Operación " + lcrSolicitud.getNumOperacion();
                enviarNotificacion(usuario, List.of(slug.equals("negocios") ? "legal" : "contabilidad"),
                        "MOVIMIENTO DE ESTADO DE SOLICITUD PARA LA OPERACION "
                                + lcrSolicitud.getNumOperacion(),
                        titulo, contenido, 2L, lcrSolicitud.getNumOperacion());
                if (slug.equals("legal")) {
                    actualizarEtapa(lcrSolicitud, usuario, null, "contabilidad", 0);
                    enviarSocketContabilidad(usuario, lcrSolicitud.getNumOperacion(), statusPrevio);
                }
            }

            enviarSocket(usuario, lcrSolicitud.getNumOperacion());

            apiResponse.setSuccess(true);
            apiResponse.setMessage("ok");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return apiResponse;
        }
    }

    private Boolean verificarMonto(Long numOperacion) {
        BigDecimal monto = liquidacionCostosRealesRepository.getTotalPeritajeByNumOperacion(numOperacion);
        System.out.println("monto: " + monto);
        return monto != null && monto.compareTo(BigDecimal.ZERO) > 0;
    }

    public ApiResponse observar(Long lcrSolicitudId, Long usuarioId, String mensaje, Integer type) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            LCRSolicitud lcrSolicitud = lcrSolicitudRepository.findById(lcrSolicitudId).get();
            if (lcrSolicitud == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No se encontro la solicitud");
                return apiResponse;
            }
            Usuario usuario = usuarioRepository.findById(usuarioId).get();
            if (usuario == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No se encontro el usuario");
                return apiResponse;
            }

            String mensajeCorreo = "La operacion " + lcrSolicitud.getNumOperacion()
                    + " ha sido observada por el usuario " + usuario.getUsername();

            if (type == 2) {
                observarEtapa(lcrSolicitud, usuario, mensaje, "negocios");

            }

            observarEtapa(lcrSolicitud, usuario, mensaje, "legal");

            observarEtapa(lcrSolicitud, usuario, mensaje, "contabilidad");

            enviarNotificacion(usuario, List.of("negocios", "legal"),
                    "Observacion de Solicitud - Operacion " + lcrSolicitud.getNumOperacion(),
                    "Solicitud Observada", mensajeCorreo, 2L, lcrSolicitud.getNumOperacion());

            enviarSocket(usuario, lcrSolicitud.getNumOperacion());
            enviarSocketContabilidad(usuario, lcrSolicitud.getNumOperacion(), 2);

            apiResponse.setSuccess(true);
            apiResponse.setMessage("ok");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse aprobar(Long lcrSolicitudId, Long usuarioId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            LCRSolicitud lcrSolicitud = lcrSolicitudRepository.findById(lcrSolicitudId).get();
            if (lcrSolicitud == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No se encontro la solicitud");
                return apiResponse;
            }
            Usuario usuario = usuarioRepository.findById(usuarioId).get();
            if (usuario == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("No se encontro el usuario");
                return apiResponse;
            }

            actualizarEtapa(lcrSolicitud, usuario, null, "contabilidad", 1);

            enviarNotificacion(usuario, List.of("administracion"),
                    "Nuevo TCHN para Reembolso - Operacion " + lcrSolicitud.getNumOperacion(),
                    "Nuevo TCHN para Reembolso",
                    "La Operación " + lcrSolicitud.getNumOperacion() + " está listo para el reembolso", 1L,
                    lcrSolicitud.getNumOperacion());

            LCRReembolsoSubEstados lcrReembolsoSubEstado = lcrReembolsoSubEstadosRepository.findBySlug("pendiente");

            LCRReembolso lcrReembolso = new LCRReembolso();
            lcrReembolso.setNumOperacion(lcrSolicitud.getNumOperacion());
            lcrReembolso.setLcrReembolsoSubEstados(lcrReembolsoSubEstado);
            lcrReembolso.setCreatedBy(usuario);
            lcrReembolso.setCreatedAt(LocalDateTime.now());
            lcrReembolsoRepository.save(lcrReembolso);
            enviarSocket(usuario, lcrSolicitud.getNumOperacion());
            enviarSocketContabilidad(usuario, lcrSolicitud.getNumOperacion(), 1);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("ok");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return apiResponse;
        }
    }

    public void observarEtapa(LCRSolicitud lcrSolicitud, Usuario usuario, String mensaje, String slug) {
        actualizarEtapa(lcrSolicitud, usuario, mensaje, slug, 2);
    }

    public void actualizarEtapa(LCRSolicitud lcrSolicitud, Usuario usuario, String mensaje, String slug,
            Integer statusActual) {
        try {
            BigDecimal montoSimulado = liquidacionCostosTransaccionRepository
                    .getTotalCostosTransaccionByNumOperacion(lcrSolicitud.getNumOperacion());
            BigDecimal montoReal = liquidacionCostosRealesRepository
                    .getTotalRegistroPagosByNumOperacion(lcrSolicitud.getNumOperacion());

            LCRSolicitudEtapaSeguimiento etapa = lcrSolicitudEtapaSeguimientoRepository
                    .findByLcrSolicitudIdAndLcrSolicitudEtapaSlug(lcrSolicitud.getId(), slug);
            Integer statusPrevio = etapa.getStatus();
            etapa.setStatus(statusActual);
            etapa.setUpdatedBy(slug == "contabilidad" ? usuario : etapa.getUpdatedBy());
            etapa.setUpdatedAt(LocalDateTime.now());
            lcrSolicitudEtapaSeguimientoRepository.save(etapa);

            LCRSolicitudHistory lcrSolicitudHistoryPrevio = lcrSolicitudHistoryRepository
                    .findFirstByOrderByIdDesc(lcrSolicitud.getId());

            if (lcrSolicitudHistoryPrevio == null) {
                lcrSolicitudHistoryPrevio.setUpdatedBy(usuario);
                lcrSolicitudHistoryPrevio.setUpdatedDate(LocalDateTime.now());
                // lcrSolicitudHistoryPrevio.setMensaje(mensaje);
                lcrSolicitudHistoryRepository.save(lcrSolicitudHistoryPrevio);
            }

            LCRSolicitudHistory lcrSolicitudHistory = new LCRSolicitudHistory();
            lcrSolicitudHistory.setCreatedBy(usuario);
            lcrSolicitudHistory.setCreatedDate(LocalDateTime.now());
            lcrSolicitudHistory.setMontoSimulado(montoSimulado);
            lcrSolicitudHistory.setMontoReal(montoReal);
            lcrSolicitudHistory.setLctSolicitud(lcrSolicitud);
            lcrSolicitudHistory.setStatusPrevio(statusPrevio);
            lcrSolicitudHistory.setStatusActual(statusActual);
            lcrSolicitudHistory.setMensaje(mensaje);
            lcrSolicitudHistory.setLctSolicitudEtapa(etapa.getLcrSolicitudEtapa());
            lcrSolicitudHistoryRepository.save(lcrSolicitudHistory);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error al actualizar etapa: " + e.getMessage());
        }

    }

    public void enviarSocket(Usuario usuario, Long numOperacion) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("numOperacion", numOperacion);
        payload.put("usuario", usuario);
        payload.put("message", "El usuario " + usuario.getUsername()
                + " ha realizado un cambio al estado de la solicitud de liquidacion, de la operacion " + numOperacion);
        messagingTemplate.convertAndSend("/topic/lcr-solicitud/refresh", payload);
    }

    public void enviarSocketContabilidad(Usuario usuario, Long numOperacion, Integer status) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("numOperacion", numOperacion);
        payload.put("usuario", usuario);
        payload.put("status", status);
        payload.put("message", "El usuario " + usuario.getUsername()
                + " ha realizado un cambio al estado de la solicitud de liquidacion, de la operacion " + numOperacion);

        messagingTemplate.convertAndSend("/topic/contabilidad/lcr-solicitud/refresh", payload);
    }

    @Async
    public void enviarNotificacion(Usuario usuario, List<String> slugs, String subject, String titulo, String contenido,
            Long rol, Long numOperacion) {
        Boolean existAdmin = slugs.contains("administracion");
        Boolean existConta = slugs.contains("contabilidad");

        List<Usuario> usuariosParaCorreo = usuarioRepository.findActivosByModulosSlugIn(slugs, rol);

        Notificaciones notificaciones = new Notificaciones();
        notificaciones.setTitulo(titulo);
        notificaciones.setDescripcion("El usuario " + usuario.getUsername()
                + " ha realizado un cambio al estado de la solicitud de liquidación - Operación " + numOperacion);
        notificaciones.setUrl((existAdmin ? "reembolso" : (existConta ? "lcr-solicitud" : "liquidacion-costos"))
                + "?tchn=" + numOperacion);
        notificaciones.setCreatedBy(usuario);
        notificaciones.setCreatedAt(LocalDateTime.now());
        Long idNotificacion = notificacionesRepository.save(notificaciones).getId();

        for (Usuario user : usuariosParaCorreo) {
            NotificacionesUsuarios notificacionesUsuario = new NotificacionesUsuarios();
            notificacionesUsuario.setNotificacion(notificacionesRepository.findById(idNotificacion).orElse(null));
            notificacionesUsuario.setUsuario(user);
            notificacionesUsuario.setIsView(false);
            notificacionesUsuario.setCreatedAt(LocalDateTime.now());
            notificacionesUsuariosRepository.save(notificacionesUsuario);

            if (user.isNotificacionCorreo()) {
                List<String> emails = List.of(user.getEmail());

                emailService.sendEmailMovimientoDeSolicitud(subject, titulo, contenido, emails);
            }

        }

    }

}
