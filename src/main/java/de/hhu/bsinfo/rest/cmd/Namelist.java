package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.nameservice.NameserviceEntryStr;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

public class Namelist extends AbstractRestCommand {
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/namelist", (request, response) -> {
            List<NameserviceEntryStr> entries = services.nameService.getAllEntries();

            String output = gson.toJson(entries);

            return output;
        });
    }
}
