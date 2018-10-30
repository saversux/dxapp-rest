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

import java.nio.ByteBuffer;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.Requests.ChunkputRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.chunk.data.ChunkAnon;

public class Chunkput extends AbstractRestCommand {

    public Chunkput() {
        setInfo("chunkput", "cid, data, type", "Put <data> with <type> on Chunk with <cid>");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkput", (request, response) -> {
            ChunkputRequest chunkputRequest;
            try {
                chunkputRequest = gson.fromJson(request.body(), ChunkputRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put cid, type and data into body as json.", response);
            }

            String stringCid = chunkputRequest.getCid();
            String data = chunkputRequest.getData();
            String type = chunkputRequest.getType();

            ChunkAnonService chunkAnon = services.getService(ChunkAnonService.class);
            int offset = 0;

            if (stringCid == null || data == null || type == null) {
                return createError("Please put cid, type and data into body as json.", response);
            }

            if (!isChunkID(stringCid)) {
                return createError("Invalid ChunkID", response);
            }

            long cid = ChunkID.parse(stringCid);

            if (cid == ChunkID.INVALID_ID) {
                return createError("No cid specified", response);
            }

            if (data == null) {
                return createError("No data specified", response);
            }

            if (ChunkID.getLocalID(cid) == 0) {
                return createError("Put of index chunk is not allowed", response);
            }

            ChunkAnon[] chunks = new ChunkAnon[1];

            if (chunkAnon.getAnon().get(chunks, cid) != 1) {
                return createError("Getting chunk " + ChunkID.toHexString(cid) + " failed: " + chunks[0].getState(),
                        response);
            }

            ChunkAnon chunk = chunks[0];
            if (offset == -1) {
                // wipe chunk
                for (int i = 0; i < chunk.getDataSize(); i++) {
                    chunk.getData()[i] = 0;
                }
                offset = 0;
            }

            if (offset > chunk.sizeofObject()) {
                offset = chunk.sizeofObject();
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(chunk.getData());
            byteBuffer.position(offset);

            switch (type) {
                case "str":
                    byte[] bytes = data.getBytes(java.nio.charset.StandardCharsets.US_ASCII);

                    try {
                        int size = byteBuffer.capacity() - byteBuffer.position();
                        if (bytes.length < size) {
                            size = bytes.length;
                        }
                        byteBuffer.put(bytes, 0, size);
                    } catch (final Exception ignored) {
                        // that's fine, trunc data
                    }

                    break;

                case "byte":
                    try {
                        byteBuffer.put((byte) (Integer.parseInt(data) & 0xFF));
                    } catch (final Exception ignored) {
                        // that's fine, trunc data
                    }

                    break;

                case "short":
                    try {
                        byteBuffer.putShort((short) (Integer.parseInt(data) & 0xFFFF));
                    } catch (final Exception ignored) {
                        // that's fine, trunc data
                    }

                    break;

                case "int":
                    try {
                        byteBuffer.putInt(Integer.parseInt(data));
                    } catch (final Exception ignored) {
                        // that's fine, trunc data
                    }

                    break;

                case "long":
                    try {
                        byteBuffer.putLong(Long.parseLong(data));
                    } catch (final Exception ignored) {
                        // that's fine, trunc data
                    }

                    break;

                case "hex":
                    String[] tokens = data.split(" ");

                    for (String str : tokens) {
                        try {
                            byteBuffer.put((byte) Integer.parseInt(str, 16));
                        } catch (final Exception ignored) {
                            // that's fine, trunc data
                        }
                    }

                    break;

                default:
                    return createError("Unsupported data type.", response);
            }

            // put chunk back
            if (chunkAnon.putAnon().put(chunk) != 1) {
                return createError("Put to chunk " + ChunkID.toHexString(cid) + " failed: " + chunk.getState(),
                        response);
            } else {
                return createMessage("Put to chunk " + ChunkID.toHexString(cid) + " successful");
            }
        });
    }
}
