package com.prestaclub.prestaclub_backend.persistence.nuevo.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;

import jakarta.persistence.*;

@Entity
@Table(name = "liquidacion_costos_transacciones")
public class LiquidacionCostosTransaccion{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "num_operacion")
    @JsonProperty("num_operacion")
    private Long numOperacion;

    @Column(name = "nro_firmantes")
    @JsonProperty("nro_firmantes")
    private Long nroFirmantes;

    @Column(name = "interviniente_obligado")
    @JsonProperty("interviniente_obligado")
    private String intervinienteObligado;

    @Column(name = "tipo_contrato_id")
    @JsonProperty("tipo_contrato_id")
    private Long tipoContratoId;

    @Column(name = "nro_cheques_gerencia_extra")
    @JsonProperty("nro_cheques_gerencia_extra")
    private Long nroChequesGerenciaExtra;

    @Column(name = "inversionista_id")
    @JsonProperty("inversionista_id")
    private Long inversionistaId; 

    @Column(name = "prestamo_neto")
    @JsonProperty("prestamo_neto")
    private BigDecimal prestamoNeto; 

    @Column(name = "gravamen_a_levantar")
    @JsonProperty("gravamen_a_levantar")
    private BigDecimal gravamenALevantar;

    @Column(name = "monto_adeudado")
    @JsonProperty("monto_adeudado")
    private BigDecimal montoAdeudado;

    @Column(name = "fecha_adeudado")
    @JsonProperty("fecha_adeudado")
    private LocalDateTime fechaAdeudado;

    @Column(name = "tipo_cambio_peritaje" , precision = 38, scale = 3)
    @JsonProperty("tipo_cambio_peritaje")
    private BigDecimal tipoCambioPeritaje;

    @Column(name = "tipo_valorizacion")
    @JsonProperty("tipo_valorizacion")
    private Long tipoValorizacion;

    @Column(name = "gravamen")
    @JsonProperty("gravamen")
    private BigDecimal gravamen;


    @Column(name = "certificado_registral_inmobiliario")
    @JsonProperty("certificado_registral_inmobiliario")
    private BigDecimal certificadoRegistralInmobiliario;

    @Column(name = "bloqueo_registral")
    @JsonProperty("bloqueo_registral")
    private BigDecimal bloqueoRegistral;

    @Column(name = "peritaje")
    @JsonProperty("peritaje")
    private BigDecimal peritaje;

    @Column(name = "costo_inspeccion")
    @JsonProperty("costo_inspeccion")
    private BigDecimal costoInspeccion;

    @Column(name = "cri_bloqueo_peritaje")
    @JsonProperty("cri_bloqueo_peritaje")
    private BigDecimal criBloqueoPeritaje; 

    @Column(name = "derechos_notariales")
    @JsonProperty("derechos_notariales")
    private BigDecimal derechosNotariales;

    @Column(name = "derechos_registrales")
    @JsonProperty("derechos_registrales")
    private BigDecimal derechosRegistrales;

    @Column(name = "asesoria_prestaclub")
    @JsonProperty("asesoria_prestaclub")
    private BigDecimal asesoriaPrestaclub;
    
    @Column(name = "cargo_pj")
    @JsonProperty("cargo_pj")
    private BigDecimal cargoPj;

    @Column(name = "pagos_descontados_cheque")
    @JsonProperty("pagos_descontados_cheque")
    private BigDecimal pagosDescontadosCheque;

    @Column(name = "registrales_levantamiento")
    @JsonProperty("registrales_levantamiento")
    private BigDecimal registralesLevantamiento;

    @Column(name = "toma_firmas_biometrico")
    @JsonProperty("toma_firmas_biometrico")
    private BigDecimal tomaFirmasBiometrico;

    @Column(name = "otorgamiento_poder")
    @JsonProperty("otorgamiento_poder")
    private BigDecimal otorgamientoPoder;

    @Column(name = "cheque_gerencia_extra")
    @JsonProperty("cheque_gerencia_extra")
    private BigDecimal chequeGerenciaExtra;

    @Column(name = "costos_adicionales")
    @JsonProperty("costos_adicionales")
    private BigDecimal costosAdicionales;

    @Column(name = "pagado_cliente")
    @JsonProperty("pagado_cliente")
    private BigDecimal pagadoCliente;

    @Column(name = "costos_transaccion")
    @JsonProperty("costos_transaccion")
    private BigDecimal totalCostosTransaccion;

    @Column(name = "financiamiento_total")
    @JsonProperty("financiamiento_total")
    private BigDecimal financiamientoTotal;

    @Column(name = "tea")
    @JsonProperty("tea")
    private BigDecimal tea;

    @Column(name = "nro_cuotas")
    @JsonProperty("nro_cuotas")
    private Long nroCuotas;

    @Column(name = "tem")
    @JsonProperty("tem")
    private BigDecimal tem;

    @Column(name = "tasa_interes_moratoria" , precision = 38, scale = 3)
    @JsonProperty("tasa_interes_moratoria")
    private BigDecimal tasaInteresMoratoria;

    @Column(name = "total_interes")
    @JsonProperty("total_interes")
    private BigDecimal totalInteres;

    @Column(name = "interes_promedio_anual")
    @JsonProperty("interes_promedio_anual")
    private BigDecimal interesPromedioAnual;

    @Column(name = "porcentaje_deuda")
    @JsonProperty("porcentaje_deuda")
    private BigDecimal porcentajeDeuda;

    @Column(name = "es_final")
    @JsonProperty("es_final")
    private boolean esFinal;

    @Column(name = "es_desembolso")
    @JsonProperty("es_desembolso")
    private boolean esDesembolso; 

    @Column(name = "calificacion_hipoteca")
    @JsonProperty("calificacion_hipoteca")
    private BigDecimal calificacionHipoteca;

    @Column(name = "emision_tchn")
    @JsonProperty("emision_tchn")
    private BigDecimal emisionTchn;

    @Column(name = "expedicion_tchn")
    @JsonProperty("expedicion_tchn")
    private BigDecimal expedicionTchn;

    @Column(name = "calificacion_poderes")
    @JsonProperty("calificacion_poderes")
    private BigDecimal calificacionPoderes; 

    @Column(name = "propietario_id")
    @JsonProperty("propietario_id")
    private Long propietarioId;

    @Column(name = "solicitante_id")
    @JsonProperty("solicitante_id")
    private Long solicitanteId;

    @Column(name = "es_tercero")
    @JsonProperty("es_tercero")
    private boolean esTercero;

    @Column(name = "nombre_solicitante_cheque")
    @JsonProperty("nombre_solicitante_cheque")
    private String nombreSolicitanteCheque;

    @Column(name = "numero_documento_solicitante_cheque")
    @JsonProperty("numero_documento_solicitante_cheque")
    private String numeroDocumentoSolicitanteCheque;

    @Transient
    @JsonProperty("es_final_operacion")
    private boolean esFinalOperacion;

    @Column(name = "updated_by")
    @JsonProperty("updated_by")
    private Long updatedBy;

    @Column(name = "updated_date")
    @JsonProperty("updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "created_by")
    @JsonProperty("created_by")
    private Long createdBy;

    @Transient
    @JsonProperty("created_by_user")
    private UsuarioMantenimientoDTO createdByName;

    @Column(name = "created_date")
    @JsonProperty("created_date")
    private LocalDateTime createdDate;

    @Column(name = "deleted_by")
    @JsonProperty("deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_date")
    @JsonProperty("deleted_date")
    private LocalDateTime deletedDate;

    @Column(name = "es_dolares")
    @JsonProperty("es_dolares")
    private boolean esDolares;

    @Column(name = "tipo_cambio" , precision = 38, scale = 3)
    @JsonProperty("tipo_cambio")
    private BigDecimal tipoCambio;

    @Column(name = "fondo_id")
    @JsonProperty("fondo_id")
    private Long fondoId;

    @Column(name = "asesor_id")
    @JsonProperty("asesor_id")
    private Long asesorId;

    @Column(name = "es_autocalculo"  , nullable = false , columnDefinition = "boolean default true" )
    @JsonProperty("es_autocalculo")
    private boolean esAutocalculo;

    @Transient
    @JsonProperty("asesor_user")
    private UsuarioMantenimientoDTO asesorUser; ;

    @Transient
    @JsonProperty("files")
    private List<LCTFiles> files;


    public LiquidacionCostosTransaccion() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Long getNumOperacion() {
        return numOperacion;
    }


    public void setNumOperacion(Long numOperacion) {
        this.numOperacion = numOperacion;
    }


    public Long getNroFirmantes() {
        return nroFirmantes;
    }


    public void setNroFirmantes(Long nroFirmantes) {
        this.nroFirmantes = nroFirmantes;
    }


    public Long getTipoContratoId() {
        return tipoContratoId;
    }


    public void setTipoContratoId(Long tipoContratoId) {
        this.tipoContratoId = tipoContratoId;
    }


    public Long getNroChequesGerenciaExtra() {
        return nroChequesGerenciaExtra;
    }


    public void setNroChequesGerenciaExtra(Long nroChequesGerenciaExtra) {
        this.nroChequesGerenciaExtra = nroChequesGerenciaExtra;
    }


    public Long getInversionistaId() {
        return inversionistaId;
    }


    public void setInversionistaId(Long inversionistaId) {
        this.inversionistaId = inversionistaId;
    }


    public BigDecimal getPrestamoNeto() {
        return prestamoNeto;
    }


    public void setPrestamoNeto(BigDecimal prestamoNeto) {
        this.prestamoNeto = prestamoNeto;
    }


    public BigDecimal getGravamenALevantar() {
        return gravamenALevantar;
    }


    public void setGravamenALevantar(BigDecimal gravamenALevantar) {
        this.gravamenALevantar = gravamenALevantar;
    }


    public BigDecimal getMontoAdeudado() {
        return montoAdeudado;
    }


    public void setMontoAdeudado(BigDecimal montoAdeudado) {
        this.montoAdeudado = montoAdeudado;
    }


    public LocalDateTime getFechaAdeudado() {
        return fechaAdeudado;
    }


    public void setFechaAdeudado(LocalDateTime fechaAdeudado) {
        this.fechaAdeudado = fechaAdeudado;
    }


    public BigDecimal getTipoCambioPeritaje() {
        return tipoCambioPeritaje;
    }


    public void setTipoCambioPeritaje(BigDecimal tipoCambioPeritaje) {
        this.tipoCambioPeritaje = tipoCambioPeritaje;
    }


    public Long getTipoValorizacion() {
        return tipoValorizacion;
    }


    public void setTipoValorizacion(Long tipoValorizacion) {
        this.tipoValorizacion = tipoValorizacion;
    }


    public BigDecimal getCertificadoRegistralInmobiliario() {
        return certificadoRegistralInmobiliario;
    }


    public void setCertificadoRegistralInmobiliario(BigDecimal certificadoRegistralInmobiliario) {
        this.certificadoRegistralInmobiliario = certificadoRegistralInmobiliario;
    }


    public BigDecimal getBloqueoRegistral() {
        return bloqueoRegistral;
    }


    public void setBloqueoRegistral(BigDecimal bloqueoRegistral) {
        this.bloqueoRegistral = bloqueoRegistral;
    }


    public BigDecimal getPeritaje() {
        return peritaje;
    }


    public void setPeritaje(BigDecimal peritaje) {
        this.peritaje = peritaje;
    }


    public BigDecimal getCostoInspeccion() {
        return costoInspeccion;
    }


    public void setCostoInspeccion(BigDecimal costoInspeccion) {
        this.costoInspeccion = costoInspeccion;
    }


    public BigDecimal getCriBloqueoPeritaje() {
        return criBloqueoPeritaje;
    }


    public void setCriBloqueoPeritaje(BigDecimal criBloqueoPeritaje) {
        this.criBloqueoPeritaje = criBloqueoPeritaje;
    }


    public BigDecimal getDerechosNotariales() {
        return derechosNotariales;
    }


    public void setDerechosNotariales(BigDecimal derechosNotariales) {
        this.derechosNotariales = derechosNotariales;
    }


    public BigDecimal getDerechosRegistrales() {
        return derechosRegistrales;
    }


    public void setDerechosRegistrales(BigDecimal derechosRegistrales) {
        this.derechosRegistrales = derechosRegistrales;
    }


    public BigDecimal getAsesoriaPrestaclub() {
        return asesoriaPrestaclub;
    }


    public void setAsesoriaPrestaclub(BigDecimal asesoriaPrestaclub) {
        this.asesoriaPrestaclub = asesoriaPrestaclub;
    }


    public BigDecimal getPagosDescontadosCheque() {
        return pagosDescontadosCheque;
    }


    public void setPagosDescontadosCheque(BigDecimal pagosDescontadosCheque) {
        this.pagosDescontadosCheque = pagosDescontadosCheque;
    }


    public BigDecimal getRegistralesLevantamiento() {
        return registralesLevantamiento;
    }


    public void setRegistralesLevantamiento(BigDecimal registralesLevantamiento) {
        this.registralesLevantamiento = registralesLevantamiento;
    }


    public BigDecimal getTomaFirmasBiometrico() {
        return tomaFirmasBiometrico;
    }


    public void setTomaFirmasBiometrico(BigDecimal tomaFirmasBiometrico) {
        this.tomaFirmasBiometrico = tomaFirmasBiometrico;
    }


    public BigDecimal getOtorgamientoPoder() {
        return otorgamientoPoder;
    }


    public void setOtorgamientoPoder(BigDecimal otorgamientoPoder) {
        this.otorgamientoPoder = otorgamientoPoder;
    }


    public BigDecimal getChequeGerenciaExtra() {
        return chequeGerenciaExtra;
    }


    public void setChequeGerenciaExtra(BigDecimal chequeGerenciaExtra) {
        this.chequeGerenciaExtra = chequeGerenciaExtra;
    }


    public BigDecimal getCostosAdicionales() {
        return costosAdicionales;
    }


    public void setCostosAdicionales(BigDecimal costosAdicionales) {
        this.costosAdicionales = costosAdicionales;
    }


    public BigDecimal getPagadoCliente() {
        return pagadoCliente;
    }


    public void setPagadoCliente(BigDecimal pagadoCliente) {
        this.pagadoCliente = pagadoCliente;
    }


    public BigDecimal getTotalCostosTransaccion() {
        return totalCostosTransaccion;
    }


    public void setTotalCostosTransaccion(BigDecimal totalCostosTransaccion) {
        this.totalCostosTransaccion = totalCostosTransaccion;
    }


    public BigDecimal getFinanciamientoTotal() {
        return financiamientoTotal;
    }


    public void setFinanciamientoTotal(BigDecimal financiamientoTotal) {
        this.financiamientoTotal = financiamientoTotal;
    }


    public BigDecimal getTea() {
        return tea;
    }


    public void setTea(BigDecimal tea) {
        this.tea = tea;
    }


    public Long getNroCuotas() {
        return nroCuotas;
    }


    public void setNroCuotas(Long nroCuotas) {
        this.nroCuotas = nroCuotas;
    }


    public BigDecimal getTem() {
        return tem;
    }


    public void setTem(BigDecimal tem) {
        this.tem = tem;
    }


    public BigDecimal getTasaInteresMoratoria() {
        return tasaInteresMoratoria;
    }


    public void setTasaInteresMoratoria(BigDecimal tasaInteresMoratoria) {
        this.tasaInteresMoratoria = tasaInteresMoratoria;
    }


    public BigDecimal getTotalInteres() {
        return totalInteres;
    }


    public void setTotalInteres(BigDecimal totalInteres) {
        this.totalInteres = totalInteres;
    }


    public BigDecimal getInteresPromedioAnual() {
        return interesPromedioAnual;
    }


    public void setInteresPromedioAnual(BigDecimal interesPromedioAnual) {
        this.interesPromedioAnual = interesPromedioAnual;
    }


    public BigDecimal getPorcentajeDeuda() { 
        return porcentajeDeuda;
    }


    public void setPorcentajeDeuda(BigDecimal porcentajeDeuda) {
        this.porcentajeDeuda = porcentajeDeuda;
    }


    public Long getUpdatedBy() {
        return updatedBy;
    }


    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }


    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }


    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }


    public Long getCreatedBy() {
        return createdBy;
    }


    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }


    public LocalDateTime getCreatedDate() {
        return createdDate;
    }


    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }


    public Long getDeletedBy() {
        return deletedBy;
    }


    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
    }


    public LocalDateTime getDeletedDate() {
        return deletedDate;
    }


    public void setDeletedDate(LocalDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }


    public UsuarioMantenimientoDTO getCreatedByName() {
        return createdByName;
    }


    public void setCreatedByName(UsuarioMantenimientoDTO createdByName) {
        this.createdByName = createdByName;
    }


    public List<LCTFiles> getFiles() {
        return files;
    }


    public void setFiles(List<LCTFiles> files) {
        this.files = files;
    }


    public boolean isEsFinal() {
        return esFinal;
    }


    public void setEsFinal(boolean esFinal) {
        this.esFinal = esFinal;
    }


    public boolean isEsFinalOperacion() {
        return esFinalOperacion;
    }


    public void setEsFinalOperacion(boolean esFinalOperacion) {
        this.esFinalOperacion = esFinalOperacion;
    }


    public BigDecimal getCargoPj() {
        return cargoPj;
    }


    public void setCargoPj(BigDecimal cargoPj) {
        this.cargoPj = cargoPj;
    }


    public BigDecimal getGravamen() {
        return gravamen;
    }


    public void setGravamen(BigDecimal gravamen) {
        this.gravamen = gravamen;
    }


    public boolean isEsDesembolso() {
        return esDesembolso;
    }


    public void setEsDesembolso(boolean esDesembolso) {
        this.esDesembolso = esDesembolso;
    }


    public BigDecimal getCalificacionHipoteca() {
        return calificacionHipoteca;
    }


    public void setCalificacionHipoteca(BigDecimal calificacionHipoteca) {
        this.calificacionHipoteca = calificacionHipoteca;
    }


    public BigDecimal getEmisionTchn() {
        return emisionTchn;
    }


    public void setEmisionTchn(BigDecimal emisionTchn) {
        this.emisionTchn = emisionTchn;
    }


    public BigDecimal getExpedicionTchn() {
        return expedicionTchn;
    }


    public void setExpedicionTchn(BigDecimal expedicionTchn) {
        this.expedicionTchn = expedicionTchn;
    }


    public BigDecimal getCalificacionPoderes() {
        return calificacionPoderes;
    }


    public void setCalificacionPoderes(BigDecimal calificacionPoderes) {
        this.calificacionPoderes = calificacionPoderes;
    }


    public Long getPropietarioId() {
        return propietarioId;
    }


    public void setPropietarioId(Long propietarioId) {
        this.propietarioId = propietarioId;
    }


    public Long getSolicitanteId() {
        return solicitanteId;
    }


    public void setSolicitanteId(Long solicitanteId) {
        this.solicitanteId = solicitanteId;
    }


    public boolean isEsTercero() {
        return esTercero;
    }


    public void setEsTercero(boolean esTercero) {
        this.esTercero = esTercero;
    }


    public String getNombreSolicitanteCheque() {
        return nombreSolicitanteCheque;
    }


    public void setNombreSolicitanteCheque(String nombreSolicitanteCheque) {
        this.nombreSolicitanteCheque = nombreSolicitanteCheque;
    }


    public String getNumeroDocumentoSolicitanteCheque() {
        return numeroDocumentoSolicitanteCheque;
    }


    public void setNumeroDocumentoSolicitanteCheque(String numeroDocumentoSolicitanteCheque) {
        this.numeroDocumentoSolicitanteCheque = numeroDocumentoSolicitanteCheque;
    }


    public String getIntervinienteObligado() {
        return intervinienteObligado;
    }


    public void setIntervinienteObligado(String intervinienteObligado) {
        this.intervinienteObligado = intervinienteObligado;
    }


    public boolean isEsDolares() {
        return esDolares;
    }


    public void setEsDolares(boolean esDolares) {
        this.esDolares = esDolares;
    }


    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }


    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }


    public Long getFondoId() {
        return fondoId;
    }


    public void setFondoId(Long fondoId) {
        this.fondoId = fondoId;
    }


    public Long getAsesorId() {
        return asesorId;
    }


    public void setAsesorId(Long asesorId) {
        this.asesorId = asesorId;
    }


    public UsuarioMantenimientoDTO getAsesorUser() {
        return asesorUser;
    }


    public void setAsesorUser(UsuarioMantenimientoDTO asesorUser) {
        this.asesorUser = asesorUser;
    }

    public boolean isEsAutocalculo() {
        return esAutocalculo;
    }


    public void setEsAutocalculo(boolean esAutocalculo) {
        this.esAutocalculo = esAutocalculo;
    }
 
}
