package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

public class ChunkGetResponse {
    @SerializedName("content")
    private Object m_content;

    public ChunkGetResponse(Object m_content) {
        this.m_content = m_content;
    }
}
