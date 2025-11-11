package com.tiv.jedis.server.protocol;

import io.netty.buffer.ByteBuf;

/**
 * RESP协议
 * <a href="https://redis.io/docs/latest/develop/reference/protocol-spec/">Redis serialization protocol</a>
 */
public abstract class RESPProtocol {

    /**
     * 回车换行符字节数组
     */
    public static final byte[] CRLF = "\r\n".getBytes();

    /**
     * RESP协议解码
     * 支持的类型:
     * - RESPSimpleStrings "+OK\r\n"
     * - RESPErrors "-Error message\r\n"
     * - RESPIntegers ":0\r\n"
     * - RESPBulkStrings "$6\r\nfoobar\r\n"
     * - RESPArrays "*2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n"
     *
     * @param byteBuf
     * @return
     */
    public static RESPProtocol decode(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() <= 0) {
            return null;
        }
        char c = (char) byteBuf.readByte();
        switch (c) {
            case '+':
                return new RESPSimpleStrings(getString(byteBuf));
            case '-':
                return new RESPErrors(getString(byteBuf));
            case ':':
                return new RESPIntegers(getNumber(byteBuf));
            case '$':
                return null;
            case '*':
                return null;
            default:
                throw new RuntimeException("RESP协议不支持");
        }
    }

    public abstract void encode(RESPProtocol respProtocol, ByteBuf byteBuf);

    private static String getString(ByteBuf byteBuf) {
        char c;
        StringBuilder sb = new StringBuilder();
        // 拼接字符串,读取到\r为止
        while (byteBuf.readableBytes() > 0 && (c = (char) byteBuf.readByte()) != '\r') {
            sb.append(c);
        }
        if (byteBuf.readableBytes() <= 0 || byteBuf.readByte() != '\n') {
            throw new RuntimeException("格式错误,缺少换行符");
        }
        return sb.toString();
    }

    private static int getNumber(ByteBuf byteBuf) {
        // 首位可能是负号
        char c = (char) byteBuf.readByte();
        boolean positive = c != '-';
        int val = 0;
        if (positive) {
            val = c - '0';
        }
        while (byteBuf.readableBytes() > 0 && (c = (char) byteBuf.readByte()) != '\r') {
            val = val * 10 + (c - '0');
        }
        if (byteBuf.readableBytes() <= 0 || byteBuf.readByte() != '\n') {
            throw new RuntimeException("格式错误,缺少换行符");
        }
        return positive ? val : -val;
    }

}
