package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.ErrorDatos;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.UsuarioServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.OperacionesAntiguoService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitud;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapa;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Notificaciones;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.NotificacionesUsuarios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.TchnEndosado;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudEtapaRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudEtapaSeguimientoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosTransaccionRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesUsuariosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.TchnEndosadoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.UsuarioRepository;
import com.prestaclub.prestaclub_backend.service.EmailService;

@Service
public class TchnEndosadoService {

    @Value("${spring.mail.username}")
    private String mailFrom;
    @Value("${spring.mail.password}")
    private String mailPass;
    @Autowired
    private TchnEndosadoRepository tchnEndosadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LiquidacionCostosTransaccionRepository liquidacionCostosTransaccionRepository;

    @Autowired
    private LCRSolicitudRepository lcrSolicitudRepository;

    @Autowired
    private LCRSolicitudEtapaRepository lcrSolicitudEtapaRepository;

    @Autowired
    private LCRSolicitudEtapaSeguimientoRepository lcrSolicitudEtapaSeguimientoRepository;

    @Autowired
    private NotificacionesUsuariosRepository notificacionesUsuariosRepository;

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Autowired
    private UsuarioServiceImpl usuarioServiceImpl;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OperacionesAntiguoService operacionesAntiguoService;

    public ApiResponse cargaMasiva(MultipartFile file, Long userId){
        Usuario user = usuarioRepository.findById(userId).orElse(null);
        if(user == null) return new ApiResponse("El userId es requerido", false);

        Integer rowNum = 0; // POSICION DEL ROW A LEER 

        System.out.println("datos incorrectos1111");
        String contenido = "";
        List<ErrorDatos> listaError = new ArrayList();
        contenido = "<html><body><table><tr><td>Observaciones</td></tr>";


        Workbook workbook = null;
        List<String> nombresEsperados = Arrays.asList(
                "TCHN_INT", "FECHA_ENDOSO", "TCHN_SUNARP","P_REGISTRAL"
        );

        try (InputStream is = file.getInputStream()) {
            workbook = new XSSFWorkbook(is);
            // Sheet sheet = workbook.getSheetAt(0); 

            /////////////////////////// COMIENZO DE VALIDACIONES ///////////////////////// 

            Sheet sheet = workbook.getSheetAt(0);
            Row rowHeader = sheet.getRow(rowNum);  // lee la fila de los nombres 
            if (rowHeader == null) { return new ApiResponse("Error al procesar el archivo: Fila de encabezado no encontrada", false); }

            for (int i = 0; i < nombresEsperados.size(); i++) {
                Cell cell = rowHeader.getCell(i);
                if (cell == null) {
                    return new ApiResponse("Error al buscar la celda " + nombresEsperados.get(i), false);
                }
                if (!cell.toString().trim().equalsIgnoreCase(nombresEsperados.get(i))) {
                    return new ApiResponse("Error al buscar la celda " + nombresEsperados.get(i), false);
                }
            }

            ////////// cambio ///

            Boolean esDesembolso=false;
            Boolean esError=false;

            for (int i = rowNum + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                ErrorDatos newErrorDatos = new ErrorDatos();


                if (row == null) { continue; }
                if (row.getCell(0) == null || row.getCell(0).toString().trim().isEmpty()) {
                    continue; // Ignorar celdas vacías
                }


                int aa = i + 1;
                Long tchn = (long)row.getCell(0).getNumericCellValue();
                if (row.getCell(0).getCellType() != CellType.NUMERIC) {
                    String columLetter = CellReference.convertNumToColString(row.getCell(0).getColumnIndex());
                    newErrorDatos.setTexto("Error al capturar el Numero de TCHN :" + tchn + " en la Columna " + columLetter + " en la fila " + aa);
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }

                if (tchn.toString().trim().length() > 6) {
                    newErrorDatos.setTexto("Error al capturar el Numero de TCHN : " + tchn + " en la fila " + aa + " tiene mas de 6 digitos");
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }


                newErrorDatos.setCodigoOperacion(tchn);
                if (this.tchnEndosadoRepository.findByCodigoOperacion(tchn) != null) {
                    newErrorDatos.setTexto("Error al capturar el Numero de TCHN : " + tchn + " en la fila " + aa + " ya existe");
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }

                esDesembolso = listaError.stream().anyMatch((t) -> {
                    return t.getCodigoOperacion().equals(tchn);
                });
                if (esDesembolso) {
                    newErrorDatos.setTexto("Error al capturar el Numero de TCHN : " + tchn + " en la fila " + aa + " ya existe en la lista");
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }

                esDesembolso = this.liquidacionCostosTransaccionRepository.existsDesembolso(tchn);
                if (!esDesembolso) {
                    newErrorDatos.setTexto("Error al capturar el Numero de TCHN : " + tchn + " en la fila " + aa + " no es desembolso");
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }
                String tchnSunarp = row.getCell(2).getStringCellValue();

                LocalDateTime fechaEndoso = LocalDateTime.ofInstant(row.getCell(1).getDateCellValue().toInstant(), ZoneId.systemDefault());
                if (fechaEndoso.isAfter(LocalDateTime.now())) {
                    newErrorDatos.setTexto("Error al capturar " + tchn + " la fecha : " + String.valueOf(fechaEndoso) + " en la fila " + aa + " es mayor a la fecha actual");
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }

                if (row.getCell(2).getCellType() != CellType.STRING) {
                    tchnSunarp = CellReference.convertNumToColString(row.getCell(2).getColumnIndex());
                    newErrorDatos.setTexto("Error al capturar el TCHN " + tchn + " SUNARP : en la Columna " + tchnSunarp + " en la fila " + aa);
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }

                newErrorDatos.setCodigoSunarp(tchnSunarp);
                if (tchnEndosadoRepository.findByCodigoSunarp(tchnSunarp) != null) {
                    newErrorDatos.setTexto("Error al capturar el TCHN " + tchn + " SUNARP : " + tchnSunarp + " en la fila " + aa + " ya existe en la lista");
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }

                String finalTchnSunarp = tchnSunarp;
                boolean existeSunarp = listaError.stream().anyMatch(
                        t -> t.getCodigoSunarp().equals(finalTchnSunarp)
                );

                if (existeSunarp) {
                    newErrorDatos.setTexto("Error al capturar el TCHN " + tchn + " SUNARP : " + tchnSunarp + " en la fila " + aa + " ya existe en la lista");
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }

                String p_registral = "";
                if (row.getCell(3).getCellType() != CellType.STRING) {
                    int valorEntero = (int)row.getCell(3).getNumericCellValue();
                    String valor = String.valueOf(valorEntero);
                    p_registral = valor;
                } else {
                    p_registral = row.getCell(3).getStringCellValue();
                }

                Integer tchnInt = tchn.intValue();
                if (!this.operacionesAntiguoService.existPRegistral(tchnInt, p_registral.trim())) {
                    newErrorDatos.setTexto("Error al capturar " + tchn + "  el P_REGISTRAL : " + p_registral + " en la fila " + aa + " , el p_registral no coincide con el tchn");
                    contenido = contenido + "<tr><td>" + newErrorDatos.getTexto() + "</td></tr>";
                    esError=true;
                }
                if (esError==true) {
                    listaError.add(newErrorDatos);
                }
                rowNum = i;

            }
            if (listaError.size() > 0) {
                this.enviaError(List.of("contabilidad","negocios","legal"), "ERRORES DE TCHN SUBIDOS", "ERRORES-TCHN SUBIDOS AL SISTEMA", contenido, 2L, user.getId());
                return new ApiResponse("Error de carga", false);
            }

            contenido = "";


            ///////////////////////// COMIENZO DE LECTURA DE DATOS ///////////////////////// 
            List<TchnEndosado> listaTchn = new ArrayList<>();
            rowNum = 0;
            for (int i = rowNum + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) { continue; }
                if (row.getCell(0) == null || row.getCell(0).toString().trim().isEmpty()) {
                    continue; // Ignorar celdas vacías
                }

                /////////////// TCHN //////////////

                //verificar que el tchn sea numerico
                if(row.getCell(0).getCellType() != CellType.NUMERIC) {
                    String columLetter = CellReference.convertNumToColString(row.getCell(0).getColumnIndex());
                    return new ApiResponse("Error al capturar el Numero de TCHN : en la Columna "+columLetter + " en la fila " + (i + 1) , false);
                }

                //capturar el tchn
                Long tchn = (long) row.getCell(0).getNumericCellValue();

                //verificar que el tchn tenga maximo 6 digitos
                if(tchn.toString().trim().length() > 6) {
                    return new ApiResponse("Error al capturar el Numero de TCHN : "+tchn + " en la fila " + (i + 1) + " tiene mas de 6 digitos", false);
                }

                //verificar que el tchn no exista en la BD
                if(tchnEndosadoRepository.findByCodigoOperacion(tchn) != null) {
                    return new ApiResponse("Error al capturar el Numero de TCHN : "+tchn + " en la fila " + (i + 1) + " ya existe", false);
                }

                //verificar que el tchn no exista en listaTchn
                Boolean existeTchn = listaTchn.stream().anyMatch(t -> t.getCodigoOperacion().equals(tchn));
                if (existeTchn) {
                    return new ApiResponse("Error al capturar el Numero de TCHN : "+tchn + " en la fila " + (i + 1) + " ya existe en la lista", false);
                }

                //verificar que el tchn sea desembolso
                esDesembolso = liquidacionCostosTransaccionRepository.existsDesembolso(tchn);
                if(!esDesembolso) {
                    return new ApiResponse("Error al capturar el Numero de TCHN : "+tchn + " en la fila " + (i + 1) + " no es desembolso", false);
                }

                /////////////// FECHA ENDOSO //////////////

                //verificar que la fecha sea numerica
                if(row.getCell(1).getCellType() != CellType.NUMERIC) {
                    String columLetter = CellReference.convertNumToColString(row.getCell(1).getColumnIndex());
                    return new ApiResponse("Error al capturar la fecha : en la Columna "+columLetter + " en la fila " + (i + 1) , false);
                }

                //capturar la fecha
                LocalDateTime fechaEndoso = LocalDateTime.ofInstant(row.getCell(1).getDateCellValue().toInstant(), ZoneId.systemDefault());

                //verificar que la fecha endoso no sea mayor a la fecha actual
                if(fechaEndoso.isAfter(LocalDateTime.now())) {
                    return new ApiResponse("Error al capturar la fecha : "+fechaEndoso + " en la fila " + (i + 1) + " es mayor a la fecha actual", false);
                }

                /////////////// TCHN SUNARP //////////////

                //verificar que el tchnSunarp sea string
                if(row.getCell(2).getCellType() != CellType.STRING) {
                    //String columLetter = CellReference.convertNumToColString(row.getCell(2).getColumnIndex());
                    String columLetter = CellReference.convertNumToColString(row.getCell(2).getColumnIndex());
                    return new ApiResponse("Error al capturar el TCHN SUNARP : en la Columna "+columLetter + " en la fila " + (i + 1) , false);
                }

                //capturar el tchnSunarp
                String tchnSunarp = row.getCell(2).getStringCellValue();

                //verificar que el tchnSunarp no exista en la BD
                if(tchnEndosadoRepository.findByCodigoSunarp(tchnSunarp) != null) {
                    return new ApiResponse("Error al capturar el TCHN SUNARP : "+tchnSunarp + " en la fila " + (i + 1) + " ya existe en la lista", false);
                }

                //verificar que el tchnSunarp no exista en listaTchn
                boolean existeSunarp = listaTchn.stream().anyMatch(t -> t.getCodigoSunarp().equals(tchnSunarp));
                if (existeSunarp) {
                    return new ApiResponse("Error al capturar el TCHN SUNARP : "+tchnSunarp + " en la fila " + (i + 1) + " ya existe en la lista", false);
                }

                ///////////// P_REGISTRAL //////////////
                String p_registral="";
                //verificar que el p_registral sea numerico
                if(row.getCell(3).getCellType() != CellType.STRING) {
                    int valorEntero = (int)row.getCell(3).getNumericCellValue();
                    String valor = String.valueOf(valorEntero);
                    p_registral = valor;
                }else{
                    p_registral = row.getCell(3).getStringCellValue();
                }

                //capturar el p_registral
                // String p_registralInt = p_registral.trim();
                Integer tchnInt = tchn.intValue();
                //verificar que exista correctamente el p_registral
                if(!operacionesAntiguoService.existPRegistral(tchnInt, p_registral.trim())) {
                    return new ApiResponse("Error al capturar el P_REGISTRAL : "+p_registral + " en la fila " + (i + 1) + " , el p_registral no coincide con el tchn", false);
                }

                /////////////// FIN DE LECTURA DE DATOS //////////////
                /// INICIO DE ASIGNACION DE DATOS ///

                TchnEndosado tchnEndosado = new TchnEndosado();
                tchnEndosado.setCodigoOperacion(tchn);
                tchnEndosado.setFechaEndoso(fechaEndoso);
                tchnEndosado.setCodigoSunarp(tchnSunarp);

                listaTchn.add(tchnEndosado);

                rowNum = i;

            }


            if (listaTchn.isEmpty()) { return new ApiResponse("Error al procesar el archivo: No se encontraron datos", false); }




            LCRSolicitudEtapa etapaNegocios = lcrSolicitudEtapaRepository.findBySlug("negocios");
            LCRSolicitudEtapa etapaLegal = lcrSolicitudEtapaRepository.findBySlug("legal");
            LCRSolicitudEtapa etapaContabilidad = lcrSolicitudEtapaRepository.findBySlug("contabilidad");
            // LCRSolicitudEtapa etapaAdministracion = lcrSolicitudEtapaRepository.findBySlug("administracion");

            // LCRSolicitudEtapaSeguimiento lcrSolicitudEtapaSeguimientoNegocios = new LCRSolicitudEtapaSeguimiento();
            // LCRSolicitudEtapaSeguimiento lcrSolicitudEtapaSeguimientoLegal = new LCRSolicitudEtapaSeguimiento();
            // LCRSolicitudEtapaSeguimiento lcrSolicitudEtapaSeguimientoContabilidad = new LCRSolicitudEtapaSeguimiento();
            // LCRSolicitudEtapaSeguimiento lcrSolicitudEtapaSeguimientoAdministracion = new LCRSolicitudEtapaSeguimiento();






            listaTchn.forEach(
                    tchnEndosado -> {

                        LCRSolicitudEtapaSeguimiento seguimientoNegocios = new LCRSolicitudEtapaSeguimiento();
                        LCRSolicitudEtapaSeguimiento seguimientoLegal = new LCRSolicitudEtapaSeguimiento();
                        LCRSolicitudEtapaSeguimiento seguimientoContabilidad = new LCRSolicitudEtapaSeguimiento();
                        // LCRSolicitudEtapaSeguimiento seguimientoAdministracion = new LCRSolicitudEtapaSeguimiento();

                        Usuario usuarioSistema = usuarioRepository.findById(0L).orElse(user);

                        LCRSolicitud lcrSolicitud =  new LCRSolicitud();
                        lcrSolicitud.setCreatedAt(LocalDateTime.now());
                        lcrSolicitud.setCreatedBy(usuarioSistema);
                        lcrSolicitud.setUpdatedAt(LocalDateTime.now());
                        lcrSolicitud.setUpdatedBy(usuarioSistema);

                        Boolean esDolares = liquidacionCostosTransaccionRepository.getEsDolaresbyTchn(tchnEndosado.getCodigoOperacion());
                        lcrSolicitud.setEsDolares(esDolares != null ? esDolares : false);
                        lcrSolicitud.setNumOperacion(tchnEndosado.getCodigoOperacion());
                        LCRSolicitud lcrSolicitudSaved = lcrSolicitudRepository.save(lcrSolicitud);

                        //Negocios
                        seguimientoNegocios.setStatus(0);
                        seguimientoNegocios.setActual(true);
                        seguimientoNegocios.setLcrSolicitudEtapa(etapaNegocios);
                        seguimientoNegocios.setLcrSolicitud(lcrSolicitudSaved);
                        seguimientoNegocios.setUpdatedAt(LocalDateTime.now());
                        seguimientoNegocios.setUpdatedBy(usuarioSistema);
                        seguimientoNegocios.setCreatedAt(LocalDateTime.now());
                        seguimientoNegocios.setCreatedBy(usuarioSistema);
                        lcrSolicitudEtapaSeguimientoRepository.save(seguimientoNegocios);

                        //Legal
                        seguimientoLegal.setActual(false);
                        seguimientoLegal.setStatus(0);
                        seguimientoLegal.setLcrSolicitudEtapa(etapaLegal);
                        seguimientoLegal.setLcrSolicitud(lcrSolicitudSaved);
                        seguimientoLegal.setUpdatedAt(LocalDateTime.now());
                        seguimientoLegal.setUpdatedBy(usuarioSistema);
                        seguimientoLegal.setCreatedAt(LocalDateTime.now());
                        seguimientoLegal.setCreatedBy(usuarioSistema);
                        lcrSolicitudEtapaSeguimientoRepository.save(seguimientoLegal);

                        //Contabilidad
                        seguimientoContabilidad.setActual(false);
                        seguimientoContabilidad.setStatus(0);
                        seguimientoContabilidad.setLcrSolicitudEtapa(etapaContabilidad);
                        seguimientoContabilidad.setLcrSolicitud(lcrSolicitudSaved);
                        seguimientoContabilidad.setUpdatedAt(LocalDateTime.now());
                        seguimientoContabilidad.setUpdatedBy(usuarioSistema);
                        seguimientoContabilidad.setCreatedAt(LocalDateTime.now());
                        seguimientoContabilidad.setCreatedBy(usuarioSistema);
                        lcrSolicitudEtapaSeguimientoRepository.save(seguimientoContabilidad);

                        lcrSolicitudRepository.save(lcrSolicitud);

                    }
            );

            contenido = "Los TCHN subidos al Sistema son :";
            // a contenido añadir una lista de listaTchn

            for (TchnEndosado tchnEndosado : listaTchn) {
                contenido += "\n" + tchnEndosado.getCodigoOperacion();
            }
            System.out.println("contenido"+contenido);
            System.out.println("paso correo");
            enviarNotificacion(user, List.of("negocios","legal"), "TCHN SUBIDOS", "TCHN SUBIDOS AL SISTEMA", contenido, 2L);

            tchnEndosadoRepository.saveAll(listaTchn);

            return new ApiResponse("Archivo cargado correctamente", true);

        } catch (IOException e) {
            e.printStackTrace();  // Manejar excepciones adecuadamente
            return new ApiResponse("Error al procesar el archivo", false);
        } finally {
            // Cerrar el workbook para evitar la fuga de recursos
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();  // Manejar excepción de cierre de workbook
                }
            }
        }
    }

    public void enviarNotificacion(Usuario usuario, List<String> slugs ,String subject ,  String titulo, String contenido, Long rol) {
        List<Usuario> usuariosParaCorreo = usuarioRepository.findActivosByModulosSlugIn(slugs,rol);

        Notificaciones notificaciones = new Notificaciones();
        notificaciones.setTitulo(titulo);
        notificaciones.setDescripcion("El usuario " + usuario.getUsername() + " ha subido TCHN Endosados al sistema");
        notificaciones.setUrl("liquidacion-costos");
        notificaciones.setCreatedBy(usuario);
        notificaciones.setCreatedAt(LocalDateTime.now());
        Long idNotificacion = notificacionesRepository.save(notificaciones).getId();
        System.out.println("notificacion"+idNotificacion);
        for (Usuario user : usuariosParaCorreo) {
            NotificacionesUsuarios notificacionesUsuario = new NotificacionesUsuarios();
            notificacionesUsuario.setNotificacion(notificacionesRepository.findById(idNotificacion).orElse(null));
            notificacionesUsuario.setUsuario(user);
            notificacionesUsuario.setIsView(false);
            notificacionesUsuario.setCreatedAt(LocalDateTime.now());
            notificacionesUsuariosRepository.save(notificacionesUsuario);
            System.out.println("correo"+user.getEmail());
            if(user.isNotificacionCorreo()){
                List<String> emails = List.of(user.getEmail());

                emailService.sendEmailMovimientoDeSolicitud(subject,titulo,contenido, emails);
            }
        }
    }

    public void enviaError(List<String> slugs, String subject, String titulo, String contenido, Long rol, Long userId) {
        List<Usuario> usuariosParaCorreo = this.usuarioRepository.findActivosByModulosSlugIn(slugs, rol);
        Notificaciones notificaciones = new Notificaciones();
        notificaciones.setTitulo(titulo);
        Usuario user = usuarioServiceImpl.findById(userId);
        notificaciones.setDescripcion("El usuario " + user.getUsername() + " ha subido TCHN Endosados al sistema");
        notificaciones.setUrl("solicitudes-de-liquidacion");
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

        String[] arrayEmails = (String[])emails.toArray(new String[0]);
        this.emailService.sendFromGMail(this.mailFrom, this.mailPass, arrayEmails, subject, contenido);
    }
}