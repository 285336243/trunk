### Feipai
`feipai` android client<br>
this is `feipai` androdi client code,not include `setting` document,only a module

***
here is all that is required to get application working:<br>
run this module,you need configuration `settings.gradle`and`build.gradle`file,like this:<br>

`settings.gradle`
```gradle
include ':Feipai'
```
`build.gradle`
```gradle
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:0.12.2'
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}
```
---
###screetshot as beblow
- home page<br>
　  <img src="art/home.png" width="250px">
　　<img src="art/execution.png" width="250px">
- invoke baidu map and select tranfic page.<br>
  　<img src="art/map.png" width="250px">
  　　 <img src="art/select_trans.png" width="250px">
- location and ruborder page<br>.
   　<img src="art/uplocation.png" width="250px">
　 　<img src="art/rub_order.png" width="250px">
- my and other page<br>
  　<img src="art/my.png" width="250px">
  　　　<img src="art/other.png" width="250px">
