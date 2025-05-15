package org.yuliu.spring3.demos.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yuliu.spring3.demos.web.mapper.StuMapper;

@Controller
@RequestMapping("/regedit")
public class RegeditController {

    @Autowired
    private StuMapper stuMapper;

    @RequestMapping("/regedit")
    @ResponseBody
    public String regedit(@RequestParam("name") String name,@RequestParam("password") String password,@RequestParam("className") String className) {
        int result = stuMapper.insert(name, password,className);
        return result > 0 ? "注册成功" : "注册失败";
    }
}
