package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a nodeinfo request
 * concerning a specific peer
 */
public class NodeInfoResponse {
    @SerializedName("nid")
    private String m_nid;
    @SerializedName("role")
    private String m_role;
    @SerializedName("address")
    private String m_address;
    @SerializedName("capabilities")
    private String m_capabilities;

    public NodeInfoResponse(String p_nid, String p_role, String p_address, String p_capabilities) {
        this.m_nid = p_nid;
        this.m_role = p_role;
        this.m_address = p_address;
        this.m_capabilities = p_capabilities;
    }
}

