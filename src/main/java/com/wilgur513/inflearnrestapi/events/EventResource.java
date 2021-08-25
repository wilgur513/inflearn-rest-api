package com.wilgur513.inflearnrestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends RepresentationModel<EventResource> {
    @JsonUnwrapped
    private final Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public static EventResource of(Event event) {
        EventResource resource = new EventResource(event);
        resource.add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
        return resource;
    }

    public Event getEvent() {
        return event;
    }
}
