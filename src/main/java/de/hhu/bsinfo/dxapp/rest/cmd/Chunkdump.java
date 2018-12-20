/*
 * Copyright (C) 2018 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science,
 * Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.dxapp.rest.cmd;

import spark.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.AppRunRequest;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.ChunkdumpRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.chunk.data.ChunkAnon;

/**
 * Dump chunk to file
 *
 * @author Julien Bernhart, 2018-11-26
 *  @author Maximilian Loose
 *  Modifications:
 *  - in the case of a successful response, no response body is sent
 *  - parsing of the the String cid is not necessary anymmore because cids are sent as longs
 */
public class Chunkdump extends AbstractRestCommand {

    public Chunkdump() {
        setInfo("chunkdump", "name, cid", "Creates a Filedump of Chunk <cid> with <name>.");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/chunkdump", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            ChunkdumpRequest chunkdumpRequest;
            try {
                chunkdumpRequest = gson.fromJson(request.body(), ChunkdumpRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put cid and name into body as json.", response);
            }
            String fileName = chunkdumpRequest.getName();
            Long cid = chunkdumpRequest.getCid();

            if (cid == 0L || fileName == null) {
                return createError("Please put cid and name into body as json.", response);
            }

            if (cid == ChunkID.INVALID_ID) {
                return createError("Invalid ChunkID", response);
            }

            ChunkAnonService chunkAnon = services.getService(ChunkAnonService.class);

            ChunkAnon[] chunks = new ChunkAnon[1];
            if (chunkAnon.getAnon().get(chunks, cid) != 1) {
                return createError("Getting chunk " + ChunkID.toHexString(cid) + " failed: " + chunks[0].getState(),
                        response);
            }

            ChunkAnon chunk = chunks[0];

            //p_stdout.printfln("Dumping chunk 0x%X to file %s...", cid, fileName);

            File file = new File(fileName);

            if (file.exists()) {
                if (!file.delete()) {
                    return createError("Deleting existing file " + fileName + " failed", response);
                }
            }

            if (!file.createNewFile()) {
                return createError("Could not create file.", response);
            }

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
            response.status(200);
            return "";
        });
    }
}
