package com.edocs.entities;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Software_Development on 12/28/2017.
 */
public class StorageDetails {

    private int storageId;

    private int tenantId;

    private double totalAllowedStorage;

    private double totalUsedStorage;

    private double totalFreeStorage;

    private double totalUsedStorageInPercentage;

    private double totalFreeStorageInPercentage;


    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public double getTotalAllowedStorage() {
        return totalAllowedStorage;
    }

    public void setTotalAllowedStorage(double totalAllowedStorage) {
        this.totalAllowedStorage = totalAllowedStorage;
    }

    public double getTotalUsedStorage() {
        return totalUsedStorage;
    }

    public void setTotalUsedStorage(double totalUsedStorage) {
        this.totalUsedStorage = totalUsedStorage;
    }

    public double getTotalFreeStorage() {
        return totalFreeStorage;
    }

    public void setTotalFreeStorage(double totalFreeStorage) {
        this.totalFreeStorage = totalFreeStorage;
    }

    public double getTotalUsedStorageInPercentage() {
        NumberFormat formatter = new DecimalFormat("#0.00");
        totalUsedStorageInPercentage  = Double.valueOf(formatter.format(totalUsedStorageInPercentage));
        return totalUsedStorageInPercentage;
    }

    public void setTotalUsedStorageInPercentage(double totalUsedStorageInPercentage) {
        this.totalUsedStorageInPercentage = totalUsedStorageInPercentage;
    }

    public double getTotalFreeStorageInPercentage() {
        NumberFormat formatter = new DecimalFormat("#0.00");
        totalFreeStorageInPercentage  = Double.valueOf(formatter.format(totalFreeStorageInPercentage));
        return totalFreeStorageInPercentage;
    }

    public void setTotalFreeStorageInPercentage(double totalFreeStorageInPercentage) {
        this.totalFreeStorageInPercentage = totalFreeStorageInPercentage;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
}
