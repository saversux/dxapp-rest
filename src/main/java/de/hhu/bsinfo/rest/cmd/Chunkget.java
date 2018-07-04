package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.data.ChunkAnon;
import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class Chunkget extends AbstractRestCommand {

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkget/:chunkId/:type", (request, response) -> {
            long cid = ChunkID.parse(request.params(":chunkId"));

            if(cid == ChunkID.INVALID_ID){
                return "Invalid ChunkID";
            }

            ChunkAnon[] chunks = new ChunkAnon[1];
            if (services.chunkAnonService.get(chunks, cid) != 1) {
                return "Could not get Chunk";
            }

            ChunkAnon chunk = chunks[0];

            int offset = 0;
            String type = request.params(":type");
            boolean hex = true;


            String str = "";
            ByteBuffer byteBuffer = ByteBuffer.wrap(chunk.getData());
            byteBuffer.position(offset);
            int length = chunk.getDataSize();

            switch (type) {
                case "str":
                    try {
                        str = new String(chunk.getData(), offset, length, "US-ASCII");
                    } catch (final UnsupportedEncodingException e) {
                        return "Error encoding string";
                    }

                    break;

                case "byte":
                    for (int i = 0; i < length; i += Byte.BYTES) {
                        if (hex) {
                            str += Integer.toHexString(byteBuffer.get() & 0xFF) + ' ';
                        } else {
                            str += byteBuffer.get() + " ";
                        }
                    }
                    break;

                case "short":
                    for (int i = 0; i < length; i += java.lang.Short.BYTES) {
                        if (hex) {
                            str += Integer.toHexString(byteBuffer.getShort() & 0xFFFF) + ' ';
                        } else {
                            str += byteBuffer.getShort() + " ";
                        }
                    }
                    break;

                case "int":
                    for (int i = 0; i < length; i += java.lang.Integer.BYTES) {
                        if (hex) {
                            str += Integer.toHexString(byteBuffer.getInt()) + ' ';
                        } else {
                            str += byteBuffer.getInt() + " ";
                        }
                    }
                    break;

                case "long":
                    for (int i = 0; i < length; i += java.lang.Long.BYTES) {
                        if (hex) {
                            str += Long.toHexString(byteBuffer.getLong()) + ' ';
                        } else {
                            str += byteBuffer.getLong() + " ";
                        }
                    }
                    break;

                default:
                    return "Unsuported data type";
            }
            return gson.toJson(str);


        });
    }
}
