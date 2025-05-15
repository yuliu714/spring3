package org.yuliu.spring3.demos.web.mapper;

import org.apache.ibatis.annotations.*;
import org.yuliu.spring3.demos.web.pojo.Stu;

import java.util.List;

@Mapper
public interface StuMapper {

    @Select("select * from students where name = #{name} and password = #{password} and className = #{className}")
    Stu findByNamePassword(@Param("name")String name, @Param("password") String password, @Param("className")String className);

    @Select("select * from students")
    public List<Stu> findAll();

    @Insert("insert into students(name,password,className) values(#{name},#{password},#{className})")
    public int insert(@Param("name") String name, @Param("password") String password ,@Param("className") String className);

    @Delete("delete from students where name =#{name}")
    public int delete(String name);

  }

