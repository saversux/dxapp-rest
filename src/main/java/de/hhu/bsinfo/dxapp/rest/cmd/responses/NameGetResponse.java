package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a nameget request
 */
public class NameGetResponse {
    @SerializedName("cid")
    private long m_cid;

    public NameGetResponse(long p_cid) {
        this.m_cid = p_cid;
    }
}
