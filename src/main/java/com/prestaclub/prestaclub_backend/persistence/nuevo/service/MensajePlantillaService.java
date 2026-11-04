package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRReembolsoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.*;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.*;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.UsuarioServiceImpl;
import com.prestaclub.prestaclub_backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.OperacionesAntiguoService;

@Service
public class MensajePlantillaService {

    @Value("${spring.mail.username}")
    private String mailFrom;
    @Value("${spring.mail.password}")
    private String mailPass;


    @Value("${correo}")
    private String mailFromGestion;
    @Value("${password}")
    private String mailPassGestion;
    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Autowired
    private UsuarioServiceImpl usuarioServiceImpl;


    @Autowired
    private MensajePlantillaRepository mensajePlantillaRepository;

    @Autowired
    private MensajeConfiguracionRepository mensajeConfiguracionRepository;
    @Autowired
    private RptMensajeRepository rptMensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OperacionesAntiguoService operacionService;

    @Autowired
    private EmailService emailService;

    public RptMensaje listaRptMesaje(Long numoperacion) {
        System.out.println("numoperacion" + numoperacion);
        RptMensaje rptMensajeInfo = rptMensajeRepository.findByNumOperacion(numoperacion);
        return rptMensajeInfo;
    }

    public ApiResponseData<List<MensajePlantilla>> getListaByTipoSlug(String slug) {
        ApiResponseData<List<MensajePlantilla>> l = new ApiResponseData<List<MensajePlantilla>>();
        try {
            List<MensajePlantilla> mensajePlantilla = mensajePlantillaRepository.getListaByTipoSlug(slug);
            l.setData(mensajePlantilla);
            l.setSuccess(true);
            l.setMessage("OK");
            return l;
        } catch (Exception e) {
            l.setSuccess(false);
            l.setMessage(e.getMessage());
            return l;
        }
    }

    public ApiResponse asignar(Long id, Long userId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            MensajePlantilla mensajePlantilla = mensajePlantillaRepository.findById(id).get();
            mensajePlantilla.setIsDefault(true);

            String slug = mensajePlantilla.getMensajeConfiguracion().getSlug();
            MensajePlantilla mensajePlantillaDefault = mensajePlantillaRepository.findByIsDefaultBySlug(slug);
            if (mensajePlantillaDefault != null) {
                mensajePlantillaDefault.setIsDefault(false);
                mensajePlantillaRepository.save(mensajePlantillaDefault);
            }

            mensajePlantillaRepository.save(mensajePlantilla);

            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse actualizarPlantilla(Long id, Long userId, String subject, String contenido) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            MensajePlantilla mensajePlantilla = mensajePlantillaRepository.findById(id).get();
            if (mensajePlantilla == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Plantilla no encontrada");
                return apiResponse;
            }
            mensajePlantilla.setSubject(subject);
            mensajePlantilla.setContenido(contenido);
            mensajePlantillaRepository.save(mensajePlantilla);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse eliminarPlantilla(Long id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            MensajePlantilla mensajePlantilla = mensajePlantillaRepository.findById(id).get();
            if (mensajePlantilla == null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Plantilla no encontrada");
                return apiResponse;
            }
            // mensajePlantillaRepository.delete(mensajePlantilla);
            mensajePlantilla.setActivo(false);
            mensajePlantillaRepository.save(mensajePlantilla);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse crearPlantilla(Long userId, String subject, String contenido, String nombre, String slug) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            MensajePlantilla mensajePlantilla = new MensajePlantilla();
            mensajePlantilla.setSubject(subject);
            mensajePlantilla.setContenido(contenido);
            mensajePlantilla.setActivo(true);
            mensajePlantilla.setIsDefault(false);
            mensajePlantilla.setDescripcion(nombre);
            mensajePlantilla.setMensajeConfiguracion(mensajeConfiguracionRepository.findBySlug(slug));
            mensajePlantilla.setFechaCreacion(LocalDateTime.now());
            mensajePlantillaRepository.save(mensajePlantilla);

            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse rectifi_rptCorreo(Long nrooperacion) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            rptMensajeRepository.rectificaEstadoCorreo(nrooperacion);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse act_rptCorreo(Long nrooperacion, Boolean estado, String msgerror, Long modify_by,
                                     Double nsaldo) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            rptMensajeRepository.actualizaEstadoCorreo(nrooperacion, estado, msgerror, modify_by, nsaldo);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse ins_rptMensaje(Long idReembolso, String asunto, String contenido, String correo,
            Long nrooperacion, Long UserId, Boolean esdolares, Double nsaldo, String telefono, String documento) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            rptMensajeRepository.insertaRtaMensaje(idReembolso, asunto, contenido, correo, nrooperacion, UserId,
                    esdolares, nsaldo, telefono, documento);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse act_rptMensaje(Long nrooperacion, String banco, String ctaban, String copro, String correo,
            String adicion, Long modify_by) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            RptMensaje rptMensajeInfo = rptMensajeRepository.findByNumOperacion(nrooperacion);
            rptMensajeRepository.actuSegundMensaje(nrooperacion);
            rptMensajeRepository.actualizaRtaMensajeHistorico(rptMensajeInfo.getId(),
                    rptMensajeInfo.getActivo(), rptMensajeInfo.getCreated_By(), rptMensajeInfo.getDatosadicional(),
                    rptMensajeInfo.getEstado(), rptMensajeInfo.getNum_operacion(),
                    rptMensajeInfo.getIdreembolso().getId());
            rptMensajeRepository.actualizaRtaMensaje(nrooperacion, banco, ctaban, copro, correo, adicion, modify_by);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse ins_rptFile(Long reembolsoId, Long nrooperacion, String url, String UserId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Long user = Long.parseLong(UserId);
            rptMensajeRepository.ins_rpt_files(reembolsoId, nrooperacion, url, user);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse actualizaFDevol(Long nrooperacion, Date fecha, Long UserId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            rptMensajeRepository.actualizaFDevol(nrooperacion, fecha, UserId);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse eliminarArchivo(Long id, Long nrooperacion) {
        System.out.println("idArchivo" + id);
        System.out.println("eliminarArchivo" + nrooperacion);
        ApiResponse apiResponse = new ApiResponse();
        try {
            rptMensajeRepository.eliminar_rptfiles(id, nrooperacion);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            System.out.println("eliminado con exito" + nrooperacion);
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponse actualizaCorreo(Long nrooperacion, String correo, Long UserId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            rptMensajeRepository.actualizaCorreo(nrooperacion, correo, UserId);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public MensajePlantilla consultarPlantilla(String slug, Long nroOperacion) {
        MensajePlantilla mensajePlantilla = mensajePlantillaRepository.findByIsDefaultBySlug(slug);
        // captura data de blanco para algunos datos requeridos
        OperacionDTO operacion = operacionService.getOperacionXNumeroOperacion(nroOperacion.intValue());
        RptMensaje oRptMensaje = rptMensajeRepository.findByNumOperacion(nroOperacion);
        // Capturar el @1
        String Arroba1 = operacion.getNombre_cliente();
        String Arroba6 = "";
        BigDecimal Arroba3 = null;
        String textoCorto = "";
        String Arroba5 = "";
        String texto = "";
        if (oRptMensaje != null) {
            Arroba6 = oRptMensaje.getCopropietario();

            Arroba3 = oRptMensaje.getNsaldo();
            Boolean esMoneda = oRptMensaje.getEs_dolares();
            String stip = "";
            if (esMoneda) {
                stip = "$";
            } else {
                stip = "S/";
            }
            Date Arroba4 = oRptMensaje.getFdevolucion();
            SimpleDateFormat sdfCorto = new SimpleDateFormat("dd/MM/yyyy");
            textoCorto = sdfCorto.format(Arroba4);
            System.out.println("Formato corto: " + textoCorto);
            Arroba5 = oRptMensaje.getCuentabancaria();
            texto = stip + Arroba3.toPlainString();
        }
        String newContenido = mensajePlantilla.getContenido().replaceAll("@nom_clien", Arroba1.toString());
        mensajePlantilla.setContenido(newContenido);

        newContenido = mensajePlantilla.getContenido().replaceAll("@cod_clien", nroOperacion.toString());
        mensajePlantilla.setContenido(newContenido);

        newContenido = mensajePlantilla.getContenido().replaceAll("@monto_dev", texto.toString());
        mensajePlantilla.setContenido(newContenido);

        newContenido = mensajePlantilla.getContenido().replaceAll("@fecha_dev", textoCorto);
        mensajePlantilla.setContenido(newContenido);

        newContenido = mensajePlantilla.getContenido().replaceAll("@cuenta_clien", Arroba5);
        mensajePlantilla.setContenido(newContenido);

        newContenido = mensajePlantilla.getContenido().replaceAll("@nom_propietario", Arroba6);
        mensajePlantilla.setContenido(newContenido);

        return mensajePlantilla;
    }

    public ApiResponse listaArchivos(Long nrooperacion, Date fecha, Long UserId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            rptMensajeRepository.actualizaFDevol(nrooperacion, fecha, UserId);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return apiResponse;
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage(e.getMessage());
            return apiResponse;
        }
    }

    public ApiResponseData<List<RptFiles>> obtenerArchivos(Long nrooperacion) {
        ApiResponseData<List<RptFiles>> lista = new ApiResponseData<List<RptFiles>>();

        try {
            List<RptFiles> RptFilesList = rptMensajeRepository.lista_rpt_files(nrooperacion);
            System.out.println("listaes" + RptFilesList.size());
            lista.setData(RptFilesList);
            lista.setSuccess(true);
            lista.setMessage("OK");
            return lista;
        } catch (Exception e) {
            lista.setSuccess(false);
            lista.setMessage(e.getMessage());
            return lista;
        }
    }

    public ApiResponseData<List<Banco>> listarbanco() {
        ApiResponseData<List<Banco>> lista = new ApiResponseData<List<Banco>>();
        List<Banco> bancolista = new ArrayList<>();
        try {
            List<Object[]> BancoList = rptMensajeRepository.listarbanco();
            System.out.println("listaes" + BancoList.size());
            for (Object[] obj : BancoList) {
                Banco info = new Banco();
                info.setId(((Number) obj[0]).longValue());
                info.setDescripcion(obj[2].toString());
                info.setCodigo(((Number) obj[1]).longValue());
                // Crear DTO
                bancolista.add(info);
            }

            lista.setData(bancolista);
            lista.setSuccess(true);
            lista.setMessage("OK");
            return lista;
        } catch (Exception e) {
            System.out.println("errorrrr listabanco");
            lista.setSuccess(false);
            lista.setMessage(e.getMessage());
            return lista;
        }
    }


    public void enviaNotificacion(List<String> slugs, String subject, String titulo, String contenido, Long rol, Long userId,String correoCliente) {
        List<Usuario> usuariosParaCorreo = this.usuarioRepository.findActivosByModulosSlugIn(slugs, rol);
        Notificaciones notificaciones = new Notificaciones();
        notificaciones.setTitulo(titulo);
        Usuario user = usuarioServiceImpl.findById(userId);
        notificaciones.setDescripcion("El usuario " + user.getUsername() + " ha generado una nueva notificación: " + titulo);
        notificaciones.setUrl("desembolso");
        notificaciones.setCreatedBy(user);
        notificaciones.setCreatedAt(LocalDateTime.now());
        Long idNotificacion = ((Notificaciones)this.notificacionesRepository.save(notificaciones)).getId();
        List<String> emails = new ArrayList();
        Iterator var12 = usuariosParaCorreo.iterator();

        for (Usuario user2 : usuariosParaCorreo) {
            NotificacionesUsuarios notificacionesUsuario = new NotificacionesUsuarios();
            notificacionesUsuario.setNotificacion(notificacionesRepository.findById(idNotificacion).orElse(null));
            notificacionesUsuario.setUsuario(user2);
            notificacionesUsuario.setIsView(false);
            notificacionesUsuario.setCreatedAt(LocalDateTime.now());
            if (user2.isNotificacionCorreo()) {
                emails.add(user2.getEmail());
            }
        }
        //emails.add(correoCliente);
        String[] arrayEmails = (String[])emails.toArray(new String[0]);
        this.emailService.sendFromGMail2(this.mailFromGestion, this.mailPassGestion, arrayEmails, subject, contenido);
    }



}