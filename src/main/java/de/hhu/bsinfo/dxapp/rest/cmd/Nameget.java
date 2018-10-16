package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import spark.Service;

public class Nameget extends AbstractRestCommand {
    public Nameget(){
        setInfo("nameget", "name", "Get chunk by name from nameservice.");
    }
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/nameget", (request, response) -> {
            String name = request.queryParams("name");

            if (name == null){
                createError("Invalid Parameter, please use: /nameget?=[name]", response);
            }

            NameserviceService nameservice = services.getService(NameserviceService.class);

            long cid = nameservice.getChunkID(name, 2000);

            if (cid == ChunkID.INVALID_ID) {
                return createError("Could not get name entry for "+name+", does not exist", response);
            } else {
                return gson.toJson(ChunkID.toHexString(cid));
            }

        });
    }
}
