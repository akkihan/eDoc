package com.quascenta.edocs.service.impl;

import com.quascenta.edocs.dao.StorageDAO;
import com.quascenta.edocs.entities.StorageDetails;
import com.quascenta.edocs.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by Software_Development on 1/24/2018.
 */
@Component
@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    StorageDAO storageDAO;

    @Override
    public StorageDetails getStorageDetails(int storageID) {
        return storageDAO.getStorageDetails(storageID);
    }

    @Override
    public void add(StorageDetails storageDetails) {
        storageDAO.add(storageDetails);

    }

    @Override
    public void save(StorageDetails storageDetails) {
        storageDAO.save(storageDetails);
    }

    @Override
    public void delete(int tenantId) {
        storageDAO.delete(tenantId);
    }
}
