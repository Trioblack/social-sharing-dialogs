<h1>
  <font color="red">Don't supported, please don't use</font>
</h1>


social-sharing-dialogs
====================

SocilaSharingDialogs is simple library for sharing in social network (while only facebook and twitter) without use mechanism of intents on android.

Download
--------------------
Add repository
```
repositories {
  mavenCentral()

  maven {
    url 'https://github.com/tttzof351/maven-repository/raw/master/' // For social-sharing-dialogs
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

First you must generate [applicationId][1] for facebook and pair of [api consumer keys][2] for twitter.

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
  [1]: https://www.google.ru/url?sa=t&rct=j&q=&esrc=s&source=web&cd=3&cad=rja&uact=8&ved=0CDsQFjAC&url=https%3A%2F%2Fdevelopers.facebook.com%2Fapps&ei=w24lU66VIoGU4ATH04B4&usg=AFQjCNFc1hDNo7MCSTqnT-YIQdISR7hgeQ&sig2=VQMh8zmnNnRaerfrE0YeYw&bvm=bv.62922401,d.bGE
  [2]: https://apps.twitter.com/app/new
