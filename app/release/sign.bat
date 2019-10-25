cd "C:\Program Files\Java\jdk1.8.0_101\bin"
jarsigner -digestalg SHA1 -sigalg MD5withRSA -verbose -keystore guet_sign.jks -signedjar F:\coursetable\GUET-CourseTable\app\release\signed.apk F:\coursetable\GUET-CourseTable\app\release\app-release.apk fxl
