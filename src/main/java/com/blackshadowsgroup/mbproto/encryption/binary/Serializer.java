package com.blackshadowsgroup.mbproto.encryption.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Behi198 on 8/30/2016.
 */
public class Serializer {
    public static void write(String value, ByteArrayOutputStream b) throws IOException {
        b.write(value.getBytes().length);
        b.write(value.getBytes());
    }

    public static String read(BinaryReaderDotNet reader) throws IOException {
        int code = reader.readInt32();
        int length = reader.readInt32();
        String s = new String(reader.readBytes(length));
        return s;
    }

    public static byte[] intToByteArray ( final int i ) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(i);
        dos.flush();
        dos.close();
        return bos.toByteArray();
    }

    public static long byteArrayToLong (byte[] arr) throws IOException {
        ByteArrayInputStream bais=new ByteArrayInputStream(arr);
        DataInputStream dais=new DataInputStream(bais);
        long i=dais.readLong();
        return i;
    }

    public static int byteArrayToInt (byte[] arr) throws IOException {
        ByteArrayInputStream bais=new ByteArrayInputStream(arr);
        DataInputStream dais=new DataInputStream(bais);
        int i=dais.readInt();
        return i;
    }

    public static double byteArrayToDouble(byte[] arr) throws IOException{
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.put(arr);
        return buf.getDouble();
    }
}
