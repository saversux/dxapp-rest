package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a statsprint request
 */
public class StatsPrintResponse {
    @SerializedName("stats")
    private String m_htmlResponse;

    public StatsPrintResponse(String p_message, String p_interval) {
        this.m_htmlResponse = "<html><head><title>DXRAM Statistics</title><meta " +
                "http-equiv=\"refresh\" content=\"" + p_interval +
                "\" ></head><body> <pre> <code>" + p_message + "</code> </pre> </body> </html>";
    }
}
