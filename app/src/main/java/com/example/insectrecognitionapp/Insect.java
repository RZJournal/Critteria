package com.example.insectrecognitionapp;

import java.util.List;

public class Insect {
    private String name;
    private List<String> location;
    private String description;
    private String habitat;
    private String identificationTips;
    private String ecologicalImpact;
    private String controlMethods;
    private String preventionTips;

    public String getName() {
        return name;
    }

    public List<String> getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getHabitat() {
        return habitat;
    }

    public String getIdentificationTips() {
        return identificationTips;
    }

    public String getEcologicalImpact() {
        return ecologicalImpact;
    }

    public String getControlMethods() {
        return controlMethods;
    }

    public String getPreventionTips() {
        return preventionTips;
    }
}
