package org.trajectory;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Position;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyMouseListener extends MouseAdapter {
    private WorldWindowGLCanvas worldWindow;
    private WorldWind example;

    public MyMouseListener(WorldWindowGLCanvas worldWindow, WorldWind example) {
        this.worldWindow = worldWindow;
        this.example = example;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            int x = e.getX();
            int y = e.getY();
            Position position = worldWindow.getView().computePositionFromScreenPoint(x, y);
            if (position != null) {
                double latitude = position.getLatitude().getDegrees();
                double longitude = position.getLongitude().getDegrees();
                double altitude = worldWindow.getModel().getGlobe().getElevation(position.getLatitude(), position.getLongitude());
                // Actualizam GeographicText cu poziția curentă
                example.updateGeographicText(latitude, longitude, altitude);

                // Salvam coordonatele selectate
                example.setSelectedCoordinates(latitude, longitude, altitude);
                System.out.println("Latitude: " + latitude + ", Longitude: " + longitude + ", Altitude: " + altitude);
            }
        }
    }
}
