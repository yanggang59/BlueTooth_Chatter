package com.example.a14493.bluetooth_chatter.bluetooth_connection;

public interface ProtocolHandler<T> {
    public byte[] encodePackage(T data);

    public T decodePackage(byte[] netData);
}
