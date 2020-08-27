package com.sawoo.pipeline.api.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class DummySecurityController {

    @RequestMapping(
            value = "/public/hello",
            method = RequestMethod.GET,
            produces = { MediaType.TEXT_HTML_VALUE })
    @ResponseBody
    public String helloPublic() {
        return "<h3>Hello, here I am. Public Url!!!</h3>";
    }

    @RequestMapping(
            value = "/private/main/hello",
            method = RequestMethod.GET,
            produces = { MediaType.TEXT_HTML_VALUE })
    @ResponseBody
    public String helloPrivate() {
        return "<h3>Hello, here I am!!! This is protected URL by authentication</h3>";
    }

    @RequestMapping(
            value = "/private/admin/hello",
            method = RequestMethod.GET,
            produces = { MediaType.TEXT_HTML_VALUE })
    @ResponseBody
    public String helloPrivateAdmin() {
        return "<h3>Hello, here I am!!! This is protected URL by authentication and for Admin Role</h3>";
    }
}


