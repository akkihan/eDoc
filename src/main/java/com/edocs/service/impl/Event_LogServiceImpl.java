package com.edocs.service.impl;

import com.edocs.dao.EventLogDAO;
import com.edocs.entities.Event_Log;
import com.edocs.service.Event_LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by Software_Development on 12/17/2017.
 */
@Component
@Service
public class Event_LogServiceImpl implements Event_LogService {

    @Autowired
    EventLogDAO eventLogDAO;


    @Override
    public void save(Event_Log eventLog) {
        eventLogDAO.save(eventLog);
    }
}
