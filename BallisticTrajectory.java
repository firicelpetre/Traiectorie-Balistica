package org.trajectory;



import gov.nasa.worldwind.geom.Position;

import java.util.List;

public class BallisticTrajectory {
    private List<Position> positions;

    public BallisticTrajectory(List<Position> positions) {
        this.positions = positions;
    }

    public List<Position> getPositions() {


        return positions;
    }
}
