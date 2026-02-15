package io.github.blackspherefollower.buttplug4j.protocol.messages;

import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

/**
 * RequestDeviceList message.
 */
public final class RequestDeviceList extends ButtplugMessage {

    /**
     * Constructor.
     */
    @SuppressWarnings("unused")
    private RequestDeviceList() {
        super(ButtplugConsts.DEFAULT_MSG_ID);
    }

    /**
     * Constructor.
     *
     * @param id message ID
     */
    public RequestDeviceList(final int id) {
        super(id);
    }
}
