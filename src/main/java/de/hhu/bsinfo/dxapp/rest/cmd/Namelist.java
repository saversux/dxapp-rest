package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.nameservice.NameserviceEntryStr;
import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import spark.Service;

import java.util.List;

public class Namelist extends AbstractRestCommand {

    public Namelist(){
        setInfo("namelist", "", "Get Namelist");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/namelist", (request, response) -> {
            List<NameserviceEntryStr> entries = services.nameService.getAllEntries();

            return gson.toJson(entries);
        });
    }
}
