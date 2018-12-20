package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a chunklist request
 */
public class ChunkListResponse {
    @SerializedName("localChunkRanges")
    private String m_localChunkRanges;
    @SerializedName("migratedChunkRanges")
    private String m_migratedChunkRanges;

    public ChunkListResponse(String p_localChunkRanges, String p_migratedChunkRanges) {
        m_localChunkRanges = p_localChunkRanges;
        m_migratedChunkRanges = p_migratedChunkRanges;
    }
}
