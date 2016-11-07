package org.fife.emu;

import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

    @Test
    public void testGetHexStringUByte() {
        Assert.assertEquals("0x00", Util.getHexStringUByte(0));
        Assert.assertEquals("0x01", Util.getHexStringUByte(1));
        Assert.assertEquals("0x02", Util.getHexStringUByte(2));
        Assert.assertEquals("0x03", Util.getHexStringUByte(3));
        Assert.assertEquals("0x04", Util.getHexStringUByte(4));
        Assert.assertEquals("0x05", Util.getHexStringUByte(5));
        Assert.assertEquals("0x06", Util.getHexStringUByte(6));
        Assert.assertEquals("0x07", Util.getHexStringUByte(7));
        Assert.assertEquals("0x08", Util.getHexStringUByte(8));
        Assert.assertEquals("0x09", Util.getHexStringUByte(9));
        Assert.assertEquals("0x0a", Util.getHexStringUByte(10));
        Assert.assertEquals("0x0b", Util.getHexStringUByte(11));
        Assert.assertEquals("0x0c", Util.getHexStringUByte(12));
        Assert.assertEquals("0x0d", Util.getHexStringUByte(13));
        Assert.assertEquals("0x0e", Util.getHexStringUByte(14));
        Assert.assertEquals("0x0f", Util.getHexStringUByte(15));
        Assert.assertEquals("0x10", Util.getHexStringUByte(16));
    }

    @Test
    public void testGetHexStringUWord() {
        Assert.assertEquals("0x0000", Util.getHexStringUWord(0));
        Assert.assertEquals("0x0001", Util.getHexStringUWord(1));
        Assert.assertEquals("0x0002", Util.getHexStringUWord(2));
        Assert.assertEquals("0x0003", Util.getHexStringUWord(3));
        Assert.assertEquals("0x0004", Util.getHexStringUWord(4));
        Assert.assertEquals("0x0005", Util.getHexStringUWord(5));
        Assert.assertEquals("0x0006", Util.getHexStringUWord(6));
        Assert.assertEquals("0x0007", Util.getHexStringUWord(7));
        Assert.assertEquals("0x0008", Util.getHexStringUWord(8));
        Assert.assertEquals("0x0009", Util.getHexStringUWord(9));
        Assert.assertEquals("0x000a", Util.getHexStringUWord(10));
        Assert.assertEquals("0x000b", Util.getHexStringUWord(11));
        Assert.assertEquals("0x000c", Util.getHexStringUWord(12));
        Assert.assertEquals("0x000d", Util.getHexStringUWord(13));
        Assert.assertEquals("0x000e", Util.getHexStringUWord(14));
        Assert.assertEquals("0x000f", Util.getHexStringUWord(15));
        Assert.assertEquals("0x0010", Util.getHexStringUWord(16));
    }
}