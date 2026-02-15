package io.github.blackspherefollower.buttplug4j.protocol;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * ButtplugJsonMessageParser.
 */
public final class ButtplugJsonMessageParser {

    /**
     * JSON mapper.
     */
    private final ObjectMapper mapper;

    /**
     * Constructor.
     */
    public ButtplugJsonMessageParser() {
        mapper = JsonMapper.builder()
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
                .build();
        TypeResolverBuilder<?> typer = DefaultTypeResolverBuilder.construct(DefaultTyping.JAVA_LANG_OBJECT,
                this.mapper.getPolymorphicTypeValidator());
        typer = typer.init(JsonTypeInfo.Id.NAME, null);
        typer = typer.inclusion(As.WRAPPER_OBJECT);
        mapper.setDefaultTyping(typer);
    }

    /**
     * Parse JSON to Buttplug messages.
     *
     * @param json JSON string
     * @return list of messages
     * @throws ButtplugProtocolException if parsing fails
     */
    public List<ButtplugMessage> parseJson(final String json)
            throws ButtplugProtocolException {
        try {
            return Arrays.asList(mapper.readValue(json, ButtplugMessage[].class));
        } catch (JsonProcessingException e) {
            throw new ButtplugProtocolException(e);
        }
    }

    /**
     * Format Buttplug messages to JSON.
     *
     * @param msgs list of messages
     * @return JSON string
     * @throws ButtplugProtocolException if formatting fails
     */
    public String formatJson(final List<ButtplugMessage> msgs)
            throws ButtplugProtocolException {
        try {
            return mapper.writeValueAsString(msgs);
        } catch (JsonProcessingException e) {
            throw new ButtplugProtocolException(e);
        }
    }

    /**
     * Format a single Buttplug message to JSON.
     *
     * @param msg message
     * @return JSON string
     * @throws ButtplugProtocolException if formatting fails
     */
    public String formatJson(final ButtplugMessage msg)
            throws ButtplugProtocolException {
        try {
            return mapper.writeValueAsString(new ButtplugMessage[]{msg});
        } catch (JsonProcessingException e) {
            throw new ButtplugProtocolException(e);
        }
    }
}
