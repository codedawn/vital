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
        if(!StringUtils.isEmpty(ClientVitalGenericOption.ID.value())){
            if(count.get()>500000){
                count.set(count.get()%500000);
            }
            return ClientVitalGenericOption.ID.value() +"-"+ count.getAndIncrement();
        }else {
            return UUID.randomUUID().toString();
        }
    }
}
