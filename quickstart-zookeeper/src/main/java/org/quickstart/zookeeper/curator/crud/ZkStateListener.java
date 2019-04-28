/**
 * 项目名称：quickstart-zookeeper 
 * 文件名：ZkStateListener.java
 * 版本信息：
 * 日期：2017年7月2日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.zookeeper.curator.crud;

/**
 * ZkStateListener
 * 
 * @author：youngzil@163.com
 * @2017年7月2日 下午9:06:22
 * @version 1.0
 */
public interface ZkStateListener {

    void reconnected();

}
