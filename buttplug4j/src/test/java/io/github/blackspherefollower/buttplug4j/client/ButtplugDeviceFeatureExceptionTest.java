package io.github.blackspherefollower.buttplug4j.client;

import io.github.blackspherefollower.buttplug4j.ButtplugException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ButtplugDeviceFeatureExceptionTest {

    @Test
    public void testExceptionWithMessage() {
        ButtplugDeviceFeatureException exception = new ButtplugDeviceFeatureException(ButtplugOutput.VIBRATE);

        assertEquals("Buttplug Device Feature does not support Vibrate", exception.getMessage());
    }

    @Test
    public void testExceptionInheritance() {
        ButtplugDeviceFeatureException exception = new ButtplugDeviceFeatureException(ButtplugOutput.ROTATE);
        assertInstanceOf(ButtplugException.class, exception);
    }
}
