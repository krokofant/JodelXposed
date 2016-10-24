Jodel Enhancements
==================

This modules utilises the [Xposed Framework](https://www.youtube.com/watch?v=uRR0Flqx9M8) to add features to [Jodel](https://jodel-app.com/).

## Install
1. Install via the Xposed Installer
2. Disable automatic Jodel updates via Play Store

## Features
* Spoof location
* Disabled anti-xposed measures
* Track posters within a thread
* Removed blur on images
* Upload custom images
* Change/manage account

### Upload custom images
1. Take picture with Jodel camera
2. Select *Replace image*
3. Choose the image shared to Jodel or select from gallery

### Increased control over account
- The account id (UID) is automatically backed upp in `.jodel-settings-v2`
- The UID can be manually changed to change account
- **PS**: Clear all Jodel app data after changing the UID

## Version compatibility
The module aims for compatibility with the latest Jodel version. Jodel apks are usually linked in [the thread on XDA](http://forum.xda-developers.com/xposed/modules/mod-jodelxposed-enhancements-t3350019/).

The latest supported Jodel version is noted [here](https://github.com/krokofant/JodelXposed/blob/master/app/build.gradle#L7).

## Support
* Are you using Android?
* Do you have Xposed working?
* Are you sure your version is compatible?
* Have you read [the thread on XDA](http://forum.xda-developers.com/xposed/modules/mod-jodelxposed-enhancements-t3350019/)?

## Contributions
Pull requests are welcome if you would like to provide additional features or just update the hooks.

Please *test the PR* before you submit it.

# Special thanks
* Charlie Kelly
* Unbrick
