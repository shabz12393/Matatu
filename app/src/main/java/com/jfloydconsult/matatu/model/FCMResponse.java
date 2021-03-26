package com.jfloydconsult.matatu.model;

import java.util.List;

public class FCMResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canoniccal_ids;
    public List<Result> results;

    public FCMResponse() {
    }

    public FCMResponse(long multicast_id, int success, int failure, int canoniccal_ids, List<Result> results) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.canoniccal_ids = canoniccal_ids;
        this.results = results;
    }

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanoniccal_ids() {
        return canoniccal_ids;
    }

    public void setCanoniccal_ids(int canoniccal_ids) {
        this.canoniccal_ids = canoniccal_ids;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
