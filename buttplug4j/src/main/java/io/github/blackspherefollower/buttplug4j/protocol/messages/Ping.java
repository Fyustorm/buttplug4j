package io.github.blackspherefollower.buttplug4j.protocol.messages;

import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

/**
 * Ping message.
 */
public final class Ping extends ButtplugMessage {

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private Ping() {
        super(ButtplugConsts.DEFAULT_MSG_ID);
    }

    /**
     * Constructor.
     *
     * @param id message ID
     */
    public Ping(final int id) {
        super(id);
    }
}
