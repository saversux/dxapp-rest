package de.hhu.bsinfo.dxhelloworld;



import com.google.gson.Gson;
import de.hhu.bsinfo.dxram.DXRAM;
import de.hhu.bsinfo.dxram.app.AbstractApplication;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.dxram.data.DataStructure;
import de.hhu.bsinfo.dxram.data.DummyDataStructure;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

import java.util.List;
import java.util.ArrayList;


public class RestServerApplication extends AbstractApplication{

    private static Service server;
    private boolean run;
    private ChunkService chunkService;
    private BootService bootService;
    private Gson gson;

    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return DXRAM.VERSION;
    }

    @Override
    public String getApplicationName() {
        return "RestServer";
    }

    @Override
    public boolean useConfigurationFile() {
        return false;
    }

    @Override
    public void main() {
        chunkService = super.getService(ChunkService.class);
        bootService = super.getService(BootService.class);
        NameserviceService nameService = super.getService(NameserviceService.class);
        gson = new Gson();

        run = true;

        System.out.println("Starting REST Server ............. :)");

        startServer();

        String[] commands = {"nodelist, chunklist, chunkget"};

        server.get("/", (req, res) -> gson.toJson(commands));

        server.get("/nodelist", (request, response) -> {
            System.out.println(bootService.getOnlineNodeIDs().size());
            List<Short> nodes  = bootService.getOnlineNodeIDs();
            List<String> stringNodes = new ArrayList();
            for (Short node : nodes) {
                stringNodes.add(Integer.toHexString(node & 0xffff));
            }

            String output = gson.toJson(stringNodes);

            return output;
        });

        server.get("/chunklist/:nodeId", (request, response) -> {
            short nid = NodeID.parse(request.params(":nodeId"));

            if(nid != NodeID.INVALID_ID){
                return chunkService.getAllLocalChunkIDRanges(nid);
            }else{
                return "NID invalid.";
            }

        });

        server.get("/chunkget/:chunkId", (request, response) -> {
            long cid = ChunkID.parse(request.params(":chunkId"));

            if(cid != ChunkID.INVALID_ID){
                return "dummy";
            }else{
                return "CID invalid.";
            }

        });

        server.get("/chunklist/", (request, response) -> "Please enter NID as first parameter.");


        while(run){
            //keep server alive
        }
    }


    private static void startServer(){
        server = Service.ignite().port(8009);


    }

    @Override
    public void signalShutdown() {
        server.stop();
        run = false;

    }

}
