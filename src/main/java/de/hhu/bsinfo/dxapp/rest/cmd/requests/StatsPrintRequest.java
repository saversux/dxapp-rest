package de.hhu.bsinfo.dxapp.rest.cmd.requests;

import com.google.gson.annotations.Expose;

/**
 * @author Maximilian Loose
 * created a new request class for handling StatsPrint requests
 */
public class StatsPrintRequest {
    @Expose
    private String interval;

    public String getInterval() {
        return interval;
    }
}
