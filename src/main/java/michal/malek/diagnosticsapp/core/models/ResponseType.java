package michal.malek.diagnosticsapp.core.models;

import lombok.Getter;

@Getter
public enum ResponseType {
    WARNING("yellow"),
    NOTIFICATION("blue"),
    SUCCESS("green"),
    FAILURE("red"),
    ERROR("orange");

    private final String color;

    ResponseType(String color) {
        this.color = color;
    }

}
