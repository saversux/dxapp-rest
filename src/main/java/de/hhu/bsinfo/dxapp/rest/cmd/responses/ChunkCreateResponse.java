package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

public class ChunkCreateResponse {
    @SerializedName("cid")
    private String m_cid;

    public ChunkCreateResponse(String p_cid) {
        this.m_cid = p_cid;
    }
}
