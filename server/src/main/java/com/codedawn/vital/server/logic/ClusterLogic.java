package com.codedawn.vital.server.logic;

import com.codedawn.vital.server.config.VitalGenericOption;

/**
 * 集群情况下返回id所在节点的默认实现
 */
public class ClusterLogic {
    /**
     * 重写该方法，返回id所在的节点，格式为 ip:port
     * @param id
     * @return
     */
    public String onAddress(String id) {
//        return "127.0.0.1:9091";
        if(VitalGenericOption.SERVER_TCP_PORT.value()==8000){
            return "127.0.0.1:9091";
        }
        else {
            return "127.0.0.1:9099";
        }
    }
}
