package com.fred.labplanner.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fred.labplanner.model.TimeSlot;

import java.io.IOException;
import java.time.LocalTime;

public class TimeSlotDeserializer
        extends JsonDeserializer<TimeSlot> {

    public static TimeSlot deserialize(String value) {

        String[] parts = value.split("-");

        return new TimeSlot(
                LocalTime.parse(parts[0]),
                LocalTime.parse(parts[1])
        );
    }

    @Override
    public TimeSlot deserialize(
            JsonParser parser,
            DeserializationContext context
    ) throws IOException {

        String value = parser.getValueAsString();

        String[] parts = value.split("-");

        return new TimeSlot(
                LocalTime.parse(parts[0]),
                LocalTime.parse(parts[1])
        );
    }
}