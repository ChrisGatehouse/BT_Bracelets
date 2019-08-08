# BT_Bracelets

CS Capstone - Bluetooth Bracelets

---
## Team Members
|Member|Primary Role|Backup Role|
|:---:|:---:|:---:|
|Chris Gatehouse|Team Lead|
|Amanda Williams|Front-end Lead| Team Lead/ Licensing|
|Kallen Harvey|Front-end|Prototyping|
|Edward Koroteev|Back-end|Prototyping|
|Ben Portis|Back-end Lead|Git Master|
|Lucas Phillips|Back-end (Hardware)|
|Ian O'Brien|Front-end|Licensing|Website|

## Sponsor
Dan Hahn
---
## Project Overview
BT_Bracelets is a collaborative Senior Capstone Project seeking to implement an Android application to control and manage the prototyped BlueTooth compatible bracelet. 
The Bracelet has an integrated LED lightstrip controlled by the onboard microcontroller, as well as onboard haptic feedback (NOTE:is there a better way to say vibrator?:NOTE).
The controlling application manages the bracelet by:
- Setting a timer that uses the LED strip as a visual countdown
- Modify the timer
- Set a timer schedule
- Vibrate bracelet at will
- Control LED strip color (?)  

## Stretch Goals
- Integrate and control GPS locator
    - Locate the bracelet in the app
- Ability to link two bracelets together/interact
    - Light up other bracelet (in app or on bracelet A) by button push
    - Send alert to app and vibrate both bracelets if they go too far from each other (geofencing?)

## Requirements

* This application depends on [Android BLE Library](https://github.com/NordicSemiconductor/Android-BLE-Library/) version 2.
* Android 4.3 or newer is required.
* nRF5 DK is required in order to test the BLE Blinky service.

## Note

In order to scan for Bluetooth LE device the Location permission must be granted and, on some phones, 
the Location must be enabled. This app will not use the location information in any way.
