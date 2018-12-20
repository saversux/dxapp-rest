package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a metadata request
 * concerning a specific peer
 */
public class MetadataResponseOnePeer {
    @SerializedName("nid")
    private String m_nid;
    @SerializedName("metadata")
    private String m_metadata;

    public MetadataResponseOnePeer(String p_nid, String p_metadata) {
        m_nid = p_nid;
        m_metadata = p_metadata;
    }
}

