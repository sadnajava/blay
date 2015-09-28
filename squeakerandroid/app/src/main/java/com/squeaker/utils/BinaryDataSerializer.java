package com.squeaker.utils;

public interface BinaryDataSerializer {
    String encode(byte[] data) throws Exception;
    byte[] decode(String data) throws Exception;
}
