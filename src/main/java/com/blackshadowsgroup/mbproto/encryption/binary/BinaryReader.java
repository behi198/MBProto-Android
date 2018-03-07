package com.blackshadowsgroup.mbproto.encryption.binary;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by Behi198 on 8/31/2016.
 */

public class BinaryReader {

    private int position = 0;
    private DataInputStream memory;

    public DataInputStream getMemory() {
        return memory;
    }

    public BinaryReader(byte[] data) {
        this.memory = new DataInputStream(new ByteArrayInputStream(data));
    }

    public int getPosition() {
        return position;
    }

    public int readInt32() throws IOException {

        byte[] Int = new byte[4];
        memory.read(Int);
        reverse(Int);
        position += 4;
        return Serializer.byteArrayToInt(Int);
//        return memory.readInt();
    }

    public String readString() throws IOException {
        int l = readInt32();
        byte[] str = new byte[l];
//        memory.read(str);
        memory.read(str);
        position += str.length;
//        position += l;
        return new String(str);

    }

    public void close() throws IOException {
        memory.close();
    }

    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    public byte readByte() throws IOException {
//        byte[] str = new byte[1];
//        memory.read(str);
//        return str[0];
        position++;
        return memory.readByte();
    }

    public byte[] readByteArray() throws IOException {
        int length = readInt32();
        byte[] str = new byte[length];
//        memory.read(str);
       int k = memory.read(str);
        position += str.length;
        return str;
    }

    public byte[] readByteArray(int length) throws IOException {
        byte[] str = new byte[length];
        int k = memory.read(str);
        position += str.length;
        return str;
    }

    public Long readInt64() throws IOException {

        byte[] Int = new byte[8];
        memory.read(Int);
        reverse(Int);
        position += 8;
        return Serializer.byteArrayToLong(Int);
//        return memory.readInt();
    }


    public double readDouble() throws IOException{
        byte[] Int = new byte[8];
        memory.read(Int);
        reverse(Int);
        position += 8;
        return Serializer.byteArrayToDouble(Int);
    }
}
