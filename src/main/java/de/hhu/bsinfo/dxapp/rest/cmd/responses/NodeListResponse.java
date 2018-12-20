package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import java.util.List;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a nodelist request
 */
public class NodeListResponse {
    private List<String> m_nodes;

    public NodeListResponse(List<String> p_nodes) {
        this.m_nodes = p_nodes;
    }
}
