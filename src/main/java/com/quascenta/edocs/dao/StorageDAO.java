package com.quascenta.edocs.dao;

import com.quascenta.edocs.entities.StorageDetails;

/**
 * Created by Software_Development on 1/24/2018.
 */
public interface StorageDAO {

    StorageDetails getStorageDetails(int storageId);
    void add(StorageDetails storageDetails);

    void save(StorageDetails storageDetails);

    void delete(int tenantId);
}
