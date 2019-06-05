/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：MainTest.java
 * 版本信息：
 * 日期：2019年1月17日
 * Copyright youngzil Corporation 2019
 * 版权所有 *
 */
package org.quickstart.zookeeper.zookeeperapi.jute;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.zookeeper.server.ByteBufferInputStream;

/**
 * MainTest
 * 
 * @author：youngzil@163.com
 * @2019年1月17日 上午10:32:22
 * @since 1.0
 */
public class MainTest {

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
        new MockReHeader(0x3421eccb92a34el, "ping").serialize(boa, "header");

        ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());

        ByteBufferInputStream bbis = new ByteBufferInputStream(bb);
        BinaryInputArchive bia = BinaryInputArchive.getArchive(bbis);

        MockReHeader header2 = new MockReHeader();
        System.out.println(header2);
        header2.deserialize(bia, "header");
        System.out.println(header2);
        bbis.close();
        baos.close();
    }

}
