package com.prestaclub.prestaclub_backend.persistence.nuevo.service.impl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prestaclub.prestaclub_backend.dto.ApiResponse;
import com.prestaclub.prestaclub_backend.dto.ListaResponse;
import com.prestaclub.prestaclub_backend.dto.UsuarioResponse;
import com.prestaclub.prestaclub_backend.persistence.antiguo.dto.UsuarioDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.dto.UsuarioMantenimientoDTO;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Empleado;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Modulo;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Rol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.UsuarioModuloRol;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.EmpleadoRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.repository.UsuarioRepository;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.ModuloService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.RolService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioModuloRolService;
import com.prestaclub.prestaclub_backend.persistence.nuevo.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ModuloService moduloService;

    @Autowired
    private RolService rolService;

    @Autowired
    private UsuarioModuloRolService usuarioModuloRolService;

    @Override
    public Usuario guardarUsuario(Usuario usuario) { 
        Usuario usuarioLocal = usuarioRepository.findByUsername(usuario.getUsername());
        
        if (usuarioLocal != null) {  
            return null;
        }else{  
             usuarioLocal = usuarioRepository.save(usuario);
        }
        return usuarioLocal;
    }

    @Override
    public Usuario obtenerUsuario(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public void EliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public void migrarUsuarios(List<UsuarioDTO> usuariosDTO) {
        try {
            for (UsuarioDTO usuarioDTO : usuariosDTO) {  
                if (usuarioRepository.findByUsername(usuarioDTO.getUsuario()) != null) {
                    continue; 
                } 
                Empleado empleado = new Empleado();
                empleado.setNombre(usuarioDTO.getNombre());
                empleado.setApellidoPaterno(usuarioDTO.getApellidos());
                empleado.setApellidoMaterno(usuarioDTO.getApellidoMaterno()); 
                empleado.setCreatedBy(1L);
                empleado.setCreatedDate(LocalDateTime.now());
                empleadoRepository.save(empleado);
    
                Usuario usuario = new Usuario();
                usuario.setUsername(usuarioDTO.getUsuario()); 
                usuario.setPassword(bCryptPasswordEncoder.encode(usuarioDTO.getUsuario() + "@@" + LocalDateTime.now().getYear()));
                usuario.setEmpleado(empleado);
                usuario.setEmail(usuarioDTO.getCorreo());
                usuario.setCreatedBy(1L);
                usuario.setCreatedDate(LocalDateTime.now());
                usuarioRepository.save(usuario);
    
                 if (usuarioDTO.getModulos() != null) {
                    for (Map<String, Object> moduloData : usuarioDTO.getModulos()) {
                        // Obtener nombre del módulo y rol desde el mapa
                        String nombreModulo = (String) moduloData.get("nombre");
                        Integer rolId = (Integer) moduloData.get("rol");
    
                        // Buscar o crear el módulo
                        Modulo modulo = moduloService.findBySlug(nombreModulo); 
    
                        // Buscar o crear el rol
                        Rol rol = rolService.findById(rolId.longValue()); 
                        // Crear y guardar UsuarioModuloRol
                        UsuarioModuloRol usuarioModuloRol = new UsuarioModuloRol();
                        usuarioModuloRol.setUsuario(usuario);
                        usuarioModuloRol.setModulo(modulo);
                        usuarioModuloRol.setRol(rol);
                        usuarioModuloRolService.guardarUsuarioModuloRol(usuarioModuloRol);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al migrar los usuarios");
            
        }
    }

    @Override
    public List<UsuarioDTO> cargarUsuariosActivos() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("json/usuarios-activos.json"); 
        if (is == null) {
            throw new FileNotFoundException("Archivo usuarios-activos.json no encontrado");
        }

        // Crear un ObjectMapper para deserializar el contenido JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Leer el archivo JSON y convertirlo en una lista de mapas
        List<Map<String, Object>> dataList = objectMapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});

        // Lista para almacenar los objetos UsuarioDTO
        List<UsuarioDTO> usuariosActivos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // Convertir cada mapa en un objeto UsuarioDTO
        for (Map<String, Object> data : dataList) {
            //captura de la fecha
            // String fechaRegistro = (String) data.get("fecha_registro");
            // if (fechaRegistro.matches(".*\\.\\d{1,2}$")) {
            //     fechaRegistro = fechaRegistro + "0".repeat(3 - fechaRegistro.substring(fechaRegistro.lastIndexOf('.') + 1).length());
            // }
            // Crear un objeto UsuarioDTO
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setUsuario((String) data.get("usuario")); 
            // usuario.setFechaRegistro(LocalDateTime.parse(fechaRegistro, formatter));
            usuario.setNombre((String) data.get("nombre"));
            usuario.setApellidos((String) data.get("apellidos"));
            usuario.setCorreo((String) data.get("correo"));
            usuario.setApellidoMaterno((String) data.get("materno"));
            Object modulosObj = data.get("modulos");
            if (modulosObj instanceof List<?>) {
                List<?> rawList = (List<?>) modulosObj;
                List<Map<String, Object>> modulos = new ArrayList<>();

                for (Object item : rawList) {
                    if (item instanceof Map<?, ?>) {
                        // Convertimos el mapa genérico al tipo esperado
                        modulos.add((Map<String, Object>) item);
                    }
                }
                usuario.setModulos(modulos);
            }
            usuariosActivos.add(usuario);
        }

        // Retornar la lista de usuarios activos
        return usuariosActivos;
    }
    
    public UsuarioMantenimientoDTO obtenerUsuarioMantenimientoById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).get();
        return new UsuarioMantenimientoDTO(  usuario.getId(), usuario.getUsername(),  usuario.getEmpleado().getNombre() + " " + usuario.getEmpleado().getApellidoPaterno());
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).get();
    }
 
    public ListaResponse listaUsuarios( Integer pageSize, Integer pageNumber, String filterText) {
        ListaResponse response = new ListaResponse();
        List<UsuarioResponse> data = new ArrayList<>();
        List<Usuario> listaUsuario = usuarioRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        for (Usuario u : listaUsuario) {
            if (
                filterText != null && !filterText.isEmpty() && 
                !u.getEmail().toUpperCase().contains(filterText.toUpperCase()) && 
                !u.getUsername().toUpperCase().contains(filterText.toUpperCase()) && 
                !(u.getEmpleado().getNombre() + " " + u.getEmpleado().getApellidoPaterno()+ " " + u.getEmpleado().getApellidoMaterno()).toUpperCase().contains(filterText.toUpperCase()) 
                ) 
            {
                continue; // Saltar usuarios que no coincidan con el filtro
            }
            Usuario usuarioCreacion = usuarioRepository.findById(u.getCreatedBy()).get();

            UsuarioMantenimientoDTO usuarioMantenimientoDTO = new UsuarioMantenimientoDTO();
            usuarioMantenimientoDTO.setId(usuarioCreacion.getId());
            usuarioMantenimientoDTO.setUsername(usuarioCreacion.getUsername());
            usuarioMantenimientoDTO.setNombreCompleto(usuarioCreacion.getEmpleado().getNombre() + " " + usuarioCreacion.getEmpleado().getApellidoPaterno());

            UsuarioResponse usuarioResponse = new UsuarioResponse();
            List<UsuarioModuloRol> usuarioModuloRol = usuarioModuloRolService.findByUsuarioId(u.getId()); 
            usuarioModuloRol.forEach(umRol -> umRol.setUsuario(null));
            usuarioResponse.setUsername(u.getUsername());  
            usuarioResponse.setEmail(u.getEmail());
            usuarioResponse.setId(u.getId());
            usuarioResponse.setUsuarioModuloRol(usuarioModuloRol); 
            usuarioResponse.setActive(u.isActive());
            usuarioResponse.setEmpleado(u.getEmpleado()); 
            usuarioResponse.setUserCreation(usuarioMantenimientoDTO);
            usuarioResponse.setCreatedDate(u.getCreatedDate());
            data.add(usuarioResponse);
        }
        int totalSize = data.size();
        int fromIndex = ((pageNumber - 1) * pageSize) ;
        int toIndex = Math.min(fromIndex + pageSize, totalSize);
        response.setSuccess(true);
        response.setData(data.subList(fromIndex, toIndex));
        response.setMessage("ok");
        response.setPageNumber(pageNumber);
        response.setPageSize(pageSize);
        response.setTotal(totalSize);

        return response;
    }

    public UsuarioResponse obtenerUsuarioById(Long id){
        Usuario usuario = usuarioRepository.findById(id).get();
        List<UsuarioModuloRol> usuarioModuloRol = usuarioModuloRolService.findByUsuarioId(id);
        usuarioModuloRol.forEach(umRol -> umRol.setUsuario(null));
        
        UsuarioResponse usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(id);
        usuarioResponse.setUsername(usuario.getUsername());
        usuarioResponse.setEmail(usuario.getEmail());
        usuarioResponse.setActive(usuario.isActive());
        usuarioResponse.setEmpleado(usuario.getEmpleado());
        usuarioResponse.setUsuarioModuloRol(usuarioModuloRol);
        usuarioResponse.setNotificacionCorreo(usuario.isNotificacionCorreo());

        return usuarioResponse;
    } 

    public ApiResponse changeStatus(Boolean status, Long id, Long userId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Usuario usuario = usuarioRepository.findById(id).get();
            usuario.setActive(status);
            usuario.setUpdatedBy(userId);
            usuario.setUpdatedDate(LocalDateTime.now());
            usuarioRepository.save(usuario);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("El Usuario "+usuario.getUsername()+" ha sido actualizado a estado "+(status ? "Activo" : "Inactivo"));
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error interno del servidor : "+e.getMessage());
            e.printStackTrace();
        }
        return apiResponse;
    }

    public ApiResponse addUser(String nombres, String apellidPaterno, String apellidMaterno, String email, Long user_id) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            Usuario usuarioExist = usuarioRepository.findByEmail(email);
            if (usuarioExist != null) {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("El correo \" "+email+" \" ya existe registrado en el sistema con el usuario "+usuarioExist.getUsername()); 
                return apiResponse;
            }

            Empleado empleado = new Empleado();
            empleado.setApellidoMaterno(apellidMaterno);
            empleado.setApellidoPaterno(apellidPaterno);
            empleado.setNombre(nombres);
            empleado.setCreatedBy(user_id);
            empleado.setCreatedDate(LocalDateTime.now());

            String username = this.generateUsername(nombres, apellidPaterno, apellidMaterno);

            Usuario usuario = new Usuario();
            usuario.setActive(true);
            usuario.setCreatedDate(LocalDateTime.now());
            usuario.setCreatedBy(user_id);
            usuario.setUsername(username);
            usuario.setPassword(bCryptPasswordEncoder.encode(username+"@@"+LocalDateTime.now().getYear()));
            usuario.setEmpleado(empleado);
            usuario.setEmail(email);
            empleadoRepository.save(empleado);
            usuarioRepository.save(usuario);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("El Usuario ha sido creado");
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error interno del servidor : "+e.getMessage());
            e.printStackTrace();
        }
        return apiResponse;
    }

    private String generateUsername(String nombres, String apellidPaterno, String apellidMaterno) {
        nombres = nombres.trim();
        apellidPaterno = normalizeString(apellidPaterno.replace(" ", ""));

        String username = "";
        for (int i = 1; i <= nombres.length(); i++) {
            username = nombres.substring(0, i).toUpperCase() + apellidPaterno.toUpperCase();
            if (usuarioRepository.findByUsername(username) == null) {
                return username; // Devuelve el nombre de usuario si no está en uso
            }
        }

        // Si aún no se encuentra un nombre único, añade un número al final
        int counter = 1;
        String originalUsername = username;
        while (usuarioRepository.findByUsername(username) != null) {
            username = originalUsername + counter;
            counter++;
        }
        return username;
    }

    public static String normalizeString(String input) {
        // Eliminar acentos
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[^\\p{ASCII}]", "");

        // Reemplazar 'ñ' por 'n'
        normalized = normalized.replaceAll("ñ", "n").replaceAll("Ñ", "N");

        return normalized;
    }


    public ApiResponse updateNotificacionCorreo(Long id, Boolean notificacionCorreo){
        ApiResponse apiResponse = new ApiResponse();
        try {
            Usuario usuario = usuarioRepository.findById(id).get();
            usuario.setNotificacionCorreo(notificacionCorreo);
            usuarioRepository.save(usuario);
            apiResponse.setSuccess(true);
            apiResponse.setMessage("El Usuario ha sido actualizado");
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error interno del servidor : "+e.getMessage());
            e.printStackTrace();
        }
        return apiResponse;
    }
 
}
