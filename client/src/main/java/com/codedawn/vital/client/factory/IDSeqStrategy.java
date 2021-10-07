package com.codedawn.vital.client.factory;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.server.factory.SeqStrategy;
import com.codedawn.vital.server.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class IDSeqStrategy implements SeqStrategy {

    private final AtomicInteger count=new AtomicInteger(0);

    @Override
    public String getSeqID() {
        //bug 同一个账号多个设备来回登录可能会相同seq,所以添加小时级别的时间戳
        if(!StringUtils.isEmpty(ClientVitalGenericOption.ID.value())){
            if(count.get()>500000){
                count.set(count.get()%500000);
            }
            return ClientVitalGenericOption.ID.value() +"-"+System.currentTimeMillis()%3600000+"-"+ count.getAndIncrement();
        }else {
            return UUID.randomUUID().toString();
        }
    }
}
