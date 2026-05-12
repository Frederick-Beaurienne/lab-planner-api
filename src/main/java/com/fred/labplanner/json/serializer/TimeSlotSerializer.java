package com.fred.labplanner.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fred.labplanner.model.TimeSlot;

import java.io.IOException;

public class TimeSlotSerializer
        extends JsonSerializer<TimeSlot> {

    @Override
    public void serialize(
            TimeSlot value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {

        String formatted =
                value.getStart()
                        + "-"
                        + value.getEnd();

        gen.writeString(formatted);
    }
}
