package pl.mbak.traffic.engine.io;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddVehicleCommand.class, name = "addVehicle"),
        @JsonSubTypes.Type(value = StepCommand.class, name = "step")
})
public sealed interface Command permits AddVehicleCommand, StepCommand {
}
