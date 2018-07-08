package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.dxram.nameservice.NameserviceEntryStr;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ResponseError;
import de.hhu.bsinfo.rest.ResponseMessage;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.util.List;

public class Namereg extends AbstractRestCommand {
    @Override
    public void register(Service server, ServiceHelper services) {
        //server.get("/namereg", (request, response) -> createError("Invalid Parameter, please use: /namereg/:cid/:name"));
        server.get("/namereg", (request, response) -> {
            String stringCid = request.queryParams("cid");
            String name = request.queryParams("name");

            if (stringCid == null || name == null){
                return createError("Invalid Parameter, please use: /namereg?cid=[CID]?=name=[NameToRegister]");
            }

            long cid = ChunkID.parse(stringCid);

            if(cid != ChunkID.INVALID_ID){
                services.nameService.register(cid,name);
                return createMessage("Registered '"+name+"'");
            }else{
                return createError("CID invalid");
            }

        });
    }
}
