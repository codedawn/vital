package com.codedawn.vital.server.command;

import com.codedawn.vital.server.context.ServerContext;
import com.codedawn.vital.server.processor.ProcessorManager;
import com.codedawn.vital.server.processor.ServerProcessor;
import com.codedawn.vital.server.proto.VitalProtobuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-24 22:30
 */
public class DefaultCommandHandler implements CommandHandler{

    private static Logger log = LoggerFactory.getLogger(DefaultCommandHandler.class);

    private ProcessorManager processorManager;

    public DefaultCommandHandler() {
        this.processorManager = new ProcessorManager();
    }

    @Override
    public void handle(ServerContext serverContext, VitalProtobuf.Protocol protocol) {
        String dataTypeStr = protocol.getDataType().toString();
        ServerProcessor processor = processorManager.getProcessor(dataTypeStr);
        processor.process();

    }

}
