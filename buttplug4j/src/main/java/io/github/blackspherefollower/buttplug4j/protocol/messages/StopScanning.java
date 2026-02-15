package io.github.blackspherefollower.buttplug4j.protocol.messages;

import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

/**
 * StopScanning message.
 */
public final class StopScanning extends ButtplugMessage {

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private StopScanning() {
        super(ButtplugConsts.DEFAULT_MSG_ID);
    }

    /**
     * Constructor.
     *
     * @param id message ID
     */
    public StopScanning(final int id) {
        super(id);
    }
}
