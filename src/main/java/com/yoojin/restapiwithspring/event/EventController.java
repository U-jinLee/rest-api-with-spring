package com.yoojin.restapiwithspring.event;

import com.yoojin.restapiwithspring.common.ErrorsResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@Controller
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;


    @Transactional(readOnly = true)
    @GetMapping("")
    public ResponseEntity getEvents(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<Event> events = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> pagedModel = assembler.toModel(events, e -> new EventResource((Event) e));
        pagedModel.add(Link.of("/docs/index.html#resource-events-list").withRel("profile"));
        return ResponseEntity.status(HttpStatus.OK).body(pagedModel);
    }
    @GetMapping("{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        EventResource eventResource = new EventResource(optionalEvent.get());
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.status(HttpStatus.OK).body(eventResource);
    }

    @PostMapping("")
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withSelfRel().withRel("update"));
        eventResource.add(Link.of("/docs/index.html#resource-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateEvent(@PathVariable(name = "id") Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if (optionalEvent.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        if(errors.hasErrors()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorsResource(errors));
        this.eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorsResource(errors));
        //eventDto를 존재하는 이벤트로 변환한다.
        Event existingEvent = optionalEvent.get();
        this.modelMapper.map(eventDto, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(Link.of("/docs/index.html#resource-events-update").withRel("profile"));
        return ResponseEntity.status(HttpStatus.OK).body(eventResource);
    }

}
