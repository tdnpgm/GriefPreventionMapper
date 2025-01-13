package net.toarupgm.griefpreventionmapper.client.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

import java.util.regex.Pattern;

public class GeneralConfig {
    @SerialEntry
    public String landownerRegex = "このブロックは (.+) が保護しています。";
    @SerialEntry
    public Pattern landownerPattern = Pattern.compile(landownerRegex);
    @SerialEntry
    public String waypointSetName = "Claim Area";
}
