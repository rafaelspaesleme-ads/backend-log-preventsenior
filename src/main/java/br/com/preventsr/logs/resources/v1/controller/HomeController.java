package br.com.preventsr.logs.resources.v1.controller;

import br.com.preventsr.logs.utils.functions.TemplatesFunctions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/")
public class HomeController {
    private final TemplatesFunctions templatesFunctions;

    public HomeController(TemplatesFunctions templatesFunctions) {
        this.templatesFunctions = templatesFunctions;
    }


    @ApiIgnore
    @CrossOrigin
    @GetMapping(value = "/")
    public ResponseEntity<?> start() {
        String s = templatesFunctions.initalPageHtml();
        return s != null ? ResponseEntity.ok(s) : ResponseEntity.noContent().build();
    }

}
