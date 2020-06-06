package com.voxelwind.server.network.session.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserAuthenticationProfile {
    @JsonProperty
    private String displayName;
    @JsonProperty
    private UUID identity;
    @JsonProperty(value = "XUID")
    private String xuid;
}
