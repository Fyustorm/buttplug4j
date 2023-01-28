package org.blackspherefollower.buttplug.protocol.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.blackspherefollower.buttplug.protocol.ButtplugConsts;
import org.blackspherefollower.buttplug.protocol.ButtplugDeviceMessage;

import java.util.ArrayList;

public class ScalarCmd extends ButtplugDeviceMessage {

    @JsonProperty(value = "Scalars", required = true)
    private ScalarSubCmd[] scalars;

    public ScalarCmd(long deviceIndex, Double[] scalars, String actuatorType, long id) {
        super(id, deviceIndex);
        long i = 0;
        ArrayList<ScalarSubCmd> scalarsubs = new ArrayList<>();
        for (Double scalar : scalars) {
            if (scalar != null) {
                scalarsubs.add(new ScalarSubCmd(i, scalar, actuatorType));
            }
            i++;
        }
        this.scalars = scalarsubs.toArray(new ScalarSubCmd[]{});
    }

    @SuppressWarnings("unused")
    private ScalarCmd() {
        super(ButtplugConsts.DefaultMsgId, -1);
    }

    public class ScalarSubCmd {
        @JsonProperty(value = "Index", required = true)
        private long index;

        @JsonProperty(value = "Scalar", required = true)
        private double speed;

        @JsonProperty(value = "ActuatorType", required = true)
        private String actuatorType;

        public ScalarSubCmd(long index, double scalar, String actuatorType) {
            this.index = index;
            this.actuatorType = actuatorType;
            SetScalar(scalar);
        }

        public double GetScalar() {
            if (speed > 1 || speed < 0) {
                return 0;
            }
            return speed;
        }

        public void SetScalar(double speed) {
            if (speed > 1) {
                throw new IllegalArgumentException(
                        "Scalar values cannot be greater than 1!");
            }

            if (speed < 0) {
                throw new IllegalArgumentException(
                        "Scalar values cannot be lower than 0!");
            }

            this.speed = speed;
        }
    }
}
