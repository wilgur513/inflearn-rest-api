package com.wilgur513.inflearnrestapi.common;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.wilgur513.inflearnrestapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ErrorsResource extends RepresentationModel {
    private final Errors errors;

    public ErrorsResource(Errors errors) {
        add(linkTo(IndexController.class).withRel("index"));
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
