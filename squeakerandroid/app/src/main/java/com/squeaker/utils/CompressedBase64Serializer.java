package com.squeaker.utils;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressedBase64Serializer implements BinaryDataSerializer {
    @NonNull
    public String encode(byte[] data) throws Exception {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);

        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);

        deflater.finish();

        byte[] buffer = new byte[1024];

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            out.write(buffer, 0, count);
        }

        out.close();
        deflater.end();

        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
    }

    @NonNull
    public byte[] decode(String data) throws Exception {
        byte[] arrayData = Base64.decode(data, Base64.NO_WRAP);

        Inflater inflater = new Inflater();
        inflater.setInput(arrayData);

        ByteArrayOutputStream out = new ByteArrayOutputStream(arrayData.length);
        byte[] buffer = new byte[1024];

        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            out.write(buffer, 0, count);
        }

        out.close();
        inflater.end();

        return out.toByteArray();
    }
}