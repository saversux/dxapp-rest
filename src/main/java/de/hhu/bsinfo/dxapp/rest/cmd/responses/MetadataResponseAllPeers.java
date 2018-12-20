package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a metadata request
 * concerning all superpeers
 */
public class MetadataResponseAllPeers {
    private List<MetadataResponseOnePeer> m_metadata;

    public MetadataResponseAllPeers(List<MetadataResponseOnePeer> p_metadata) {
        this.m_metadata = p_metadata;
    }
}
