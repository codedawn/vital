package com.codedawn.vital.server.factory.impl;

import com.codedawn.vital.server.factory.SeqStrategy;
import com.codedawn.vital.server.util.SnowflakeIdWorker;

public class SnowFlakeSeqStrategy implements SeqStrategy {

    @Override
    public String getSeqID()  {
        return SnowflakeIdWorker.getInstance().nextId().toString();
    }
}
