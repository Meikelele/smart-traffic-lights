package pl.mbak.traffic.engine.domain;

import java.util.EnumMap;
import java.util.Map;

public final class Turns {
    private static final Map<Road, Road> RIGHT_OF = new EnumMap<>(Road.class);
    private static final Map<Road, Road> LEFT_OF = new EnumMap<>(Road.class);

    static {
        RIGHT_OF.put(Road.south, Road.east);
        RIGHT_OF.put(Road.east, Road.north);
        RIGHT_OF.put(Road.north, Road.west);
        RIGHT_OF.put(Road.west, Road.south);

        LEFT_OF.put(Road.south, Road.west);
        LEFT_OF.put(Road.west, Road.north);
        LEFT_OF.put(Road.north, Road.east);
        LEFT_OF.put(Road.east, Road.south);
    }

    private Turns() {}

    public static Turn compute(Road startRoad, Road endRoad) {

        if (startRoad == null || endRoad == null) {
            throw new IllegalArgumentException("'startRoad' / 'endRoad' cannot be null");
        }
        if (startRoad == endRoad) {
            throw new IllegalArgumentException("'startRoad' must be different from 'endRoad'");
        }
        if (endRoad == RIGHT_OF.get(startRoad)) {
            return Turn.RIGHT;
        }
        if (endRoad == LEFT_OF.get(startRoad)) {
            return Turn.LEFT;
        }

        return Turn.STRAIGHT;
    }
}
