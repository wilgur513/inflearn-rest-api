package com.wilgur513.inflearnrestapi.events;

import com.wilgur513.inflearnrestapi.accounts.Account;
import com.wilgur513.inflearnrestapi.accounts.AccountAdapter;
import com.wilgur513.inflearnrestapi.accounts.CurrentUser;
import com.wilgur513.inflearnrestapi.common.ErrorsResource;
import com.wilgur513.inflearnrestapi.index.IndexController;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/events", produces = "application/hal+json;charset=UTF-8")
@RequiredArgsConstructor
public class EventController {
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final EventValidator eventValidator;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        Event newEvent = eventService.createEvent(eventDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();

        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resource-create-event").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> assembler,
                                      @CurrentUser Account currentUser) {
        Page<Event> page = eventRepository.findAll(pageable);
        PagedModel<EventResource> body = assembler.toModel(page, entity -> EventResource.of(entity));
        body.add(Link.of("/docs/index.html#resource-query-events").withRel("profile"));
        if(currentUser != null) {
            body.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryEvent(@PathVariable int id) {
        Optional<Event> event = eventRepository.findById(id);

        if(event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EventResource resource = EventResource.of(event.get());
        resource.add(Link.of("docs/index.html#resource-query-event").withRel("profile"));
        return ResponseEntity.ok(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable int id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        Optional<Event> optionalEvent = eventRepository.findById(id);

        if(optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        modelMapper.map(eventDto, event);
        eventRepository.save(event);

        EventResource resource = EventResource.of(event);
        resource.add(Link.of("docs/index.html#resource-update-event").withRel("profile"));
        return ResponseEntity.ok(resource);
    }


    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
