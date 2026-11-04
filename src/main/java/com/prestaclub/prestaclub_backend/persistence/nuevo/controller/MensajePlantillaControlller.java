package com.prestaclub.prestaclub_backend.persistence.nuevo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.*;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import com.prestaclub.prestaclub_backend.dto.OperacionRequest;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.OperacionIDDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.LCRReembolsoService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl.S3ServiceImpl;
import jakarta.mail.*;
import jakarta.mail.search.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ApiResponseData;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.MensajePlantillaService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/nuevo/mensaje-plantilla")
// @CrossOrigin("*")
public class MensajePlantillaControlller {

    @Value("${spring.mail.username}")
    private String userMail;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${correo}")
    private String userMailGestion;

    @Value("${password}")
    private String passwordGestion;


    @Autowired
    private LCRReembolsoService lcrReembolsoService;
    @Autowired
    private MensajePlantillaService liquidacionCostosRealesService;

    @Autowired
    private MensajePlantillaService mensajePlantillaService;

    @Autowired
    S3ServiceImpl s3Service;

    @PostMapping("/lista")
    public ResponseEntity<ApiResponseData<List<MensajePlantilla>>> listaByTipoSlug(
            @RequestBody Map<String, ?> requestBody) {
        try {
            String slug = requestBody.get("slug") != null ? requestBody.get("slug").toString() : "";

            ApiResponseData<List<MensajePlantilla>> l = liquidacionCostosRealesService.getListaByTipoSlug(slug);
            return ResponseEntity.ok(l);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/asignar")
    public ResponseEntity<ApiResponse> asignar(@RequestBody Map<String, ?> requestBody) {
        try {
            Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()) : null;
            Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString())
                    : null;
            ApiResponse apiResponse = mensajePlantillaService.asignar(id, userId);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/actualizar")
    public ResponseEntity<ApiResponse> actualizar(@RequestBody Map<String, ?> requestBody) {
        try {
            Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()) : null;
            Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString())
                    : null;
            String subject = requestBody.get("subject") != null ? requestBody.get("subject").toString() : null;
            String contenido = requestBody.get("contenido") != null ? requestBody.get("contenido").toString() : null;
            ApiResponse apiResponse = mensajePlantillaService.actualizarPlantilla(id, userId, subject, contenido);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/eliminar")
    public ResponseEntity<ApiResponse> eliminar(@RequestBody Map<String, ?> requestBody) {
        try {
            Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()) : null;
            ApiResponse apiResponse = mensajePlantillaService.eliminarPlantilla(id);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<ApiResponse> crear(@RequestBody Map<String, ?> requestBody) {
        try {
            Long userId = requestBody.get("userId") != null ? Long.parseLong(requestBody.get("userId").toString())
                    : null;
            String subject = requestBody.get("subject") != null ? requestBody.get("subject").toString() : null;
            String contenido = requestBody.get("contenido") != null ? requestBody.get("contenido").toString() : null;
            String slug = requestBody.get("slug") != null ? requestBody.get("slug").toString() : null;
            String nombre = requestBody.get("nombre") != null ? requestBody.get("nombre").toString() : null;
            ApiResponse apiResponse = mensajePlantillaService.crearPlantilla(userId, subject, contenido, nombre, slug);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/consultar")
    public ResponseEntity<ApiResponseData<MensajePlantilla>> consultar(@RequestBody Map<String, ?> requestBody) {
        ApiResponseData<MensajePlantilla> apiResponseData = new ApiResponseData<>();
        try {
            String slug = requestBody.get("slug") != null ? requestBody.get("slug").toString() : null;
            Long nroOperacion = requestBody.get("nroOperacion") != null
                    ? Long.parseLong(requestBody.get("nroOperacion").toString())
                    : null;
            System.out.println("slug" + slug);
            System.out.println("nroOperacion" + nroOperacion);
            MensajePlantilla mensajePlantilla = mensajePlantillaService.consultarPlantilla(slug, nroOperacion);
            apiResponseData.setData(mensajePlantilla);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK");
            return ResponseEntity.ok(apiResponseData);
        } catch (Exception e) {
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(apiResponseData);
        }
    }

    @PostMapping("/consultarenviar")
    public ResponseEntity<ApiResponseData<RptMensaje>> consultarenviar(@RequestBody Map<String, ?> requestBody) {
        ApiResponseData<RptMensaje> apiResponseData = new ApiResponseData<>();
        try {
            Long nroOperacion = requestBody.get("nroOperacion") != null
                    ? Long.parseLong(requestBody.get("nroOperacion").toString())
                    : null;
            RptMensaje rptMensaje = mensajePlantillaService.listaRptMesaje(nroOperacion);
            apiResponseData.setData(rptMensaje);
            apiResponseData.setSuccess(true);
            apiResponseData.setMessage("OK");
            return ResponseEntity.ok(apiResponseData);
        } catch (Exception e) {
            apiResponseData.setSuccess(false);
            apiResponseData.setMessage("no hay información");
            return ResponseEntity.badRequest().body(apiResponseData);
        }
    }

    @PostMapping("/reenviarcorreo")
    public ResponseEntity<ApiResponse> reenviarCorreo(@RequestBody Map<String, ?> requestBody) {
        ApiResponseData<RptMensaje> apiResponseData = new ApiResponseData<>();
        try {
            System.out.println("rptMensaje");
            String asunto = requestBody.get("asunto") != null ? requestBody.get("asunto").toString() : null;
            String contenido = requestBody.get("contenido") != null ? requestBody.get("contenido").toString() : null;
            String correo = requestBody.get("correo") != null ? requestBody.get("correo").toString() : null;
            Long nroOperacion = requestBody.get("num_operacion") != null
                    ? Long.parseLong(requestBody.get("num_operacion").toString())
                    : null;
            Long modify_by = requestBody.get("modify_by") != null
                    ? Long.parseLong(requestBody.get("modify_by").toString())
                    : Long.parseLong(requestBody.get("UserId").toString());

            // RptMensaje rptMensaje = mensajePlantillaService.listaRptMesaje(nroOperacion);
            ApiResponse apiMSG = sendFromGMail(userMailGestion, passwordGestion, correo,"Rectificacion - " + asunto, contenido);
            if (apiMSG.isSuccess()) {
                mensajePlantillaService.enviaNotificacion(List.of("administracion"),"Rectificacion - " + asunto, asunto, contenido, 2L, modify_by, correo);
            }
            Double nsaldo = 0.0d;
            mensajePlantillaService.rectifi_rptCorreo(nroOperacion);

            System.out.println("actualizo" + nroOperacion);
            return ResponseEntity.ok(apiMSG);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/enviarcorreo")
    public ResponseEntity<ApiResponse> enviarCorreo(@RequestBody Map<String, ?> requestBody) {
        try {
            Long idReembolso = requestBody.get("reembolsoId") != null
                    ? Long.parseLong(requestBody.get("reembolsoId").toString())
                    : null;
            String asunto = requestBody.get("asunto") != null ? requestBody.get("asunto").toString() : null;
            String contenido = requestBody.get("contenido") != null ? requestBody.get("contenido").toString() : null;
            String correo = requestBody.get("correo") != null ? requestBody.get("correo").toString() : null;
            Long nroOperacion = requestBody.get("nroOperacion") != null
                    ? Long.parseLong(requestBody.get("nroOperacion").toString())
                    : null;
            Long UserId = requestBody.get("UserId") != null ? Long.parseLong(requestBody.get("UserId").toString())
                    : null;
            Boolean dolares = requestBody.get("esDolares") != null
                    ? Boolean.parseBoolean(requestBody.get("esDolares").toString())
                    : null;
            Double nsaldo = requestBody.get("nsaldo") != null ? Double.parseDouble(requestBody.get("nsaldo").toString())
                    : null;
            String documento = requestBody.get("documento") != null ? requestBody.get("documento").toString() : null;

            ApiResponse apiResponse = mensajePlantillaService.ins_rptMensaje(idReembolso, asunto, contenido, correo,
                    nroOperacion, UserId, dolares, nsaldo, "", documento);
            ApiResponse apiMSG = sendFromGMail(userMailGestion, passwordGestion, correo, "PRIMER ENVIO - "+asunto + " " +nroOperacion + "( "+ correo+ " ) ", contenido);
            System.out.println("apiMSG" + apiMSG.isSuccess());
            if (apiMSG.isSuccess()) {
                mensajePlantillaService.enviaNotificacion(List.of("administracion"), "PRIMER ENVIO - " + asunto + " " +nroOperacion + "( "+ correo+ " ) ", "PRIMER ENVIO - " + asunto  + " " +nroOperacion + " ("+ correo+ " )", contenido, 2L, UserId, correo);
            }
            mensajePlantillaService.act_rptCorreo(nroOperacion, apiMSG.isSuccess(), apiMSG.getMessage(), UserId,
                    nsaldo);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }

    }

    @PostMapping("/registrarsms")
    public ResponseEntity<ApiResponse> registrarSMS(@RequestBody Map<String, ?> requestBody) {
        try {
            Long idReembolso = requestBody.get("reembolsoId") != null
                    ? Long.parseLong(requestBody.get("reembolsoId").toString())
                    : null;
            String asunto = requestBody.get("asunto") != null ? requestBody.get("asunto").toString() : null;
            String contenido = requestBody.get("contenido") != null ? requestBody.get("contenido").toString() : null;
            String telefono = requestBody.get("telefono") != null ? requestBody.get("telefono").toString() : null;
            Long nroOperacion = requestBody.get("nroOperacion") != null
                    ? Long.parseLong(requestBody.get("nroOperacion").toString())
                    : null;
            Long UserId = requestBody.get("UserId") != null ? Long.parseLong(requestBody.get("UserId").toString())
                    : null;
            String documento = requestBody.get("documento") != null ? requestBody.get("documento").toString() : null;

            System.out.println("asunto" + asunto);
            System.out.println("contenido" + contenido);
            String correo = "";
            Boolean dolares = requestBody.get("esDolares") != null
                    ? Boolean.parseBoolean(requestBody.get("esDolares").toString())
                    : null;
            Double nsaldo = requestBody.get("nsaldo") != null ? Double.parseDouble(requestBody.get("nsaldo").toString())
                    : null;
            mensajePlantillaService.enviaNotificacion(List.of("administracion"),asunto + " " +nroOperacion + "( "+ telefono+ " ) ",asunto+ " " +nroOperacion + "( "+ telefono+ " ) ",contenido,2L,UserId,"yrobles@popular-safi.pe");

            ApiResponse apiResponse = mensajePlantillaService.ins_rptMensaje(idReembolso, asunto, contenido, correo,
                    nroOperacion, UserId, dolares, nsaldo, telefono, documento);
            mensajePlantillaService.act_rptCorreo(nroOperacion, true, "primer envio", UserId, nsaldo);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }

    }

    @PostMapping("/reenviosms")
    public ResponseEntity<ApiResponse> reenviosms(@RequestBody Map<String, ?> requestBody) {
        try {
            String asunto = requestBody.get("asunto") != null ? requestBody.get("asunto").toString() : null;
            String contenido = requestBody.get("contenido") != null ? requestBody.get("contenido").toString() : null;
            String telefono = requestBody.get("telefono") != null ? requestBody.get("telefono").toString() : null;
            Long nroOperacion = requestBody.get("nroOperacion") != null
                    ? Long.parseLong(requestBody.get("nroOperacion").toString())
                    : null;
            Long UserId = requestBody.get("UserId") != null ? Long.parseLong(requestBody.get("UserId").toString())
                    : null;
            Boolean dolares = requestBody.get("esDolares") != null
                    ? Boolean.parseBoolean(requestBody.get("esDolares").toString())
                    : null;
            Double nsaldo = requestBody.get("nsaldo") != null ? Double.parseDouble(requestBody.get("nsaldo").toString())
                    : null;
            ;
            String correo = "";
            // ApiResponse apiResponse =
            // mensajePlantillaService.ins_rptMensaje(asunto,contenido,correo,nroOperacion,UserId,dolares,nsaldo);
            ApiResponse apiResponse = mensajePlantillaService.act_rptCorreo(nroOperacion, true, "primer envio", UserId,
                    nsaldo);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }

    }


    @PostMapping("/actenvcorreo")
    public ResponseEntity<ApiResponse> actualizarenvCorreo(@RequestBody Map<String, ?> requestBody) {
        try {
            Long nroOperacion = requestBody.get("num_operacion") != null
                    ? Long.parseLong(requestBody.get("num_operacion").toString())
                    : null;
            String ctabancaria = requestBody.get("cuentabancaria") != null
                    ? requestBody.get("cuentabancaria").toString()
                    : null;
            String coppro = requestBody.get("copropietario") != null ? requestBody.get("copropietario").toString()
                    : null;
            String adicion = requestBody.get("datosadicional") != null ? requestBody.get("datosadicional").toString()
                    : null;
            String lsbanco = requestBody.get("banco") != null ? requestBody.get("banco").toString() : null;
            String correo = requestBody.get("correo") != null ? requestBody.get("correo").toString() : null;
            Long lsestado = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()) : 0;
            Long modify = requestBody.get("modify_by") != null ? Long.parseLong(requestBody.get("modify_by").toString())
                    : 0;

            if (lsestado==3) {
                lcrReembolsoService.actualizaxReembolso(nroOperacion, lsestado);
            }else{
                lcrReembolsoService.actualizaReembolso(nroOperacion, lsestado);
            }
            ApiResponse apiResponse = mensajePlantillaService.act_rptMensaje(nroOperacion, lsbanco, ctabancaria, coppro,
                    correo, adicion, modify);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }



    @PostMapping("/actfechadevol")
    public ResponseEntity<ApiResponse> actfechadevolucion(@RequestBody Map<String, ?> requestBody) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Ajusta el formato si es necesario
            Object fdevolucionObj = requestBody.get("fdevolucion");
            Date fecha = null;
            if (fdevolucionObj != null) {
                try {
                    fecha = sdf.parse(fdevolucionObj.toString());
                } catch (ParseException e) {
                    // Manejo de error si la fecha es inválida
                    e.printStackTrace();
                }
            }
            Long UserId = requestBody.get("modify_by") != null ? Long.parseLong(requestBody.get("modify_by").toString())
                    : null;
            Long nroOperacion = requestBody.get("num_operacion") != null
                    ? Long.parseLong(requestBody.get("num_operacion").toString())
                    : null;
            ApiResponse apiResponse = mensajePlantillaService.actualizaFDevol(nroOperacion, fecha, UserId);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/actcorreo")
    public ResponseEntity<ApiResponse> actcorreos(@RequestBody Map<String, ?> requestBody) {
        try {
            System.out.println("actcorreos");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Ajusta el formato si es necesario
            String correo = requestBody.get("correo") != null ? requestBody.get("correo").toString() : null;
            Long UserId = requestBody.get("modify_by") != null ? Long.parseLong(requestBody.get("modify_by").toString())
                    : null;
            Long nroOperacion = requestBody.get("num_operacion") != null
                    ? Long.parseLong(requestBody.get("num_operacion").toString())
                    : null;
            // lcrReembolsoService.actualizaReembolso(nroOperacion,lsestado);
            ApiResponse apiResponse = mensajePlantillaService.actualizaCorreo(nroOperacion, correo, UserId);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    @PostMapping("/adjuntocorreo")
    public ResponseEntity<ApiResponse> adjuntocorreo(@RequestBody Map<String, ?> requestBody) {
        ApiResponseData<RptMensaje> apiResponseData = new ApiResponseData<>();
        try {
            String asunto = requestBody.get("asunto") != null ? requestBody.get("asunto").toString() : null;
            String contenido = requestBody.get("contenido") != null ? requestBody.get("contenido").toString() : null;
            String correo = requestBody.get("correo") != null ? requestBody.get("correo").toString() : null;
            Long nroOperacion = requestBody.get("numoperacion") != null
                    ? Long.parseLong(requestBody.get("numoperacion").toString())
                    : null;
            Long modify_by = requestBody.get("modify_by") != null
                    ? Long.parseLong(requestBody.get("modify_by").toString())
                    : Long.parseLong(requestBody.get("UserId").toString());
            ApiResponseData<List<RptFiles>> l = mensajePlantillaService.obtenerArchivos(nroOperacion);
            List<String> fileNames = new ArrayList<>();
            if (l.getData() != null) {
                l.getData().forEach(file -> {
                    // String url = s3Service.getFileUrl(file.getFileName());
                    // file.setUrlTemporal(url);
                    fileNames.add(file.getFileName());
                    System.out.println("file.getFileName()" + file.getFileName());
                });
            }

            List<String> pdfs = new ArrayList<>();

            for (String key : fileNames) {

                String fileName = key.substring(key.lastIndexOf("/") + 1);
                String localPath = "C:/temp/" + fileName;

                byte[] data = s3Service.downloadFile(key);

                // Crear directorio si no existe
                new File("C:/temp").mkdirs();

                try (FileOutputStream output = new FileOutputStream(localPath)) {
                    output.write(data);
                }

                System.out.println("Archivo guardado en: " + localPath);
                pdfs.add(localPath);
            }
            // List<String> pdfs = s3Service.downloadFilesFromS3(fileNames);
            System.out.println("paso por aqui mas" + fileNames.size());

            ApiResponse apiMSG = sendEmailWithAttachments(userMailGestion, passwordGestion, correo, asunto, contenido, pdfs);
            System.out.println("mas por aqui" + fileNames.size());

            //Double nsaldo = 0.0d;
          //  mensajePlantillaService.act_rptCorreo(nroOperacion, apiMSG.isSuccess(), apiMSG.getMessage(), modify_by,
            //        nsaldo);
            for (String key : fileNames) {
                String fileName = key.substring(key.lastIndexOf("/") + 1);
                String localPath = "C://temp//" + fileName;

                File archivo = new File(localPath);

                if (archivo.exists()) {
                    if (archivo.delete()) {
                        System.out.println("Archivo eliminado");
                    } else {
                        System.out.println("No se pudo eliminar el archivo");
                    }
                } else {
                    System.out.println("El archivo no existe");
                }
            }
            return ResponseEntity.ok(apiMSG);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    public ApiResponse sendEmailWithAttachments(String from, String pass, String to, String subject, String body,
            List<String> attachmentPaths) {
        // Configuración de las propiedades del correo (SMTP)
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        ApiResponse response = new ApiResponse();

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });

        try {
            // Crear un mensaje de correo (MimeMessage)
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // Crear la parte que contendrá el cuerpo del mensaje
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            // Crear los adjuntos
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart); // Agregar el cuerpo al multipart

            // Para cada archivo adjunto, agregarlo al mensaje
            for (String path : attachmentPaths) {
                MimeBodyPart attachment = new MimeBodyPart();
                FileDataSource source = new FileDataSource(path);
                attachment.setDataHandler(new DataHandler(source));
                attachment.setFileName(source.getName());
                multipart.addBodyPart(attachment);
            }

            // Asignar el contenido multipart al mensaje
            message.setContent(multipart);

            // Enviar el correo
            Transport.send(message);
            response.setSuccess(true);
            response.setMessage("Correo enviado correctamente.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return response;

    }

    public ApiResponse sendFromGMail(String from, String pass, String to, String subject1, String body) {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.auth", "true");
        System.out.println("por aca paso");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });

        ApiResponse response = new ApiResponse();
        try {
            // Crear el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject1);
            message.setText(body);

            // Enviar el correo
            Transport.send(message);
            Thread.sleep(2500);
            response.setSuccess(true);
            response.setMessage("Correo enviado correctamente.");

            Properties properties = System.getProperties();
            properties.put("mail.store.protocol", "imaps");
            properties.put("mail.imaps.host", "imap.gmail.com");
            properties.put("mail.imaps.port", "993");
            properties.put("mail.imaps.ssl.enable", "true");

            session = Session.getInstance(properties);
            Store store = session.getStore("imaps");

            System.out.println("Connecting to Gmail...");
            store.connect("imap.gmail.com", from, pass);
            System.out.println("Connected successfully!");

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int totalMessages = inbox.getMessageCount();
            System.out.println("Total messages in Inbox: " + totalMessages);

            // Fetch last 10 messages
            int start = Math.max(1, totalMessages - 20);
            Message[] mensajesHoy = inbox.getMessages(start, totalMessages);

            for (int i = mensajesHoy.length - 1; i >= 0; i--) {
                Message message1 = mensajesHoy[i];

                if (message1.getSubject() != null && ( message1.getSubject().contains("Delivery Status Notification") || message1.getSubject().contains("Mail Delivery Subsystem") )){

                    Object content = message1.getContent();
                    String errorContent = "";
                    if (content instanceof String) {
                        // Contenido simple
                        String body2 = (String) content;
                        if (body2.contains("550")) {
                            errorContent = extraerLineaError(body2, to);
                        }
                    } else if (content instanceof MimeMultipart) {
                        MimeMultipart multipart = (MimeMultipart) content;
                        errorContent = this.obtenerTextoDesdeMultipart(multipart, to);
                    } else {
                        System.out.println("Contenido de tipo desconocido: " + content.getClass());
                    }
                    if (errorContent.trim().contains(to.trim())) {
                        response.setSuccess(false);
                        response.setMessage("Mail Delivery Subsystem ");
                        break;
                    }
                }
            }

            inbox.close(false);
            store.close();

        } catch (SendFailedException e) {
            response.setSuccess(false);
            response.setMessage("Fallo al enviar correo: " + e.getMessage());

            // Detalle de direcciones inválidas
            if (e.getInvalidAddresses() != null) {
                for (Address addr : e.getInvalidAddresses()) {
                    System.err.println("Dirección inválida: " + addr.toString());
                }
            }
        } catch (AuthenticationFailedException e) {
            response.setSuccess(false);
            response.setMessage("Fallo de autenticación: " + e.getMessage());
        } catch (MessagingException e) {
            response.setSuccess(false);
            response.setMessage("Error de envío " + e.getMessage());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error de envío " + e.getMessage());
        }

        return response;
    }

    public String obtenerTextoDesdeMultipart(MimeMultipart multipart, String correo) throws Exception {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain") || bodyPart.isMimeType("text/html")) {
                Object content = bodyPart.getContent();
                if (content instanceof String) {
                    String contenidoTexto = (String) content;
                    // Buscar línea con error SMTP (ej: 550)
                    if (contenidoTexto.contains("550")) {
                        texto.append(extraerLineaError(contenidoTexto, correo));
                    }
                    if(contenidoTexto.contains(correo)){
                        texto.append(extraerLineaError(contenidoTexto, correo));
                    }
                }
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                texto.append(obtenerTextoDesdeMultipart((MimeMultipart) bodyPart.getContent(), correo));
            }
        }

        return texto.toString();
    }

    public String extraerLineaError(String texto, String correo) {
        String[] lineas = texto.split("\n");
        String resul = "";
        for (String linea : lineas) {
            if (linea.trim().contains(correo.trim())) {
                resul = correo;
                return resul;
            }
        }
        return resul;
    }


    String GeneraPlantilla(String Asunto, String tituloMensaje, String mensaje) {
        String plantilla = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" bgcolor=\"#ffffff\"> <tbody> <tr> <td valign=\"top\" align=\"center\"> <font size=\"12px\" face=\"Arial, Helvetica, sans-serif\"></font> <table style=\"border:1px solid #dddddd;background-color:#01355c\" width=\"632\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"\"> <tbody> <tr style=\"height:80px\" valign=\"top\"> <td width=\"25\" bgcolor=\"\"> </td> <td style=\"font-family:Arial,Helvetica,sans-serif;color:#f89223;font-size:28px;text-align:left\" valign=\"middle\"> <br> <b>"
                + Asunto
                + "</b> </td> </tr> </tbody> </table> <table style=\"border:1px solid #ddd;border-top:none\" width=\"620\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\"> <tbody> <tr> <td style=\"height:30px;float:left;width:630px\"> <table cellspacing=\"0\" cellpadding=\"0\"> <tbody> <tr>  <td style=\"height:33px\" width=\"25px\"> <a shape=\"rect\" style=\"text-decoration:none\"> <img style=\"text-decoration:none;border:0\" src=\"https://ci5.googleusercontent.com/proxy/gS-2MSVGtlIb7Npx9tEd7fgJ9oIUOwaz7nqoo_x5EZdKbfbvxBnudHgnWjcNEuNgyDiwTR3oRhZVvLcSjp7aprVnhE0XRT4IsOyVMZrgD__IW-OdYuBLrok=s0-d-e1-ft#http://f.i.wix.com/i/11/2076041122/Social_Icon_2013_New_Tweeter.png\" alt=\"\" class=\"CToWUd\"> </a> </td> <td style=\"height:33px\" width=\"25px\"> <a shape=\"rect\"> <img style=\"text-decoration:none;border:0\" src=\"https://ci3.googleusercontent.com/proxy/B2YuRGO92TteUjI4vnLP4OxdW2fT1dikBNud-m_YhP2PCHpCiApk8k7xDAjAzIVn9KBkKBEanRzNop2x7xbUTkmTRnLQBmSxnC9c7tGY8GBtX4iIeJvSQQ=s0-d-e1-ft#http://f.i.wix.com/i/11/2076041122/Social_Icon_2013_New_Google.png\" alt=\"\" class=\"CToWUd\"> </a> </td> <td width=\"25px\"> <a shape=\"rect\"> <img style=\"text-decoration:none;border:0\" src=\"https://ci5.googleusercontent.com/proxy/V3DRjT02YWL7IA_ta9rBMyP_Vdm8NE_DBoQhrbo8-FXwqaQJJY8isgYHm0fmzGlCWxi08WWBztS73ve0ic-2UUbXd_XgCFkLoPJBTiVK1VEdbH1vAM8c2PxcKA=s0-d-e1-ft#http://f.i.wix.com/i/11/2076041122/Social_Icon_2013_New_Pinterest.png\" alt=\"\" class=\"CToWUd\"> </a> </td> </tr> </tbody> </table> </td> </tr> <tr> <td style=\"height:20px;width:630px\"> <table width=\"620\" cellspacing=\"0\" cellpadding=\"5\" border=\"0\"> <tbody> <tr> <td width=\"15\"> </td> <td><p style=\"margin-bottom:13px!important;font-family:Arial,Helvetica,sans-serif;font-size:13px;color:#4c4c4c;text-align:justify\" align=\"justify\">Estimado usuario,</p><p style=\"margin-bottom:13px!important;font-family:Arial,Helvetica,sans-serif;font-size:18px;color:#003366;text-align:justify\" align=\"justify\">"
                + tituloMensaje
                + "</p><p style=\"margin-bottom:13px!important;font-family:Arial,Helvetica,sans-serif;font-size:13px;color:#4c4c4c;text-align:justify\" align=\"justify\">"
                + mensaje
                + "</p> </td> <td style=\"width:6px\"> </td> </tr> </tbody> </table> <table style=\"margin-top:0px!important;padding:0\" width=\"620\" cellspacing=\"0\" cellpadding=\"5\" border=\"0\" bgcolor=\"#ffffff\"> <tbody> <tr> <td width=\"45\"> </td> </tr> </tbody> </table> <table style=\"border-top:1px solid #ddd;border-bottom:1px solid #ddd;height:60px\" width=\"630\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" bgcolor=\"#f1f1f1\"> <tbody> <tr> <td> <table style=\"margin-top:0;padding-top:0\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> <tbody> <tr> <td width=\"20\" bgcolor=\"#f1f1f1\"> </td> <td style=\"height:50px;width:63px;padding-bottom:10px\" bgcolor=\"#f1f1f1\"> <p style=\"margin:0;font-family:Arial,Helvetica,sans-serif;color:#4c4c4c;text-align:center;font-weight:bold;font-size:11px;width:63px;margin-bottom:0\" align=\"justify\"> <font size=\"1\" face=\"Arial, Helvetica, sans-serif\"> <td width=\"15\" bgcolor=\"#f1f1f1\"> </td> </tr> </tbody> </table> </td> "
                +
                " <td> <table style=\"margin-left:0px;margin-top:0px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> <tbody> <tr>  <td style=\"height:50px;padding-bottom:10px\" width=\"134\" align=\"left\"> <img src='https://ci3.googleusercontent.com/mail-sig/AIorK4zp0xnuCxJiBiWM8IhxrixsCRYNscTIa2gACv65i5giCUA-Kq0YJFc9hyfFktNgdyFY22gifu0'  width='450' height='160'/>   </td> </tr> </tbody> </table>"
                +
                " </td> </tr> </tbody> </table> <table width=\"630\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\"> <tbody>  "
                +
                "<tr> <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:11px;color:#4c4c4c;text-align:center\"> <p style=\"margin-bottom:0;margin:0\"> <font size=\"1\" face=\"Arial, Helvetica, sans-serif\">Si no esta interesado en recibir mas...</font></p></td></tr></tbody></table></td></tr></tbody></table></td></tr></tbody></table>";
        return plantilla;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> subirArchivo(@RequestParam("archivo") MultipartFile archivo,
            @RequestParam("numOperacion") Long numOperacion, @RequestParam("reembolsoId") Long reembolsoId,
            @RequestParam("userId") String userId) {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }
        try {
            String nombreArchivo = "reembolso/" + archivo.getOriginalFilename();
            byte[] contenido = archivo.getBytes();
            String url = s3Service.uploadFile(contenido, nombreArchivo);
            mensajePlantillaService.ins_rptFile(reembolsoId, numOperacion, nombreArchivo, userId);
            return ResponseEntity.ok("Archivo subido: " + url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al subir: " + e.getMessage());
        }
    }

    @PostMapping("/downloads")
    public ResponseEntity<ApiResponseData<List<RptFiles>>> descargarArchivo(@RequestBody Map<String, ?> requestBody) {
        Long numOperacion = requestBody.get("numeroOperacion") != null
                ? Long.parseLong(requestBody.get("numeroOperacion").toString())
                : null;
        try {
            ApiResponseData<List<RptFiles>> l = mensajePlantillaService.obtenerArchivos(numOperacion);
            if (l.getData() != null) {
                l.getData().forEach(file -> {
                    String url = s3Service.getFileUrl(file.getFileName());
                    file.setUrlTemporal(url);
                });
            }
            return ResponseEntity.ok(l);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/listarbanco")
    public ResponseEntity<ApiResponseData<List<Banco>>> listabanco() {
        try {
            System.out.println("listarbanco");
            ApiResponseData<List<Banco>> l = mensajePlantillaService.listarbanco();
            System.out.println("finalizo listarbanco");
            return ResponseEntity.ok(l);
        } catch (Exception e) {
            System.out.println("errrorrrr listarbanco");
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/eliminarfiles")
    public ResponseEntity<ApiResponse> eliminararchivo(@RequestBody Map<String, ?> requestBody) {
        Long id = requestBody.get("id") != null ? Long.parseLong(requestBody.get("id").toString()) : null;
        Long numOperacion = requestBody.get("numeroOperacion") != null
                ? Long.parseLong(requestBody.get("numeroOperacion").toString())
                : null;
        try {
            ApiResponse l = mensajePlantillaService.eliminarArchivo(id, numOperacion);
            return ResponseEntity.ok(l);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/descargar/{fileName}")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable String fileName) throws IOException {
        byte[] contenido = s3Service.obtenerContenido("reembolso/" + fileName);
        String tipo = obtenerTipoMime(fileName); // ejemplo: "application/pdf"

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(tipo))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(contenido);
    }

    public String obtenerTipoMime(String fileName) {
        String mimeType = URLConnection.guessContentTypeFromName(fileName);
        return mimeType != null ? mimeType : "application/octet-stream"; // Fallback
    }

    @PostMapping("/reenviarcorreoerror")
    public ResponseEntity<ApiResponse> reenviarcorreoerror(@RequestBody Map<String, ?> requestBody) {
        ApiResponseData<RptMensaje> apiResponseData = new ApiResponseData<>();
        try {
            System.out.println("reenviarCorreoerror");
            String asunto = requestBody.get("asunto") != null ? requestBody.get("asunto").toString() : null;
            String contenido = requestBody.get("contenido") != null ? requestBody.get("contenido").toString() : null;
            String correo = requestBody.get("correo") != null ? requestBody.get("correo").toString() : null;
            Long nroOperacion = requestBody.get("nroOperacion") != null
                    ? Long.parseLong(requestBody.get("nroOperacion").toString())
                    : null;
            Long modify_by = requestBody.get("modify_by") != null
                    ? Long.parseLong(requestBody.get("modify_by").toString())
                    : Long.parseLong(requestBody.get("UserId").toString());
            // RptMensaje rptMensaje = mensajePlantillaService.listaRptMesaje(nroOperacion);
            Double nsaldo = requestBody.get("nsaldo") != null ? Double.parseDouble(requestBody.get("nsaldo").toString())
                    : null;

            ApiResponse apiMSG = sendFromGMail(userMailGestion, passwordGestion, correo, asunto, contenido);
            if (apiMSG.isSuccess()) {
                mensajePlantillaService.enviaNotificacion(List.of("administracion"), asunto, asunto, contenido, 2L, modify_by, correo);
            }
            mensajePlantillaService.act_rptCorreo(nroOperacion, apiMSG.isSuccess(), apiMSG.getMessage(), modify_by,
                    nsaldo);
            System.out.println("actualizo" + nroOperacion);
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("OK");
            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error al guardar " + e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

}
