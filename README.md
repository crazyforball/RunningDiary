# RunningDiary
## What is this repository for?

This is a functional running app for Android devices. 

Features:
* Keep track of basic running stats
* Visualization of current or historical running routes
* Music player

This is the first version where data are stored on phone's local database.

## How do I get set up?
This project is developed using Android Studio.

Dependencies:
* compileSdkVersion 25
* buildToolsVersion 26.0.1
* minSdkVersion 14
* targetSdkVersion 25
* useLibrary 'org.apache.http.legacy'
* com.android.support:appcompat-v7:25.0.0
* com.android.support:design:25.0.0
* com.android.support:cardview-v7:25.0.0'
* com.google.android.gms:play-services-maps:9.8.0
* com.android.support:support-vector-drawable:25.0.0
* com.android.support.constraint:constraint-layout:1.0.2
* com.github.satyan:sugar:1.3
* junit:junit:4.12

Dependencies are defined in gradle files which would be imported automatically.

No database configuration is required since this version simply keeps data in phone's sqlite database.

It is recommended to use your own GoogleMap API key for further development.

A generated apk is also available in the APK folder.

## Who do I talk to?
Repo owner
