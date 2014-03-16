SocilaSharingDialogs
====================

SocilaSharingDialogs is simple library for sharing in social network (while only facebook and twitter) without use mechanism of intents on android.

Download
--------------------
Add repository
```
repositories {
  mavenCentral()

  maven {
    url 'https://github.com/tttzof351/maven-repository/raw/master/' // For SocialSharingDialogs
  }

  maven {
    url 'https://github.com/Goddchen/mvn-repo/raw/master/' // For Facebook SDK
  }
}
```
Add dependency
```
dependencies {
    compile 'com.noveo.dialogs:socialsharingdialogs:0.0.1'
}
```
How to use
--------------------

First you must generate applicationId for facebook and pair of api consumer keys for twitter.

Then append to AndroidManifest.xml in application section:
```xml
<!--Twitter-->
<meta-data
  android:name="twitter_consumer_key"
  android:value="<your_twitter_consumer_key>"/>

<meta-data
  android:name="twitter_consumer_secret_key"
  android:value="<your_twitter_consumer_secret_key>"/>
<!--Twitter-->

<!--Facebook-->
<meta-data
  android:name="com.facebook.sdk.ApplicationId"
  android:value="<your_facebook_app_id>"/>

<activity
  android:name="com.facebook.LoginActivity"
  android:theme="@android:style/Theme.Translucent.NoTitleBar"/>
<!--Facebook-->
```
And after you can write something like:
```java
final FacebookShareDialogFragment.Payload payload = new FacebookShareDialogFragment.Payload();
payload.setDescription("Test facebook api");
payload.setLink("http://google.com");

final FacebookShareDialogFragment dialog = FacebookShareDialogFragment.newInstance(payload);
dialog.show(getSupportFragmentManager(), null);
```

```java
final TwitterShareDialog.Payload payload = new TwitterShareDialog.Payload();
payload.setMessage("Test twitter api");
payload.setLink("http://google.com/");

final TwitterShareDialog dialog = TwitterShareDialog.newInstance(payload);
dialog.show(getSupportFragmentManager(), null);
```
