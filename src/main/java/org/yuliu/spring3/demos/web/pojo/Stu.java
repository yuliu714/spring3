package org.yuliu.spring3.demos.web.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stu {
    private String id;
    private String name;
    private String password;
    private String className;
}
