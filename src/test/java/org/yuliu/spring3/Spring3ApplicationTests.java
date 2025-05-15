package org.yuliu.spring3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yuliu.spring3.demos.web.mapper.StuMapper;
import org.yuliu.spring3.demos.web.pojo.Stu;

import java.util.List;

@SpringBootTest
class Spring3ApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private StuMapper stuMapper;




    @Test
    void stuFindAll() {
        List<Stu> stulist = stuMapper.findAll();
        for (Stu stu : stulist) {
            System.out.println(stu);
        }
    }
    @Test
    public void insertTest() {

    }
    @Test
    public void deleteTest() {
        System.out.println(stuMapper.delete("12345"));
    }


    @Test
    void test() {
        System.out.println("Hello, World!");
    }

}
