package io.github.blackspherefollower.buttplug4j.protocol.messages;

import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

/**
 * Ok message.
 */
public final class Ok extends ButtplugMessage {

    /**
     * Constructor.
     *
     * @param id message ID
     */
    public Ok(final int id) {
        super(id);
    }

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private Ok() {
        super(ButtplugConsts.DEFAULT_MSG_ID);
    }
}
