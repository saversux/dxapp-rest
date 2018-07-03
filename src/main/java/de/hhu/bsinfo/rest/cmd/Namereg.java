package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.dxram.nameservice.NameserviceEntryStr;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.util.List;

public class Namereg extends AbstractRestCommand {
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/namereg/:cid/:name", (request, response) -> {
            long cid = ChunkID.parse(request.params(":cid"));
            String name = request.params(":name");

            if(cid != ChunkID.INVALID_ID){
                services.nameService.register(cid,name);
                return "ok";
            }else{
                return "CID invalid.";
            }

        });
    }
}
