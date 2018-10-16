package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import spark.Service;

public class Namereg extends AbstractRestCommand {

    public Namereg(){
        setInfo("namereg", "cid, name", "Register Chunk <cid> with <name>");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/namereg", (request, response) -> {
            String stringCid = request.queryParams("cid");
            String name = request.queryParams("name");

            if (stringCid == null || name == null) {
                return createError("Invalid Parameter, please use: /namereg?cid=[CID]?=name=[NameToRegister]", response);
            }

            long cid = ChunkID.parse(stringCid);

            if (cid != ChunkID.INVALID_ID) {
                services.getService(NameserviceService.class).register(cid, name);
                return createMessage("Registered '" + name + "'");
            } else {
                return createError("CID invalid", response);
            }
        });
    }
}
