package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.data.ChunkAnon;
import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.dxram.nameservice.NameserviceEntryStr;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class Chunkdump extends AbstractRestCommand {
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkdump", (request, response) -> {

            String fileName = request.queryParams("name");
            String stringCid = request.queryParams("cid");

            if (stringCid == null || fileName == null){
                return createError("Invalid Parameter, please use: /chunkdump?cid=[CID]?=name=[NAME]");
            }

            long cid = ChunkID.parse(stringCid);

            if(cid == ChunkID.INVALID_ID){
                return createError("Invalid ChunkID");
            }


            ChunkAnonService chunkAnon = services.chunkAnonService;

            ChunkAnon[] chunks = new ChunkAnon[1];
            if (chunkAnon.get(chunks, cid) != 1) {
                return createError("Getting chunk 0x"+cid+" failed: "+chunks[0].getState());
            }

            ChunkAnon chunk = chunks[0];

            //p_stdout.printfln("Dumping chunk 0x%X to file %s...", cid, fileName);

            File file = new File(fileName);

            if (file.exists()) {
                if (!file.delete()) {
                    return createError("Deleting existing file "+fileName+" failed");
                } else {
                    RandomAccessFile raFile;
                    try {
                        raFile = new RandomAccessFile(file, "rw");
                    } catch (final FileNotFoundException ignored) {
                        return createError("Dumping chunk failed, file not found");
                    }

                    try {
                        raFile.write(chunk.getData());
                    } catch (final IOException e) {
                        return createError("Dumping chunk failed: "+ e.getMessage());
                    }

                    try {
                        raFile.close();
                    } catch (final IOException ignore) {

                    }
                    return createMessage("Chunk dumped");

                }
            }else{
                return createError("File does not exist.");
            }

        });
    }
}
