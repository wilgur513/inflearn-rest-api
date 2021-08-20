package com.wilgur513.inflearnrestapi.events;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public Event createEvent(EventDto eventDto) {
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        return eventRepository.save(event);
    }
}
