package com.edocs.dao.impl;

import com.edocs.dao.EventLogDAO;
import com.edocs.entities.Event_Log;
import com.edocs.utils.QueryConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Software_Development on 12/17/2017.
 */
@Component
@Transactional
@Repository
public class Event_LogDAOImpl implements EventLogDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Override
    public void save(Event_Log eventLog) {

        jdbcTemplate.update(QueryConstants.INSERT_EVENT_LOG,new Object[]{eventLog.getUser_Id(),eventLog.getUsername(),eventLog.getActivity(),eventLog.getIp_Address(),eventLog.getType(),
                                                                         eventLog.getTenant_Id(),eventLog.getCreated_Date(),eventLog.getComponent_Id()});


    }
}
