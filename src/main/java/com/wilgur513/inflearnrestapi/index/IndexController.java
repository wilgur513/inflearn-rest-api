package com.wilgur513.inflearnrestapi.index;

import com.wilgur513.inflearnrestapi.events.EventController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api")
public class IndexController {
    @GetMapping
    public RepresentationModel index() {
        RepresentationModel model = new RepresentationModel();
        model.add(linkTo(EventController.class).withRel("events"));
        return model;
    }
}
