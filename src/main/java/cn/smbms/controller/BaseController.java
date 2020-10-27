package cn.smbms.controller;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;
@Controller
@RequestMapping("/user")
public class BaseController  {

    public void initBinder(WebDataBinder webDataBinder){
        System.out.println("initBinder======================");
        webDataBinder.registerCustomEditor(Date.class,new CustomDateEditor(new SimpleDateFormat("yy-MM-dd"),true));
    }
}
