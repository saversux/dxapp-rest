package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.data.ChunkAnon;
import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import spark.Service;

import java.nio.ByteBuffer;

public class Chunkput extends AbstractRestCommand {

    public Chunkput(){
        setInfo("chunkput", "cid, data, type", "Put <data> with <type> on Chunk with <cid>");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkput", (request, response) -> {

            String stringCid = request.queryParams("cid");
            String data = request.queryParams("data");
            String type = request.queryParams("type");
            int offset = 0;

            if (stringCid == null || data == null || type == null) {
                return createError("Invalid Parameter, please use: /chunkput?cid=[CID]?type=[str,byte,short,int,long]?data=[YOURDATA]");
            }

            long cid = ChunkID.parse(stringCid);

            if (cid == ChunkID.INVALID_ID) {
                return createError("No cid specified");
            }

            if (data == null) {
                return createError("No data specified");
            }

            if (ChunkID.getLocalID(cid) == 0) {
                return createError("Put of index chunk is not allowed");
            }

            ChunkAnon[] chunks = new ChunkAnon[1];

            if (services.chunkAnonService.getAnon().get(chunks, cid) != 1) {
                return createError("Getting chunk 0x" + cid + " failed: " + chunks[0].getState());
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
                    return createError("Unsupported data type.");
            }

            // put chunk back
            if (services.chunkAnonService.putAnon().put(chunk) != 1) {
                return createError("Put to chunk 0x" + cid + " failed: " + chunk.getState());
            } else {
                return createMessage("Put to chunk 0x" + cid + " successful");
            }
        });
    }
}
