package com.quascenta.edocs.dao;

import com.quascenta.edocs.entities.DocumentObject;
import com.quascenta.edocs.entities.Event_Log;

/**
 * Created by Software_Development on 12/17/2017.
 */
public interface EventLogDAO {

    void save(Event_Log eventLog);
}
