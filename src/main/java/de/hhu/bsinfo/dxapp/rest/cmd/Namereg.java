package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
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
                return createError("Invalid Parameter, please use: /namereg?cid=[CID]?=name=[NameToRegister]");
            }

            long cid = ChunkID.parse(stringCid);

            if (cid != ChunkID.INVALID_ID) {
                services.nameService.register(cid, name);
                return createMessage("Registered '" + name + "'");
            } else {
                return createError("CID invalid");
            }
        });
    }
}
