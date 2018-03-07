package com.blackshadowsgroup.mbproto.encryption.binary;


import com.blackshadowsgroup.mbproto.encryption.encrypt.diffiehellman.BitConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Behi198 on 8/31/2016.
 */
public class BinaryWriter {

    private int position = 0;
    private ByteArrayOutputStream memory = new ByteArrayOutputStream();

    public void writeInt32(int value) throws IOException {
        byte[] arr = Serializer.intToByteArray(value);
        reverse(arr);
        memory.write(arr);
        memory.flush();

    }

    public void writeString(String value) throws IOException {
        byte[] arr = value.getBytes();
        writeInt32(arr.length);
        memory.write(arr);
        memory.flush();
    }

    public void writeByte(byte value) throws IOException {
        memory.write(value & 0xff);
        memory.flush();
    }

    public void writeByteArray(byte[] value) throws IOException {
//        memory.write(value);
        for (byte b :
                value) {
            writeByte(b);
        }
//        memory.flush();
    }

    public void writeInt32Before(int value) throws IOException {
        ByteArrayOutputStream temp = memory;
        memory = new ByteArrayOutputStream();
        writeInt32(value);
        writeByteArray(temp.toByteArray());
    }

    public void writeStringBefore(String value) throws IOException {
        ByteArrayOutputStream temp = memory;
        memory = new ByteArrayOutputStream();
        writeString(value);
        writeByteArray(temp.toByteArray());
    }

    public void writeByteBefore(byte value) throws IOException {
        ByteArrayOutputStream temp = memory;
        memory = new ByteArrayOutputStream();
        writeByte(value);
        writeByteArray(temp.toByteArray());
    }

    public void writeByteArrayBefore(byte[] value) throws IOException {
        ByteArrayOutputStream temp = memory;
        memory = new ByteArrayOutputStream();
        writeByteArray(value);
        writeByteArray(temp.toByteArray());
    }

    public byte[] toByteArray() {
        return memory.toByteArray();
    }

    public void close() throws IOException {
        memory.flush();
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

    public void writeInt64(long value) throws IOException {
        byte[] writeBuffer = new byte[8];
        writeBuffer[0] = (byte)(value >>> 56);
        writeBuffer[1] = (byte)(value >>> 48);
        writeBuffer[2] = (byte)(value >>> 40);
        writeBuffer[3] = (byte)(value >>> 32);
        writeBuffer[4] = (byte)(value >>> 24);
        writeBuffer[5] = (byte)(value >>> 16);
        writeBuffer[6] = (byte)(value >>>  8);
        writeBuffer[7] = (byte)(value >>>  0);
        reverse(writeBuffer);
        writeByteArray(writeBuffer);
    }

    public void writeInt64Before(long value) throws IOException {
        byte[] writeBuffer = new byte[8];
        writeBuffer[0] = (byte)(value >>> 56);
        writeBuffer[1] = (byte)(value >>> 48);
        writeBuffer[2] = (byte)(value >>> 40);
        writeBuffer[3] = (byte)(value >>> 32);
        writeBuffer[4] = (byte)(value >>> 24);
        writeBuffer[5] = (byte)(value >>> 16);
        writeBuffer[6] = (byte)(value >>>  8);
        writeBuffer[7] = (byte)(value >>>  0);
        reverse(writeBuffer);
        writeByteArrayBefore(writeBuffer);
    }

    public void writeDouble(double value) throws IOException{
        writeByteArray(BitConverter.getBytes(value));
    }

    public void writeDoubleBefore(double value) throws IOException{
        writeByteArrayBefore(BitConverter.getBytes(value));
    }

}
