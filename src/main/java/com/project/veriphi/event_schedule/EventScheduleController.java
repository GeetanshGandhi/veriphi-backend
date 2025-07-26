package com.project.veriphi.event_schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/eventSchedule")
public class EventScheduleController {
    @Autowired
    EventScheduleService esService;


}
