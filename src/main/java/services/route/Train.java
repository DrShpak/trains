package services.route;

import lombok.Value;

@Value
public class Train {
    int number;

    public Train(int number) {
        this.number = number;
    }
}
