# MenuArrowAnimation

![](https://laewoong.github.io/assets/img/2018_03_20_cover.gif?raw=true)

Menu/Arrow Animation using Canvas api in android.
You can see [the original design](https://dribbble.com/shots/2550799-Menu-Arrow-Animation) by Apostol Voicu.

Check the detail in [blog](https://laewoong.github.io/Menu-Arrow-Animation-by-ApostolVoicu/)

## How to use

If you want use this library, you only add the project as a library in your android project settings.

### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```xml
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

### Step 2. Add the dependency

```xml
dependencies {
    implementation 'com.github.laewoong:MenuArrowAnimation:1.0.2'
}
```

This components have custom attributes, if you want use them, you must add this line in your xml file in the first component:

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    ...
    >
</RelativeLayout>
```

You can set two attributes: strokeWidth, strokeColor.

```xml
<com.laewoong.menuarrowanimation.MenuArrowAnimationButton
        android:layout_width="80dp"
        android:layout_height="40dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:strokeWidth="20"
        app:strokeColor="#d7e4f0"/>
```

## Demo

Check the video in [youtube](https://youtu.be/euGaUdCzsxo)
