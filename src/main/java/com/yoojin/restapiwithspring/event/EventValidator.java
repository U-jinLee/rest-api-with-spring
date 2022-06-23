package com.yoojin.restapiwithspring.event;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors) {
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
            errors.reject("wrongPrice", "Values for Prices are wrong");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();

        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getEndEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "Values for endEventDateTime are wrong");
        }

    }
}
