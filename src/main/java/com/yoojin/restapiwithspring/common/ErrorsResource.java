package com.yoojin.restapiwithspring.common;

import com.yoojin.restapiwithspring.Index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    public ErrorsResource(Errors errors, Link... links) {
        super(errors, Arrays.asList(links));
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}