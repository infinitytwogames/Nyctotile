package org.infinitytwo.umbralore.core.network.data;

import com.esotericsoftware.kryonet.Connection;
import org.infinitytwo.umbralore.core.data.io.DataSchematica;

@FunctionalInterface
public interface NetworkCommandProcessor {
    void process(DataSchematica.Data packet, Connection client);
}
