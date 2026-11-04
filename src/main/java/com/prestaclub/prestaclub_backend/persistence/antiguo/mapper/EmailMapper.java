package com.prestaclub.prestaclub_backend.persistence.antiguo.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Select;

public interface EmailMapper {
    @Select("SELECT * FROM email WHERE idemail = #{id}")
    Map<String, Object> obtenerEmail(int id);
}
