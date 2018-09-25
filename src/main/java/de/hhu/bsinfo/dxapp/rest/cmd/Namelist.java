package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.nameservice.NameserviceEntryStr;
import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

public class Namelist extends AbstractRestCommand {

    public Namelist(){
        setInfo("namelist", "", "Get Namelist");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/namelist", (request, response) -> {
            NamelistRest entries = new NamelistRest(services.nameService.getAllEntries());
            return gson.toJson(entries);
        });
    }

    private class NamelistRest {
        List<NamelistEntryRest> namelist;

        public NamelistRest(ArrayList<NameserviceEntryStr> entries) {
            namelist = new ArrayList<NamelistEntryRest>();
            for (NameserviceEntryStr entry : entries) {
                namelist.add(new NamelistEntryRest(entry));
            }
        }
    }

    private class NamelistEntryRest {
        String name;
        String cid;

        public NamelistEntryRest(NameserviceEntryStr entry) {
            this.name = entry.getName();
            this.cid = ChunkID.toHexString(entry.getValue());
        }
    }
}
