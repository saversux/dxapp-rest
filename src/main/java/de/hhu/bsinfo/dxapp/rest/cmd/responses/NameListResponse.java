package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import de.hhu.bsinfo.dxram.nameservice.NameserviceEntryStr;

/**
 * @author Maximilian Loose
 * created a new response class for sending responses after a namelist request
 */
public class NameListResponse {
    List<NamelistEntryRest> namelist;

    public NameListResponse(ArrayList<NameserviceEntryStr> entries) {
        namelist = new ArrayList<NamelistEntryRest>();
        for (NameserviceEntryStr entry : entries) {
            namelist.add(new NamelistEntryRest(entry));
        }
    }
    private class NamelistEntryRest {
        @SerializedName("name")
        private String m_name;
        @SerializedName("cid")
        private long m_cid;

        public NamelistEntryRest(NameserviceEntryStr entry) {
            this.m_name = entry.getName();
            this.m_cid = entry.getValue();
        }
    }
}

