package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a nodelist request
 */
public class NodeListResponse {
    @SerializedName("nodes")
    private List<String> m_nodes;

    public NodeListResponse(List<String> p_nodes) {
        this.m_nodes = p_nodes;
    }
}
