package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a chunkcreate request
 */
public class ChunkCreateResponse {
    @SerializedName("cid")
    private long m_cid;

    public ChunkCreateResponse(long p_cid) {
        this.m_cid = p_cid;
    }
}
