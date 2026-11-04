package com.prestaclub.prestaclub_backend.persistence.nuevo.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.itextpdf.text.pdf.*;
import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.LiquidacionCostosTransaccionResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.dto.UsuarioResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.PropietariosDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.OperacionesAntiguoService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LCRMontoEstadosDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapaSeguimiento;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTCronograma;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTFiles;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosReales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.UsuarioModuloRol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudEtapaSeguimientoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCTCronogramaRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCTFilesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosRealesRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosTransaccionRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.ExcelServiceImpl;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.PdfServiceImpl;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.S3ServiceImpl;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.UsuarioServiceImpl;

@Service
public class LiquidacionCostosTransaccionService {
    @Autowired
    OperacionesAntiguoService operacionesAntiguoService;
    @Autowired
    LiquidacionCostosTransaccionRepository liquidacionCostosTransaccionRepository;
    @Autowired
    LCTCronogramaRepository lctCronogramaRepository;
    @Autowired
    UsuarioServiceImpl usuarioServiceImpl;
    @Autowired
    PdfServiceImpl pdfServiceImpl;
    @Autowired
    S3ServiceImpl s3Service;
    @Autowired
    LCTFilesRepository lctFilesRepository;
    @Autowired
    ExcelServiceImpl excelService;
    // @Autowired
    // LCRSolicitudService lcrSolicitudService;
    @Autowired
    LiquidacionCostosRealesRepository liquidacionCostosRealesRepository;
    @Autowired
    LCRServiciosService lcrServiciosService;
    @Autowired
    LCRSolicitudEtapaSeguimientoRepository lcrSolicitudEtapaSeguimientoRepository;

    private List<OperacionDTO> obtenerOperacionesXNumeroOperacion(Integer numeroOperacion) {
        return operacionesAntiguoService.obtenerOperacionesXNumeroOperacion(numeroOperacion);
    }

    public List<LiquidacionCostosTransaccionResponse> obtenerOperacionesXFecha(String fechaini, String fechafin,
            String estado) {
        List<OperacionDTO> operaciones = operacionesAntiguoService.obtenerOperacionesXFecha(fechaini, fechafin, estado);
        List<LiquidacionCostosTransaccionResponse> responses = new ArrayList<>();
        for (OperacionDTO operacion : operaciones) {
            LiquidacionCostosTransaccionResponse response = new LiquidacionCostosTransaccionResponse();
            response.setOperacion(operacion);
            responses.add(response);
        }
        return responses;
    }

    public LiquidacionCostosTransaccionResponse liquidacionCostosTransaccionesUltimoPrevio(Integer numOperacion,
            Integer liquidacionCostosTransaccionId) {
        System.out.println("paso por aca liquidacionCostosTransaccionesUltimoPrevio");
        LiquidacionCostosTransaccionResponse liquidacionCostosTransaccionesUltimoPrevio = new LiquidacionCostosTransaccionResponse();
        List<OperacionDTO> operacionesXNumeroOperacion = obtenerOperacionesXNumeroOperacion(numOperacion);
        if (operacionesXNumeroOperacion.isEmpty()) {
            return null;
        }
        OperacionDTO operacion = operacionesXNumeroOperacion.get(0);
        liquidacionCostosTransaccionesUltimoPrevio.setOperacion(operacion);
        if (liquidacionCostosTransaccionId == null) {
            LiquidacionCostosTransaccion l1 = liquidacionCostosTransaccionRepository
                    .findLastByNumOperacion(operacion.getNumero_operacion());
            if (l1 != null) {
                UsuarioMantenimientoDTO userMantenimientoDTO = usuarioServiceImpl
                        .obtenerUsuarioMantenimientoById(l1.getCreatedBy());
                l1.setCreatedByName(userMantenimientoDTO);
                UsuarioMantenimientoDTO uDTO = usuarioServiceImpl.obtenerUsuarioMantenimientoById(l1.getAsesorId());
                l1.setAsesorUser(uDTO);
            }
            liquidacionCostosTransaccionesUltimoPrevio.setLiquidacionCostosTransaccion(l1);
        } else {
            System.out.println("paso por aca liquidacionCostosTransaccionId" + liquidacionCostosTransaccionId);
            LiquidacionCostosTransaccion l2 = liquidacionCostosTransaccionRepository
                    .findLastById(liquidacionCostosTransaccionId);
            if (l2 != null) {
                UsuarioMantenimientoDTO userMantenimientoDTO = usuarioServiceImpl
                        .obtenerUsuarioMantenimientoById(l2.getCreatedBy());
                l2.setCreatedByName(userMantenimientoDTO);
                UsuarioMantenimientoDTO uDTO = usuarioServiceImpl.obtenerUsuarioMantenimientoById(l2.getAsesorId());
                l2.setAsesorUser(uDTO);
            }
            liquidacionCostosTransaccionesUltimoPrevio.setLiquidacionCostosTransaccion(l2);
        }


        liquidacionCostosTransaccionesUltimoPrevio
                .setPropietarios(operacionesAntiguoService.getPropietariosXNumeroOperacion(numOperacion));
        // liquidacionCostosTransaccionesUltimoPrevio.setStatus(lcrSolicitudRepository.getStatusByNumOperacion(numOperacion.longValue()));
        LCRSolicitudEtapaSeguimiento lcrSolicitudEtapaSeguimiento = lcrSolicitudEtapaSeguimientoRepository
                .findByLcrSolicitudNumOperacionAndActual(numOperacion.longValue());
        if (lcrSolicitudEtapaSeguimiento != null) {
            liquidacionCostosTransaccionesUltimoPrevio.setLcrSolicitudEtapaSeguimiento(lcrSolicitudEtapaSeguimiento);
        }
        return liquidacionCostosTransaccionesUltimoPrevio;
    }

    public ListaResponse liquidacionCostosTransaccionesLista(Integer numOperacion, Integer pageSize,
            Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<LiquidacionCostosTransaccion> liquidacionCostosTransaccionesPage = liquidacionCostosTransaccionRepository
                .getListaLiquidacionCostosTransaccion(numOperacion, pageable);
        if (liquidacionCostosTransaccionesPage.isEmpty()) {
            return null;
        }
        boolean esFinalOperacion = liquidacionCostosTransaccionRepository.existsFinal(numOperacion.longValue());
        liquidacionCostosTransaccionesPage.forEach(liq -> {
            // DATOS DEL USUARIO DE CREACION
            UsuarioMantenimientoDTO userMantenimientoDTO = usuarioServiceImpl
                    .obtenerUsuarioMantenimientoById(liq.getCreatedBy());
            liq.setCreatedByName(userMantenimientoDTO);
            UsuarioMantenimientoDTO asesorUser = usuarioServiceImpl.obtenerUsuarioMantenimientoById(liq.getAsesorId());
            liq.setAsesorUser(asesorUser);
            // CARGAR URL TEMPORAL DE LOS ARCHIVOS
            List<LCTFiles> files = lctFilesRepository.findByLiquidacionCostosTransaccionId(liq.getId());
            if (files != null) {
                files.forEach(file -> {
                    String url = s3Service.getFileUrl(file.getUrl());
                    file.setUrlTemporal(url);
                    file.setLiquidacionCostosTransaccionId(null);
                });
                liq.setFiles(files);
            }
            // MOSTRAR SI EXISTE FINAL EN EL NUMERO DE OPERACION
            liq.setEsFinalOperacion(esFinalOperacion);

        });
        ListaResponse response = new ListaResponse();
        response.setData(liquidacionCostosTransaccionesPage.toList());
        response.setTotal((int) liquidacionCostosTransaccionesPage.getTotalElements());
        response.setPageNumber(pageNumber); // Página actual
        response.setPageSize(pageSize);
        response.setSuccess(true);
        response.setMessage("ok");
        return response;
    }

    public ApiResponse saveLiquidacionCostosTransaccion(Long numOperacion,
            LiquidacionCostosTransaccion liquidacionCostosTransaccion, List<LCTCronograma> listaLctCronograma) {
        try {
            // GUARDAR LIQUIDACION Y RETORNAR EL ID QUE SE GUARDO
            liquidacionCostosTransaccion.setEsFinal(false);
            liquidacionCostosTransaccion.setCreatedDate(LocalDateTime.now());
            Long id = liquidacionCostosTransaccionRepository.save(liquidacionCostosTransaccion).getId();
            LiquidacionCostosTransaccion lct = new LiquidacionCostosTransaccion();
            lct.setId(id);
            lct.setCreatedDate(LocalDateTime.now());
            // guardar id en todos los cronogramas
            for (LCTCronograma lctCronograma : listaLctCronograma) {
                lctCronograma.setLiquidacionCostosTransaccionId(lct);
            }
            lctCronogramaRepository.saveAll(listaLctCronograma);

            byte[] pdfLiquidacionCostosTransaccion = pdfServiceImpl
                    .generatePdfLiquidacionCostosTransaccion(liquidacionCostosTransaccion);
            byte[] pdfLctCronogramaPagos = pdfServiceImpl.generatePdfLctCronogramaPagos(liquidacionCostosTransaccion,
                    listaLctCronograma);
            if (pdfLiquidacionCostosTransaccion != null && pdfLctCronogramaPagos != null) {
                // Combinar los dos PDFs
                byte[] mergedPdf = mergePdfFiles(pdfLiquidacionCostosTransaccion, pdfLctCronogramaPagos);

                // Subir el archivo combinado a S3
                String fileName = "lct_simulacion_" + liquidacionCostosTransaccion.getNumOperacion() + "_"
                        + Instant.now().toEpochMilli() + ".pdf";
                String url = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-"
                        + LocalDate.now().getDayOfMonth() + "/lct/" + fileName;

                s3Service.uploadFile(mergedPdf, url);

                // Guardar detalles del archivo en la base de datos
                LCTFiles lctFiles = new LCTFiles();
                lctFiles.setLiquidacionCostosTransaccionId(liquidacionCostosTransaccion);
                lctFiles.setFileName(fileName);
                lctFiles.setExtension("pdf");
                lctFiles.setUrl(url);
                lctFiles.setTag("simulador");
                s3Service.uploadFile(mergedPdf, url);
                lctFilesRepository.save(lctFiles);
            }
            return new ApiResponse("ok", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }

    public ApiResponse guardarBorrador(LiquidacionCostosTransaccion liquidacionCostosTransaccion,
            List<LCTCronograma> listaLctCronograma, Long userId) {
        try {
            liquidacionCostosTransaccion.setUpdatedBy(userId);
            liquidacionCostosTransaccion.setUpdatedDate(LocalDateTime.now());
            liquidacionCostosTransaccionRepository.save(liquidacionCostosTransaccion);
            // Guardar Cronograma
            // eliminar registros de cronograma si existen
            List<LCTCronograma> listLctCronograma = lctCronogramaRepository
                    .findByLiquidacionCostosTransaccionId(liquidacionCostosTransaccion.getId());
            if (listLctCronograma != null) {
                lctCronogramaRepository.deleteAll(listLctCronograma);
            }

            for (LCTCronograma lctCronograma : listaLctCronograma) {
                lctCronograma.setLiquidacionCostosTransaccionId(liquidacionCostosTransaccion);
            }
            lctCronogramaRepository.saveAll(listaLctCronograma);

            byte[] pdfLiquidacionCostosTransaccion = pdfServiceImpl
                    .generatePdfLiquidacionCostosTransaccion(liquidacionCostosTransaccion);
            byte[] pdfLctCronogramaPagos = pdfServiceImpl.generatePdfLctCronogramaPagos(liquidacionCostosTransaccion,
                    listaLctCronograma);
            if (pdfLiquidacionCostosTransaccion != null && pdfLctCronogramaPagos != null) {
                // Combinar los dos PDFs
                byte[] mergedPdf = mergePdfFiles(pdfLiquidacionCostosTransaccion, pdfLctCronogramaPagos);

                LCTFiles lctFiles = lctFilesRepository
                        .findByLiquidacionCostosTransaccionIdAndTag(liquidacionCostosTransaccion.getId(), "simulador");
                if (lctFiles == null) {
                    lctFiles = new LCTFiles();
                }

                // Subir el archivo combinado a S3
                String fileName = "lct_simulacion_" + liquidacionCostosTransaccion.getNumOperacion() + "_"
                        + Instant.now().toEpochMilli() + ".pdf";
                String url = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-"
                        + LocalDate.now().getDayOfMonth() + "/lct/" + fileName;

                s3Service.uploadFile(mergedPdf, url);

                // Guardar detalles del archivo en la base de datos
                // LCTFiles lctFiles = new LCTFiles();
                lctFiles.setLiquidacionCostosTransaccionId(liquidacionCostosTransaccion);
                lctFiles.setFileName(fileName);
                lctFiles.setExtension("pdf");
                lctFiles.setUrl(url);
                lctFiles.setTag("simulador");
                s3Service.uploadFile(mergedPdf, url);
                lctFilesRepository.save(lctFiles);
            }

            return new ApiResponse("ok", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }

    private byte[] mergePdfFiles(byte[] pdf1, byte[] pdf2) throws IOException {
        // Crear documentos PDF para combinar
        PDDocument doc1 = PDDocument.load(new ByteArrayInputStream(pdf1));
        PDDocument doc2 = PDDocument.load(new ByteArrayInputStream(pdf2));

        // Crear un nuevo documento PDF combinado
        PDDocument mergedDocument = new PDDocument();

        // Copiar las páginas de los dos documentos al documento combinado
        for (int i = 0; i < doc1.getNumberOfPages(); i++) {
            PDPage page = doc1.getPage(i);
            mergedDocument.addPage(page);
        }

        for (int i = 0; i < doc2.getNumberOfPages(); i++) {
            PDPage page = doc2.getPage(i);
            mergedDocument.addPage(page);
        }

        // Agregar numeración de páginas
        addPageNumbers(mergedDocument);

        // Guardar el documento combinado en un ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mergedDocument.save(outputStream);
        mergedDocument.close();
        doc1.close();
        doc2.close();

        return outputStream.toByteArray();
    }

    private void addPageNumbers(PDDocument document) throws IOException {
        int totalPages = document.getNumberOfPages();

        for (int i = 0; i < totalPages; i++) {
            PDPage page = document.getPage(i);
            PDPageContentStream contentStream = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true, true);

            // Agregar numeración de página
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.newLineAtOffset(530, 10); // Ajusta la posición de la numeración según lo necesites

            // Agregar el texto "Página X de Y"
            String text = "Página " + (i + 1) + " de " + totalPages;
            contentStream.showText(text);

            contentStream.endText();
            contentStream.close();
        }
    }

    public ApiResponse updateFinalLiquidacion(Integer numOperacion, Integer liquidacionCostosTransaccionId) {
        try {
            boolean esFinalOperacion = liquidacionCostosTransaccionRepository.existsFinal(numOperacion.longValue());
            if (esFinalOperacion) {
                return new ApiResponse("Este Numero de Operacion ya tiene una liquidación en Final", false);
            }
            LiquidacionCostosTransaccion liquidacionCostosTransaccion = liquidacionCostosTransaccionRepository
                    .findLastById(liquidacionCostosTransaccionId);
            if (liquidacionCostosTransaccion == null) {
                return new ApiResponse("Liquidacion no encontrada", false);
            }
            liquidacionCostosTransaccion.setEsFinal(true);
            liquidacionCostosTransaccionRepository.save(liquidacionCostosTransaccion);
            return new ApiResponse("ok", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }

    }

    public ApiResponse updateRevertirDesembolsoLiquidacion(Integer numOperacion,
            Integer liquidacionCostosTransaccionId) {
        try {
            boolean esDesembolsoOperacion = liquidacionCostosTransaccionRepository
                    .existsDesembolso(numOperacion.longValue());
            if (!esDesembolsoOperacion) {
                return new ApiResponse("Este Numero de Operacion no tiene una liquidación en Desembolso", false);
            }
            LiquidacionCostosTransaccion liquidacionCostosTransaccion = liquidacionCostosTransaccionRepository
                    .findLastById(liquidacionCostosTransaccionId);
            if (liquidacionCostosTransaccion == null) {
                return new ApiResponse("Liquidacion no encontrada", false);
            }
            LCTFiles lctFiles = lctFilesRepository.findByLiquidacionCostosTransaccionIdAndTag(
                    liquidacionCostosTransaccion.getId(), "resumen-cronograma-pagos");
            lctFilesRepository.delete(lctFiles);
            liquidacionCostosTransaccion.setEsFinal(true);
            liquidacionCostosTransaccion.setEsDesembolso(false);
            liquidacionCostosTransaccionRepository.save(liquidacionCostosTransaccion);
            return new ApiResponse("ok", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }

    public ApiResponse updateDesembolsoLiquidacion(Integer numOperacion, BigDecimal tasaMoratoria,
            Integer liquidacionCostosTransaccionId, Long userId) {
        try {
            boolean newTasaMoratoria = false;
            boolean esDesembolsoOperacion = liquidacionCostosTransaccionRepository
                    .existsDesembolso(numOperacion.longValue());
            if (esDesembolsoOperacion) {
                return new ApiResponse("Este Numero de Operacion ya tiene una liquidación en Desembolso", false);
            }
            LiquidacionCostosTransaccion liquidacionCostosTransaccion = liquidacionCostosTransaccionRepository
                    .findLastById(liquidacionCostosTransaccionId);
            if (liquidacionCostosTransaccion == null) {
                return new ApiResponse("Liquidacion no encontrada", false);
            }

            newTasaMoratoria = liquidacionCostosTransaccion.getTasaInteresMoratoria().compareTo(tasaMoratoria) != 0;
            liquidacionCostosTransaccion.setTasaInteresMoratoria(tasaMoratoria);
            liquidacionCostosTransaccion.setEsFinal(false);
            liquidacionCostosTransaccion.setEsDesembolso(true);
            liquidacionCostosTransaccion.setUpdatedDate(LocalDateTime.now());
            liquidacionCostosTransaccion.setUpdatedBy(userId);
            liquidacionCostosTransaccionRepository.save(liquidacionCostosTransaccion);

            if (newTasaMoratoria) {
                List<LCTCronograma> lctCronograma = lctCronogramaRepository
                        .findByLiquidacionCostosTransaccionId(liquidacionCostosTransaccion.getId());
                byte[] pdfLiquidacionCostosTransaccion = pdfServiceImpl
                        .generatePdfLiquidacionCostosTransaccion(liquidacionCostosTransaccion);
                byte[] pdfLctCronogramaPagos = pdfServiceImpl
                        .generatePdfLctCronogramaPagos(liquidacionCostosTransaccion, lctCronograma);
                if (pdfLctCronogramaPagos != null) {
                    byte[] mergedPdf = mergePdfFiles(pdfLiquidacionCostosTransaccion, pdfLctCronogramaPagos);

                    LCTFiles lctFiles = lctFilesRepository.findByLiquidacionCostosTransaccionIdAndTag(
                            liquidacionCostosTransaccion.getId(), "simulador");
                    if (lctFiles == null) {
                        lctFiles = new LCTFiles();
                    }
                    String fileName = "lct_simulacion_" + liquidacionCostosTransaccion.getNumOperacion() + "_"
                            + Instant.now().toEpochMilli() + ".pdf";
                    String url = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-"
                            + LocalDate.now().getDayOfMonth() + "/lct/" + fileName;

                    lctFiles.setLiquidacionCostosTransaccionId(liquidacionCostosTransaccion);
                    lctFiles.setFileName(fileName);
                    lctFiles.setExtension("pdf");
                    lctFiles.setUrl(url);
                    lctFiles.setTag("simulador");

                    s3Service.uploadFile(mergedPdf, url);
                    lctFilesRepository.save(lctFiles);
                }
            }
            Usuario usuario = usuarioServiceImpl.findById(0L);
            List<LiquidacionCostosReales> listaLcr = liquidacionCostosRealesRepository
                    .findByNumOperacion(numOperacion.longValue());
            if (listaLcr.size() > 0) {
                listaLcr.forEach(lcr -> {
                    lcr.setDeletedBy(usuario);
                    lcr.setDeletedAt(LocalDateTime.now());
                    liquidacionCostosRealesRepository.save(lcr);
                });
            }

            LiquidacionCostosReales lcr = new LiquidacionCostosReales();
            lcr.setReferencia(null);
            lcr.setKardex(null);
            lcr.setNumOperacion(numOperacion.longValue());
            lcr.setEsDolares(liquidacionCostosTransaccion.isEsDolares());
            lcr.setTipoCambio(liquidacionCostosTransaccion.getTipoCambio());
            lcr.setLcrServicios(lcrServiciosService.findById(4L));
            lcr.setMonto(liquidacionCostosTransaccion.getAsesoriaPrestaclub());
            lcr.setCreatedAt(LocalDateTime.now());
            lcr.setCreatedBy(usuario);
            liquidacionCostosRealesRepository.save(lcr);

            byte[] excelLctCronograma = excelService.generateExcelLctCronogramaPagos(numOperacion,
                    liquidacionCostosTransaccionId.longValue());
            if (excelLctCronograma != null) {
                LCTFiles lctFiles = new LCTFiles();
                String fileName = "resumen_cronograma_pagos_" + liquidacionCostosTransaccion.getNumOperacion() + "_"
                        + Instant.now().toEpochMilli() + ".xlsx";
                String url = LocalDate.now().getYear() + "-" + LocalDate.now().getMonthValue() + "-"
                        + LocalDate.now().getDayOfMonth() + "/" + "lct" + "/" + liquidacionCostosTransaccionId + "/"
                        + fileName;
                LiquidacionCostosTransaccion lct = liquidacionCostosTransaccionRepository
                        .findLastById(liquidacionCostosTransaccionId);
                lctFiles.setLiquidacionCostosTransaccionId(lct);
                lctFiles.setFileName(fileName);
                lctFiles.setExtension("xlsx");
                lctFiles.setUrl(url);
                lctFiles.setTag("resumen-cronograma-pagos");

                s3Service.uploadFile(excelLctCronograma, url);
                lctFilesRepository.save(lctFiles);
            }

            return new ApiResponse("ok", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }

    public ApiResponse reconsiderarLiquidacion(Integer numOperacion) {
        try {
            boolean esFinalOperacion = liquidacionCostosTransaccionRepository.existsFinal(numOperacion.longValue());
            if (!esFinalOperacion) {
                return new ApiResponse("Este Numero de Operacion no tiene una liquidación Aprobada", false);
            }
            LiquidacionCostosTransaccion liquidacionCostosTransaccion = liquidacionCostosTransaccionRepository
                    .findEsFinalByNumOperacion(numOperacion.longValue());
            liquidacionCostosTransaccion.setEsFinal(false);
            liquidacionCostosTransaccionRepository.save(liquidacionCostosTransaccion);
            return new ApiResponse("ok", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }

    public ListaResponse listaFinalLiquidaciones(Integer pageSize, Integer pageNumber, String filterText) {
        ListaResponse response = new ListaResponse();
        List<LiquidacionCostosTransaccionResponse> data = new ArrayList<>();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<LiquidacionCostosTransaccion> liquidaciones = liquidacionCostosTransaccionRepository
                .listaFinalLiquidacionesByCust(filterText, pageable);
        if (liquidaciones.isEmpty()) {
            response.setData(Arrays.asList());
            response.setTotal(0);
            response.setPageNumber(pageNumber);
            response.setPageSize(pageSize);
            response.setSuccess(false);
            response.setMessage("No se encontraron registros");
            return response;
        }

        liquidaciones.forEach(liq -> {
            OperacionDTO operacion = operacionesAntiguoService
                    .getOperacionXNumeroOperacion(liq.getNumOperacion().intValue());

            if (operacion != null) {
                UsuarioMantenimientoDTO userMantenimientoDTO = usuarioServiceImpl
                        .obtenerUsuarioMantenimientoById(liq.getCreatedBy());
                UsuarioMantenimientoDTO asesorUser = usuarioServiceImpl
                        .obtenerUsuarioMantenimientoById(liq.getAsesorId());

                liq.setCreatedByName(userMantenimientoDTO);
                liq.setAsesorUser(asesorUser);

                List<LCTFiles> files = lctFilesRepository.findByLiquidacionCostosTransaccionId(liq.getId());
                if (files != null) {
                    files.forEach(file -> {
                        file.setUrlTemporal(s3Service.getFileUrl(file.getUrl()));
                        file.setLiquidacionCostosTransaccionId(null);
                    });
                    liq.setFiles(files);
                }

                LiquidacionCostosTransaccionResponse lctResponse = new LiquidacionCostosTransaccionResponse();
                lctResponse.setOperacion(operacion);
                lctResponse.setLiquidacionCostosTransaccion(liq);

                LCRSolicitudEtapaSeguimiento lcrSolicitudEtapaSeguimiento = lcrSolicitudEtapaSeguimientoRepository
                        .findByLcrSolicitudNumOperacionAndActual(operacion.getNumero_operacion());
                if (lcrSolicitudEtapaSeguimiento != null) {
                    lctResponse.setLcrSolicitudEtapaSeguimiento(lcrSolicitudEtapaSeguimiento);
                }

                data.add(lctResponse);
            }

        });
        response.setSuccess(true);
        response.setData(data);
        response.setMessage("ok");
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotal((int) liquidaciones.getTotalElements());

        return response;

    }

    public ListaResponse listaDesembolsoLiquidaciones(Integer pageSize, Integer pageNumber, String filterText,
            String slug, Boolean filterPending, Boolean filterApproved, Boolean filterObservado) {
        ListaResponse response = new ListaResponse();
        List<LiquidacionCostosTransaccionResponse> data = new ArrayList<>();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        List<Long> statusList = new ArrayList<>();
        if (filterPending) {
            statusList.add(0L);
        }

        if (filterApproved) {
            statusList.add(1L);
        }

        if (filterObservado) {
            statusList.add(2L);
        }

        if (statusList.isEmpty()) {
            statusList = null; // Así activa la parte de "IS NULL"
        }

        Page<LiquidacionCostosTransaccion> liquidaciones = liquidacionCostosTransaccionRepository
                .listaDesembolsoLiquidacionesByCust(filterText, slug, statusList, pageable);
        if (liquidaciones.isEmpty()) {
            response.setData(Arrays.asList());
            response.setTotal(0);
            response.setPageNumber(pageNumber);
            response.setPageSize(pageSize);
            response.setSuccess(false);
            response.setMessage("No se encontraron registros");
            return response;
        }
        System.out.println("paso por aca1");
        liquidaciones.forEach(liq -> {
            System.out.println("paso por aca2");
            OperacionDTO operacion = null;
            try {
                if (liq.getNumOperacion() == null) {
                    System.out.println("Operacion nula para numOperacion: " + liq.getNumOperacion());
                    return; // Saltar esta iteración
                }
                System.out.println("numOperacion: " + liq.toString());
                operacion = operacionesAntiguoService
                        .getOperacionXNumeroOperacion(liq.getNumOperacion().intValue());

                if (operacion == null) {
                    System.out.println("No se encontró operacion para numOperacion: " + liq.getNumOperacion());
                    return; // Saltar esta iteración
                }
            } catch (Exception e) {
                System.out.println("Error al obtener operacion: " + e.getMessage());
                return; // Saltar esta iteración
            }

            System.out.println("paso por aca3");
            UsuarioMantenimientoDTO userMantenimientoDTO = usuarioServiceImpl
                    .obtenerUsuarioMantenimientoById(liq.getCreatedBy());
            UsuarioMantenimientoDTO asesorUser = usuarioServiceImpl.obtenerUsuarioMantenimientoById(liq.getAsesorId());

            System.out.println("paso por aca4");
            liq.setCreatedByName(userMantenimientoDTO);
            liq.setAsesorUser(asesorUser);
            System.out.println("paso por aca5");

            List<LCTFiles> files = lctFilesRepository.findByLiquidacionCostosTransaccionId(liq.getId());

            if (files != null) {
                files.forEach(file -> {
                    file.setUrlTemporal(s3Service.getFileUrl(file.getUrl()));
                    file.setLiquidacionCostosTransaccionId(null);
                });
                liq.setFiles(files);
            }

            System.out.println("paso por aca6");
            LiquidacionCostosTransaccionResponse lctResponse = new LiquidacionCostosTransaccionResponse();
            lctResponse.setOperacion(operacion);
            lctResponse.setLiquidacionCostosTransaccion(liq);
            System.out.println("paso por aca7");

            LCRSolicitudEtapaSeguimiento lcrSolicitudEtapaSeguimiento = lcrSolicitudEtapaSeguimientoRepository
                    .findByLcrSolicitudNumOperacionAndActual(operacion.getNumero_operacion());
            System.out.println("paso por aca8");
            if (lcrSolicitudEtapaSeguimiento != null) {
                lctResponse.setLcrSolicitudEtapaSeguimiento(lcrSolicitudEtapaSeguimiento);
            }

            data.add(lctResponse);
        });
        response.setSuccess(true);
        response.setData(data);
        response.setMessage("ok");
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotal((int) liquidaciones.getTotalElements());

        return response;
    }

    public Integer countLctFinal() {
        return liquidacionCostosTransaccionRepository.countLctFinal();
    }

    public Integer countLctDesembolso() {
        return liquidacionCostosTransaccionRepository.countLctDesembolso();
    }

    public byte[] descargarReporteDesembolso(String startDate, String endDate) {
        return excelService.generateExcelDesembolsoLiqCostos(startDate, endDate);
    }

    public byte[] descargarReporteSaldos(String startDate, String endDate, String estado) {
        return excelService.generateExcelSaldosLiqCostos(startDate, endDate, estado);
    }

    public LCRMontoEstadosDTO getMontoEstados(String slug) {
        return liquidacionCostosTransaccionRepository.getMontoEstados(slug);
    }

}
