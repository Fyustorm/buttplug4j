package io.github.blackspherefollower.buttplug4j.protocol.messages;

import io.github.blackspherefollower.buttplug4j.protocol.ButtplugConsts;
import io.github.blackspherefollower.buttplug4j.protocol.ButtplugMessage;

/**
 * ScanningFinished message.
 */
public final class ScanningFinished extends ButtplugMessage {

    /**
     * Constructor.
     */
    public ScanningFinished() {
        super(ButtplugConsts.SYSTEM_MSG_ID);
    }
}
