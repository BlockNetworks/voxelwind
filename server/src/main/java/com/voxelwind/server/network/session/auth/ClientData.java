package com.voxelwind.server.network.session.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ClientData {
    @JsonProperty("CapeData")
    private byte[] capeData; // deserialized
    @JsonProperty("ClientRandomId")
    private long clientRandomId;
    @JsonProperty("CurrentInputMode")
    private int currentInputMode;
    @JsonProperty("DefaultInputMode")
    private int defaultInputMode;
    @JsonProperty("DeviceId")
    private String deviceId;
    @JsonProperty("DeviceModel")
    private String deviceModel;
    @JsonProperty("DeviceOS")
    private int deviceOS;
    @JsonProperty("GameVersion")
    private String gameVersion;
    @JsonProperty("GuiScale")
    private int guiScale;
    @JsonProperty("LanguageCode")
    private String languageCode;
    @JsonProperty("PlatformOfflineId")
    private String platformOfflineId;
    @JsonProperty("PlatformOnlineId")
    private String platformOnlineId;
    @JsonProperty("PremiumSkin")
    private boolean premiumSkin;
    @JsonProperty("SelfSignedId")
    private UUID selfSignedId;
    @JsonProperty("ServerAddress")
    private String serverAddress;
    @JsonProperty("SkinData")
    private byte[] skinData; // deserialized
    @JsonProperty("SkinGeometryData")
    private byte[] skinGeometry; // deserialized
    @JsonProperty("SkinResourcePatch")
    private String skinGeometryName;
    @JsonProperty("SkinImageHeight")
    private int skinImageHeight;
    @JsonProperty("SkinImageWidth")
    private int skinImageWidth;
    @JsonProperty("SkinId")
    private String skinId;
    @JsonProperty("ThirdPartyName")
    private String thirdPartyName;
    @JsonProperty("UIProfile")
    private int uiProfile;
    @JsonProperty("CapeOnClassicSkin")
    private boolean capeOnClassicSkin;
    @JsonProperty("PersonaSkin")
    private boolean personaSkin;
    @JsonProperty("CapeImageHeight")
    private int capeImageHeight;
    @JsonProperty("CapeImageWidth")
    private int capeImageWidth;
    @JsonProperty("CapeId")
    private String capeId;
}
