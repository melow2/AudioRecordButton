
# 오디오 녹음 버튼.
Simple audio recorder component for android

## Demo
<p align="center">
  <img src="etc/audio-button.gif" height="500" alt="progress image view" />
</p>

### Add permissions in your androidmanifest.xml
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

## 사용방법.
Add the dependecy

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
   
}

```
## Usage

### XML

```xml
    <com.khs.audiorecorder.AudioRecordButton
        android:id="@+id/audio_record_button"
        android:layout_centerInParent="true"
        android:layout_width="250dp"
        android:layout_height="wrap_content">
    </com.khs.audiorecorder.AudioRecordButton>
```
### Configure XML

* recorder_image_size: size to image micro voice
* remove_image_size: size to image cancel audio
* recorder_image: drawable to image voice
* remove_image: drawable to image voice
* remove_position: left or right (Todo)

### Java

```java

private AudioRecordButton audioRecordButton;
audioRecordButton = (AudioRecordButton) findViewById(R.id.audio_record_button);
```

Starting audio record

```java
audioRecordButton.setOnAudioListener(new AudioListener() {
            @Override
            public void onStop(RecordingItem recordingItem) {
                Toast.makeText(getBaseContext(), "Audio...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Log.d("MainActivity", "Error: " + e.getMessage());
            }
        });
```

If you prefer to execute the sound after the audio capture, just call the `play()` method inside `onStop()`

```java
@Override
public void onStop(RecordingItem recordingItem) {
    Toast.makeText(getBaseContext(), "Audio...", Toast.LENGTH_SHORT).show();
    new AudioRecording(getBaseContext()).play(recordingItem);
}
```