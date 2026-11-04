package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRReembolsoDTO;
// import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolso;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolsoSubEstados;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosReales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Notificaciones;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.NotificacionesUsuarios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRReembolsoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRReembolsoSubEstadosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudEtapaSeguimientoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesUsuariosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.UsuarioRepository;
import com.prestaclub.prestaclub_backend.service.EmailService;

@Service
public class LCRReembolsoService {

    @Autowired
    private LCRReembolsoRepository lcrReembolsoRepository;

    @Autowired
    private LCRReembolsoSubEstadosRepository lcrReembolsoSubEstadosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacionesUsuariosRepository notificacionesUsuariosRepository;

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioServiceImpl usuarioServiceImpl;

    @Autowired
    private LCRSolicitudEtapaSeguimientoRepository lcrSolicitudEtapaSeguimientoRepository;

    public ListaResponse listaLcrReembolso(Integer pageSize, Integer pageNumber, String filterText, Long status) {
        ListaResponse response = new ListaResponse();
        System.out.println("pageNumber"+pageNumber);
        System.out.println("pageSize"+pageSize);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<LCRReembolsoDTO> page = lcrReembolsoRepository.getListaLcrReembolsoByFilter(filterText, status, pageable);
        System.out.println("pageable"+pageable);
        System.out.println("page"+page.getTotalElements());

        page.getContent().forEach(item -> {
            LiquidacionCostosTransaccion lcTransaccion = lcrSolicitudEtapaSeguimientoRepository
                    .getLiquidacionCostosTransaccion(item.getLcrReembolso().getNumOperacion());
            BigDecimal montoSimulado = BigDecimal.ZERO;
            BigDecimal montoReal = BigDecimal.ZERO;
            List<LiquidacionCostosReales> lcReales = lcrSolicitudEtapaSeguimientoRepository
                    .getLiquidacionCostosReales(item.getLcrReembolso().getNumOperacion());

            item.setFenvio(item.getFechaenvio2() != null ? item.getFechaenvio2() : item.getFechaenvio1());

            if (item.getLcrSolicitud().getEsDolares() != null && item.getLcrSolicitud().getEsDolares() == true) {
                BigDecimal tc = lcrSolicitudEtapaSeguimientoRepository
                        .getTCSol(item.getLcrReembolso().getNumOperacion());
                // BigDecimal montoSimulado =
                // lcTransaccion.getTotalCostosTransaccion().multiply(tc.subtract(BigDecimal.valueOf(0.2)));
                montoSimulado = lcTransaccion.getCriBloqueoPeritaje()
                        .add(lcTransaccion.getPagosDescontadosCheque())
                        .add(lcTransaccion.getCostosAdicionales())
                        .subtract(lcTransaccion.getPagadoCliente())
                        .multiply(tc.subtract(BigDecimal.valueOf(0.2)));

                item.setMontoSimulado(montoSimulado);
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
                item.setSaldo(montoSimulado.subtract(montoReal));
            } else {
                for (LiquidacionCostosReales lcReal : lcReales) {
                    if (lcReal.getEsDolares()) {

                        montoReal = montoReal.add(lcReal.getMonto().multiply(lcReal.getTipoCambio()).setScale(2,
                                BigDecimal.ROUND_HALF_UP));

                    } else {
                        montoReal = montoReal.add(lcReal.getMonto().setScale(2, BigDecimal.ROUND_HALF_UP));

                    }
                }
                montoReal = montoReal.subtract(lcTransaccion.getPagadoCliente());
                item.setMontoReal(montoReal);
                montoSimulado = lcTransaccion.getCriBloqueoPeritaje()
                        .add(lcTransaccion.getPagosDescontadosCheque())
                        .add(lcTransaccion.getCostosAdicionales())
                        .subtract(lcTransaccion.getPagadoCliente())  ;

                item.setMontoSimulado(montoSimulado);
            }
            item.setSaldo(montoSimulado.subtract(montoReal).setScale(2, BigDecimal.ROUND_HALF_UP));
        });
        System.out.println("pageable"+pageable);
        System.out.println("pagefinal"+page.getTotalElements());

        response.setSuccess(true);
        response.setData(page.getContent());
        response.setMessage("ok");
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotal((int) page.getTotalElements());

        return response;
    }

    public ListaResponse listaLcrReembolsoContabilidad(Integer pageSize, Integer pageNumber, String filterText,
            Long status) {
        ListaResponse response = new ListaResponse();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<LCRReembolsoDTO> page = lcrReembolsoRepository.getListaLcrReembolsoByFilterContabilidad(filterText, status,
                pageable);

        page.getContent().forEach(item -> {
            LiquidacionCostosTransaccion lcTransaccion = lcrSolicitudEtapaSeguimientoRepository
                    .getLiquidacionCostosTransaccion(item.getLcrReembolso().getNumOperacion());
            BigDecimal montoSimulado = BigDecimal.ZERO;
            BigDecimal montoReal = BigDecimal.ZERO;
            List<LiquidacionCostosReales> lcReales = lcrSolicitudEtapaSeguimientoRepository
                    .getLiquidacionCostosReales(item.getLcrReembolso().getNumOperacion());

            item.setFenvio(item.getFechaenvio2() != null ? item.getFechaenvio2() : item.getFechaenvio1());

            if (item.getLcrSolicitud().getEsDolares() != null && item.getLcrSolicitud().getEsDolares() == true) {
                BigDecimal tc = lcrSolicitudEtapaSeguimientoRepository
                        .getTCSol(item.getLcrReembolso().getNumOperacion());
                // BigDecimal montoSimulado =
                // lcTransaccion.getTotalCostosTransaccion().multiply(tc.subtract(BigDecimal.valueOf(0.2)));
                montoSimulado = lcTransaccion.getCriBloqueoPeritaje()
                        .add(lcTransaccion.getPagosDescontadosCheque())
                        .add(lcTransaccion.getCostosAdicionales())
                        .subtract(lcTransaccion.getPagadoCliente())
                        .multiply(tc.subtract(BigDecimal.valueOf(0.2)));

                item.setMontoSimulado(montoSimulado);
                for (LiquidacionCostosReales lcReal : lcReales) {
                    if (lcReal.getEsDolares()) {
                        montoReal = montoReal.add(lcReal.getMonto().multiply(lcReal.getTipoCambio()).setScale(2,
                                BigDecimal.ROUND_HALF_UP));
                    } else {
                        montoReal = montoReal.add(lcReal.getMonto().setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }
                montoReal =montoReal.subtract(lcTransaccion.getPagadoCliente().multiply(tc.subtract(BigDecimal.valueOf(0.2))));

                item.setMontoReal(montoReal);
                item.setSaldo(montoSimulado.subtract(montoReal));
            } else {
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
            item.setSaldo(montoSimulado.subtract(montoReal).setScale(2, BigDecimal.ROUND_HALF_UP));
        });

        response.setSuccess(true);
        response.setData(page.getContent());
        response.setMessage("ok");
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotal((int) page.getTotalElements());

        return response;
    }

    public Integer totalContabilidad() {

        Integer resp = lcrReembolsoRepository.getTotalContabilidad();
        return resp;
    }

    public ListaResponse listaContabilidad() {
        ListaResponse response = new ListaResponse();
        List<LCRReembolsoDTO> reembolsolist = new ArrayList<>();
        List<Object[]> lista = lcrReembolsoRepository.getListaLcrContabilidad();
        for (Object[] obj : lista) {
            LCRReembolso info = new LCRReembolso();
            info.setId(((Number) obj[0]).longValue());
            info.setNumOperacion(((Number) obj[1]).longValue());
            info.setEs_dolares(Boolean.parseBoolean(obj[2].toString()));
            info.setFlag(Boolean.parseBoolean(obj[7].toString()));
            // Crear DTO
            LCRReembolsoDTO dto = new LCRReembolsoDTO();
            dto.setLcrReembolso(info);
            dto.setEnvio1((String) obj[3]);
            dto.setMontoSimulado((BigDecimal) obj[4]);
            dto.setMontoReal((BigDecimal) obj[5]);
            dto.setSaldo((BigDecimal) obj[6]);

            reembolsolist.add(dto);
        }
        response.setSuccess(true);
        response.setData(reembolsolist);
        response.setMessage("ok");
        return response;
    }

    public ApiResponse aprobarPendiente(Long lcrReembolsoId, Long userId) {
        ApiResponse response = new ApiResponse();
        try {
            LCRReembolsoSubEstados lcrReembolsoSubEstados = lcrReembolsoSubEstadosRepository.findBySlug("en-proceso");
            Usuario usuario = usuarioRepository.findById(userId).get();
            if (usuario == null) {
                response.setSuccess(false);
                response.setMessage("No se encontro el usuario");
                return response;
            }

            LCRReembolso lcrReembolso = lcrReembolsoRepository.findById(lcrReembolsoId).get();
            lcrReembolso.setLcrReembolsoSubEstados(lcrReembolsoSubEstados);
            lcrReembolso.setFechaAprobacion(LocalDateTime.now());
            lcrReembolso.setUsuarioAprobacion(usuario);
            lcrReembolsoRepository.save(lcrReembolso);

            enviarNotificacion(usuario, List.of("administracion"),
                    "Inicio de Reembolso - Operacion " + lcrReembolso.getNumOperacion(), "Inicio de Reembolso",
                    "La Operación " + lcrReembolso.getNumOperacion() + " está listo para el reembolso", 2L);

            response.setSuccess(true);
            response.setMessage("Solicitud aprobada");
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    public ApiResponse aprobarPendienteTodos(ListaResponse lcr) {
        ApiResponse response = new ApiResponse();
        StringBuilder htmlBuilder = new StringBuilder();

        // Construir tabla HTML con estilos
        htmlBuilder.append("<table style='border-collapse: collapse; width: 100%; margin: 20px 0;'>");
        htmlBuilder.append("<thead>");
        htmlBuilder.append("<tr style='background-color: #4CAF50; color: white;'>");
        htmlBuilder.append("<th style='border: 1px solid #ddd; padding: 12px; text-align: left;'>N°</th>");
        htmlBuilder.append("<th style='border: 1px solid #ddd; padding: 12px; text-align: left;'>Operación</th>");
        htmlBuilder.append("</tr>");
        htmlBuilder.append("</thead>");
        htmlBuilder.append("<tbody>");

        try {
            int contador = 1;
            for (Object obj : lcr.getData()) {
                LCRReembolsoDTO dto = (LCRReembolsoDTO) obj;
                Long numOperacion = dto.getLcrReembolso().getNumOperacion();

                // Alternar colores de filas
                String bgColor = (contador % 2 == 0) ? "#f2f2f2" : "#ffffff";
                htmlBuilder.append("<tr style='background-color: ").append(bgColor).append(";'>");
                htmlBuilder.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(contador)
                        .append("</td>");
                htmlBuilder.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(numOperacion)
                        .append("</td>");
                htmlBuilder.append("</tr>");

                Long lcrReembolsoId = dto.getLcrReembolso().getId();
                Long userId = dto.getLcrReembolso().getUsuarioAprobacion() == null ? 1L
                        : dto.getLcrReembolso().getUsuarioAprobacion().getId();

                LCRReembolsoSubEstados lcrReembolsoSubEstados = lcrReembolsoSubEstadosRepository
                        .findBySlug("en-proceso");
                Usuario usuario = usuarioRepository.findById(userId).get();

                if (usuario == null) {
                    response.setSuccess(false);
                    response.setMessage("No se encontró el usuario");
                    return response;
                }

                LCRReembolso lcrReembolso = lcrReembolsoRepository.findById(lcrReembolsoId).get();
                lcrReembolso.setLcrReembolsoSubEstados(lcrReembolsoSubEstados);
                lcrReembolso.setFechaAprobacion(LocalDateTime.now());
                lcrReembolso.setUsuarioAprobacion(usuario);
                lcrReembolsoRepository.save(lcrReembolso);

                contador++;
            }

            htmlBuilder.append("</tbody>");
            htmlBuilder.append("</table>");
            htmlBuilder.append("<p style='margin-top: 20px;'><strong>Total de operaciones: </strong>")
                    .append(contador - 1).append("</p>");

            Usuario usuario = usuarioRepository.findById(1L).get();
            enviarNotificacion(usuario, List.of("administracion"),
                    "Inicio de Reembolso - Aprobación masiva",
                    "Inicio de Reembolso",
                    htmlBuilder.toString(),
                    2L);

            response.setSuccess(true);
            response.setMessage("Solicitudes aprobadas correctamente");
            return response;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            e.printStackTrace();
            return response;
        }
    }

    public void enviarNotificacion(Usuario usuario, List<String> slugs, String subject, String titulo, String contenido,
            Long rol) {
        List<Usuario> usuariosParaCorreo = usuarioRepository.findActivosByModulosSlugIn(slugs, rol);

        Notificaciones notificaciones = new Notificaciones();
        notificaciones.setTitulo(titulo);
        notificaciones.setDescripcion("El usuario " + usuario.getUsername() + " ha subido TCHN Endosados al sistema");
        notificaciones.setUrl("solicitudes-de-liquidacion");
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

                emailService.sendEmailMovimientoDeSolicitudHtml(subject, titulo, contenido, emails);
            }

        }

    }

    public void actualizaReembolso(Long numoperacion, Long estado) {
        lcrReembolsoSubEstadosRepository.actualizaReembolso(numoperacion, estado);
       // lcrReembolsoSubEstadosRepository.actualizafaplicacionConsulta(numoperacion);
    }

    public void actualizafaplicacion(Long numoperacion, Date fec) {
        lcrReembolsoSubEstadosRepository.actualizafaplicacion(numoperacion, fec);
    }

    public void actualizaContabilidad(Long numoperacion, Long estado) {
        lcrReembolsoSubEstadosRepository.actualizaReembolso(numoperacion, estado);
        lcrReembolsoSubEstadosRepository.actualizafcontabilidad(numoperacion);
    }


    public void actualizaxReembolso(Long numoperacion, Long estado) {
        lcrReembolsoSubEstadosRepository.actualizaReembolso(numoperacion, estado);
        lcrReembolsoSubEstadosRepository.actualizaCuenta(numoperacion);
    }

  public void enviaContabilidad(Long numeracion, List<String> slugs, String subject, String titulo, String contenido,
            Long rol, Long userId) {
        List<Usuario> usuariosParaCorreo = usuarioRepository.findActivosByModulosSlugIn(slugs, rol);
        Notificaciones notificaciones = new Notificaciones();
        notificaciones.setTitulo(titulo);
        Usuario user = usuarioServiceImpl.findById(userId);
        notificaciones.setDescripcion("El usuario " + user.getUsername() + "envio la operación " + numeracion + " a contabilidad");
        notificaciones.setUrl("devoluciones");
        notificaciones.setCreatedBy(user);
        notificaciones.setCreatedAt(LocalDateTime.now());
        Long idNotificacion = notificacionesRepository.save(notificaciones).getId();
        List<String> emails = new ArrayList<>();
        for (Usuario user2 : usuariosParaCorreo) {
            NotificacionesUsuarios notificacionesUsuario = new NotificacionesUsuarios();
            notificacionesUsuario.setNotificacion(notificacionesRepository.findById(idNotificacion).orElse(null));
            notificacionesUsuario.setUsuario(user2);
            notificacionesUsuario.setIsView(false);
            notificacionesUsuario.setCreatedAt(LocalDateTime.now());
            notificacionesUsuariosRepository.save(notificacionesUsuario);
            if (user2.isNotificacionCorreo()) {
                emails.add(user2.getEmail());
            }
        }
        emailService.sendEmailMovimientoDeSolicitud(subject, titulo, contenido, emails);
    }

    public void soloflag(Long numoperacion) {
        lcrReembolsoSubEstadosRepository.soloFlag(numoperacion);
    }

}
