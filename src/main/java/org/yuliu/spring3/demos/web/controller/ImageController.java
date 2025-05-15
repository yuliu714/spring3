package org.yuliu.spring3.demos.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileWriter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/upload")
public class ImageController {

    private static final String UPLOAD_DIR = "C:/uploads";
    private static final String URL_PREFIX = "/uploads/";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");






    // 工具方法：构建用户上传路径
    private String getUserPath(HttpSession session) {
        String className = (session.getAttribute("className") != null) ? session.getAttribute("className").toString() : "1";
        return Paths.get(UPLOAD_DIR, className ).toString();
    }

    // 工具方法：构建用户的 URL 前缀
    private String getUserUrlPrefix(HttpSession session) {
        String name = (session.getAttribute("name") != null) ? session.getAttribute("name").toString() : "测试账号1";
        String className = (session.getAttribute("className") != null) ? session.getAttribute("className").toString() : "1";
        return URL_PREFIX + className + "/" ;
    }


    // 列出用户上传的图片
    @ResponseBody
    @GetMapping("/list_stu")
    public List<String> listImages(HttpSession session) {
        String path = getUserPath(session);
        File folder = new File(path);
        System.out.println("图片目录路径: " + folder.getAbsolutePath());

        String name = session.getAttribute("name").toString();

        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("没有找到图片文件");
            return Collections.emptyList();
        }

        String urlPrefix = getUserUrlPrefix(session);

        return Arrays.stream(files)
                .filter(file -> {
                    // 必须是文件，扩展名合法，并且文件名中包含用户名
                    return file.isFile()
                            && file.getName().matches(".*\\.(jpg|jpeg|png|gif|bmp)$")
                            && file.getName().contains(name);
                })
                .map(file -> {
                    try {
                        return urlPrefix + URLEncoder.encode(file.getName(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return "";
                    }
                })
                .filter(url -> !url.isEmpty())
                .collect(Collectors.toList());
    }


    // 上传图片
    @PostMapping("/image")
    public String uploadImage(@RequestParam("file") MultipartFile file,
                              HttpSession session,
                              @RequestParam("imgName") String imgName) {

        if (file.isEmpty()) {
            return "redirect:/error.html";
        }

        try {
            String path = getUserPath(session);
            String urlPrefix = getUserUrlPrefix(session);

            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            }

            System.out.println("上传的文件后缀是：" + "jpg");

            // 非法扩展名处理
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return "redirect:/error.html";
            }

            // 构建保存路径
            if (imgName == null || imgName.trim().isEmpty()) {
                imgName = String.valueOf(System.currentTimeMillis());
            }

            File uploadDir = new File(path);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String name=session.getAttribute("name").toString();

            File dest = new File(uploadDir, name+"_"+imgName + "." + extension);
            System.out.println("保存路径：" + dest.getAbsolutePath());

            // 写入文件
            file.transferTo(dest);

            // 记录路径
            session.setAttribute("lastUploadPath", dest.getAbsolutePath());
            if(session.getAttribute("className").equals("23art1") || session.getAttribute("className").equals("23art2") ){
                return "redirect:/uploadImg/imageupload2.html";
            }
            return "redirect:/uploadImg/imageupload.html";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/error.html";
        }
    }

    // 获取图片路径接口（可用于展示页面）
    @RequestMapping("/getImagePath")
    public String getImagePath(HttpSession session) {
        String path = (session.getAttribute("lastUploadPath") != null)
                ? session.getAttribute("lastUploadPath").toString()
                : null;

        if (path == null) {
            return "redirect:/error.html";
        }

        return "showimage";
    }



    @PostMapping("/file")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/error";
        }
        try {
            String originalFilename = file.getOriginalFilename();

            // 保存文件
            Files.write(Path.of(UPLOAD_DIR+File.separator+originalFilename), file.getBytes());

            // 对中文消息进行 URL 编码
            String encodedMessage = URLEncoder.encode("文件上传成功", StandardCharsets.UTF_8.toString());
            return "redirect:/uploadFile/fileupload.html?message=" + encodedMessage;
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/error.html";
        }
    }

    @PostMapping("/text")
    public String handleTextUpload(@RequestParam("multilineText") String text,HttpSession session) throws UnsupportedEncodingException {
        // 打印或处理上传的多行文本
        System.out.println("收到的多行文本：\n" + text);
        String name = session.getAttribute("name").toString();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File file = new File(UPLOAD_DIR +File.separator+name+timestamp+".txt");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/yourPage?message=fail！";
        }

        String encodedMessage = URLEncoder.encode("文件上传成功", StandardCharsets.UTF_8.toString());
        // 处理完成后，重定向或返回视图
        return "redirect:/uploadFile/fileupload.html?message=" + encodedMessage;
    }




}
