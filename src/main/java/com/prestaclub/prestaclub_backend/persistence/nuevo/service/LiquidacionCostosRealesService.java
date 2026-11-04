package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.EmailValues;
import com.prestaclub.prestaclub_backend.dto.LcrResponse;
import com.prestaclub.prestaclub_backend.dto.LcrTotalResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.OperacionesAntiguoService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRConceptosServicios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRServicios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitud;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudHistory;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosReales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Notificaciones;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.NotificacionesUsuarios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudHistoryRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosRealesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.NotificacionesUsuariosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.UsuarioModuloRolRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.UsuarioServiceImpl;
import com.prestaclub.prestaclub_backend.service.EmailService;

@Service
public class LiquidacionCostosRealesService {

    @Autowired
    private LiquidacionCostosRealesRepository liquidacionCostosRealesRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioServiceImpl usuarioServiceImpl;

    @Autowired
    private LCRServiciosService lcrServiciosService;

    @Autowired
    private LCRConceptosServiciosService lcrConceptosServiciosService;

    @Autowired
    private LCRSolicitudRepository lcrSolicitudRepository;

    @Autowired
    private LCRSolicitudHistoryRepository lcrSolicitudHistoryRepository;

    @Autowired
    private NotificacionesRepository notificacionesRepository;

    @Autowired
    private UsuarioModuloRolRepository usuarioModuloRolRepository;

    @Autowired
    private NotificacionesUsuariosRepository notificacionesUsuariosRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private OperacionesAntiguoService operacionesAntiguoService;

    @Autowired
    private EmailService emailService;

    public ApiResponse cargaMasiva(MultipartFile file, Long userId, Long moduloId) {
        Usuario user = usuarioService.findById(userId);
        // switch
        switch (moduloId.intValue()) {
            case 1: // Legal
                return cargaMasivaNegocios(file, user);
            case 3: // Contabilidad
                return cargaMasivaLegal(file, user);
            default:
                return new ApiResponse("Modulo no encontrado", false);
        }
    }

    public ApiResponse validacionExcel(Workbook workbook, Integer rowNum, List<String> nombresEsperados) {
        Sheet sheet = workbook.getSheetAt(0);
        Row rowHeader = sheet.getRow(rowNum); // lee la fila de los nombres
        if (rowHeader == null) {
            return new ApiResponse("Error al procesar el archivo: Fila de encabezado no encontrada", false);
        }

        for (int i = 0; i < nombresEsperados.size(); i++) {
            Cell cell = rowHeader.getCell(i);
            if (cell == null) {
                return new ApiResponse("Error al buscar la celda " + nombresEsperados.get(i), false);
            }
            if (!cell.toString().trim().equalsIgnoreCase(nombresEsperados.get(i))) {
                return new ApiResponse("Error al buscar la celda " + nombresEsperados.get(i), false);
            }
        }

        return new ApiResponse("Archivo validado correctamente", true);
    }

    public ApiResponse guardarExcel(Workbook workbook, Integer rowNum, Usuario user, List<String> listaslug) {
        Sheet sheet = workbook.getSheetAt(0);
        List<LiquidacionCostosReales> listaLcr = new ArrayList<>();
        for (int i = rowNum + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);

            if (row == null) {
                continue;
            }
            if (row.getCell(0) == null || row.getCell(0).toString().trim().isEmpty()) {
                continue; // Ignorar celdas vacías
            }

            String referencia = row.getCell(0).toString();
            if (row.getCell(1) != null && !row.getCell(1).toString().trim().isEmpty()
                    && row.getCell(1).getCellType() != CellType.NUMERIC) {
                return new ApiResponse(
                        "Error al capturar el kardex : " + row.getCell(1).toString() + " en la fila " + (i + 1), false);
            }
            Long kardex = (row.getCell(1) == null || row.getCell(1).toString().trim().isEmpty()) ? 0
                    : (long) row.getCell(1).getNumericCellValue();

            if (row.getCell(2).getCellType() != CellType.NUMERIC) {
                return new ApiResponse(
                        "Error al capturar el numOperacion : " + row.getCell(2).toString() + " en la fila " + (i + 1),
                        false);
            }
            Long numOperacion = (long) row.getCell(2).getNumericCellValue();
            OperacionDTO operacion = operacionesAntiguoService.getOperacionXNumeroOperacion(numOperacion.intValue());

            if (operacion == null) {
                continue;
            }

            for (int j = 5; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);

                if (cell == null || cell.toString().trim().isEmpty()) {
                    continue; // Ignorar celdas vacías
                }

                // Convertir el valor a Double limpiando el formato
                Double valor;
                try {
                    String cellValue;

                    // Verificar el tipo de celda y obtener el valor como String
                    switch (cell.getCellType()) {
                        case NUMERIC:
                            // Si es numérico, convertir directamente a String
                            cellValue = String.valueOf(cell.getNumericCellValue());
                            break;
                        case STRING:
                            // Si es texto, obtener el valor como String
                            cellValue = cell.getStringCellValue().trim();
                            break;
                        case FORMULA:
                            // Si es una fórmula, evaluar y obtener el resultado
                            cellValue = cell.getCellFormula();
                            break;
                        case BLANK:
                            continue; // Saltar celdas en blanco
                        default:
                            cellValue = cell.toString().trim();
                            break;
                    }

                    // Limpiar espacios entre el signo negativo y el número: "- 1.40" -> "-1.40"
                    cellValue = cellValue.replaceAll("\\s+", "");
                    // Remover cualquier caracter que no sea dígito, punto, coma o signo negativo
                    cellValue = cellValue.replaceAll("[^0-9.,-]", "");
                    // Reemplazar coma por punto si existe
                    cellValue = cellValue.replace(",", ".");

                    valor = Double.parseDouble(cellValue);

                    if (valor == 0) {
                        continue;
                    }
                } catch (NumberFormatException | IllegalStateException e) {
                    String columLetter = CellReference.convertNumToColString(cell.getColumnIndex());
                    return new ApiResponse(
                            "Error al capturar el monto: '" + cell.toString() + "' en la columna " + columLetter
                                    + ", en la fila " + (i + 1) + ", con el numero de operacion " + numOperacion,
                            false);
                }

                LiquidacionCostosReales lcr = new LiquidacionCostosReales();
                lcr.setReferencia(referencia);
                lcr.setKardex(kardex);
                lcr.setNumOperacion(numOperacion);
                lcr.setEsDolares(false);
                lcr.setTipoCambio(BigDecimal.valueOf(1.0));
                if (valor < 0) {
                    lcr.setLcrServicios(lcrServiciosService.findBySlugAndLcrConceptosServiciosId("devolucion", lcrServiciosService.findBySlug(listaslug.get(j - 5)).getLcrConceptosServicios().getId()));   
                } else {
                    lcr.setLcrServicios(lcrServiciosService.findBySlug(listaslug.get(j - 5)));
                }
                lcr.setMonto(BigDecimal.valueOf(valor));
                lcr.setCreatedAt(LocalDateTime.now());
                lcr.setCreatedBy(user);
                listaLcr.add(lcr);
            }
            rowNum = i;

        }
        liquidacionCostosRealesRepository.saveAll(listaLcr);
        return new ApiResponse("Archivo cargado correctamente", true);
    }

    public ApiResponse cargaMasivaLegal(MultipartFile file, Usuario user) {
        Integer rowNum = 2; // POSICION DEL ROW A LEER

        Workbook workbook = null;
        List<String> nombresEsperados = Arrays.asList(
                "Referencia", "Kardex", "TCHN", "Cliente", "DNI", "Derechos Notariales",
                "Toma de firmas con lector biométrico",
                "Certificado Registral Inmobiliario + Copia Literal", "Bloqueo Registral",
                "Renovacion de bloqueo", "Otorgamiento de poder", "Emisión TCHN", "Expedición TCHN",
                "Inscripción Hipoteca", "Levantamiento de Hipoteca", "Cancelación de TCHN",
                "Levantamiento de bloqueo por caducidad / desistimiento", "Tasa envío Callao", "Tasa de Tachas");
        List<String> listaslug = Arrays.asList("derechos-notariales", "toma-de-firmas-con-lector-biometrico",
                "certificado-registral-inmobiliario-copia-literal", "bloqueo-registral",
                "renovacion-de-bloqueo", "otorgamiento-de-poder", "emision-tchn", "expedicion-tchn",
                "inscripcion-hipoteca", "levantamiento-de-hipoteca", "cancelacion-de-tchn",
                "levantamiento-de-bloqueo-por-caducidad-desistimiento", "tasa-envio-callao", "tasa-de-tachas");

        try (InputStream is = file.getInputStream()) {
            workbook = new XSSFWorkbook(is);
            // Sheet sheet = workbook.getSheetAt(0);

            /////////////////////////// COMIENZO DE VALIDACIONES /////////////////////////

            ApiResponse validacion = validacionExcel(workbook, rowNum, nombresEsperados);
            if (!validacion.isSuccess()) {
                return validacion;
            }

            ///////////////////////// COMIENZO DE LECTURA DE DATOS /////////////////////////

            ApiResponse respuesta = guardarExcel(workbook, rowNum, user, listaslug);
            return respuesta;

        } catch (IOException e) {
            e.printStackTrace(); // Manejar excepciones adecuadamente
            return new ApiResponse("Error al procesar el archivo", false);
        } finally {
            // Cerrar el workbook para evitar la fuga de recursos
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace(); // Manejar excepción de cierre de workbook
                }
            }
        }

    }

    public ApiResponse cargaMasivaNegocios(MultipartFile file, Usuario user) {
        Integer rowNum = 2; // POSICION DEL ROW A LEER

        Workbook workbook = null;
        List<String> nombresEsperados = Arrays.asList(
                "Referencia", "Kardex", "TCHN", "Cliente", "DNI", "Costo de inspección", "Peritaje");
        List<String> listaslug = Arrays.asList("costo-de-inspeccion", "peritaje");

        try (InputStream is = file.getInputStream()) {
            workbook = new XSSFWorkbook(is);
            // Sheet sheet = workbook.getSheetAt(0);

            /////////////////////////// COMIENZO DE VALIDACIONES /////////////////////////

            ApiResponse validacion = validacionExcel(workbook, rowNum, nombresEsperados);
            if (!validacion.isSuccess()) {
                return validacion;
            }

            ///////////////////////// COMIENZO DE LECTURA DE DATOS /////////////////////////

            ApiResponse respuesta = guardarExcel(workbook, rowNum, user, listaslug);

            return respuesta;

        } catch (IOException e) {
            e.printStackTrace(); // Manejar excepciones adecuadamente
            return new ApiResponse("Error al procesar el archivo", false);
        } finally {
            // Cerrar el workbook para evitar la fuga de recursos
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace(); // Manejar excepción de cierre de workbook
                }
            }
        }

        // return new ApiResponse("Archivo cargado con exito ", true);
    }

    public LcrResponse generarlcrDTO(LiquidacionCostosReales lcr) {
        UsuarioMantenimientoDTO userMantenimientoDTO = usuarioServiceImpl
                .obtenerUsuarioMantenimientoById(lcr.getCreatedBy().getId());
        LcrResponse lcrResponse = new LcrResponse();
        lcrResponse.setId(lcr.getId());
        lcrResponse.setReferencia(lcr.getReferencia());
        lcrResponse.setKardex(lcr.getKardex());
        lcrResponse.setNumOperacion(lcr.getNumOperacion());
        lcrResponse.setLcrServicios(lcr.getLcrServicios());
        lcrResponse.setMonto(lcr.getMonto());
        lcrResponse.setEsDolares(lcr.getEsDolares());
        lcrResponse.setTipoCambio(lcr.getTipoCambio());
        lcrResponse.setCreatedAt(lcr.getCreatedAt());
        lcrResponse.setCreatedBy(userMantenimientoDTO);
        lcrResponse.setDeletedAt(lcr.getDeletedAt());
        lcrResponse.setUpdatedAt(lcr.getUpdatedAt());
        return lcrResponse;
    }

    public ListaResponse listaLcrByNumOperacion(Long numOperacion, Integer pageSize, Integer pageNumber,
            String filterText) {
        ListaResponse response = new ListaResponse();
        List<LcrResponse> data = new ArrayList<>();
        List<LiquidacionCostosReales> listaLcr = liquidacionCostosRealesRepository.findByNumOperacion(numOperacion);
        for (LiquidacionCostosReales lcr : listaLcr) {
            if (filterText != null && !filterText.isEmpty()
                    && (lcr.getReferencia() == null || !lcr.getReferencia().contains(filterText))
                    && (lcr.getKardex() == null || !lcr.getKardex().toString().contains(filterText))
                    && (lcr.getLcrServicios() == null || lcr.getLcrServicios().getDescripcion() == null
                            || !lcr.getLcrServicios().getDescripcion().toUpperCase().contains(filterText.toUpperCase()))
                    && (lcr.getLcrServicios() == null || lcr.getLcrServicios().getLcrConceptosServicios() == null
                            || lcr.getLcrServicios().getLcrConceptosServicios().getDescripcion() == null
                            || !lcr.getLcrServicios().getLcrConceptosServicios().getDescripcion().toUpperCase()
                                    .contains(filterText.toUpperCase()))
                    && (lcr.getLcrServicios() == null || lcr.getLcrServicios().getLcrConceptosServicios() == null
                            || lcr.getLcrServicios().getLcrConceptosServicios().getSlug() == null
                            || !lcr.getLcrServicios().getLcrConceptosServicios().getSlug().toUpperCase()
                                    .contains(filterText.toUpperCase()))) {
                continue; // Saltar usuarios que no coincidan con el filtro
            }
            LcrResponse lcrResponse = generarlcrDTO(lcr);
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

    public List<LcrTotalResponse> listaLcrAllByNumOperacion(Long numOperacion, Boolean esDolaresGeneral) {
        List<LcrTotalResponse> data = new ArrayList<>();
        List<LiquidacionCostosReales> listaLcr = liquidacionCostosRealesRepository.findByNumOperacion(numOperacion);
        List<LCRConceptosServicios> listaLcrConceptosServicios = lcrConceptosServiciosService.findAll();
        Map<String, BigDecimal> montoPorSlug = new HashMap<>();
        Map<String, Boolean> esDolaresPorSlug = new HashMap<>();
        long idConcepto=0;
        for (LiquidacionCostosReales lcr : listaLcr) {
            String slug = lcr.getLcrServicios().getLcrConceptosServicios().getSlug();
            if (slug.equals("Variación por Tipo de Cambio")) {
                idConcepto=lcr.getId();
            }
            Boolean esDolares = lcr.getEsDolares();
            BigDecimal tipoCambio = lcr.getTipoCambio();
            BigDecimal monto = lcr.getMonto();
            BigDecimal total = BigDecimal.ZERO;

            total = esDolares ? monto.multiply(tipoCambio) : monto;
            /*
             * if (!esDolaresGeneral) {
             * total = esDolares ? monto.multiply(tipoCambio) : monto;
             * }else{
             * total = esDolares ? monto : monto.divide(tipoCambio, 2, RoundingMode.HALF_UP)
             * ;
             * }
             */
            montoPorSlug.put(slug, montoPorSlug.getOrDefault(slug, BigDecimal.ZERO).add(total));
            if (!esDolaresPorSlug.containsKey(slug)) {
                esDolaresPorSlug.put(slug, esDolares);
            } else {
                if (esDolaresPorSlug.get(slug) == null) {
                    continue;
                }
                if (!esDolaresPorSlug.get(slug).equals(esDolares)) {
                    esDolaresPorSlug.put(slug, null);
                }

            }
        }

        for (LCRConceptosServicios lcrConceptosServicios : listaLcrConceptosServicios) {
            LcrTotalResponse lcrTotalResponse = new LcrTotalResponse();
            lcrTotalResponse.setSlug(lcrConceptosServicios.getSlug());
            lcrTotalResponse.setEsDolares(esDolaresPorSlug.getOrDefault(lcrConceptosServicios.getSlug(), null));
            Boolean status = !((montoPorSlug.getOrDefault(lcrConceptosServicios.getSlug(), BigDecimal.ZERO)
                    .compareTo(BigDecimal.ZERO) != 0)
                    && esDolaresPorSlug.getOrDefault(lcrConceptosServicios.getSlug(), null) == null);
            // lcrTotalResponse.setMonto(status ?
            // montoPorSlug.getOrDefault(lcrConceptosServicios.getSlug(), BigDecimal.ZERO) :
            // BigDecimal.ZERO);
            lcrTotalResponse.setMonto(montoPorSlug.getOrDefault(lcrConceptosServicios.getSlug(), BigDecimal.ZERO));
            lcrTotalResponse.setStatus(status);
            lcrTotalResponse.setNombre(lcrConceptosServicios.getDescripcion());
            lcrTotalResponse.setId(idConcepto);
            data.add(lcrTotalResponse);
        }
        return data;
    }

    public ApiResponse addLcr(Long numOperacion, Long idLCRConceptoServicio, Long idLCRServicio, String referencia,
            Long kardex, BigDecimal valor, Boolean esDolares, BigDecimal tipoCambio, Long userId) {
        try {
            LiquidacionCostosReales lcr = new LiquidacionCostosReales();
            lcr.setReferencia(referencia);
            lcr.setKardex(kardex);
            lcr.setNumOperacion(numOperacion);
            lcr.setEsDolares(esDolares);
            lcr.setTipoCambio(tipoCambio);
            lcr.setLcrServicios(lcrServiciosService.findById(idLCRServicio));
            lcr.setMonto(valor);
            lcr.setCreatedAt(LocalDateTime.now());
            Usuario usuario = usuarioServiceImpl.findById(userId);
            lcr.setCreatedBy(usuario);
            liquidacionCostosRealesRepository.save(lcr);
            return new ApiResponse("Lcr agregado correctamente", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Error al agregar lcr", false);
        }
    }

    public ApiResponse updateLcr(Long id, String referencia, Long kardex, BigDecimal valor, Boolean esDolares,
            BigDecimal tipoCambio, Long userId) {
        try {
            LiquidacionCostosReales lcr = liquidacionCostosRealesRepository.findById(id).orElse(null);
            if (lcr == null) {
                return new ApiResponse("Lcr no encontrado", false);
            }
            lcr.setReferencia(referencia);
            lcr.setKardex(kardex);
            lcr.setEsDolares(esDolares);
            lcr.setMonto(valor);
            lcr.setUpdatedAt(LocalDateTime.now());
            lcr.setTipoCambio(tipoCambio);
            Usuario usuario = usuarioServiceImpl.findById(userId);
            lcr.setUpdatedBy(usuario);
            liquidacionCostosRealesRepository.save(lcr);
            return new ApiResponse("Lcr actualizado correctamente", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Error al actualizar lcr", false);
        }
    }

    public LcrResponse buscarLcr(Long id) {
        try {
            LiquidacionCostosReales lcr = liquidacionCostosRealesRepository.findById(id).orElse(null);
            LcrResponse lcrResponse = generarlcrDTO(lcr);
            return lcrResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ApiResponse deleteLcr(Long id, Long userId) {
        try {
            LiquidacionCostosReales lcr = liquidacionCostosRealesRepository.findById(id).orElse(null);
            if (lcr == null) {
                return new ApiResponse("Lcr no encontrado", false);
            }
            lcr.setDeletedAt(LocalDateTime.now());
            Usuario usuario = usuarioServiceImpl.findById(userId);
            lcr.setDeletedBy(usuario);
            liquidacionCostosRealesRepository.save(lcr);
            return new ApiResponse("Lcr eliminado correctamente", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Error al eliminar lcr", false);
        }
    }

    public ApiResponse solicitarAprobacion(BigDecimal montoSimulado, BigDecimal montoReal, Long numOperacion,
            Long userId, Boolean esDolares, String cuentaBancaria, String cci) {
        try {
            Usuario usuario = usuarioServiceImpl.findById(userId);
            LCRSolicitud lcrSolicitud = new LCRSolicitud();
            lcrSolicitud.setCreatedAt(LocalDateTime.now());
            lcrSolicitud.setCreatedBy(usuario);
            lcrSolicitud.setUpdatedAt(LocalDateTime.now());
            lcrSolicitud.setUpdatedBy(usuario);
            // lcrSolicitud.setStatus(1);
            lcrSolicitud.setMontoReal(montoReal);
            lcrSolicitud.setMontoSimulado(montoSimulado);
            lcrSolicitud.setNumOperacion(numOperacion);
            // lcrSolicitud.setEsDolares(esDolares);
            // lcrSolicitud.setCuentaBancaria(cuentaBancaria);
            // lcrSolicitud.setCci(cci);
            Long id = lcrSolicitudRepository.save(lcrSolicitud).getId();

            LCRSolicitudHistory lcrSolicitudHistory = new LCRSolicitudHistory();
            // lcrSolicitudHistory.setMontoRealPrevio(BigDecimal.ZERO);
            // lcrSolicitudHistory.setMontoRealActual(montoReal);
            lcrSolicitudHistory.setLctSolicitud(lcrSolicitudRepository.findById(id).orElse(null));
            // lcrSolicitudHistory.setStatus(1);
            // lcrSolicitudHistory.setSolicitante(usuario);
            lcrSolicitudHistory.setCreatedBy(usuario);
            lcrSolicitudHistory.setCreatedDate(LocalDateTime.now());
            // lcrSolicitudHistory.setEsDolares(esDolares);
            lcrSolicitudHistoryRepository.save(lcrSolicitudHistory);

            String descripcion = "Se ha generado una nueva solicitud de liquidación para ser aprobada.";

            Notificaciones notificaciones = new Notificaciones();
            notificaciones.setTitulo("Solicitud de Aprobación - " + numOperacion);
            notificaciones.setDescripcion(descripcion);
            notificaciones.setUrl("lcr-solicitud");
            notificaciones.setCreatedBy(usuario);
            notificaciones.setCreatedAt(LocalDateTime.now());

            Long idNotificacion = notificacionesRepository.save(notificaciones).getId();
            // ver los usuarios que estan en contabilidad y enviarle la notificacion
            List<Usuario> usuariosContabilidad = usuarioModuloRolRepository.findByModuloId(5L);

            for (Usuario usuarioContabilidad : usuariosContabilidad) {
                NotificacionesUsuarios notificacionesUsuario = new NotificacionesUsuarios();
                notificacionesUsuario.setNotificacion(notificacionesRepository.findById(idNotificacion).orElse(null));
                notificacionesUsuario.setUsuario(usuarioContabilidad);
                notificacionesUsuario.setIsView(false);
                notificacionesUsuario.setCreatedAt(LocalDateTime.now());
                notificacionesUsuariosRepository.save(notificacionesUsuario);

                if (usuarioContabilidad.isNotificacionCorreo()) {
                    EmailValues emailValues = new EmailValues();
                    emailValues.setEmail(usuarioContabilidad.getEmail());
                    emailValues.setSubject("NUEVA NOTIFICACION - PRESTACLUB");
                    emailValues.setUsername(usuarioContabilidad.getUsername());
                    emailService.sendEmailNewNotificacion(emailValues, descripcion);
                }

                Map<String, Object> payload = new HashMap<>();
                payload.put("userId", usuarioContabilidad.getId());
                messagingTemplate.convertAndSend("/topic/global/notificaciones", payload);
            }

            return new ApiResponse("Solicitud generada correctamente", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Error al generar solicitud", false);
        }
    }

    public ApiResponse reenviarSolicitud(Long id, BigDecimal montoReal, Long userId) {
        try {
            BigDecimal montoRealPrevio = BigDecimal.ZERO;
            Usuario usuario = usuarioServiceImpl.findById(userId);
            LCRSolicitud lcrSolicitud = lcrSolicitudRepository.findById(id).orElse(null);
            if (lcrSolicitud == null) {
                return new ApiResponse("Solicitud no encontrada", false);
            }

            lcrSolicitud.setUpdatedAt(LocalDateTime.now());
            lcrSolicitud.setUpdatedBy(usuario);
            // lcrSolicitud.setStatus(1);
            lcrSolicitud.setMontoReal(montoReal);
            lcrSolicitudRepository.save(lcrSolicitud);

            LCRSolicitudHistory ultimSolicitudHistory = lcrSolicitudHistoryRepository
                    .findTopByUpdatedByIsNull(lcrSolicitud.getId());
            if (ultimSolicitudHistory != null) {
                // montoRealPrevio = ultimSolicitudHistory.getMontoRealActual();
                ultimSolicitudHistory.setUpdatedBy(usuario);
                ultimSolicitudHistory.setUpdatedDate(LocalDateTime.now());
                lcrSolicitudHistoryRepository.save(ultimSolicitudHistory);
            }

            LCRSolicitudHistory lcrSolicitudHistory = new LCRSolicitudHistory();

            // lcrSolicitudHistory.setMontoRealPrevio(montoRealPrevio);
            // lcrSolicitudHistory.setMontoRealActual(montoReal);
            lcrSolicitudHistory.setLctSolicitud(lcrSolicitud);
            // lcrSolicitudHistory.setStatus(1);
            // lcrSolicitudHistory.setSolicitante(usuario);
            lcrSolicitudHistory.setCreatedBy(usuario);
            lcrSolicitudHistory.setCreatedDate(LocalDateTime.now());
            // lcrSolicitudHistory.setEsDolares(lcrSolicitud.getEsDolares());
            lcrSolicitudHistoryRepository.save(lcrSolicitudHistory);

            String descripcion = "Se ha reenviado la solicitud de liquidación con el numero de operación "
                    + lcrSolicitud.getNumOperacion() + " para ser aprobada.";

            Notificaciones notificaciones = new Notificaciones();
            notificaciones.setTitulo("Reenvio de Solicitud- " + lcrSolicitud.getNumOperacion());
            notificaciones.setDescripcion(descripcion);
            notificaciones.setUrl("lcr-solicitud");
            notificaciones.setCreatedBy(usuario);
            notificaciones.setCreatedAt(LocalDateTime.now());

            Long idNotificacion = notificacionesRepository.save(notificaciones).getId();
            // ver los usuarios que estan en contabilidad y enviarle la notificacion
            List<Usuario> usuariosContabilidad = usuarioModuloRolRepository.findByModuloId(5L); // id modulo de
                                                                                                // contabilidad

            for (Usuario usuarioContabilidad : usuariosContabilidad) {
                NotificacionesUsuarios notificacionesUsuario = new NotificacionesUsuarios();
                notificacionesUsuario.setNotificacion(notificacionesRepository.findById(idNotificacion).orElse(null));
                notificacionesUsuario.setUsuario(usuarioContabilidad);
                notificacionesUsuario.setIsView(false);
                notificacionesUsuario.setCreatedAt(LocalDateTime.now());
                notificacionesUsuariosRepository.save(notificacionesUsuario);

                if (usuarioContabilidad.isNotificacionCorreo()) {
                    EmailValues emailValues = new EmailValues();
                    emailValues.setEmail(usuarioContabilidad.getEmail());
                    emailValues.setSubject("NUEVA NOTIFICACION - PRESTACLUB");
                    emailValues.setUsername(usuarioContabilidad.getUsername());
                    emailService.sendEmailNewNotificacion(emailValues, descripcion);
                }

                Map<String, Object> payload = new HashMap<>();
                payload.put("userId", usuarioContabilidad.getId());
                messagingTemplate.convertAndSend("/topic/global/notificaciones", payload);
            }

            return new ApiResponse("Solicitud reenviada correctamente", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Error al reenviar solicitud", false);
        }
    }
}
