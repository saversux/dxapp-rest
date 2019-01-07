package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after an applist request
 */
public class AppListResponse {
    @SerializedName("applications")
    private List<String> m_applist;

    public AppListResponse(List<String> p_applist) {
        this.m_applist = p_applist;
    }
}
