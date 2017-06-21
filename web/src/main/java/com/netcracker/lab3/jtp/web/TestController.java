package com.netcracker.lab3.jtp.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

    @RequestMapping("/test")
    @ResponseBody
    public ModelAndView testPage(Model model) {
        ModelAndView mav = new ModelAndView("test");
        return mav;
    }

}