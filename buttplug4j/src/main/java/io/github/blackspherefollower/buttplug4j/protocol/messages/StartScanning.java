package io.github.blackspherefollower.buttplug4j.protocol.messages;

import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

/**
 * StartScanning message.
 */
public final class StartScanning extends ButtplugMessage {

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private StartScanning() {
        super(ButtplugConsts.DEFAULT_MSG_ID);
    }

    /**
     * Constructor.
     *
     * @param id message ID
     */
    public StartScanning(final int id) {
        super(id);
    }
}
