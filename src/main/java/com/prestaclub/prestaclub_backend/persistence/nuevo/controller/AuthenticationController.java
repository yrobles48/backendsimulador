package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List; 
import java.util.UUID;

import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionIDDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.RestController; 
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
 
import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.ChangePasswordRequest;
import com.prestaclub.prestaclub_backend.dto.ChangePasswordValues;
import com.prestaclub.prestaclub_backend.dto.EmailValues;
import com.prestaclub.prestaclub_backend.dto.GrabarLiquidacionRequest;
import com.prestaclub.prestaclub_backend.dto.JwtResponse;
import com.prestaclub.prestaclub_backend.dto.LcrTotalResponse;
import com.prestaclub.prestaclub_backend.dto.UsuarioResponse;
import com.prestaclub.prestaclub_backend.dto.ValidatePasswordTokenRequest;
import com.prestaclub.prestaclub_backend.dto.ValidatePasswordTokenResponse;
import com.prestaclub.prestaclub_backend.exception.UsuarioNotFoundException;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionDTO;
import com.prestaclub.prestaclub_backend.persistence.antiguo.service.OperacionesAntiguoService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.LcrSolicitudResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitud;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCTCronograma;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LiquidacionCostosTransaccion;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.UsuarioModuloRol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LiquidacionCostosTransaccionRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.IS3Service;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRSolicitudService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LiquidacionCostosRealesService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LiquidacionCostosTransaccionService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.PasswordResetTokenService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioModuloRolService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.ExcelServiceImpl;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.S3ServiceImpl;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.UserDetailsServiceImpl;
import com.prestaclub.prestaclub_backend.security.JwtUtils;
import com.prestaclub.prestaclub_backend.service.EmailService;
 

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;
    @Autowired
    private JwtUtils jwtUtils; 
    @Autowired
    private EmailService emailService;

    @Autowired
    public IS3Service iS3Service;

    @Autowired
    private UsuarioModuloRolService usuarioModuloRolService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ExcelServiceImpl excelService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/generate-token")
    @CrossOrigin("*")
    public ResponseEntity<ApiResponseData<JwtResponse>> generateToken(@RequestBody com.prestaclub.prestaclub_backend.dto.JwtRequest jwtRequest) throws Exception {
         
        try { 
            ApiResponse apiResponse = authenticate(jwtRequest.getUsername(), jwtRequest.getPassword()); 
            if (!apiResponse.isSuccess()) {
                return ResponseEntity.ok(new ApiResponseData(apiResponse.getMessage(), false));
            }else{
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
                String token = this.jwtUtils.generateToken(userDetails);
                return ResponseEntity.ok(new ApiResponseData(new JwtResponse(token),"Login exitoso", true));
            }
        } catch (UsuarioNotFoundException e) { 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseData("Error en la autenticación", false)); 
        } 
    }

    private ApiResponse authenticate(String username, String password) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Login exitoso"); 
        } catch (DisabledException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Usuario Deshabilitado");
            //throw new Exception("Usuario Deshabilitado"+ e.getMessage());
        } catch (BadCredentialsException e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Credenciales inválidas");
            //throw new Exception("Credenciale inválidas"+ e.getMessage());
        }  
        return apiResponse;
    }

    @GetMapping("/actual-usuario")
    public ResponseEntity<ApiResponseData<UsuarioResponse>> obtenerUsuarioActual(Principal principal) { 
        ApiResponseData<UsuarioResponse> apiResponseData = new ApiResponseData<>();
        try {
            Usuario usuario = this.userDetailsService.loadUserByUsername(principal.getName());
            UsuarioResponse usuarioResponse = new UsuarioResponse();
            List<UsuarioModuloRol> usuarioModuloRol = usuarioModuloRolService.findByUsuarioId(usuario.getId());
            //quitar los usuarios de todos los usuarioModuloRol
            usuarioModuloRol.forEach(umRol -> umRol.setUsuario(null));
            usuarioResponse.setUsername(usuario.getUsername());  
            usuarioResponse.setEmail(usuario.getEmail());
            usuarioResponse.setId(usuario.getId());
            usuarioResponse.setUsuarioModuloRol(usuarioModuloRol);
            // usuarioResponse.setRol(usuario.getRol());
            usuarioResponse.setActive(usuario.isActive());
            usuarioResponse.setEmpleado(usuario.getEmpleado());

            apiResponseData.setData(usuarioResponse);
            apiResponseData.setMessage("Usuario actual");
            apiResponseData.setSuccess(true);
            return ResponseEntity.ok().body(apiResponseData);
        } catch (Exception e) {
            e.printStackTrace(); 
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(apiResponseData);
        }
        //return (UsuarioResponse) this.userDetailsService.loadUserByUsername(principal.getName());
    }

     @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody EmailValues emailValues) { 
        try { 
            if (emailValues.getEmail() == null || emailValues.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse("El email es obligatorio", false));
            }    
            UserDetails usuario = userDetailsService.loadUserByEmail(emailValues.getEmail());  
            if (usuario == null) {
                //return ResponseEntity.badRequest().body(new ApiResponse("El Email no a sido encontrado", false));
                return ResponseEntity.ok().body(new ApiResponse("Email no encontrado", false));
            }  
            String token = UUID.randomUUID().toString(); 
            Usuario us = (Usuario) usuario;
            passwordResetTokenService.savePasswordResetToken(us, token);
            emailValues.setJwt(token);
            emailValues.setSubject("Tu enlace de recuperación de contraseña está listo");
            emailValues.setUsername(usuario.getUsername());
            emailService.sendEmailTemplateForgotPassword(emailValues);  
             return ResponseEntity.ok(new ApiResponse("Email enviado", true));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Error en el envio de email", false));
        }
    }

    @PostMapping("/restablecer-contra")
    public ResponseEntity<?> restablecerContra(@RequestBody ChangePasswordValues request) { 
        ValidatePasswordTokenResponse validatePasswordTokenResponse = passwordResetTokenService.validatePasswordToken(request.getToken());
        if(validatePasswordTokenResponse.isValid()){ 
            boolean success = passwordResetTokenService.changePasswordByToken(request.getToken(), request.getNewPassword());
            if (success) {
                return ResponseEntity.ok(new ApiResponse("Contraseña cambiada", true));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Error al cambiar contraseña", false));
            }
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(validatePasswordTokenResponse.getMensaje(), false));
        } 
    }

    @PostMapping("/cambiar-contra")
    public ResponseEntity<?> cambiarContra(@RequestBody ChangePasswordRequest request) throws Exception {
        try { 
            authenticate(request.getUsername(), request.getOldPassword());
            Usuario usuario = this.userDetailsService.loadUserByUsername(request.getUsername());
            passwordResetTokenService.cambiarContra(usuario, request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse("Contraseña cambiada", true));
        } catch (UsuarioNotFoundException e) {
            e.printStackTrace();
            throw new Exception("Credenciales inválidas"); 
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Credenciales inválidas", false));
        }

    }

    @PostMapping("/validate-password-token")
    public ResponseEntity<ValidatePasswordTokenResponse> validatePasswordToken(@Validated @RequestBody ValidatePasswordTokenRequest request) {
        ValidatePasswordTokenResponse validatePasswordTokenResponse = passwordResetTokenService.validatePasswordToken(request.getToken());
        return ResponseEntity.ok(validatePasswordTokenResponse); 
    }

    @GetMapping("/health")
    public ResponseEntity<Integer> postMethodName() {    
        //  EmailValues emailValues = new EmailValues();
        // emailValues.setEmail("epalomino@prestaclub.com");
        // emailValues.setSubject("NUEVA NOTIFICACION - PRESTACLUB");
        // emailValues.setUsername("Eduardo Palomino");
        // emailService.sendEmailNewNotificacion(emailValues);
        // Verifica si el perfil activo es "prod" o "dev"
        //String activeProfile = environment.getActiveProfiles()[0]; 

        // if ("dev".equals(activeProfile)) {
        //     System.out.println("Estas en dev");
        // }  else {
        //     System.out.println("Estas en prod");
        // } 
        // mostrar fecha y hora 

        return ResponseEntity.ok(30);
    }

    @Autowired
    OperacionesAntiguoService operacionesAntiguoService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    S3ServiceImpl s3Service;
    @Autowired
     private LiquidacionCostosTransaccionRepository liquidacionCostosTransaccionRepository;
    @Autowired
    private LiquidacionCostosRealesService liquidacionCostosRealesService;
    @Autowired
    private LCRSolicitudRepository lcrSolicitudRepository;

    @GetMapping("prueba-s3")
    public ResponseEntity<byte[]> generarPDF(@RequestBody GrabarLiquidacionRequest entity) {
        try {
            String inversionistaName = "";
            String inversionistaRuc = ""; 
            String tipoValorizacionName = "";
            String tipoContratoName = "";
            String nombreAsesor = "";
            Long numOperacion =11L;
            LiquidacionCostosTransaccion lct = liquidacionCostosTransaccionRepository.findEsDesembolsoByNumOperacion(numOperacion); 
            List<LcrTotalResponse> listaTotalesLcr = liquidacionCostosRealesService.listaLcrAllByNumOperacion(numOperacion,lct.isEsDolares());
            LCRSolicitud lcrSolicitud = lcrSolicitudRepository.findByNumOperacion(numOperacion);

            List<OperacionDTO> operacionesXNumeroOperacion = operacionesAntiguoService.obtenerOperacionesXNumeroOperacion(numOperacion.intValue());
            if (operacionesXNumeroOperacion.isEmpty()) {
                return null;
            } 
            OperacionDTO operacion = operacionesXNumeroOperacion.get(0);
            //String templateName = "template-lct-cronograma-pagos.html"; // Reemplaza con el nombre de tu template
            switch (lct.getInversionistaId().intValue()) {
                case 1:
                    inversionistaName = "FONDO CAPITAL EMPRENDEDOR";
                    inversionistaRuc = "20544837835";
                case 2:
                    inversionistaName = "FONDO POPULAR 1";
                    inversionistaRuc = "20516816733";
                    break;
                case 3:
                    inversionistaName = "FONDO MYPE";
                    inversionistaRuc = "20519075581";
                    break;
                case 4:
                    inversionistaName = "FONDO PEREZ HIDALGO";
                    inversionistaRuc = "20553718067";
                    break; 
                default:
                    inversionistaName = "-";
                    inversionistaRuc = "-";
                    break;
            }
            BigDecimal cri = listaTotalesLcr.stream().filter(item -> "cri-y-copia-literal".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal bloqueoRegistral = listaTotalesLcr.stream().filter(item -> "bloqueo-registral".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal peritaje = listaTotalesLcr.stream().filter(item -> "peritaje".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal costoInspeccion = listaTotalesLcr.stream().filter(item -> "costo-de-inspeccion".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal totalCRI = cri.add(bloqueoRegistral).add(peritaje).add(costoInspeccion);

            BigDecimal derechosNotariales = listaTotalesLcr.stream().filter(item -> "derechos-notariales".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal derechosRegistrales = listaTotalesLcr.stream().filter(item -> "derechos-registrales".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal asesoriaPrestaclub = listaTotalesLcr.stream().filter(item -> "asesoria-prestaclub".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal pagosDescontadosCheque = derechosNotariales.add(derechosRegistrales).add(asesoriaPrestaclub);

            BigDecimal registralesLevantamiento = listaTotalesLcr.stream().filter(item -> "registrales-de-levantamiento".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal tomaFirmasBiometrico = listaTotalesLcr.stream().filter(item -> "toma-de-firmas-con-lector-biometrico".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal otorgamientoPoder = listaTotalesLcr.stream().filter(item -> "otorgamiento-de-poder".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal chequeGerenciaExtra = listaTotalesLcr.stream().filter(item -> "cheque-de-gerencia-extra".equals(item.getSlug())).map(LcrTotalResponse::getMonto).findFirst().orElse(BigDecimal.ZERO);
            BigDecimal totalCostosAdicionales = registralesLevantamiento.add(tomaFirmasBiometrico).add(otorgamientoPoder).add(chequeGerenciaExtra);

            BigDecimal financiamientoTotal  = totalCRI.add(pagosDescontadosCheque).add(totalCostosAdicionales);

            tipoContratoName = lct.getTipoContratoId().intValue() == 2 ? "TCHN C/ levantamiento" : "TCHN"  ;
            tipoValorizacionName = lct.getTipoValorizacion().intValue() == 1 ? "Tasación" : "-";
            nombreAsesor = usuarioService.findById(8L).getEmpleado().getNombre() ;
            String templateName = "template-lcr-solicitud-aprobada.html"; 
            //String templateName = "template-lct-cronograma-pagos.html"; 
            Context context = new Context(); 
            context.setVariable("lct",lct);  
            context.setVariable("operacion", operacion);
            context.setVariable("inversionistaName", inversionistaName);
            context.setVariable("inversionistaRuc", inversionistaRuc);
            context.setVariable("tipoContratoName", tipoContratoName);
            context.setVariable("tipoValorizacionName", tipoValorizacionName);
            context.setVariable("nombreAsesor", nombreAsesor);
            context.setVariable("cri", cri);
            context.setVariable("bloqueoRegistral", bloqueoRegistral);
            context.setVariable("peritaje", peritaje);
            context.setVariable("costoInspeccion", costoInspeccion);
            context.setVariable("totalCRI", totalCRI);
            context.setVariable("derechosNotariales", derechosNotariales);
            context.setVariable("derechosRegistrales", derechosRegistrales);
            context.setVariable("asesoriaPrestaclub", asesoriaPrestaclub);
            context.setVariable("pagosDescontadosCheque", pagosDescontadosCheque);
            context.setVariable("registralesLevantamiento", registralesLevantamiento);
            context.setVariable("tomaFirmasBiometrico", tomaFirmasBiometrico);
            context.setVariable("otorgamientoPoder", otorgamientoPoder);
            context.setVariable("chequeGerenciaExtra", chequeGerenciaExtra);
            context.setVariable("totalCostosAdicionales", totalCostosAdicionales);
            context.setVariable("financiamientoTotal", financiamientoTotal);
            context.setVariable("saldoFavor",(lct.getTotalCostosTransaccion().subtract(financiamientoTotal)));
            // context.setVariable("cuentaBancaria", lcrSolicitud.getCuentaBancaria() != null ? lcrSolicitud.getCuentaBancaria():"-");
            // context.setVariable("cci", lcrSolicitud.getCci() != null ? lcrSolicitud.getCci():"-");


            String htmlContent = templateEngine.process(templateName, context);  
            // Convertir HTML a PDF usando Flying Saucer con iText5
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "documento.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(byteArrayOutputStream.toByteArray()); 
    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    
} 
