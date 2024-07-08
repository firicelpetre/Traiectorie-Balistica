package org.trajectory;

import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;

import java.io.File;

public class OrekitConfig {
    public static void configureOrekit() {
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
