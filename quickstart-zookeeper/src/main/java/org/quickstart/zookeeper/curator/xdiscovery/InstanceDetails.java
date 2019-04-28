/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：InstanceDetails.java
 * 版本信息：
 * 日期：2017年7月19日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.xdiscovery;

import org.codehaus.jackson.map.annotate.JsonRootName;

/**
 * InstanceDetails 
 *  
 * @author：youngzil@163.com
 * @2017年7月19日 下午9:46:11 
 * @version 2.0
 */
/**
 * In a real application, the Service payload will most likely be more detailed than this. But, this gives a good example.
 */
@JsonRootName("details")
public class InstanceDetails {
    private String description;

    public InstanceDetails() {
        this("");
    }

    public InstanceDetails(String description) {
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
