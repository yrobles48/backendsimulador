package com.prestaclub.prestaclub_backend; 
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.prestaclub.prestaclub_backend.persistence.nuevo.model.CostosLegales;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRConceptosServicios; 
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolsoEstados;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRReembolsoSubEstados;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRServicios;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.LCRSolicitudEtapa;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.MensajeConfiguracion;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.MensajeTipo;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Modulo;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Rol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRConceptosServiciosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRReembolsoEstadosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRReembolsoSubEstadosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRServiciosRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.LCRSolicitudEtapaRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.MensajeConfiguracionRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.MensajeTipoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.CostosLegalesService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.ModuloService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.RolService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService; 

@SpringBootApplication 
@EnableScheduling
public class PrestaclubBackendApplication extends SpringBootServletInitializer implements CommandLineRunner { 

	@Autowired
	private UsuarioService usuarioService; 

	@Autowired
	private RolService rolService;

	@Autowired
	private ModuloService moduloService;

	@Autowired
	private CostosLegalesService costosLegalesService;

	@Autowired
	private LCRServiciosRepository lcrServiciosRepository;

	@Autowired
	private LCRConceptosServiciosRepository lcrConceptosServiciosRepository;

	@Autowired
	private LCRSolicitudEtapaRepository lcrSolicitudEtapaRepository; 

	@Autowired
	private LCRReembolsoEstadosRepository lcrReembolsoEstadosRepository;

	@Autowired
	private LCRReembolsoSubEstadosRepository lcrReembolsoSubEstadosRepository;

	@Autowired
	private MensajeTipoRepository mensajeTipoRepository;

	@Autowired
	private MensajeConfiguracionRepository mensajeConfiguracionRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(PrestaclubBackendApplication.class, args);
	}

	private void seedLCRServicios() { 

		String [][] listaslug = {
			{"cheque-de-gerencia-extra", "Cheque de gerencia extra","cheque-de-gerencia-extra"},
			{"costo-de-inspeccion", "Costo de inspección","costo-de-inspeccion"},
			{"peritaje", "Peritaje","peritaje"},
			{"asesoria-prestaclub", "Asesoria prestaclub","asesoria-prestaclub"},
			{"derechos-notariales", "Derechos Notariales","derechos-notariales"},
			{"toma-de-firmas-con-lector-biometrico", "Toma de firmas con lector biometrico","toma-de-firmas-con-lector-biometrico"},
			{"certificado-registral-inmobiliario-copia-literal", "Certificado Registral Inmobiliario + Copia Literal","cri-y-copia-literal"},
			{"bloqueo-registral", "Bloqueo Registral","bloqueo-registral"},
			{"renovacion-de-bloqueo", "Renovacion de bloqueo","derechos-registrales"},
			{"otorgamiento-de-poder", "Otorgamiento de poder","otorgamiento-de-poder"},
			{"emision-tchn", "Emisión TCHN","derechos-registrales"},
			{"expedicion-tchn", "Expedición TCHN","derechos-registrales"},
			{"inscripcion-hipoteca", "Inscripción Hipoteca","derechos-registrales"},
			{"levantamiento-de-hipoteca", "Levantamiento de Hipoteca","registrales-de-levantamiento"},
			{"cancelacion-de-tchn", "Cancelación de TCHN","derechos-registrales"},
			{"levantamiento-de-bloqueo-por-caducidad-desistimiento", "Levantamiento de bloqueo por caducidad / desistimiento","derechos-registrales"},
			{"tasa-envio-callao", "Tasa Envio Callao","derechos-registrales"},
			{"tasa-de-tachas", "Tasa de tachas","derechos-registrales"}
		};

		try {
			for(String[] lista : listaslug) {
				String slug = lista[0];
				String nombre = lista[1];
				String concepto = lista[2];
				if (lcrServiciosRepository.findBySlug(slug) == null) {
					LCRServicios lcrs = new LCRServicios();
					lcrs.setSlug(slug);
					lcrs.setDescripcion(nombre);
					lcrs.setLcrConceptosServicios(lcrConceptosServiciosRepository.findBySlug(concepto));
					lcrServiciosRepository.save(lcrs);
				}
			}
			
		} catch (Exception e) {
			
		} 
	}

	private void seedLCRConceptosServicios() {
		String [][] listaslug = {
			{"cri-y-copia-literal", "CRI y Copia Literal"},
			{"bloqueo-registral", "Bloqueo Registral"},
			{"peritaje", "Peritaje"},
			{"derechos-notariales", "Derechos Notariales"},
			{"derechos-registrales", "Derechos Registrales"},
			{"asesoria-prestaclub", "Asesoria prestaclub"},
			{"registrales-de-levantamiento", "Registrales de levantamiento"},
			{"toma-de-firmas-con-lector-biometrico", "Toma de firmas con lector biometrico"},
			{"otorgamiento-de-poder", "Otorgamiento de poder"},
			{"cheque-de-gerencia-extra", "Cheque de gerencia extra"},
			{"costo-de-inspeccion", "Costo de inspección"}
		};

		try {
			for(String[] lista : listaslug) {
				String slug = lista[0];
				String nombre = lista[1];
				if (lcrConceptosServiciosRepository.findBySlug(slug) == null) {
					LCRConceptosServicios lcrCS = new LCRConceptosServicios();
					lcrCS.setSlug(slug);
					lcrCS.setDescripcion(nombre);
					lcrConceptosServiciosRepository.save(lcrCS);
				}
			}
			
		} catch (Exception e) {
			
		} 
	}

	private void seedRoles() {
		String[] roles = {"CEO", "ADMINISTRADOR", "SUPERVISOR", "ASESOR"};
		
		try {
			for (String rol : roles) {
				if (rolService.findByNombre(rol) == null) {
					Rol r = new Rol();
					r.setNombre(rol);
					rolService.guardarRol(r);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Roles ya existen");
		} 
	} 

	private void seedModulos() {
		String[][] modulos = {
				{"NEGOCIOS", "/negocios", "negocios"},
				{"CRM", "/crm", "crm"},
				{"LEGAL", "/legal", "legal"},
				{"CONFIGURADOR", "/configurador", "configurador"},
				{"CONTABILIDAD", "/contabilidad", "contabilidad"},
				{"REPORTES", "/reportes", "reportes"},
				{"ADMINISTRACION", "/administracion", "administracion"},
		};
 
		try {
			for (String[] modulo : modulos) {
				if (moduloService.findBySlug(modulo[2]) == null) {
					Modulo m = new Modulo();
					m.setNombre(modulo[0]);
					m.setUrl(modulo[1]);
					m.setImagen(modulo[2]);
					m.setActivo(true);
					m.setSlug(modulo[2]);
					m.setIdPadre(null);
					m.setCreatedBy(0L);
					m.setCreatedDate(LocalDateTime.now());
					moduloService.guardarModulo(m);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Modulos ya existen");
		} 
	}

	public void seedUsuarios() {
		try {
			usuarioService.migrarUsuarios(usuarioService.cargarUsuariosActivos()); 
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}

	public void seedCostosLegales() {
		try { 
			// Object[][] costosLegales = {
			// 	{"CALIFICACION_HIPOTECA", 49.90 , "CALIFICACION-HIPOTECA","gastos-hipotecarios"},
			// 	{"EMISION_TCHN", 49.90 , "EMISION-TCHN","gastos-hipotecarios"},
			// 	{"EXPEDICION_TCHN", 99.90 , "EXPEDICION-TCHN","gastos-hipotecarios"},
			// 	{"CALIFICACION_PODERES", 24.60 , "CALIFICACION-PODERES","gastos-hipotecarios"},	
			// };

			Object[][] costosLegales = {
				{"CALIFICACION_HIPOTECA", 51.80 , "CALIFICACION-HIPOTECA","gastos-hipotecarios"},
				{"EMISION_TCHN", 51.80 , "EMISION-TCHN","gastos-hipotecarios"},
				{"EXPEDICION_TCHN", 101.90 , "EXPEDICION-TCHN","gastos-hipotecarios"},
				{"CALIFICACION_PODERES", 25.60 , "CALIFICACION-PODERES","gastos-hipotecarios"},	
				{"BLOQUEO_REGISTRAL", 51.80 , "BLOQUEO-REGISTRAL","gastos-hipotecarios"},
				{"DERECHOS_NOTARIALES",500.0,"DERECHOS-NOTARIALES","gastos-hipotecarios"},
				{"CERTIFICADO_REGISTRAL_INMOBILIARIO",200.0,"CERTIFICADO-REGISTRAL-INMOBILIARIO","gastos-hipotecarios"},
			};

			for (Object[] costoLegal : costosLegales) {
				if (costosLegalesService.findBySlug((String) costoLegal[2]) == null) {
					CostosLegales c = new CostosLegales();
					c.setSlug((String) costoLegal[2]); 
					Double valorDouble = (Double) costoLegal[1]; 
					BigDecimal valor =BigDecimal.valueOf(valorDouble); ; 
                	c.setValor(valor); 
					c.setDescripcion((String)costoLegal[0]);
					c.setTipo((String)costoLegal[3]);
					costosLegalesService.guardar(c);
				} 
			}
			
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	public void seedLCRSolicitudEtapas() {
		try {
			String[][] etapas = {
				{"Negocios","Revisión Negocios", "negocios"},
				{"Legal","Revisión Legal", "legal"},
				{"Contabilidad","Revisión Contabilidad", "contabilidad"},
				// {"Administración", "Revisión Administración", "administracion"},
			};

			for (String[] etapa : etapas) {
				if (lcrSolicitudEtapaRepository.findBySlug(etapa[2]) == null) {
					LCRSolicitudEtapa lcrSolicitudEtapa = new LCRSolicitudEtapa();
					lcrSolicitudEtapa.setNombre(etapa[0]); 
					lcrSolicitudEtapa.setDescripcion(etapa[1]);
					lcrSolicitudEtapa.setSlug(etapa[2]);
					lcrSolicitudEtapaRepository.save(lcrSolicitudEtapa);
				}
			} 

		} catch (Exception e) {
			e.printStackTrace(); 
		}
	} 


	public void seedLCRReembolsoEstados(){
		try {
			String[][] estados = {
				{"Pendiente", "pendiente"},
				{"En Proceso", "en-proceso"},
				{"Por Reembolsar", "por-reembolsar"},
				{"Devuelto", "devuelto"},
				{"Por Aplicar", "por-aplicar"},
				{"Aplicado", "aplicado"},
			};

			for (String[] estado : estados) {
				if (lcrReembolsoEstadosRepository.findBySlug(estado[1]) == null) {
					LCRReembolsoEstados lcrReembolsoEstado = new LCRReembolsoEstados();
					lcrReembolsoEstado.setNombre(estado[0]); 
					lcrReembolsoEstado.setSlug(estado[1]);

					lcrReembolsoEstadosRepository.save(lcrReembolsoEstado);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void seedLCRReembolsoSubEstados(){
		try {
			String[][] subEstados = {
				{"Pendiente", "pendiente", "pendiente"},
				{"En Proceso", "en-proceso", "en-proceso"},
				
			};

			for (String[] subEstado : subEstados) {
				if (lcrReembolsoSubEstadosRepository.findBySlug(subEstado[1]) == null) {
					LCRReembolsoEstados lcrReembolsoEstado = lcrReembolsoEstadosRepository.findBySlug(subEstado[2]);

					LCRReembolsoSubEstados lcrReembolsoSubEstado = new LCRReembolsoSubEstados();
					lcrReembolsoSubEstado.setNombre(subEstado[0]); 
					lcrReembolsoSubEstado.setSlug(subEstado[1]);
					lcrReembolsoSubEstado.setLcrReembolsoEstados(lcrReembolsoEstado);
					lcrReembolsoSubEstadosRepository.save(lcrReembolsoSubEstado);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void seedMensajeTipo(){
		try {
			String[][] tipos = {
				{"CORREO", "email"},
				{"SMS", "sms"},
			};

			for (String[] tipo : tipos) {
				if (mensajeTipoRepository.findBySlug(tipo[1]) == null) {
					MensajeTipo mensajeTipo = new MensajeTipo();
					mensajeTipo.setNombre(tipo[0]); 
					mensajeTipo.setSlug(tipo[1]);
					mensajeTipoRepository.save(mensajeTipo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}

	public void seedMensajeConfiguracion(){
		try {
			String[][] config = {
				{"Email de contacto", "email-contacto", "email"},
				{"SMS de contacto", "sms-contacto", "sms"},
				{"Email de Reembolso", "email-reembolso", "email"},
			};

			for (String[] conf : config) {
				if (mensajeConfiguracionRepository.findBySlug(conf[1]) == null) {
					MensajeConfiguracion mensajeConfiguracion = new MensajeConfiguracion();
					mensajeConfiguracion.setNombre(conf[0]); 
					mensajeConfiguracion.setSlug(conf[1]);
					mensajeConfiguracion.setMensajeTipo(mensajeTipoRepository.findBySlug(conf[2]));
					mensajeConfiguracionRepository.save(mensajeConfiguracion);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Iniciando...");
		System.out.println("Ejecutando Seeds..."); 
		 try {
			seedRoles();
			seedModulos();
			seedUsuarios(); 
			seedCostosLegales();
			seedLCRConceptosServicios();
			seedLCRServicios();
			seedLCRSolicitudEtapas(); 
			seedLCRReembolsoEstados();
			seedLCRReembolsoSubEstados();
			seedMensajeTipo();
			seedMensajeConfiguracion();
			System.out.println("Seeds ejecutados..."); 
		} catch (Exception e) {
			System.out.println("Seeds no ejecutados...");
			System.out.println(e.getMessage());
			e.printStackTrace(); 
		}

	}
}
