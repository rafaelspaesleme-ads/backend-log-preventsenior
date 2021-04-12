package br.com.preventsr.logs.resources.v1.controller;

import br.com.preventsr.logs.utils.functions.TemplatesFunction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/")
public class HomeController {
    private final TemplatesFunction templatesFunction;

    public HomeController(TemplatesFunction templatesFunction) {
        this.templatesFunction = templatesFunction;
    }


    @ApiIgnore
    @CrossOrigin
    @GetMapping(value = "/")
    public ResponseEntity<?> start() {
        String s = templatesFunction.initalPageHtml();
        return s != null ? ResponseEntity.ok(s) : ResponseEntity.noContent().build();
    }

}
