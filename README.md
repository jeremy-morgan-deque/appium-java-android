# Appium Java Android Accessibility Testing

This project demonstrates how to perform accessibility testing on Android applications using Appium and Axe DevTools.

## Prerequisites

- Java 11 or higher
- Maven
- Appium Server
- Android Emulator or physical device
- Android SDK
- axe DevTools Mobile API Key
  - https://axe.deque.com/settings
- axeUiAutomator2 Appium Driver
  - https://docs.deque.com/devtools-mobile/2024.9.18/en/appium-setup
- axe Reporter Binary
  - https://docs.deque.com/devtools-mobile/2024.9.18/en/reporter


## Setup

1. Clone this repository
2. Configure your environment variables:
   ```bash
   # Copy the example .env file
   cp .env.example .env
   
   # Edit the .env file with your configuration
   # Required:
   AXE_DEVTOOLS_MOBILE_API_KEY=your-api-key-here
   
   # Optional (with defaults):
   DEVICE_NAME=your-device-name
   APK_PATH=/path/to/your/app.apk
   APP_PACKAGE=com.example.app
   APP_ACTIVITY=.MainActivity
   DRIVER_URL=http://localhost:4723
   ```
3. Install dependencies:
   ```bash
   mvn clean install
   ```

## Project Structure

- `src/test/java/AppiumExampleTest.java`: Main test file containing the accessibility test
- `_axe-reporter-bin/`: Directory where the axe Reporter CLI binary executable is stored.
- `_axe-results-json/`: Directory where JSON test results are stored
- `_axe-results-html/`: Directory where HTML reports are generated
- `_axe-results-csv/`: Directory where CSV reports are generated
- `_axe-results-xml/`: Directory where XML reports are generated

## Running Tests

To run the accessibility test:

```bash
mvn clean test
```

## Test Results

Test results are organized in timestamped directories with the format `yyyy-MM-dd_HH-mm-ss`. Each test run generates:

1. JSON files containing raw test results
2. HTML report for visual analysis
3. CSV report for data analysis
4. XML report for integration with other tools

## Dependencies

- Appium Java Client (9.0.0)
- Selenium (4.14.1)
  - selenium-java
  - selenium-api
  - selenium-remote-driver
- JUnit (4.13.2)
- Gson (2.10.1)
- SLF4J (2.0.9)
  - slf4j-api
  - slf4j-simple
- dotenv-java (3.0.0)
  
  
## Configuration

The test is configured to:
- Use Axe DevTools for accessibility testing
- Save results in multiple formats (JSON, HTML, CSV, XML)
- Generate timestamped directories for each test run
- Support multiple operating systems (Windows, macOS, Linux)

## Environment Variables

The following environment variables can be set in your `.env` file:

- `AXE_DEVTOOLS_MOBILE_API_KEY`: Your Axe DevTools Mobile API key
- `DEVICE_NAME`: Name of the Android device/emulator (default: "INSERT_DEVICE_NAME_HERE")
- `APK_PATH`: Path to the APK file (default: "INSERT_APK_PATH_HERE")
- `APP_PACKAGE`: Package name of the app (default: "INSERT_APP_PACKAGE_HERE")
- `APP_ACTIVITY`: Main activity of the app (default: ".MainActivity")
- `DRIVER_URL`: URL of the Appium server (default: "http://localhost:4723")

**Notes**
- Make sure your `.env` file is not committed to version control (it's in .gitignore)
- The example values in the code will be used if no environment variables are set
- You can override any configuration value by setting the corresponding environment variable 

## Troubleshooting

If you encounter issues:
1. Ensure Appium server is running
2. Verify your API key is correctly set
3. Check that the Android emulator/device is available
4. Review the generated reports for detailed error information
