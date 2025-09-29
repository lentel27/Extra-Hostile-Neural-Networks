package net.lmor.extrahnn.api;

public enum Version {

    V1("v1", 1),
    V2("v2", 4),
    V3("v3", 8),
    V4("v4", 16);

    final String id;
    final int multiplier;

    Version(String id, int multiplier){
        this.id = id;
        this.multiplier = multiplier;
    }


    public static Version getVersion(String version){
        return switch (version.toLowerCase()){
            case "v2" -> V2;
            case "v3" -> V3;
            case "v4" -> V4;
            default -> V1;
        };
    }

    public String getId(){
        return id;
    }

    public int getMultiplier(){
        return multiplier;
    }
}
