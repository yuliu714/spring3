package org.yuliu.spring3.demos.web.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.yuliu.spring3.demos.web.mapper.StuMapper;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/submitName")
public class Submit {

    @Autowired
    private StuMapper stuMapper;

    @PostMapping
    public String  submitName(@RequestParam("name") String name,@RequestParam("className")String className ,HttpSession session) throws UnsupportedEncodingException {

            session.setAttribute("name", name );
            session.setAttribute("className", className);
            return "redirect:/uploadImg/imageupload.html";
        }


    @Controller
    @RequestMapping("/submitFile")
    public class SubmitFile {

        @Autowired
        private StuMapper stuMapper;

        @PostMapping
        public String  submitName(@RequestParam("name") String name ,HttpSession session) throws UnsupportedEncodingException {

            session.setAttribute("name", name );
            return "redirect:/uploadFile/fileupload.html";
        }


    }

}

