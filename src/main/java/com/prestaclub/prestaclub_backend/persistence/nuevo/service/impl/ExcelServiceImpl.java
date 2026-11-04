package com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.prestaclub.prestaclub_backend.dto.LiquidacionCostosTransaccionResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.OperacionesAntiguoService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.FlujoTchnReporteProjection;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.RptMensaje;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitud;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTCronograma;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosReales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudEtapaSeguimientoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCTCronogramaRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosTransaccionRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRSolicitudService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LiquidacionCostosTransaccionService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService;

@Service
public class ExcelServiceImpl {

    @Autowired
    private OperacionesAntiguoService operacionesAntiguoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LCTCronogramaRepository lctCronogramaRepository;

    @Autowired
    private LiquidacionCostosTransaccionRepository liquidacionCostosTransaccionRepository;

    @Autowired
    private LCRSolicitudRepository lcrSolicitudRepository;

    @Autowired
    private LCRSolicitudEtapaSeguimientoRepository lcrSolicitudEtapaSeguimientoRepository;

    public byte[] generateExcelLctCronogramaPagos(Integer numOperacion, Long liquidacionCostosTransaccionId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            String nombreAsesor = "";
            List<OperacionDTO> operacionesXNumeroOperacion = operacionesAntiguoService
                    .obtenerOperacionesXNumeroOperacion(numOperacion);
            List<LCTCronograma> lctCronograma = lctCronogramaRepository
                    .findByLiquidacionCostosTransaccionId(liquidacionCostosTransaccionId);
            LiquidacionCostosTransaccion lct = liquidacionCostosTransaccionRepository
                    .findLastById(liquidacionCostosTransaccionId.intValue());
            if (operacionesXNumeroOperacion.isEmpty()) {
                return null;
            }
            OperacionDTO operacion = operacionesXNumeroOperacion.get(0);
            Usuario asesor = usuarioService.findById(lct.getAsesorId());
            nombreAsesor = (asesor.getEmpleado().getNombre() != null ? asesor.getEmpleado().getNombre() : "") + " " +
                    (asesor.getEmpleado().getApellidoPaterno() != null ? asesor.getEmpleado().getApellidoPaterno() : "")
                    + " " +
                    (asesor.getEmpleado().getApellidoMaterno() != null ? asesor.getEmpleado().getApellidoMaterno()
                            : "");

            Sheet sheet = workbook.createSheet("Cronograma de Pagos");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            Font tableHeaderFont = workbook.createFont();
            tableHeaderFont.setBold(true);
            tableHeaderStyle.setFont(tableHeaderFont);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle tableCellStyle = workbook.createCellStyle();
            tableCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Crear encabezado
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(1);
            headerCell.setCellValue("RESUMEN DE CRONOGRAMA");
            headerCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 4));

            // Detalles del cliente
            int currentRow = 2;

            // Fila para "CLIENTE"
            Row clienteRow = sheet.createRow(currentRow++);
            clienteRow.createCell(1).setCellValue("CLIENTE:");
            sheet.addMergedRegion(new CellRangeAddress(clienteRow.getRowNum(), clienteRow.getRowNum(), 1, 2)); // Combinar
                                                                                                               // B y C
            clienteRow.createCell(3).setCellValue(operacion.getNombre_cliente());

            // Fila para "DOCUMENTO"
            Row documentoRow = sheet.createRow(currentRow++);
            documentoRow.createCell(1).setCellValue("DOCUMENTO:");
            sheet.addMergedRegion(new CellRangeAddress(documentoRow.getRowNum(), documentoRow.getRowNum(), 1, 2)); // Combinar
                                                                                                                   // B
                                                                                                                   // y
                                                                                                                   // C
            documentoRow.createCell(3).setCellValue(operacion.getNumero_documento());

            // Fila para "NRO. DE OPER."
            Row nroOperRow = sheet.createRow(currentRow++);
            nroOperRow.createCell(1).setCellValue("NRO. DE OPER.:");
            sheet.addMergedRegion(new CellRangeAddress(nroOperRow.getRowNum(), nroOperRow.getRowNum(), 1, 2)); // Combinar
                                                                                                               // B y C
            nroOperRow.createCell(3).setCellValue(operacion.getNumero_operacion().toString());

            Long codAnterior = operacion.getCodigo_anterior();
            // Fila para "NRO. DE OPER. ANTERIOR"
            Row nroOperAnteriorRow = sheet.createRow(currentRow++);
            nroOperAnteriorRow.createCell(1).setCellValue("NRO. DE OPER. ANTERIOR:");
            sheet.addMergedRegion(
                    new CellRangeAddress(nroOperAnteriorRow.getRowNum(), nroOperAnteriorRow.getRowNum(), 1, 2)); // Combinar
                                                                                                                 // B y
                                                                                                                 // C
            nroOperAnteriorRow.createCell(3).setCellValue((codAnterior != null ? codAnterior.toString() : "-"));

            // Fila para "ASESOR"
            Row asesorRow = sheet.createRow(currentRow++);
            asesorRow.createCell(1).setCellValue("ASESOR:");
            sheet.addMergedRegion(new CellRangeAddress(asesorRow.getRowNum(), asesorRow.getRowNum(), 1, 2)); // Combinar
                                                                                                             // B y C
            asesorRow.createCell(3).setCellValue(nombreAsesor);

            // Fila para "TIPO MONEDA"
            Row tipoMonedaRow = sheet.createRow(currentRow++);
            tipoMonedaRow.createCell(1).setCellValue("TIPO MONEDA:");
            sheet.addMergedRegion(new CellRangeAddress(tipoMonedaRow.getRowNum(), tipoMonedaRow.getRowNum(), 1, 2)); // Combinar
                                                                                                                     // B
                                                                                                                     // y
                                                                                                                     // C
            tipoMonedaRow.createCell(3).setCellValue(lct.isEsDolares() ? "USD" : "PEN");

            // Espaciado
            currentRow++;

            // Crear un estilo con bordes
            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);
            borderedStyle.setAlignment(HorizontalAlignment.CENTER); // Centrar texto horizontalmente
            borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Centrar texto verticalmente

            // Encabezados de la tabla
            Row tableHeaderRow = sheet.createRow(currentRow++);
            Cell cell1 = tableHeaderRow.createCell(1);
            cell1.setCellValue("N° cuota");
            cell1.setCellStyle(borderedStyle);

            Cell cell2 = tableHeaderRow.createCell(2);
            cell2.setCellValue("Fecha de vencimiento");
            cell2.setCellStyle(borderedStyle);

            Cell cell3 = tableHeaderRow.createCell(3);
            cell3.setCellValue("Monto total de la cuota");
            cell3.setCellStyle(borderedStyle);

            // Ajustar ancho de las columnas para mejor visibilidad (opcional)
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

            // Contenido de la tabla
            int numCuota = 1;
            for (LCTCronograma cronograma : lctCronograma) {
                Row row = sheet.createRow(currentRow++);
                row.createCell(1).setCellValue(numCuota++);
                row.createCell(2).setCellValue(cronograma.getFechaPago().toString());
                row.createCell(3).setCellValue(String.format("%.2f", cronograma.getCuotaDepositar()));

                row.getCell(1).setCellStyle(tableCellStyle);
                row.getCell(2).setCellStyle(tableCellStyle);
                row.getCell(3).setCellStyle(tableCellStyle);
            }

            // Autoajustar columnas
            for (int i = 1; i <= 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir en un ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generateExcelSolicitudAprobadasLiqCostos(String startDate, String endDate, String numOperacion) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // List<LCRSolicitud> solicitudes =
            // lcrSolicitudRepository.findByRangeUpdateDate(startDate, endDate);
            List<FlujoTchnReporteProjection> registros = lcrSolicitudRepository.obtenerDetalleCostos(numOperacion);

            Sheet sheet = workbook.createSheet("Flujo de Aprobaciones TCHN - Costos Reales");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            Font tableHeaderFont = workbook.createFont();
            tableHeaderFont.setBold(true);
            tableHeaderStyle.setFont(tableHeaderFont);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle tableCellStyle = workbook.createCellStyle();
            tableCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo con formato moneda sol peruano
            CellStyle currencySolCellStyle = workbook.createCellStyle();
            currencySolCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"S/\" #,##0.00"));
            currencySolCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Formato dólar
            CellStyle currencyDolarCellStyle = workbook.createCellStyle();
            currencyDolarCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"$\" #,##0.00"));
            currencyDolarCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Crear encabezado
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(1);
            headerCell.setCellValue("Flujo de Aprobaciones TCHN - Costos Reales");
            headerCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));

            // // Detalles del cliente
            int currentRow = 2;

            // Espaciado
            currentRow++;

            // Crear un estilo con bordes
            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);
            borderedStyle.setAlignment(HorizontalAlignment.CENTER); // Centrar texto horizontalmente
            borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Centrar texto verticalmente

            // Encabezados de la tabla
            Row tableHeaderRow = sheet.createRow(currentRow++);
            Cell cell1 = tableHeaderRow.createCell(1);
            cell1.setCellValue("TCHN");
            cell1.setCellStyle(borderedStyle);

            Cell cell2 = tableHeaderRow.createCell(2);
            cell2.setCellValue("FECHA ENDOSO");
            cell2.setCellStyle(borderedStyle);

            Cell cell3 = tableHeaderRow.createCell(3);
            cell3.setCellValue("MONTO SIMULADO");
            cell3.setCellStyle(borderedStyle);

            // saldo a favor
            Cell cell4 = tableHeaderRow.createCell(4);
            cell4.setCellValue("MONTO REAL");
            cell4.setCellStyle(borderedStyle);

            // Banco
            Cell cell5 = tableHeaderRow.createCell(5);
            cell5.setCellValue("MONTO SALDO TOTAL");
            cell5.setCellStyle(borderedStyle);

            // moneda

            Cell cell6 = tableHeaderRow.createCell(6);
            cell6.setCellValue("ESTADO POR AREA");
            cell6.setCellStyle(borderedStyle);

            // Nro de cuenta

            Cell cell7 = tableHeaderRow.createCell(7);
            cell7.setCellValue("USUARIO AREA");
            cell7.setCellStyle(borderedStyle);

            // CCI

            Cell cell8 = tableHeaderRow.createCell(8);
            cell8.setCellValue("AREA");
            cell8.setCellStyle(borderedStyle);
            // fecha aprobada

            Cell cell9 = tableHeaderRow.createCell(9);
            cell9.setCellValue("ESTADO ACTUAL");
            cell9.setCellStyle(borderedStyle);

            // USUARIO ACTUAL

            Cell cell10 = tableHeaderRow.createCell(10);
            cell10.setCellValue("USUARIO ACTUAL");
            cell10.setCellStyle(borderedStyle);

            // FECHA ACTUALIZADA

            Cell cell11 = tableHeaderRow.createCell(11);
            cell11.setCellValue("FECHA ACTUALIZADA");
            cell11.setCellStyle(borderedStyle);

            // OBSERVACIONES

            Cell cell12 = tableHeaderRow.createCell(12);
            cell12.setCellValue("OBSERVACIONES");
            cell12.setCellStyle(borderedStyle);

            // CONCEPTO

            Cell cell13 = tableHeaderRow.createCell(13);
            cell13.setCellValue("CONCEPTO");
            cell13.setCellStyle(borderedStyle);

            // GASTO SIMULADO

            Cell cell14 = tableHeaderRow.createCell(14);
            cell14.setCellValue("GASTO SIMULADO");
            cell14.setCellStyle(borderedStyle);

            // GASTO REAL

            Cell cell15 = tableHeaderRow.createCell(15);
            cell15.setCellValue("GASTO REAL");
            cell15.setCellStyle(borderedStyle);

            // SALDO

            Cell cell16 = tableHeaderRow.createCell(16);
            cell16.setCellValue("SALDO");
            cell16.setCellStyle(borderedStyle);

            // Ajustar ancho de las columnas para mejor visibilidad (opcional)
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);
            sheet.autoSizeColumn(11);
            sheet.autoSizeColumn(12);
            sheet.autoSizeColumn(13);
            sheet.autoSizeColumn(14);
            sheet.autoSizeColumn(15);
            sheet.autoSizeColumn(16);

            // Contenido de la tabla
            int numCuota = 1;
            for (FlujoTchnReporteProjection registro : registros) {
                Row row = sheet.createRow(currentRow++);
                // List<OperacionDTO> operacionesXNumeroOperacion =
                // operacionesAntiguoService.obtenerOperacionesXNumeroOperacion(solicitud.getNumOperacion().intValue());
                // OperacionDTO operacion = operacionesXNumeroOperacion.get(0);
                // String cuenta = solicitud.getCuentaBancaria();
                // String Banco = "";
                // String moneda = "";
                // String nroCuenta = "";
                // String cci = solicitud.getCci();
                // if (cuenta.equals("CUENTA INTERNA")) {
                // Banco = "-";
                // moneda = "-";
                // cci = "-";
                // nroCuenta = "CUENTA INTERNA";
                // }else{
                // String[] parts = cuenta.split(" ");
                // Banco = parts[0];
                // moneda = parts[1];
                // // cuenta pero sin la parts[0] y parts[1] para obtener el nro de cuenta
                // nroCuenta = cuenta.substring(parts[0].length() + parts[1].length() + 1);
                // }
                row.createCell(1).setCellValue(registro.getTCHN());
                row.createCell(2).setCellValue(registro.getFECHA_ENDOSO());
                BigDecimal montoSimulado = registro.getMONTO_SIMULADO() == null ? BigDecimal.ZERO
                        : registro.getMONTO_SIMULADO();
                row.createCell(3).setCellValue(montoSimulado.doubleValue());
                BigDecimal montoReal = registro.getMONTO_REAL() == null ? BigDecimal.ZERO : registro.getMONTO_REAL();
                row.createCell(4).setCellValue(montoReal.doubleValue());
                BigDecimal montoSaldoTotal = registro.getMONTO_SALDO_TOTAL() == null ? BigDecimal.ZERO
                        : registro.getMONTO_SALDO_TOTAL();
                row.createCell(5).setCellValue(montoSaldoTotal.doubleValue());
                row.createCell(6).setCellValue(registro.getESTADO_POR_AREA());
                row.createCell(7).setCellValue(registro.getUSUARIO_AREA());
                row.createCell(8).setCellValue(registro.getAREA());
                row.createCell(9).setCellValue(registro.getESTADO_ACTUAL());
                row.createCell(10).setCellValue(registro.getUSUARIO_ACTUAL());
                row.createCell(11).setCellValue(registro.getFECHA_ACTUALIZADA());
                row.createCell(12).setCellValue(registro.getOBSERVACIONES());
                row.createCell(13).setCellValue(registro.getCONCEPTO());
                BigDecimal gastoSimulado = registro.getGASTO_SIMULADO() == null ? BigDecimal.ZERO
                        : registro.getGASTO_SIMULADO();
                row.createCell(14).setCellValue(gastoSimulado.doubleValue());
                BigDecimal gastoReal = registro.getGASTO_REAL() == null ? BigDecimal.ZERO : registro.getGASTO_REAL();
                row.createCell(15).setCellValue(gastoReal.doubleValue());
                BigDecimal saldo = registro.getSALDO() == null ? BigDecimal.ZERO : registro.getSALDO();
                row.createCell(16).setCellValue(saldo.doubleValue());

                row.getCell(1).setCellStyle(tableCellStyle);
                row.getCell(2).setCellStyle(dateCellStyle);
                row.getCell(3).setCellStyle(tableCellStyle);
                // row.getCell(4).setCellStyle(solicitud.getEsDolares() ? currencyDolarCellStyle
                // : currencySolCellStyle);
                row.getCell(5).setCellStyle(tableCellStyle);
                row.getCell(6).setCellStyle(tableCellStyle);
                row.getCell(7).setCellStyle(tableCellStyle);
                row.getCell(8).setCellStyle(tableCellStyle);
                row.getCell(9).setCellStyle(tableCellStyle);
                row.getCell(10).setCellStyle(tableCellStyle);
                row.getCell(11).setCellStyle(dateCellStyle);
                row.getCell(12).setCellStyle(tableCellStyle);
                row.getCell(13).setCellStyle(tableCellStyle);
                row.getCell(14).setCellStyle(tableCellStyle);
                row.getCell(15).setCellStyle(tableCellStyle);
                row.getCell(16).setCellStyle(tableCellStyle);

            }

            // Autoajustar columnas
            for (int i = 1; i <= 16; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir en un ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generateExcelDesembolsoLiqCostos(String startDate, String endDate) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // List<LCRSolicitud> solicitudes =
            // lcrSolicitudRepository.findByRangeUpdateDate(startDate, endDate);
            List<LiquidacionCostosTransaccion> listaLctDesembolso = liquidacionCostosTransaccionRepository
                    .listaDesembolsoLiquidacionesByRangeUpdateDate(startDate, endDate);

            Sheet sheet = workbook.createSheet("Reporte Desembolso ");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            Font tableHeaderFont = workbook.createFont();
            tableHeaderFont.setBold(true);
            tableHeaderStyle.setFont(tableHeaderFont);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle tableCellStyle = workbook.createCellStyle();
            tableCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo con formato moneda sol peruano
            CellStyle currencySolCellStyle = workbook.createCellStyle();
            currencySolCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"S/\" #,##0.00"));
            currencySolCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Formato dólar
            CellStyle currencyDolarCellStyle = workbook.createCellStyle();
            currencyDolarCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"$\" #,##0.00"));
            currencyDolarCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Crear encabezado
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(1);
            headerCell.setCellValue("LISTADO DE LIQUIDACIONES EN DESEMBOLSO");
            headerCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));

            // // Detalles del cliente
            int currentRow = 2;

            // Espaciado
            currentRow++;

            // Crear un estilo con bordes
            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);
            borderedStyle.setAlignment(HorizontalAlignment.CENTER); // Centrar texto horizontalmente
            borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Centrar texto verticalmente

            // Encabezados de la tabla
            Row tableHeaderRow = sheet.createRow(currentRow++);

            // Tipo Operación

            Cell cell1 = tableHeaderRow.createCell(1);
            cell1.setCellValue("Tipo Operación");
            cell1.setCellStyle(borderedStyle);

            // Nro Operación

            Cell cell2 = tableHeaderRow.createCell(2);
            cell2.setCellValue("N° Operación");
            cell2.setCellStyle(borderedStyle);

            // DNI

            Cell cell3 = tableHeaderRow.createCell(3);
            cell3.setCellValue("DNI");
            cell3.setCellStyle(borderedStyle);

            // Cliente
            Cell cell4 = tableHeaderRow.createCell(4);
            cell4.setCellValue("Cliente");
            cell4.setCellStyle(borderedStyle);

            // Fecha Desembolso
            Cell cell5 = tableHeaderRow.createCell(5);
            cell5.setCellValue("Fecha Desembolso");
            cell5.setCellStyle(borderedStyle);

            // Fondo acreedor

            Cell cell6 = tableHeaderRow.createCell(6);
            cell6.setCellValue("Fondo Acreedor");
            cell6.setCellStyle(borderedStyle);

            // Entregado a Cliente

            Cell cell7 = tableHeaderRow.createCell(7);
            cell7.setCellValue("Entregado a Cliente");
            cell7.setCellStyle(borderedStyle);

            // CRI/BLOQUEO/PERITAJE

            Cell cell8 = tableHeaderRow.createCell(8);
            cell8.setCellValue("CRI/Bloqueo/Peritaje");
            cell8.setCellStyle(borderedStyle);
            // Pagos descontados del cheque

            Cell cell9 = tableHeaderRow.createCell(9);
            cell9.setCellValue("Pagos descontados del cheque");
            cell9.setCellStyle(borderedStyle);

            // Costos adicionales

            Cell cell10 = tableHeaderRow.createCell(10);
            cell10.setCellValue("Costos adicionales");
            cell10.setCellStyle(borderedStyle);

            // Ajustar ancho de las columnas para mejor visibilidad (opcional)
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);

            for (LiquidacionCostosTransaccion desembolso : listaLctDesembolso) {

                List<OperacionDTO> operacionesXNumeroOperacion = operacionesAntiguoService
                        .obtenerOperacionesXNumeroOperacion(desembolso.getNumOperacion().intValue());
                if (!operacionesXNumeroOperacion.isEmpty()) {
                    Row row = sheet.createRow(currentRow++);
                    OperacionDTO operacion = operacionesXNumeroOperacion.get(0);

                    String fondoAcreedor = "";
                    int fondoId = desembolso.getFondoId().intValue();
                    switch (fondoId) {
                        case 1:
                            fondoAcreedor = "FONDO CAPITAL EMPRENDEDOR";
                            // inversionistaRuc = "20544837835";
                            break;
                        case 2:
                            fondoAcreedor = "FONDO POPULAR 1";
                            // inversionistaRuc = "20516816733";
                            break;
                        case 3:
                            fondoAcreedor = "FONDO MYPE";
                            // inversionistaRuc = "20519075581";
                            break;
                        case 4:
                            fondoAcreedor = "FONDO PEREZ HIDALGO";
                            // inversionistaRuc = "20553718067";
                            break;
                        default:
                            fondoAcreedor = "-";
                            // inversionistaRuc = "-";
                            break;
                    }
                    row.createCell(1).setCellValue(operacion.getTipo_operacion());
                    row.createCell(2).setCellValue(operacion.getNumero_operacion());
                    row.createCell(3).setCellValue(operacion.getNumero_documento());
                    row.createCell(4).setCellValue(operacion.getNombre_cliente());
                    row.createCell(5).setCellValue(desembolso.getFechaAdeudado());
                    row.createCell(6).setCellValue(fondoAcreedor);
                    row.createCell(7).setCellValue(desembolso.getPrestamoNeto().doubleValue());
                    row.createCell(8).setCellValue(desembolso.getCriBloqueoPeritaje().doubleValue());
                    row.createCell(9).setCellValue(desembolso.getPagosDescontadosCheque() == null ? 0
                            : desembolso.getPagosDescontadosCheque().doubleValue());
                    row.createCell(10).setCellValue(desembolso.getCostosAdicionales().doubleValue());

                    row.getCell(1).setCellStyle(tableCellStyle);
                    row.getCell(2).setCellStyle(tableCellStyle);
                    row.getCell(3).setCellStyle(tableCellStyle);
                    row.getCell(4).setCellStyle(tableCellStyle);
                    row.getCell(5).setCellStyle(dateCellStyle);
                    row.getCell(6).setCellStyle(tableCellStyle);
                    row.getCell(7)
                            .setCellStyle((desembolso.isEsDolares() ? currencyDolarCellStyle : currencySolCellStyle));
                    row.getCell(8)
                            .setCellStyle((desembolso.isEsDolares() ? currencyDolarCellStyle : currencySolCellStyle));
                    row.getCell(9)
                            .setCellStyle((desembolso.isEsDolares() ? currencyDolarCellStyle : currencySolCellStyle));
                    row.getCell(10)
                            .setCellStyle((desembolso.isEsDolares() ? currencyDolarCellStyle : currencySolCellStyle));
                }
            }

            // Autoajustar columnas
            for (int i = 1; i <= 9; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir en un ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

    public byte[] generateExcelSaldosLiqCostos(String startDate, String endDate, String estado) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // Convertir strings de fecha a LocalDateTime (inicio y fin del día)
            LocalDateTime startDateTime = toStartOfDay(startDate);
            LocalDateTime endDateTime = toEndOfDay(endDate);

            List<LcrSolicitudResponse> sols = lcrSolicitudEtapaSeguimientoRepository.getLcrSolicitudByStatusByDates(
                    startDateTime, endDateTime, estado.isEmpty() ? null : Integer.parseInt(estado));

            List<LiquidacionCostosTransaccionResponse> lSaldo = new ArrayList<>();

            sols.forEach(sol -> {
                LiquidacionCostosTransaccionResponse lc = new LiquidacionCostosTransaccionResponse();
                LiquidacionCostosTransaccion lct = liquidacionCostosTransaccionRepository
                        .getTotalCostosTransaccionBySolNumOperacion(
                                sol.getNumOperacion());

                List<LiquidacionCostosReales> lcReales = lcrSolicitudEtapaSeguimientoRepository
                        .getLiquidacionCostosReales(sol.getNumOperacion());
                BigDecimal montoReal = BigDecimal.ZERO;

                for (LiquidacionCostosReales lcReal : lcReales) {
                    if (lcReal.getEsDolares()) {
                        montoReal = montoReal.add(lcReal.getMonto().multiply(lcReal.getTipoCambio()).setScale(2,
                                BigDecimal.ROUND_HALF_UP));
                    } else {
                        montoReal = montoReal.add(lcReal.getMonto().setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }
//.subtract(lct.getPagadoCliente()).subtract(BigDecimal.valueOf(0.2))
                montoReal = montoReal.subtract(lct.getPagadoCliente());

                sol.setMontoReal(montoReal);
                lc.setStatus(sol.getStatus());
                lc.setLiquidacionCostosTransaccion(lct);
                System.out.println("sol.getMontoSimulado(): " + sol.getNumOperacion());
                if (sol.getNumOperacion() == 15587) {
                    System.out.println("sol.getMontoSimulado(): " + sol.getMontoSimulado());
                }
                if (sol.getEsDolares()) {
                    lc.setGravamen(sol.getMontoSimulado()
                            .multiply(lct.getTipoCambioPeritaje().subtract(BigDecimal.valueOf(0.2))).setScale(2,
                                    BigDecimal.ROUND_HALF_UP)
                            .subtract(sol.getMontoReal()));
                    // lc.setGravamen(sol.getMontoSimulado().subtract(sol.getMontoReal()).multiply(lct.getTipoCambio()));
                } else {
                    lc.setGravamen(sol.getMontoSimulado().subtract(lct.getPagadoCliente()).subtract(sol.getMontoReal()));
                }

                /*
                 * List<OperacionDTO> operacionesXNumeroOperacion = operacionesAntiguoService
                 * .obtenerOperacionesXNumeroOperacion(sol.getNumOperacion().intValue());
                 * -
                 * if (!operacionesXNumeroOperacion.isEmpty()) {
                 * lc.setOperacion(operacionesXNumeroOperacion.get(0));
                 * }
                 */
                lSaldo.add(lc);
            });

            Sheet sheet = workbook.createSheet("Reporte Saldo ");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            Font tableHeaderFont = workbook.createFont();
            tableHeaderFont.setBold(true);
            tableHeaderStyle.setFont(tableHeaderFont);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle tableCellStyle = workbook.createCellStyle();
            tableCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo con formato moneda sol peruano
            CellStyle currencySolCellStyle = workbook.createCellStyle();
            currencySolCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"S/\" #,##0.00"));
            currencySolCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Formato dólar
            CellStyle currencyDolarCellStyle = workbook.createCellStyle();
            currencyDolarCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"$\" #,##0.00"));
            currencyDolarCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Crear encabezado
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(1);
            headerCell.setCellValue("LISTADO DE SALDOS EN DESEMBOLSO");
            headerCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 5));

            // // Detalles del cliente
            int currentRow = 2;

            // Espaciado
            currentRow++;

            // Crear un estilo con bordes
            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);
            borderedStyle.setAlignment(HorizontalAlignment.CENTER); // Centrar texto horizontalmente
            borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Centrar texto verticalmente

            // Encabezados de la tabla
            Row tableHeaderRow = sheet.createRow(currentRow++);

            // Tipo Operación

            Cell cell1 = tableHeaderRow.createCell(1);
            cell1.setCellValue("DNI");
            cell1.setCellStyle(borderedStyle);

            // Nro Operación

            Cell cell2 = tableHeaderRow.createCell(2);
            cell2.setCellValue("NOMBRE");
            cell2.setCellStyle(borderedStyle);

            // DNI

            Cell cell3 = tableHeaderRow.createCell(3);
            cell3.setCellValue("CÓDIGO");
            cell3.setCellStyle(borderedStyle);

            // Cliente
            Cell cell4 = tableHeaderRow.createCell(4);
            cell4.setCellValue("SALDO");
            cell4.setCellStyle(borderedStyle);

            // Fecha Desembolso
            Cell cell5 = tableHeaderRow.createCell(5);
            cell5.setCellValue("ESTADO");
            cell5.setCellStyle(borderedStyle);

            // Ajustar ancho de las columnas para mejor visibilidad (opcional)
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);

            for (LiquidacionCostosTransaccionResponse saldo : lSaldo) {

                // List<OperacionDTO> operacionesXNumeroOperacion = operacionesAntiguoService
                // .obtenerOperacionesXNumeroOperacion(desembolso.getNumOperacion().intValue());
                // if (saldo.getLcrSolicitudEtapaSeguimiento() != null) {
                Row row = sheet.createRow(currentRow++);

                row.createCell(1)
                        .setCellValue(saldo.getLiquidacionCostosTransaccion().getNumeroDocumentoSolicitanteCheque());
                row.createCell(2).setCellValue(saldo.getLiquidacionCostosTransaccion().getNombreSolicitanteCheque());
                row.createCell(3).setCellValue(saldo.getLiquidacionCostosTransaccion().getNumOperacion());
                row.createCell(4).setCellValue(saldo.getGravamen().doubleValue());
                String estadoSaldo = "";
                switch (saldo.getStatus()) {
                    case 0:
                        estadoSaldo = "PENDIENTE";
                        break;
                    case 1:
                        estadoSaldo = "APROBADO";
                        break;
                    case 2:
                        estadoSaldo = "OBSERVADO";
                        break;
                    default:
                        break;
                }
                row.createCell(5)
                        .setCellValue(estadoSaldo);

                row.getCell(1).setCellStyle(tableCellStyle);
                row.getCell(2).setCellStyle(tableCellStyle);
                row.getCell(3).setCellStyle(tableCellStyle);
                row.getCell(4).setCellStyle(tableCellStyle);
                row.getCell(5).setCellStyle(dateCellStyle);

                // }
            }

            // Autoajustar columnas
            for (int i = 1; i <= 9; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir en un ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper methods for parsing date strings into LocalDateTime (start/end of day)
    private static LocalDateTime toStartOfDay(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("startDate is null or empty");
        }
        // Try multiple formats
        try {
            // ISO_LOCAL_DATE_TIME (e.g. 2023-01-01T10:15:30)
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // ignore and try next
        }
        try {
            // ISO_LOCAL_DATE (e.g. 2023-01-01)
            LocalDate d = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return d.atStartOfDay();
        } catch (DateTimeParseException e) {
            // ignore and try next
        }
        DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate d = LocalDate.parse(dateStr, fmt1);
            return d.atStartOfDay();
        } catch (DateTimeParseException e) {
            // ignore
        }
        DateTimeFormatter fmt2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return LocalDateTime.parse(dateStr, fmt2);
        } catch (DateTimeParseException e) {
            // ignore
        }

        // If none matched, throw to surface the error early
        throw new IllegalArgumentException("Unsupported date format for startDate: " + dateStr);
    }

    private static LocalDateTime toEndOfDay(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("endDate is null or empty");
        }
        try {
            // If full datetime provided, return as-is
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // ignore
        }
        try {
            LocalDate d = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return d.atTime(LocalTime.MAX);
        } catch (DateTimeParseException e) {
            // ignore
        }
        DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate d = LocalDate.parse(dateStr, fmt1);
            return d.atTime(LocalTime.MAX);
        } catch (DateTimeParseException e) {
            // ignore
        }
        DateTimeFormatter fmt2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return LocalDateTime.parse(dateStr, fmt2);
        } catch (DateTimeParseException e) {
            // ignore
        }

        throw new IllegalArgumentException("Unsupported date format for endDate: " + dateStr);
    }

    public byte[] generateExcelSolicitudDevolucionesResoluciones(String startDate, String endDate, String tchn,
            String reembolsados, String aplicados) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // List<LCRSolicitud> solicitudes =
            // lcrSolicitudRepository.findByRangeUpdateDate(startDate, endDate);

            // List<FlujoTchnReporteProjection> registros =
            // lcrSolicitudRepository.obtenerDetalleCostos(tchn);

            List<RptMensaje> registrosRPTMensaje = lcrSolicitudRepository.obtenerRptMensaje(startDate, endDate, tchn,
                    reembolsados, aplicados);
            // List<RptMensaje> registrosRPTMensaje =
            // lcrSolicitudRepository.obtenerRptMensaje(startDate ,endDate , tchn);

            Sheet sheet = workbook.createSheet("Flujo de Aprobaciones TCHN - Costos Reales");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            Font tableHeaderFont = workbook.createFont();
            tableHeaderFont.setBold(true);
            tableHeaderStyle.setFont(tableHeaderFont);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle tableCellStyle = workbook.createCellStyle();
            tableCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo con formato moneda sol peruano
            CellStyle currencySolCellStyle = workbook.createCellStyle();
            currencySolCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"S/\" #,##0.00"));
            currencySolCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Formato dólar
            CellStyle currencyDolarCellStyle = workbook.createCellStyle();
            currencyDolarCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"$\" #,##0.00"));
            currencyDolarCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Crear encabezado
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(1);
            headerCell.setCellValue("Reporte Devoluciones y Resoluciones de TCHN");
            headerCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));

            // // Detalles del cliente
            int currentRow = 2;

            // Espaciado
            currentRow++;

            // Crear un estilo con bordes
            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);
            borderedStyle.setAlignment(HorizontalAlignment.CENTER); // Centrar texto horizontalmente
            borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Centrar texto verticalmente

            // Encabezados de la tabla
            Row tableHeaderRow = sheet.createRow(currentRow++);
            Cell cell1 = tableHeaderRow.createCell(1);
            cell1.setCellValue("CODIGO OPERACION");
            cell1.setCellStyle(borderedStyle);

            Cell cell2 = tableHeaderRow.createCell(2);
            cell2.setCellValue("CLIENTE");
            cell2.setCellStyle(borderedStyle);

            Cell cell3 = tableHeaderRow.createCell(3);
            cell3.setCellValue("MONTO DEVOLUCION");
            cell3.setCellStyle(borderedStyle);

            Cell cell4 = tableHeaderRow.createCell(4);
            cell4.setCellValue("FECHA ENDOSO");
            cell4.setCellStyle(borderedStyle);

            Cell cell5 = tableHeaderRow.createCell(5);
            cell5.setCellValue("1ER AVISO");
            cell5.setCellStyle(borderedStyle);

            Cell cell6 = tableHeaderRow.createCell(6);
            cell6.setCellValue("TIPO ENVIO");
            cell6.setCellStyle(borderedStyle);

            // Nro de cuenta

            Cell cell7 = tableHeaderRow.createCell(7);
            cell7.setCellValue("TIPO RESPUESTA");
            cell7.setCellStyle(borderedStyle);

            Cell cell9 = tableHeaderRow.createCell(8);
            cell9.setCellValue("2DO AVISO");
            cell9.setCellStyle(borderedStyle);

            Cell cell10 = tableHeaderRow.createCell(9);
            cell10.setCellValue("TIPO ENVIO");
            cell10.setCellStyle(borderedStyle);

            Cell cell11 = tableHeaderRow.createCell(10);
            cell11.setCellValue("TIPO RESPUESTA");
            cell11.setCellStyle(borderedStyle);

            Cell cell12 = tableHeaderRow.createCell(11);
            cell12.setCellValue("FECHA RESPUESTA");
            cell12.setCellStyle(borderedStyle);

            Cell cell13 = tableHeaderRow.createCell(12);
            cell13.setCellValue("ESTADO FINAL");
            cell13.setCellStyle(borderedStyle);

            Cell cell14 = tableHeaderRow.createCell(13);
            cell14.setCellValue("FECHA ULT MOVIMIENTO");
            cell14.setCellStyle(borderedStyle);

            Cell cell15 = tableHeaderRow.createCell(14);
            cell15.setCellValue("USUARIO RESPONSABLE");
            cell15.setCellStyle(borderedStyle);

            Cell cell16 = tableHeaderRow.createCell(15);
            cell16.setCellValue("AREA RESPONSABLE");
            cell16.setCellStyle(borderedStyle);

            // Ajustar ancho de las columnas para mejor visibilidad (opcional)
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);
            sheet.autoSizeColumn(11);
            sheet.autoSizeColumn(12);
            sheet.autoSizeColumn(13);
            sheet.autoSizeColumn(14);
            sheet.autoSizeColumn(15);
            sheet.autoSizeColumn(16);

            for (RptMensaje registro : registrosRPTMensaje) {
                Row row = sheet.createRow(currentRow++);

                row.createCell(1).setCellValue(registro.getnumoperacion());
                row.createCell(2).setCellValue(registro.getcopropietario());
                row.createCell(3).setCellValue(registro.getdevolucion());
                row.createCell(4).setCellValue(registro.getfechaendoso());
                row.createCell(5).setCellValue(registro.getfechaenvio1());
                row.createCell(6).setCellValue(registro.gettipoenvio1());
                row.createCell(7).setCellValue(registro.gettiporespuesta1());
                row.createCell(8).setCellValue(registro.getfechaenvio2());
                row.createCell(9).setCellValue(registro.gettipoenvio2());
                row.createCell(10).setCellValue(registro.gettiporespuesta2());
                row.createCell(11).setCellValue(registro.getfecharespuesta());
                row.createCell(12).setCellValue(registro.getestadofinal());
                row.createCell(13).setCellValue(registro.getultimamodificacion());
                row.createCell(14).setCellValue(registro.getusername());
                row.createCell(15).setCellValue(registro.getarea());

                row.getCell(1).setCellStyle(tableCellStyle);
                row.getCell(2).setCellStyle(tableCellStyle);
                row.getCell(3).setCellStyle(tableCellStyle);
                row.getCell(4).setCellStyle(tableCellStyle);
                // row.getCell(4).setCellStyle(solicitud.getEsDolares() ? currencyDolarCellStyle
                // : currencySolCellStyle);
                row.getCell(5).setCellStyle(tableCellStyle);
                row.getCell(6).setCellStyle(tableCellStyle);
                row.getCell(7).setCellStyle(tableCellStyle);
                row.getCell(8).setCellStyle(tableCellStyle);
                row.getCell(9).setCellStyle(tableCellStyle);
                row.getCell(10).setCellStyle(tableCellStyle);
                row.getCell(11).setCellStyle(tableCellStyle);
                row.getCell(12).setCellStyle(tableCellStyle);
                row.getCell(13).setCellStyle(tableCellStyle);
                row.getCell(14).setCellStyle(tableCellStyle);
                row.getCell(15).setCellStyle(tableCellStyle);
                // row.getCell(16).setCellStyle(tableCellStyle);

            }

            // Autoajustar columnas
            for (int i = 1; i <= 16; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir en un ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generateExcelSolicitudDevolucionesResoluciones(String startDate, String endDate, String tchn,
            Integer estado) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // List<LCRSolicitud> solicitudes =
            // lcrSolicitudRepository.findByRangeUpdateDate(startDate, endDate);

            // List<FlujoTchnReporteProjection> registros =
            // lcrSolicitudRepository.obtenerDetalleCostos(tchn);

            List<RptMensaje> registrosRPTMensaje = lcrSolicitudRepository.obtenerRptMensaje(startDate, endDate, tchn,
                    estado);
            // List<RptMensaje> registrosRPTMensaje =
            // lcrSolicitudRepository.obtenerRptMensaje(startDate ,endDate , tchn);

            System.out.println("listado respuesta mensaje es" + registrosRPTMensaje.size());
            Sheet sheet = workbook.createSheet("Flujo de Aprobaciones TCHN - Costos Reales");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            Font tableHeaderFont = workbook.createFont();
            tableHeaderFont.setBold(true);
            tableHeaderStyle.setFont(tableHeaderFont);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle tableCellStyle = workbook.createCellStyle();
            tableCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo con formato moneda sol peruano
            CellStyle currencySolCellStyle = workbook.createCellStyle();
            currencySolCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"S/\" #,##0.00"));
            currencySolCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Formato dÃ³lar
            CellStyle currencyDolarCellStyle = workbook.createCellStyle();
            currencyDolarCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"$\" #,##0.00"));
            currencyDolarCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Crear encabezado
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(1);
            headerCell.setCellValue("Reporte Devoluciones y Resoluciones de TCHN");
            headerCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));

            // // Detalles del cliente
            int currentRow = 2;

            // Espaciado
            currentRow++;

            // Crear un estilo con bordes
            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);
            borderedStyle.setAlignment(HorizontalAlignment.CENTER); // Centrar texto horizontalmente
            borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Centrar texto verticalmente

            // Encabezados de la tabla
            Row tableHeaderRow = sheet.createRow(currentRow++);
            Cell cell1 = tableHeaderRow.createCell(1);
            cell1.setCellValue("CODIGO OPERACION");
            cell1.setCellStyle(borderedStyle);

            Cell cell2 = tableHeaderRow.createCell(2);
            cell2.setCellValue("CLIENTE");
            cell2.setCellStyle(borderedStyle);

            Cell cell3 = tableHeaderRow.createCell(3);
            cell3.setCellValue("MONTO DEVOLUCION");
            cell3.setCellStyle(borderedStyle);

            Cell cell4 = tableHeaderRow.createCell(4);
            cell4.setCellValue("FECHA ENDOSO");
            cell4.setCellStyle(borderedStyle);

            Cell cell5 = tableHeaderRow.createCell(5);
            cell5.setCellValue("1ER AVISO");
            cell5.setCellStyle(borderedStyle);

            Cell cell6 = tableHeaderRow.createCell(6);
            cell6.setCellValue("TIPO ENVIO");
            cell6.setCellStyle(borderedStyle);

            // Nro de cuenta

            Cell cell7 = tableHeaderRow.createCell(7);
            cell7.setCellValue("TIPO RESPUESTA");
            cell7.setCellStyle(borderedStyle);

            Cell cell9 = tableHeaderRow.createCell(8);
            cell9.setCellValue("2DO AVISO");
            cell9.setCellStyle(borderedStyle);

            Cell cell10 = tableHeaderRow.createCell(9);
            cell10.setCellValue("TIPO ENVIO");
            cell10.setCellStyle(borderedStyle);

            Cell cell11 = tableHeaderRow.createCell(10);
            cell11.setCellValue("TIPO RESPUESTA");
            cell11.setCellStyle(borderedStyle);

            Cell cell12 = tableHeaderRow.createCell(11);
            cell12.setCellValue("FECHA RESPUESTA");
            cell12.setCellStyle(borderedStyle);

            Cell cell13 = tableHeaderRow.createCell(12);
            cell13.setCellValue("ESTADO FINAL");
            cell13.setCellStyle(borderedStyle);

            Cell cell14 = tableHeaderRow.createCell(13);
            cell14.setCellValue("FECHA ULT MOVIMIENTO");
            cell14.setCellStyle(borderedStyle);

            Cell cell15 = tableHeaderRow.createCell(14);
            cell15.setCellValue("USUARIO RESPONSABLE");
            cell15.setCellStyle(borderedStyle);

            Cell cell16 = tableHeaderRow.createCell(15);
            cell16.setCellValue("AREA RESPONSABLE");
            cell16.setCellStyle(borderedStyle);

            // Ajustar ancho de las columnas para mejor visibilidad (opcional)
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);
            sheet.autoSizeColumn(11);
            sheet.autoSizeColumn(12);
            sheet.autoSizeColumn(13);
            sheet.autoSizeColumn(14);
            sheet.autoSizeColumn(15);
            sheet.autoSizeColumn(16);

            for (RptMensaje registro : registrosRPTMensaje) {
                Row row = sheet.createRow(currentRow++);
                /* comentado yr103482
                List<OperacionDTO> operacionesXNumeroOperacion = obtenerOperacionesXNumeroOperacion(
                        Integer.valueOf(registro.getnumoperacion()));
                row.createCell(1).setCellValue(registro.getnumoperacion() == null ? "" : registro.getnumoperacion());
                row.createCell(2)
                        .setCellValue(operacionesXNumeroOperacion.size() > 0
                                ? operacionesXNumeroOperacion.get(0).getNombre_cliente()
                                : "");

                 */
                row.createCell(1).setCellValue(registro.getnumoperacion() == null ? "" : registro.getnumoperacion());
                row.createCell(2).setCellValue(registro.getcopropietario() == null ? "" : registro.getcopropietario());
                row.createCell(3).setCellValue(registro.getdevolucion() == null ? "" : registro.getdevolucion());
                row.createCell(4).setCellValue(registro.getfechaendoso() == null ? "" : registro.getfechaendoso());
                row.createCell(5).setCellValue(registro.getfechaenvio1() == null ? "" : registro.getfechaenvio1());
                row.createCell(6).setCellValue(registro.gettipoenvio1() == null ? "" : registro.gettipoenvio1());
                row.createCell(7)
                        .setCellValue(registro.gettiporespuesta1() == null ? "" : registro.gettiporespuesta1());
                row.createCell(8).setCellValue(registro.getfechaenvio2() == null ? "" : registro.getfechaenvio2());
                row.createCell(9).setCellValue(registro.gettipoenvio2() == null ? "" : registro.gettipoenvio2());
                row.createCell(10)
                        .setCellValue(registro.gettiporespuesta2() == null ? "" : registro.gettiporespuesta2());
                row.createCell(11)
                        .setCellValue(registro.getfecharespuesta() == null ? "" : registro.getfecharespuesta());
                row.createCell(12).setCellValue(registro.getestadofinal() == null ? "" : registro.getestadofinal());
                row.createCell(13)
                        .setCellValue(registro.getultimamodificacion() == null ? "" : registro.getultimamodificacion());
                row.createCell(14).setCellValue(registro.getusername() == null ? "" : registro.getusername());
                row.createCell(15).setCellValue(registro.getarea() == null ? "" : registro.getarea());

                row.getCell(1).setCellStyle(tableCellStyle);
                row.getCell(2).setCellStyle(tableCellStyle);
                row.getCell(3).setCellStyle(tableCellStyle);
                row.getCell(4).setCellStyle(tableCellStyle);
                // row.getCell(4).setCellStyle(solicitud.getEsDolares() ? currencyDolarCellStyle
                // : currencySolCellStyle);
                row.getCell(5).setCellStyle(tableCellStyle);
                row.getCell(6).setCellStyle(tableCellStyle);
                row.getCell(7).setCellStyle(tableCellStyle);
                row.getCell(8).setCellStyle(tableCellStyle);
                row.getCell(9).setCellStyle(tableCellStyle);
                row.getCell(10).setCellStyle(tableCellStyle);
                row.getCell(11).setCellStyle(tableCellStyle);
                row.getCell(12).setCellStyle(tableCellStyle);
                row.getCell(13).setCellStyle(tableCellStyle);
                row.getCell(14).setCellStyle(tableCellStyle);
                row.getCell(15).setCellStyle(tableCellStyle);
                // row.getCell(16).setCellStyle(tableCellStyle);

            }

            // Autoajustar columnas
            for (int i = 1; i <= 16; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir en un ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<OperacionDTO> obtenerOperacionesXNumeroOperacion(Integer numeroOperacion) {
        return operacionesAntiguoService.obtenerOperacionesXNumeroOperacion(numeroOperacion);
    }

    public byte[] generateExcelSolicitudAprobadasLiqCostos(String startDate, String endDate, String numOperacion,
            String estado) {
        try (Workbook workbook = new XSSFWorkbook()) {

            // List<LCRSolicitud> solicitudes =
            // lcrSolicitudRepository.findByRangeUpdateDate(startDate, endDate);
            LocalDateTime startDateTime = toStartOfDay(startDate);
            LocalDateTime endDateTime = toEndOfDay(endDate);
            System.out.println("numOperacion"+numOperacion);

            List<FlujoTchnReporteProjection> registros = lcrSolicitudRepository.obtenerDetalleCostos(startDateTime,endDateTime,numOperacion,
                    estado);

            Sheet sheet = workbook.createSheet("Flujo de Aprobaciones TCHN - Costos Reales");

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            Font tableHeaderFont = workbook.createFont();
            tableHeaderFont.setBold(true);
            tableHeaderStyle.setFont(tableHeaderFont);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle tableCellStyle = workbook.createCellStyle();
            tableCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            dateCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo con formato moneda sol peruano
            CellStyle currencySolCellStyle = workbook.createCellStyle();
            currencySolCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"S/\" #,##0.00"));
            currencySolCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Formato dÃ³lar
            CellStyle currencyDolarCellStyle = workbook.createCellStyle();
            currencyDolarCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("\"$\" #,##0.00"));
            currencyDolarCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Crear encabezado
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(1);
            headerCell.setCellValue("Flujo de Aprobaciones TCHN - Costos Reales");
            headerCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));

            // // Detalles del cliente
            int currentRow = 2;

            // Espaciado
            currentRow++;

            // Crear un estilo con bordes
            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);
            borderedStyle.setAlignment(HorizontalAlignment.CENTER); // Centrar texto horizontalmente
            borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Centrar texto verticalmente

            // Encabezados de la tabla
            Row tableHeaderRow = sheet.createRow(currentRow++);
            Cell cell1 = tableHeaderRow.createCell(1);
            cell1.setCellValue("TCHN");
            cell1.setCellStyle(borderedStyle);

            Cell cell2 = tableHeaderRow.createCell(2);
            cell2.setCellValue("FECHA ENDOSO");
            cell2.setCellStyle(borderedStyle);

            Cell cell3 = tableHeaderRow.createCell(3);
            cell3.setCellValue("MONTO SIMULADO");
            cell3.setCellStyle(borderedStyle);

            // saldo a favor
            Cell cell4 = tableHeaderRow.createCell(4);
            cell4.setCellValue("MONTO REAL");
            cell4.setCellStyle(borderedStyle);

            // Banco
            Cell cell5 = tableHeaderRow.createCell(5);
            cell5.setCellValue("MONTO SALDO TOTAL");
            cell5.setCellStyle(borderedStyle);

            // moneda

            Cell cell6 = tableHeaderRow.createCell(6);
            cell6.setCellValue("ESTADO POR AREA");
            cell6.setCellStyle(borderedStyle);

            // Nro de cuenta

            Cell cell7 = tableHeaderRow.createCell(7);
            cell7.setCellValue("USUARIO AREA");
            cell7.setCellStyle(borderedStyle);

            // CCI

            Cell cell8 = tableHeaderRow.createCell(8);
            cell8.setCellValue("AREA");
            cell8.setCellStyle(borderedStyle);
            // fecha aprobada

            Cell cell9 = tableHeaderRow.createCell(9);
            cell9.setCellValue("ESTADO ACTUAL");
            cell9.setCellStyle(borderedStyle);

            // USUARIO ACTUAL

            Cell cell10 = tableHeaderRow.createCell(10);
            cell10.setCellValue("USUARIO ACTUAL");
            cell10.setCellStyle(borderedStyle);

            // FECHA ACTUALIZADA

            Cell cell11 = tableHeaderRow.createCell(11);
            cell11.setCellValue("FECHA ACTUALIZADA");
            cell11.setCellStyle(borderedStyle);

            // OBSERVACIONES

            Cell cell12 = tableHeaderRow.createCell(12);
            cell12.setCellValue("OBSERVACIONES");
            cell12.setCellStyle(borderedStyle);

            // CONCEPTO

            Cell cell13 = tableHeaderRow.createCell(13);
            cell13.setCellValue("CONCEPTO");
            cell13.setCellStyle(borderedStyle);

            // GASTO SIMULADO

            Cell cell14 = tableHeaderRow.createCell(14);
            cell14.setCellValue("GASTO SIMULADO");
            cell14.setCellStyle(borderedStyle);

            // GASTO REAL

            Cell cell15 = tableHeaderRow.createCell(15);
            cell15.setCellValue("GASTO REAL");
            cell15.setCellStyle(borderedStyle);

            // SALDO

            Cell cell16 = tableHeaderRow.createCell(16);
            cell16.setCellValue("SALDO");
            cell16.setCellStyle(borderedStyle);

            // Ajustar ancho de las columnas para mejor visibilidad (opcional)
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);
            sheet.autoSizeColumn(11);
            sheet.autoSizeColumn(12);
            sheet.autoSizeColumn(13);
            sheet.autoSizeColumn(14);
            sheet.autoSizeColumn(15);
            sheet.autoSizeColumn(16);

            // Contenido de la tabla
            int numCuota = 1;
            for (FlujoTchnReporteProjection registro : registros) {
                Row row = sheet.createRow(currentRow++);
                // List<OperacionDTO> operacionesXNumeroOperacion =
                // operacionesAntiguoService.obtenerOperacionesXNumeroOperacion(solicitud.getNumOperacion().intValue());
                // OperacionDTO operacion = operacionesXNumeroOperacion.get(0);
                // String cuenta = solicitud.getCuentaBancaria();
                // String Banco = "";
                // String moneda = "";
                // String nroCuenta = "";
                // String cci = solicitud.getCci();
                // if (cuenta.equals("CUENTA INTERNA")) {
                // Banco = "-";
                // moneda = "-";
                // cci = "-";
                // nroCuenta = "CUENTA INTERNA";
                // }else{
                // String[] parts = cuenta.split(" ");
                // Banco = parts[0];
                // moneda = parts[1];
                // // cuenta pero sin la parts[0] y parts[1] para obtener el nro de cuenta
                // nroCuenta = cuenta.substring(parts[0].length() + parts[1].length() + 1);
                // }
                row.createCell(1).setCellValue(registro.getTCHN());
                row.createCell(2).setCellValue(registro.getFECHA_ENDOSO());
                BigDecimal montoSimulado = registro.getMONTO_SIMULADO() == null ? BigDecimal.ZERO
                        : registro.getMONTO_SIMULADO();
                row.createCell(3).setCellValue(montoSimulado.doubleValue());
                BigDecimal montoReal = registro.getMONTO_REAL() == null ? BigDecimal.ZERO : registro.getMONTO_REAL();
                row.createCell(4).setCellValue(montoReal.doubleValue());
                BigDecimal montoSaldoTotal = registro.getMONTO_SALDO_TOTAL() == null ? BigDecimal.ZERO
                        : registro.getMONTO_SALDO_TOTAL();
                row.createCell(5).setCellValue(montoSaldoTotal.doubleValue());
                row.createCell(6).setCellValue(registro.getESTADO_POR_AREA());
                row.createCell(7).setCellValue(registro.getUSUARIO_AREA());
                row.createCell(8).setCellValue(registro.getAREA());
                row.createCell(9).setCellValue(registro.getESTADO_ACTUAL());
                row.createCell(10).setCellValue(registro.getUSUARIO_ACTUAL());
                row.createCell(11).setCellValue(registro.getFECHA_ACTUALIZADA());
                row.createCell(12).setCellValue(registro.getOBSERVACIONES());
                row.createCell(13).setCellValue(registro.getCONCEPTO());
                BigDecimal gastoSimulado = registro.getGASTO_SIMULADO() == null ? BigDecimal.ZERO
                        : registro.getGASTO_SIMULADO();
                row.createCell(14).setCellValue(gastoSimulado.doubleValue());
                BigDecimal gastoReal = registro.getGASTO_REAL() == null ? BigDecimal.ZERO : registro.getGASTO_REAL();
                row.createCell(15).setCellValue(gastoReal.doubleValue());
                BigDecimal saldo = registro.getSALDO() == null ? BigDecimal.ZERO : registro.getSALDO();
                row.createCell(16).setCellValue(saldo.doubleValue());

                row.getCell(1).setCellStyle(tableCellStyle);
                row.getCell(2).setCellStyle(dateCellStyle);
                row.getCell(3).setCellStyle(tableCellStyle);
                // row.getCell(4).setCellStyle(solicitud.getEsDolares() ? currencyDolarCellStyle
                // : currencySolCellStyle);
                row.getCell(5).setCellStyle(tableCellStyle);
                row.getCell(6).setCellStyle(tableCellStyle);
                row.getCell(7).setCellStyle(tableCellStyle);
                row.getCell(8).setCellStyle(tableCellStyle);
                row.getCell(9).setCellStyle(tableCellStyle);
                row.getCell(10).setCellStyle(tableCellStyle);
                row.getCell(11).setCellStyle(dateCellStyle);
                row.getCell(12).setCellStyle(tableCellStyle);
                row.getCell(13).setCellStyle(tableCellStyle);
                row.getCell(14).setCellStyle(tableCellStyle);
                row.getCell(15).setCellStyle(tableCellStyle);
                row.getCell(16).setCellStyle(tableCellStyle);

            }

            // Autoajustar columnas
            for (int i = 1; i <= 16; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir en un ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
