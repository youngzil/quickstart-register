/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：MockReHeader.java
 * 版本信息：
 * 日期：2019年1月17日
 * Copyright youngzil Corporation 2019
 * 版权所有 *
 */
package org.quickstart.zookeeper.zookeeperapi.jute;

/**
 * MockReHeader 
 *  
 * @author：youngzil@163.com
 * @2019年1月17日 上午10:31:47 
 * @since 1.0
 */

import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;
 
public class MockReHeader implements Record {
    private long sessionId;
    private String type;
    
    public MockReHeader() {
        
    }
    
    public MockReHeader(long sessionId, String type) {
        this.sessionId = sessionId;
        this.type = type;
    }
    
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public long getSessionId() {
        return sessionId;
    }
    
    public String getType() {
        return type;
    }
    
    public void serialize(OutputArchive oa, String tag) throws java.io.IOException {
        oa.startRecord(this, tag);
        oa.writeLong(sessionId, "sessionId");
        oa.writeString(type, "type");
        oa.endRecord(this, tag);
    }
    
    public void deserialize(InputArchive ia, String tag) throws java.io.IOException {
        ia.startRecord(tag);
        this.sessionId = ia.readLong("sessionId");
        this.type = ia.readString("type");
        ia.endRecord(tag);
    }
    
    @Override
    public String toString() {
        return "sessionId = " + sessionId + ", type = " + type;
    }
}

