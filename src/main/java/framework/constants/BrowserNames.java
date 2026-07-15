package framework.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BrowserNames {

    CHROME("Chrome"),
    FIREFOX("Firefox"),
    EDGE("Edge"),
    SAFARI("Safari");

    private final String name;

}
