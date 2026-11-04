package com.prestaclub.prestaclub_backend.persistence.nuevo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface FlujoTchnReporteProjection {
String getTCHN();
    LocalDate getFECHA_ENDOSO();
    BigDecimal getMONTO_SIMULADO();
    BigDecimal getMONTO_REAL();
    BigDecimal getMONTO_SALDO_TOTAL();
    String getESTADO_POR_AREA();
    String getUSUARIO_AREA();
    String getAREA();
    String getESTADO_ACTUAL();
    String getUSUARIO_ACTUAL();
    LocalDateTime getFECHA_ACTUALIZADA();
    String getOBSERVACIONES();
    String getCONCEPTO();
    BigDecimal getGASTO_SIMULADO();
    BigDecimal getGASTO_REAL();
    BigDecimal getSALDO();
}
