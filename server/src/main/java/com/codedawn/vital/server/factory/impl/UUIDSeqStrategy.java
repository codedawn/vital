package com.codedawn.vital.server.factory.impl;

import com.codedawn.vital.server.factory.SeqStrategy;

import java.util.UUID;

public class UUIDSeqStrategy implements SeqStrategy {
    @Override
    public String getSeqID() {
        return UUID.randomUUID().toString();
    }
}
