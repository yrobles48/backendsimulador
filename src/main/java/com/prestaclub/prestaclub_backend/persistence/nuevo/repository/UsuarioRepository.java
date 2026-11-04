package com.prestaclub.prestaclub_backend.persistence.nuevo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository; 
import com.prestaclub.prestaclub_backend.persistence.nuevo.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> { 
    public Usuario findByUsername(String username); 
    public Usuario findByEmail(String email);


    @Query( value = """
        select DISTINCT u.* from usuarios u  
inner join usuario_modulo_rol umr on umr.id_usuario = u.id 
inner join roles r on r.id = umr.id_rol 
inner join modulos m on m.id = umr.id_modulo 
where u.is_active  = true and m.slug in ( :slugs)
and (
            (m.slug = 'legal' and r.id in (2, 3, 4))
            or 
            (m.slug != 'legal' and r.id = :rol)
        )
    """, nativeQuery = true) 
    public List<Usuario> findActivosByModulosSlugIn(@Param("slugs") List<String> slugs , @Param("rol") Long rol); 
}
  
  