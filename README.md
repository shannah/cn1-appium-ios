# Codename One Appium iOS Test Driver

Appium test driver for Android apps developed using Codename One

## Testing Locally

In order to run the tests, you will need to install [Apache Maven](http://maven.apache.org), and Appium (according to the Appium [installation instructions](https://github.com/appium/appium).

You will then need to start appium, eg:

    appium


To compile and run all tests, run:

    mvn test [options]

Where `[options]` should include your command-line options:

* `-Dapp=/path/to/MyApp.app` - The path to the apk file to test.  Required.
* `-DdeviceName=[Device Name]` - The device name to test on. 


## Testing On Amazon Device Farm

To do
