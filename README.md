# Angel Eyes

## Helping blind people have a normal life

### Project Summary Description : 

Angel Eyes is a mobile application made for blind people. This application was primarily made so that blind people in confusing or difficult situations can find ways to solve their problems, like for example finding the right road, or look for an object in the house... etc

The advantage that Angel Eyes offers is that it also implicates willing volunteers to help the blind people, in order to strengthen the social solidarity between members of the same communities.

Toward this same purpose, Angel Eyes strives to answer the immediate needs of blind people by offering to those users the ability to have video calls with interested volunteers in the globe, so that they could guide them or help them in whatever situation they might find themselves in. Those volunteers are signed up as special users and volunteers of the application.

Angel Eyes also offers a second premium service, that is about offering to blind people an artificial intelligence that "sees" in their stead and describes vocally their surroundings to them in real time. It can either help them recognize an obstacle, or find a specific object, or guide him through the city.

This application is also made to be controlled by blind people vocally through keywords.

### Project Full Presentation:

You can find the project's detailled presentation in full  [here](). Note that it is fully in french. An english version is coming soon.

### Testing the Application : 

You can test the application by downloading the APK through this [link](https://github.com/IhabBendidi/AngelEyes/blob/master/gradleBuild/outputs/apk/debug/android-debug.apk) and installing it on your android device.

### Compiling Source Code : 

There are two ways to compile this source code into an APK file : Compiling with gradle, or compiling with bazel. Compiling with bazel is the best option, because it enables the artificial intelligence to have the feature of object tracking, but it has a problem, is that bazel can only be installed in linux machines and has harsh requirements. Building with gradle gives u a fully working APK, but that doesn't have object tracking.

#### I - BUILDING WITH GRADLE (WINDOWS OS) : 


The simplest way to compile the demo app yourself, and try out changes to the project code is to use AndroidStudio. Simply set the "android" directory inside the AngelEyes folder as the project root.

Then edit the `build.gradle` file and change the value of `nativeBuildSystem` to `'none'` so that the project is built in the simplest way possible:

```
def nativeBuildSystem = 'none'
```

When building or synchronizing the project, note that you need to keep internet connection enabled because android studio would automatically download many necessary dependances. It will be an error if internet connection is inexistant or too slow.

Note: Currently, in this build mode, YUV -> RGB is done using a less efficient Java implementation, and object tracking is not available in the Angel Eyes artificial intelligence. Setting the build system to 'cmake' currently only builds libtensorflow_demo.so, which provides fast YUV -> RGB conversion and object tracking, while still acquiring TensorFlow support via the downloaded AAR, so it may be a lightweight way to enable these features.


#### II - Building with Bazel (Linux distribution OS required): 


##### NOTE: Bazel does not currently support building for Android on Windows.

Bazel is the primary build system for Deep Learning Models. To build with Bazel, it and the Android NDK and SDK must be installed on your system.

Make sure to edit the build.gradle file and change the value of `nativeBuildSystem` to `'bazel'` if it isn't already, so that the project is built in the best way possible:

```
def nativeBuildSystem = 'bazel'
```

Follow the next steps to have everything installed in ur linux distribution : 

###### 1 - Ensure that JDK 8, Python, Bash, zip, and the usual C++ build toolchain are installed on your system. On systems based on Debian packages (Debian, Ubuntu): you can install OpenJDK 8 and Python by running the following command in a terminal: 

```
sudo apt-get install build-essential openjdk-8-jdk python zip
```


###### 2 - Download and unpack Bazel's distribution archive. Download bazel-< version >-dist.zip (note that u should shoose the distribution that has the same version in that <> space of ur linux version) from this [link](https://github.com/bazelbuild/bazel/releases).


###### 3 - Build Bazel using ./compile.sh. Do the following to do that : 1. Unpack the distribution archive using the terminal (using the dkpg command in terminal) . 2. Go to ur terminal. 3. cd into the directory where you unpacked the distribution archive. 4. run : `bash ./compile.sh`

###### 4 - Run in ur terminal : 

```
sudo apt-get update
sudo apt-get upgrade
```


###### 5 - The Android NDK is required to build the native (C/C++) TensorFlow code. NDK 16, the revision released in November 2017 and that is automatically offered through android studio, is incompatible with Bazel. You should manually install the 14b version. 

Download the 14b version through this [link](https://developer.android.com/ndk/downloads/older_releases.html#ndk-14b-downloads)

###### 6 - Rename the extracted folder into "ndk-bundle". Go then to the "Android" folder which first appeared when you first installed android studio, go afterward into its subfolder "sdk". Delete the folder there called "ndk-bundle", and paste the folder u downloaded and renamed earlier there.

###### 7 - The Android SDK and build tools may be obtained alternatively as part of Android Studio. Build tools API >= 23 is required to build the  Android demonstration (though it will run on API >= 21 devices).

###### 8 - When first getting into the project through android studio, choose /AngelEyes/Android as root folder, and make sure to have internet enabled.

###### 9 - Once you have everything installed, go to the terminal of android studio, and paste : 

```
bazel build -c opt //android:angel_eyes
```

###### 10 - After building the apk that way, you should get out of that folder in the terminal, through pasting : 

```
cd ~/<root folder>/AngelEyes/
```

Put the root folder of ur computer, which could be git or could be androidStudioProjects.


###### 11 - Check if u have adb installed. if it isnt, run in a normal terminal outside of android studio : 

```
sudo apt-get install adb
```

afterward : 

```
sudo apt-get update
```

###### 12 - Go back to the terminal of android studio and paste : 

```
adb install -r bazel-bin/android/angel_eyes.apk
```

##### NOTE that you should have usb debugging activated in ur phone before running this command

