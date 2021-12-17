package it.auties.whatsapp.protobuf.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.protobuf.message.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * A model class that holds the information related to a native flow.
 * This class is only a model, this means that changing its values will have no real effect on WhatsappWeb's servers.
 * Instead, methods inside {@link Whatsapp} should be used.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Accessors(fluent = true)
public class NativeFlowInfo {
  /**
   * The name of the flow
   */
  @JsonProperty(value = "1")
  @JsonPropertyDescription("string")
  private String name;

  /**
   * The params of the flow, encoded as json
   */
  @JsonProperty(value = "2")
  @JsonPropertyDescription("string")
  private String paramsJson;
}
