package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.chunk.data.ChunkAnon;
import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import spark.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Chunkdump extends AbstractRestCommand {

    public Chunkdump(){
        setInfo("chunkdump", "name, cid", "Creates a Filedump of Chunk <cid> with <name>.");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkdump", (request, response) -> {

            String fileName = request.queryParams("name");
            String stringCid = request.queryParams("cid");

            if (stringCid == null || fileName == null) {
                return createError("Invalid Parameter, please use: /chunkdump?cid=[CID]?=name=[NAME]", response);
            }

            long cid = ChunkID.parse(stringCid);

            if (cid == ChunkID.INVALID_ID) {
                return createError("Invalid ChunkID", response);
            }


            ChunkAnonService chunkAnon = services.getService(ChunkAnonService.class);

            ChunkAnon[] chunks = new ChunkAnon[1];
            if (chunkAnon.getAnon().get(chunks, cid) != 1) {
                return createError("Getting chunk " + ChunkID.toHexString(cid) + " failed: " + chunks[0].getState(), response);
            }

            ChunkAnon chunk = chunks[0];

            //p_stdout.printfln("Dumping chunk 0x%X to file %s...", cid, fileName);

            File file = new File(fileName);

            if (file.exists()) {
                if (!file.delete()) {
                    return createError("Deleting existing file " + fileName + " failed", response);
                } else {
                    RandomAccessFile raFile;
                    try {
                        raFile = new RandomAccessFile(file, "rw");
                    } catch (final FileNotFoundException ignored) {
                        return createError("Dumping chunk failed, file not found", response);
                    }

                    try {
                        raFile.write(chunk.getData());
                    } catch (final IOException e) {
                        return createError("Dumping chunk failed: " + e.getMessage(), response);
                    }

                    try {
                        raFile.close();
                    } catch (final IOException ignore) {

                    }
                    return createMessage("Chunk dumped");

                }
            } else {
                return createError("File does not exist.", response);
            }
        });
    }
}
