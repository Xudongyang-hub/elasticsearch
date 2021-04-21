package controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xu
 * @Date: 2021-04-16 17:45
 * @description: index
 */
@RestController
public class IndexController {


    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }
}
