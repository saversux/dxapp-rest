package de.hhu.bsinfo.rest;

import com.google.gson.Gson;
import de.hhu.bsinfo.dxram.DXRAM;
import de.hhu.bsinfo.dxram.app.AbstractApplication;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.rest.cmd.*;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

public class RestServerApplication extends AbstractApplication {

    private static Service server;
    private boolean run;
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
        ServiceHelper services = new ServiceHelper(super.getService(BootService.class), super.getService(NameserviceService.class), super.getService(ChunkService.class));

        gson = new Gson();
        run = true;

        System.out.println("Starting REST Server ............. :)");
        startServer();
        String[] commands = {"nodelist, chunklist, chunkget, namelist, namereg"};

        server.get("/", (req, res) -> gson.toJson(commands));

        List<AbstractRestCommand> restCommands = new ArrayList<>();
        restCommands.add(new Chunkget());
        restCommands.add(new Chunklist());
        restCommands.add(new Nodelist());
        restCommands.add(new Namereg());
        restCommands.add(new Namelist());

        for (AbstractRestCommand c : restCommands) {
            c.register(server, services);
        }

        while (run) {
            //keep server alive
        }
    }


    private static void startServer() {
        server = Service.ignite().port(8009);
    }

    @Override
    public void signalShutdown() {
        server.stop();
        run = false;
    }

}
