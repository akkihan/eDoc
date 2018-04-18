package com.quascenta.edocs.service;

import com.quascenta.edocs.entities.StorageDetails;
import org.springframework.stereotype.Service;

/**
 * Created by Software_Development on 1/24/2018.
 */
@Service
public interface StorageService {

    StorageDetails getStorageDetails(int storageId);

    void add(StorageDetails storageDetails);

    void save(StorageDetails storageDetails);

    void delete(int tenantId);
}
