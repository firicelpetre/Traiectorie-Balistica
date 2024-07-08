package org.trajectory;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.globes.Earth;

import java.util.ArrayList;
import java.util.List;

public class BallisticTrajectoryCalculator {

    public static BallisticTrajectory calculateTrajectory(double latitude, double longitude, double altitude, double velocity, double launchAngle, double rotationAngle) {
        List<Position> trajectoryPositions = new ArrayList<>();

        OrekitConfig.configureOrekit();

        // Configurare Glob pamant
        Globe globe = new Earth();

        // Constante
        final double g = 9.81; // Accelerația gravitațională (m/s^2)
        final double timeStep = 0.1; // Pasul de timp în secunde
        final double earthRadius = 6371000; // Raza Pământului în metri

        // Coordonatele inițiale (convertite din grade în radiani)
        double latitudeRad = Math.toRadians(latitude);
        double longitudeRad = Math.toRadians(longitude);

        // Verificare altitudine
        double altitudeMeters = Math.max(altitude, 0);

        // Calculul vitezei inițiale în coordonate 3D
        double vx = velocity * Math.cos(Math.toRadians(launchAngle)) * Math.cos(Math.toRadians(rotationAngle));
        double vy = velocity * Math.cos(Math.toRadians(launchAngle)) * Math.sin(Math.toRadians(rotationAngle));
        double vz = velocity * Math.sin(Math.toRadians(launchAngle));

        // Inițializare variabile pentru iterare
        double t = 0;
        double x = 0;
        double y = 0;
        double z = altitudeMeters;

        double currentVx = vx;
        double currentVy = vy;
        double currentVz = vz;

        boolean hasLanded = false;

        while (!hasLanded) {
            // Calculul noii poziții
            t += timeStep;
            x += currentVx * timeStep;
            y += currentVy * timeStep;
            z += currentVz * timeStep - 0.5 * g * timeStep * timeStep;

            // Actualizare viteze
            currentVz -= g * timeStep;

            // Convertim coordonatele în latitudine și longitudine
            double distance = Math.sqrt(x * x + y * y);
            double bearing = Math.atan2(y, x);
            double angularDistance = distance / earthRadius;

            double newLatitudeRad = Math.asin(Math.sin(latitudeRad) * Math.cos(angularDistance) +
                    Math.cos(latitudeRad) * Math.sin(angularDistance) * Math.cos(bearing));
            double newLongitudeRad = longitudeRad + Math.atan2(Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(latitudeRad),
                    Math.cos(angularDistance) - Math.sin(latitudeRad) * Math.sin(newLatitudeRad));

            double newLatitude = Math.toDegrees(newLatitudeRad);
            double newLongitude = Math.toDegrees(newLongitudeRad);

            // Adăugăm noua poziție la lista traiectoriei
            trajectoryPositions.add(Position.fromDegrees(newLatitude, newLongitude, z));

            // Verificăm dacă proiectilul a atins pământul
            if (z <= 0) {
                hasLanded = true;
                // Ajustare finală a poziției pentru a fi exact la altitudinea zero
                trajectoryPositions.add(Position.fromDegrees(newLatitude, newLongitude, 0));
            }
        }

        return new BallisticTrajectory(trajectoryPositions);
    }
}
