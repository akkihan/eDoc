package com.quascenta.edocs.service;

import com.quascenta.edocs.entities.DocumentObject;
import com.quascenta.edocs.entities.Event_Log;
import org.springframework.stereotype.Service;

/**
 * Created by Software_Development on 12/17/2017.
 */
@Service
public interface Event_LogService {
    public void save(Event_Log eventLog);
}
