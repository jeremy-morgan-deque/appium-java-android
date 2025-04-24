import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

public class AppiumExampleTest {
    // dotenv
    private static final Dotenv dotenv = Dotenv.configure()
        .filename(".env")
        .ignoreIfMissing()
        .load();

    private static String getEnv(String key, String defaultValue) {
        return dotenv.get(key, defaultValue);
    }

    /**
     * Configuration variables with environment variable fallbacks
     * 
     * Required Environment Variables:
     * - AXE_DEVTOOLS_MOBILE_API_KEY: Your Axe DevTools Mobile API key
     * 
     * Optional Environment Variables (with default values):
     * - DEVICE_NAME: Name of the Android device/emulator (default: "INSERT_DEVICE_NAME_HERE")
     * - APK_PATH: Path to the APK file (default: "INSERT_APK_PATH_HERE")
     * - APP_PACKAGE: Package name of the app (default: "INSERT_APP_PACKAGE_HERE")
     * - APP_ACTIVITY: Main activity of the app (default: ".MainActivity")
     * - DRIVER_URL: URL of the Appium server (default: "http://localhost:4723")
     */

    // axe devtools mobile api key
    private static final String AXE_DEVTOOLS_MOBILE_API_KEY = getEnv("AXE_DEVTOOLS_MOBILE_API_KEY", "INSERT_API_KEY_HERE");
    // device name
    private static final String DEVICE_NAME = getEnv("DEVICE_NAME", "INSERT_DEVICE_NAME_HERE");
    // apk path
    private static final String APK_PATH = getEnv("APK_PATH", "INSERT_APK_PATH_HERE");
    // app package
    private static final String APP_PACKAGE = getEnv("APP_PACKAGE", "INSERT_APP_PACKAGE_HERE");
    // app activity
    private static final String APP_ACTIVITY = getEnv("APP_ACTIVITY", ".MainActivity");
    // driver url
    private static final String DRIVER_URL = getEnv("DRIVER_URL", "http://localhost:4723");

    AndroidDriver driver;
    Map<String, String> axeSettings;
    private String timestampDir;

    @Before
    public void setup() throws MalformedURLException {
        timestampDir = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        // Check if the axe devtools mobile api key is set
        if (AXE_DEVTOOLS_MOBILE_API_KEY == null || AXE_DEVTOOLS_MOBILE_API_KEY.isEmpty()) {
            throw new RuntimeException("AXE_DEVTOOLS_MOBILE_API_KEY variable is not set");
        }

        // Initialize the axe settings
        axeSettings = new HashMap<>();
        axeSettings.put("apiKey", AXE_DEVTOOLS_MOBILE_API_KEY);

        UiAutomator2Options caps = new UiAutomator2Options();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:deviceName", DEVICE_NAME);
        caps.setCapability("appium:automationName", "axeUiAutomator2");
        
        // Check if APK exists
        File apkFile = new File(APK_PATH);
        if (!apkFile.exists()) {
            throw new RuntimeException("APK file not found at: " + APK_PATH);
        }
        
        caps.setCapability("appium:app", APK_PATH);
        caps.setCapability("appium:appPackage", APP_PACKAGE);
        caps.setCapability("appium:appActivity", APP_ACTIVITY);

        // Initialize the driver
        driver = new AndroidDriver(URI.create(DRIVER_URL).toURL(), caps);
    }

    @After
    public void tearDown() {
        // Quit the driver
        driver.quit();
        // Generate axe reports in html, csv, and xml formats
        generateAxeReport("html");
        generateAxeReport("csv");
        generateAxeReport("xml");
    }

    /**
     * Save the axe result to a file
     * @param axeResult The axe result to save
     */
    public void saveAxeResultToFile(Object axeResult) {
        // Directory of the axe results json
        String directoryPath = "_axe-results-json/" + timestampDir;
        // File path of the axe result
        String filePath = directoryPath + "/" + UUID.randomUUID().toString() + "-axe-result.json";

        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Write the axe result to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            String json = new Gson().toJson(axeResult);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate an axe report in the specified format
     * @param format The format of the report to generate (html, xml, csv)
     */
    public void generateAxeReport(String format) {
        // Directory of the axe reporter binary
        String execDir = "/_axe-reporter-bin/";

        // Executables
        String winExec = "reporter-cli-win.exe";
        String macExec = "reporter-cli-macos";
        String linuxExec = "reporter-cli-linux";

        // Select the correct executable based on the operating system  
        String exec = winExec;
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            exec = macExec;
        } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            exec = linuxExec;
        }
        exec = execDir + exec;

        // Get and set the correct format, default to html
        String formatArg = "html";
        if (format.equals("xml")) {
            formatArg = "xml";
        } else if (format.equals("csv")) {
            formatArg = "csv";
        } else {
            formatArg = "html";
        }

        // Get the current directory
        String currentDir = System.getProperty("user.dir");

        // Run the axe reporter
        //   Example CLI: reporter-cli-linux _axe-results-json/2025-01-01_12-00-00 _axe-results-html/2025-01-01_12-00-00 --format html
        try {
            new ProcessBuilder(
                currentDir + exec,
                "_axe-results-json/" + timestampDir,
                "_axe-results-" + formatArg + "/" + timestampDir,
                "--format",
                formatArg
            ).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        // Run the axe scan
        Object axeResult = driver.executeScript("mobile: axeScan", axeSettings);

        // If there is an axe error, print it and return
        if (axeResult instanceof Map && ((Map<?, ?>) axeResult).containsKey("axeError")) {
            System.out.println("Axe error: " + ((Map<?, ?>) axeResult).get("axeError"));
            return;
        }

        // Save the axe result to a file
        saveAxeResultToFile(axeResult);
    }
} 