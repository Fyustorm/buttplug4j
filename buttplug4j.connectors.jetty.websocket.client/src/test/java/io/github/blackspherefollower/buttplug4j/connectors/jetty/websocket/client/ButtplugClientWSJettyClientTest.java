package io.github.blackspherefollower.buttplug4j.connectors.jetty.websocket.client;

import io.github.blackspherefollower.buttplug4j.client.ButtplugClientDevice;
import io.github.blackspherefollower.buttplug4j.client.ButtplugClientDeviceFeature;
import io.github.blackspherefollower.buttplug4j.client.ButtplugDeviceFeatureException;
import io.github.blackspherefollower.buttplug4j.utils.test.IntifaceEngineWrapper;
import io.github.blackspherefollower.buttplug4j.utils.test.WSDMClient;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class ButtplugClientWSJettyClientTest {


    @Test
    public void testConnect() throws Exception {
        try (IntifaceEngineWrapper wrapper = new IntifaceEngineWrapper()) {
            Thread.sleep(500);
            WSDMClient wsdev = new WSDMClient(new URI("ws://localhost:" + wrapper.getDport()), "LVS-Fake", "A9816725B");
            Thread.sleep(500);

            ButtplugClientWSClient client = new ButtplugClientWSClient("Java Test");
            client.connect(new URI("ws://localhost:" + wrapper.getCport() + "/buttplug"));
            client.startScanning();

            Thread.sleep(500);
            client.requestDeviceList();

            assertEquals(1, client.getDevices().size());
            for (ButtplugClientDevice dev : client.getDevices()) {
                for (ButtplugClientDeviceFeature feat : dev.getDeviceFeatures().values()) {
                    if (feat.hasVibrate()) {
                        feat.runVibrateFloat(0.5F).get();
                    }
                }
            }

            Thread.sleep(500);
            assertEquals("Vibrate:10;", wsdev.getMessages().poll());

            assertTrue(client.stopAllDevices());
            Thread.sleep(500);
            assertEquals("Vibrate:0;", wsdev.getMessages().poll());

            client.disconnect();
        }
    }

    @Test
    public void testBattery() throws Exception {
        try (IntifaceEngineWrapper wrapper = new IntifaceEngineWrapper()) {
            Thread.sleep(500);
            WSDMClient wsdev = new WSDMClient(new URI("ws://localhost:" + wrapper.getDport()), "LVS-Fake", "A9816725B");
            Thread.sleep(500);

            ButtplugClientWSClient client = new ButtplugClientWSClient("Java Test");
            client.connect(new URI("ws://localhost:" + wrapper.getCport() + "/buttplug"));
            client.startScanning();

            Thread.sleep(500);
            client.requestDeviceList();
            for (ButtplugClientDevice dev : client.getDevices()) {
                for (ButtplugClientDeviceFeature feat : dev.getDeviceFeatures().values()) {
                    if (feat.hasBattery()) {
                        feat.readBattery();
                    } else {
                        assertThrows(ButtplugDeviceFeatureException.class, () -> {
                            feat.readBattery();
                        });
                    }
                }
            }

            client.disconnect();
        }
    }
}
