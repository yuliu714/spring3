package org.yuliu.spring3.demos.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yuliu.spring3.demos.web.mapper.StuMapper;
import org.yuliu.spring3.demos.web.pojo.Stu;

import java.util.List;

@RestController
public class StuController {

    @Autowired
    private StuMapper stuMapper;

    @RequestMapping("/stuFindAll")
    public List<Stu>  stuFindAll() {
        List<Stu> stulist = stuMapper.findAll();
        for (Stu stu : stulist) {
            System.out.println(stu);
        }
        return stulist;
    }
}
