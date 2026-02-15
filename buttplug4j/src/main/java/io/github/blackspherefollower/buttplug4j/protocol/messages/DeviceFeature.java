package io.github.blackspherefollower.buttplug4j.protocol.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * DeviceFeature.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class DeviceFeature {

    /**
     * Feature index.
     */
    @JsonProperty(value = "FeatureIndex", required = true)
    private int featureIndex;
    /**
     * Feature description.
     */
    @JsonProperty(value = "FeatureDescription", required = true)
    private String featureDescription;
    /**
     * Output descriptors.
     */
    @JsonProperty(value = "Output", required = false)
    @JsonDeserialize(using = OutputDescriptorSetDeserialiser.class)
    @JsonSerialize(using = OutputDescriptorSetSerialiser.class)
    private ArrayList<OutputDescriptor> output;
    /**
     * Input descriptors.
     */
    @JsonProperty(value = "Input", required = false)
    @JsonDeserialize(using = InputDescriptorSetDeserialiser.class)
    @JsonSerialize(using = InputDescriptorSetSerialiser.class)
    private ArrayList<InputDescriptor> input;

    /**
     * Constructor.
     */
    public DeviceFeature() {
    }

    /**
     * Get feature description.
     *
     * @return description
     */
    public String getFeatureDescription() {
        return featureDescription;
    }

    /**
     * Set feature description.
     *
     * @param aFeatureDescription description
     */
    public void setFeatureDescription(final String aFeatureDescription) {
        this.featureDescription = aFeatureDescription;
    }

    /**
     * Get feature index.
     *
     * @return index
     */
    public int getFeatureIndex() {
        return featureIndex;
    }

    /**
     * Set feature index.
     *
     * @param aFeatureIndex index
     */
    public void setFeatureIndex(final int aFeatureIndex) {
        this.featureIndex = aFeatureIndex;
    }

    /**
     * Get output descriptors.
     *
     * @return output
     */
    public ArrayList<OutputDescriptor> getOutput() {
        return output;
    }

    /**
     * Set output descriptors.
     *
     * @param aOutput output
     */
    public void setOutput(final ArrayList<OutputDescriptor> aOutput) {
        this.output = aOutput;
    }

    /**
     * Get input descriptors.
     *
     * @return input
     */
    public ArrayList<InputDescriptor> getInput() {
        return input;
    }

    /**
     * Set input descriptors.
     *
     * @param aInput input
     */
    public void setInput(final ArrayList<InputDescriptor> aInput) {
        this.input = aInput;
    }

    /**
     * OutputDescriptor interface.
     */
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DeviceFeature.Vibrate.class, name = "Vibrate"),
            @JsonSubTypes.Type(value = DeviceFeature.Rotate.class, name = "Rotate"),
            @JsonSubTypes.Type(value = DeviceFeature.Spray.class, name = "Spray"),
            @JsonSubTypes.Type(value = DeviceFeature.Oscillate.class, name = "Oscillate"),
            @JsonSubTypes.Type(value = DeviceFeature.Position.class, name = "Position"),
            @JsonSubTypes.Type(value = DeviceFeature.Temperature.class, name = "Temperature"),
            @JsonSubTypes.Type(value = DeviceFeature.Constrict.class, name = "Constrict"),
            @JsonSubTypes.Type(value = DeviceFeature.HwPositionWithDuration.class, name = "HwPositionWithDuration"),
            @JsonSubTypes.Type(value = DeviceFeature.Led.class, name = "Led")
    })
    public interface OutputDescriptor {
    }

    /**
     * SteppedOutputDescriptor.
     */
    public abstract static class SteppedOutputDescriptor implements OutputDescriptor {
        /**
         * Value range.
         */
        @JsonProperty(value = "Value", required = true)
        private int[] value;

        /**
         * Constructor.
         *
         * @param aValue value range
         */
        public SteppedOutputDescriptor(final int[] aValue) {
            this.value = aValue;
        }

        /**
         * Get value range.
         *
         * @return range
         */
        public int[] getValue() {
            return value;
        }

        /**
         * Set value range.
         *
         * @param aValue range
         */
        public void setStepCount(final int[] aValue) {
            this.value = aValue;
        }

        /**
         * Compares with other objects.
         * If extending, either call this first or compare the value range.
         *
         * @param o other
         */
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SteppedOutputDescriptor that = (SteppedOutputDescriptor) o;
            return java.util.Arrays.equals(value, that.value);
        }

        /**
         * Generates the hashcode based on the value range array.
         */
        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }
    }

    /**
     * Vibrate output.
     */
    public static final class Vibrate extends SteppedOutputDescriptor {
        /**
         * Constructor.
         *
         * @param value range
         */
        public Vibrate(final int[] value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Vibrate() {
            super(new int[]{0, 0});
        }
    }

    /**
     * Rotate output.
     */
    public static final class Rotate extends SteppedOutputDescriptor {
        /**
         * Constructor.
         *
         * @param value range
         */
        public Rotate(final int[] value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Rotate() {
            super(new int[]{0, 0});
        }
    }

    /**
     * Oscillate output.
     */
    public static final class Oscillate extends SteppedOutputDescriptor {
        /**
         * Constructor.
         *
         * @param value range
         */
        public Oscillate(final int[] value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Oscillate() {
            super(new int[]{0, 0});
        }
    }

    /**
     * Constrict output.
     */
    public static final class Constrict extends SteppedOutputDescriptor {
        /**
         * Constructor.
         *
         * @param value range
         */
        public Constrict(final int[] value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Constrict() {
            super(new int[]{0, 0});
        }
    }

    /**
     * Spray output.
     */
    public static final class Spray extends SteppedOutputDescriptor {
        /**
         * Constructor.
         *
         * @param value range
         */
        public Spray(final int[] value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Spray() {
            super(new int[]{0, 0});
        }
    }

    /**
     * Temperature output.
     */
    public static final class Temperature extends SteppedOutputDescriptor {
        /**
         * Constructor.
         *
         * @param value range
         */
        public Temperature(final int[] value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Temperature() {
            super(new int[]{0, 0});
        }
    }

    /**
     * Led output.
     */
    public static final class Led extends SteppedOutputDescriptor {
        /**
         * Constructor.
         *
         * @param value range
         */
        public Led(final int[] value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Led() {
            super(new int[]{0, 0});
        }
    }

    /**
     * Position output.
     */
    public static final class Position extends SteppedOutputDescriptor {
        /**
         * Constructor.
         *
         * @param value range
         */
        public Position(final int[] value) {
            super(value);
        }

        /**
         * Constructor.
         */
        public Position() {
            super(new int[]{0, 0});
        }
    }

    /**
     * HW Position with duration output.
     */
    public static final class HwPositionWithDuration extends SteppedOutputDescriptor {
        /**
         * Duration range.
         */
        @JsonProperty(value = "Duration", required = true)
        private int[] duration;

        /**
         * Constructor.
         *
         * @param value     position range
         * @param aDuration duration range
         */
        public HwPositionWithDuration(final int[] value, final int[] aDuration) {
            super(value);
            this.duration = aDuration;
        }

        /**
         * Constructor.
         */
        public HwPositionWithDuration() {
            super(new int[]{0, 0});
            this.duration = new int[]{0, 0};
        }

        /**
         * Get duration range.
         *
         * @return range
         */
        public int[] getDuration() {
            return duration;
        }

        /**
         * Set duration range.
         *
         * @param aDuration range
         */
        public void setDuration(final int[] aDuration) {
            this.duration = aDuration;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            HwPositionWithDuration that = (HwPositionWithDuration) o;
            return java.util.Arrays.equals(duration, that.duration);
        }

        @Override
        public int hashCode() {
            final int magic = 31;
            int result = super.hashCode();
            result = magic * result + Arrays.hashCode(duration);
            return result;
        }
    }

    /**
     * InputDescriptor.
     */
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DeviceFeature.Battery.class, name = "Battery"),
            @JsonSubTypes.Type(value = DeviceFeature.Rssi.class, name = "RSSI"),
            @JsonSubTypes.Type(value = DeviceFeature.Button.class, name = "Button"),
            @JsonSubTypes.Type(value = DeviceFeature.Pressure.class, name = "Pressure"),
            @JsonSubTypes.Type(value = DeviceFeature.PositionInput.class, name = "Position")
    })
    public abstract static class InputDescriptor {
        /**
         * Supported command types.
         */
        @JsonProperty(value = "Command", required = true)
        private ArrayList<InputCommandType> input;

        /**
         * Constructor.
         *
         * @param aInput command types
         */
        public InputDescriptor(final ArrayList<InputCommandType> aInput) {
            this.input = aInput;
        }

        /**
         * Get command types.
         *
         * @return command types
         */
        public ArrayList<InputCommandType> getInput() {
            return input;
        }

        /**
         * Set command types.
         *
         * @param aInput command types
         */
        public void setInput(final ArrayList<InputCommandType> aInput) {
            this.input = aInput;
        }
    }

    /**
     * RangedInputDescriptor.
     */
    public abstract static class RangedInputDescriptor extends InputDescriptor {
        /**
         * Value range.
         */
        @JsonProperty(value = "Value", required = true)
        private int[][] valueRange;

        /**
         * Constructor.
         *
         * @param input       command types
         * @param aValueRange value range
         */
        public RangedInputDescriptor(final ArrayList<InputCommandType> input, final int[][] aValueRange) {
            super(input);
            this.valueRange = aValueRange;
        }

        /**
         * Constructor.
         */
        public RangedInputDescriptor() {
            super(new ArrayList<>());
            this.valueRange = new int[][]{{0, 0}, {0, 0}};
        }

        /**
         * Get value range.
         *
         * @return range
         */
        public int[][] getValueRange() {
            return valueRange;
        }

        /**
         * Set value range.
         *
         * @param aValueRange range
         */
        public void setValueRange(final int[][] aValueRange) {
            this.valueRange = aValueRange;
        }

        /**
         * Compares objects based on valueRange 2d array.
         * @param o   the reference object with which to compare.
         * @return equality
         */
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RangedInputDescriptor that = (RangedInputDescriptor) o;
            if (valueRange == null || that.valueRange == null) {
                return valueRange == that.valueRange;
            }
            if (valueRange.length != that.valueRange.length) {
                return false;
            }
            for (int i = 0; i < valueRange.length; i++) {
                if (!java.util.Arrays.equals(valueRange[i], that.valueRange[i])) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Hashcode based on valueRange 2d array.
         * @return hashcode based on valueRange 2d array.
         */
        @Override
        public int hashCode() {
            final int magic = 31;
            int result = 0;
            if (valueRange != null) {
                for (int[] element : valueRange) {
                    result = magic * result + Arrays.hashCode(element);
                }
            }
            return result;
        }
    }

    /**
     * Battery input.
     */
    public static final class Battery extends RangedInputDescriptor {
        /**
         * Constructor.
         *
         * @param input      command types
         * @param valueRange range
         */
        public Battery(final ArrayList<InputCommandType> input, final int[][] valueRange) {
            super(input, valueRange);
        }

        /**
         * Constructor.
         */
        public Battery() {
            super();
        }
    }

    /**
     * RSSI input.
     */
    public static final class Rssi extends RangedInputDescriptor {
        /**
         * Constructor.
         *
         * @param input      command types
         * @param valueRange range
         */
        public Rssi(final ArrayList<InputCommandType> input, final int[][] valueRange) {
            super(input, valueRange);
        }

        /**
         * Constructor.
         */
        public Rssi() {
            super();
        }
    }

    /**
     * Button input.
     */
    public static final class Button extends RangedInputDescriptor {
        /**
         * Constructor.
         *
         * @param input      command types
         * @param valueRange range
         */
        public Button(final ArrayList<InputCommandType> input, final int[][] valueRange) {
            super(input, valueRange);
        }

        /**
         * Constructor.
         */
        public Button() {
            super();
        }
    }

    /**
     * Pressure input.
     */
    public static final class Pressure extends RangedInputDescriptor {
        /**
         * Constructor.
         *
         * @param input      command types
         * @param valueRange range
         */
        public Pressure(final ArrayList<InputCommandType> input, final int[][] valueRange) {
            super(input, valueRange);
        }

        /**
         * Constructor.
         */
        public Pressure() {
            super();
        }
    }

    /**
     * Position input.
     */
    public static final class PositionInput extends RangedInputDescriptor {
        /**
         * Constructor.
         *
         * @param input      command types
         * @param valueRange range
         */
        public PositionInput(final ArrayList<InputCommandType> input, final int[][] valueRange) {
            super(input, valueRange);
        }

        /**
         * Constructor.
         */
        public PositionInput() {
            super();
        }
    }

    /**
     * OutputDescriptorSetDeserialiser.
     */
    static class OutputDescriptorSetDeserialiser extends JsonDeserializer<ArrayList<OutputDescriptor>> {

        @Override
        public ArrayList<OutputDescriptor> deserialize(final JsonParser jsonParser,
                                                       final DeserializationContext deserializationContext)
                throws IOException {
            ObjectMapper mapper = ((ObjectMapper) jsonParser.getCodec());
            ArrayList<OutputDescriptor> ret = new ArrayList<OutputDescriptor>();
            try {
                TreeNode tree = jsonParser.readValueAsTree();
                for (Iterator<String> it = tree.fieldNames(); it.hasNext();) {
                    String f = it.next();
                    ObjectNode node = mapper.createObjectNode();
                    node.set(f, mapper.readTree(tree.get(f).traverse(mapper)));
                    ret.add(node.traverse(mapper).readValueAs(OutputDescriptor.class));
                }
            } catch (Exception e) {
                // unknown type
            }
            return ret;
        }
    }

    /**
     * OutputDescriptorSetSerialiser.
     */
    static class OutputDescriptorSetSerialiser extends JsonSerializer<ArrayList<OutputDescriptor>> {

        @Override
        public void serialize(final ArrayList<OutputDescriptor> outputDescriptors,
                              final JsonGenerator jsonGenerator,
                              final SerializerProvider serializerProvider) throws IOException {

            ObjectMapper mapper = ((ObjectMapper) jsonGenerator.getCodec());
            ObjectNode node = null;
            for (OutputDescriptor outputDescriptor : outputDescriptors) {
                if (node == null) {
                    node = mapper.createObjectNode();
                }

                TreeNode n = mapper.readValue(mapper.writeValueAsString(outputDescriptor), JsonNode.class)
                        .traverse(mapper).readValueAsTree();
                for (Iterator<String> it = n.fieldNames(); it.hasNext();) {
                    String f = it.next();
                    node.set(f, mapper.readTree(n.get(f).traverse(mapper)));
                }
            }
            jsonGenerator.writeObject(node);
        }
    }

    /**
     * InputDescriptorSetDeserialiser.
     */
    static class InputDescriptorSetDeserialiser extends JsonDeserializer<ArrayList<InputDescriptor>> {

        @Override
        public ArrayList<InputDescriptor> deserialize(final JsonParser jsonParser,
                                                      final DeserializationContext deserializationContext)
                throws IOException {
            ObjectMapper mapper = ((ObjectMapper) jsonParser.getCodec());
            ArrayList<InputDescriptor> ret = new ArrayList<InputDescriptor>();
            try {
                TreeNode tree = jsonParser.readValueAsTree();
                for (Iterator<String> it = tree.fieldNames(); it.hasNext();) {
                    String f = it.next();
                    ObjectNode node = mapper.createObjectNode();
                    node.set(f, mapper.readTree(tree.get(f).traverse(mapper)));
                    ret.add(node.traverse(mapper).readValueAs(InputDescriptor.class));
                }
            } catch (Exception e) {
                // unknown type
            }
            return ret;
        }
    }

    /**
     * InputDescriptorSetSerialiser.
     */
    static class InputDescriptorSetSerialiser extends JsonSerializer<ArrayList<InputDescriptor>> {

        @Override
        public void serialize(final ArrayList<InputDescriptor> inputDescriptors,
                              final JsonGenerator jsonGenerator,
                              final SerializerProvider serializerProvider) throws IOException {

            ObjectMapper mapper = ((ObjectMapper) jsonGenerator.getCodec());
            ObjectNode node = null;
            for (InputDescriptor inputDescriptor : inputDescriptors) {
                if (node == null) {
                    node = mapper.createObjectNode();
                }

                TreeNode n = mapper.readValue(mapper.writeValueAsString(inputDescriptor), JsonNode.class)
                        .traverse(mapper).readValueAsTree();
                for (Iterator<String> it = n.fieldNames(); it.hasNext();) {
                    String f = it.next();
                    node.set(f, mapper.readTree(n.get(f).traverse(mapper)));
                }
            }
            jsonGenerator.writeObject(node);
        }
    }
}
