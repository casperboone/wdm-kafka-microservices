package nl.tudelft.wdm.group1.common.model;

public enum OrderStatus {
    PROCESSING,
    FAILED_DUE_TO_LACK_OF_STOCK,
    FAILED_DUE_TO_LACK_OF_PAYMENT,
    SUCCEEDED
}