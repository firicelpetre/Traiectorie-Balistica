package org.trajectory;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
//import gov.nasa.worldwind.layers.NASAWFSPlaceNameLayer;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;


import java.awt.Color;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldWind extends JFrame {
    private JTextField latitudeField;
    private JTextField longitudeField;
    private JButton calculateButton;
    private WorldWindowGLCanvas worldWindow;

    private JButton clearLatitudeButton;
    private JButton clearLongitudeButton;
    private JButton clearVelocityButton;
    private JButton clearLaunchAngleButton;
    private JButton clearRotationAngleButton;

    private JTextField velocityField;
    private JTextField launchAngleField;
    private JTextField rotationAngleField;


    private RenderableLayer textLayer;
    private double selectedLatitude;
    private double selectedLongitude;
    private double selectedAltitude;

    private RenderableLayer trajectoryLayer;

    private JButton pauseButton;
    private JButton startButton;

    private Timer timer;
    private boolean isPaused = false;


    public WorldWind() {
        setTitle("Ballistic Trajectory Calculator");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        worldWindow = new WorldWindowGLCanvas();
        worldWindow.setModel(new BasicModel());

        ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
        worldWindow.getModel().getLayers().add(viewControlsLayer);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Latitude (degrees):"), gbc);

        gbc.gridx = 1;
        latitudeField = new JTextField();
        inputPanel.add(latitudeField, gbc);

        gbc.gridx = 2;
        clearLatitudeButton = createClearButton(latitudeField);
        inputPanel.add(clearLatitudeButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Longitude (degrees):"), gbc);

        gbc.gridx = 1;
        longitudeField = new JTextField();
        inputPanel.add(longitudeField, gbc);

        gbc.gridx = 2;
        clearLongitudeButton = createClearButton(longitudeField);
        inputPanel.add(clearLongitudeButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Velocity (m/s):"), gbc);

        gbc.gridx = 1;
        velocityField = new JTextField();
        inputPanel.add(velocityField, gbc);

        gbc.gridx = 2;
        clearVelocityButton = createClearButton(velocityField);
        inputPanel.add(clearVelocityButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Launch Angle (degrees):"), gbc);

        gbc.gridx = 1;
        launchAngleField = new JTextField();
        inputPanel.add(launchAngleField, gbc);

        gbc.gridx = 2;
        clearLaunchAngleButton = createClearButton(launchAngleField);
        inputPanel.add(clearLaunchAngleButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Rotation Angle (degrees):"), gbc);

        gbc.gridx = 1;
        rotationAngleField = new JTextField();
        inputPanel.add(rotationAngleField, gbc);

        gbc.gridx = 2;
        clearRotationAngleButton = createClearButton(rotationAngleField);
        inputPanel.add(clearRotationAngleButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(new JLabel(), gbc);

        gbc.gridx = 1;
        calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(e -> calculateTrajectory());
        inputPanel.add(calculateButton, gbc);

        gbc.gridx = 2;
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> pauseTrajectory());
        inputPanel.add(pauseButton, gbc);

        gbc.gridx = 3;
        startButton = new JButton("Start");
        startButton.addActionListener(e -> startTrajectory());
        inputPanel.add(startButton, gbc);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(worldWindow, BorderLayout.CENTER);

        MyMouseListener mouseListener = new MyMouseListener(worldWindow, this);
        worldWindow.addMouseListener(mouseListener);


        ViewControlsSelectListener viewControlsListener = new ViewControlsSelectListener(worldWindow, viewControlsLayer);


        worldWindow.addSelectListener(viewControlsListener);

        add(mainPanel);

        setVisible(true);


        textLayer = new RenderableLayer();
        worldWindow.getModel().getLayers().add(textLayer);

    }
    private void pauseTrajectory() {
        if (timer != null) {
            timer.stop();
            isPaused = true;
        }
    }

    private void startTrajectory() {
        if (timer != null && isPaused) {
            timer.start();
            isPaused = false;
        }
    }
    private JTextField createInputField(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        JTextField textField = new JTextField();
        panel.add(label);
        panel.add(textField);
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> textField.setText(""));
        panel.add(clearButton);
        return textField;
    }

    private JButton createClearButton(JTextField textField) {
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> textField.setText(""));
        return clearButton;
    }

    public void updateGeographicText(double latitude, double longitude, double altitude) {
        // Șterge textele anterioare
        textLayer.removeAllRenderables();

        String text = String.format("Lat: %.6f, Lon: %.6f, Alt: %.2f m", latitude, longitude, altitude);
        Position position = Position.fromDegrees(latitude, longitude, altitude);
        GlobeAnnotation annotation = new GlobeAnnotation(text, position);

        textLayer.addRenderable(annotation);
        worldWindow.redraw();
    }

    public void setSelectedCoordinates(double latitude, double longitude, double altitude) {
        this.selectedLatitude = latitude;
        this.selectedLongitude = longitude;
        this.selectedAltitude = altitude;
    }
    public void clearGeographicText() {
        textLayer.removeAllRenderables();
        worldWindow.redraw();
    }


    public void calculateTrajectoryInternal(double latitude, double longitude, double altitude) {
        try {
            double velocity = Double.parseDouble(velocityField.getText());
            double launchAngle = Double.parseDouble(launchAngleField.getText());
            double rotationAngle = Double.parseDouble(rotationAngleField.getText());

            System.out.println("Velocity: " + velocity + ", Launch Angle: " + launchAngle + ", Rotation Angle: " + rotationAngle);

            //   returneaza un Path
            BallisticTrajectory trajectory = BallisticTrajectoryCalculator.calculateTrajectory(latitude, longitude, altitude, velocity, launchAngle, rotationAngle);

            System.out.println("Trajectory positions:");
            for (Position pos : trajectory.getPositions()) {
                System.out.println(pos.getLatitude().getDegrees() + ", " + pos.getLongitude().getDegrees() + ", " + pos.getAltitude());
            }

            // Afișează traiectoria pe glob
            displayTrajectory(trajectory);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numerical values for velocity, launch angle, and rotation angle.");
        }
    }



    private void calculateTrajectory() {
        try {
            double latitude;
            double longitude;

            // Verificăm dacă sunt disponibile coordonate introduse manual
            if (!latitudeField.getText().isEmpty() && !longitudeField.getText().isEmpty()) {
                latitude = Double.parseDouble(latitudeField.getText());
                longitude = Double.parseDouble(longitudeField.getText());
                double altitude = worldWindow.getModel().getGlobe().getElevation(Angle.fromDegrees(latitude), Angle.fromDegrees(longitude));
                calculateTrajectoryInternal(latitude, longitude, altitude);
            } else if (selectedLatitude != 0 && selectedLongitude != 0) {
                // Dacă nu sunt introduse coordonate manual, folosim coordonatele selectate
                latitude = selectedLatitude;
                longitude = selectedLongitude;
                double altitude = selectedAltitude;
                calculateTrajectoryInternal(latitude, longitude, altitude);
            } else {
                // Dacă nu sunt disponibile nici coordonate introduse manual, nici selectate, afișăm un mesaj de eroare
                JOptionPane.showMessageDialog(this, "Please enter latitude and longitude manually or select a position with the mouse.");
            }
            // Elimină textul după afișarea traiectoriei
            clearGeographicText();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numerical values for latitude and longitude.");
        }
    }


    private void displayTrajectory(BallisticTrajectory trajectory) {
        removePreviousTrajectory();

        trajectoryLayer = new RenderableLayer();
        worldWindow.getModel().getLayers().add(trajectoryLayer);

        BasicShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setOutlineMaterial(new Material(Color.RED));
        attrs.setOutlineWidth(5);
        attrs.setInteriorMaterial(new Material(new Color(255, 0, 0, 100)));

        List<Position> positions = new ArrayList<>(trajectory.getPositions());
        Path path = new Path(new ArrayList<>());
        path.setAltitudeMode(gov.nasa.worldwind.WorldWind.RELATIVE_TO_GROUND);
        path.setAttributes(attrs);

        List<Position> pathPositions = new ArrayList<>();
        Position initialPosition = positions.get(0);

         timer = new Timer(10, null); // Adjust the timer value to control the speed
        timer.addActionListener(e -> {
            if (!positions.isEmpty()) {
                for (int i = 0; i < 70 && !positions.isEmpty(); i++) {
                    Position pos = positions.remove(0);
                    pathPositions.add(pos);


                    worldWindow.getView().setEyePosition(Position.fromDegrees(pos.getLatitude().getDegrees(), pos.getLongitude().getDegrees(), pos.getAltitude() + 10000000));
                }
                path.setPositions(pathPositions);
                trajectoryLayer.addRenderable(path);
                worldWindow.redraw();
            } else {
                clearGeographicText();
                Position finalPosition = pathPositions.get(pathPositions.size() - 1);
                double altitude = worldWindow.getModel().getGlobe().getElevation(finalPosition.getLatitude(), finalPosition.getLongitude());
                updateGeographicText(finalPosition.getLatitude().getDegrees(), finalPosition.getLongitude().getDegrees(), altitude);

                timer.stop();
            }
        });
        timer.start();

        System.out.println("Trajectory displayed gradually.");
    }
    private void removePreviousTrajectory() {
        for (Layer layer : worldWindow.getModel().getLayers()) {
            if (layer instanceof RenderableLayer) {
                RenderableLayer renderableLayer = (RenderableLayer) layer;
                for (Renderable renderable : renderableLayer.getRenderables()) {
                    if (renderable instanceof Path) {
                        renderableLayer.removeRenderable(renderable);
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(WorldWind::new);


        String orekitDataPath = "D:\\facultatean4\\traiectoriebalistica\\traiectorie\\data\\orekit-data";
        File orekitData = new File(orekitDataPath);
        if (orekitData.exists()) {
            DataProvidersManager manager = DataProvidersManager.getInstance();
            manager.addProvider(new DirectoryCrawler(orekitData));
        } else {
            System.err.println("Orekit data directory not found: " + orekitDataPath);
        }
    }
}
