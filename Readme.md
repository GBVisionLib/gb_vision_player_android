
# gb vision player
component for live audio commentaries

<img src="captures/screen1.png" width="40%" /> <img src="captures/screen2.png" width="40%" />

[![](https://jitpack.io/v/alvitres01/gb_vision_player_android.svg)](https://jitpack.io/#alvitres01/gb_vision_player_android)

## Installation


```jitpack
dependencies {
	  implementation 'com.github.alvitres01:gb_vision_player_android:Release'
}
```

## How to use

Add view to your layout:

```xml
 <com.gbvision.player.GbVisionPlayer
    android:id="@+id/player"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:gbTitleOff="Play audio commentary"
    app:gbTitleOn="Stop audio commentary"
    app:gbTextColor="@color/color_white"
    app:gbBackgroundColor="@color/color_main"
/>
```

## Configure XML

- `app:gbTitleOff` : Change the title when the player is active.
- `app:gbTitleOn`: Change the title when the player is idle.
- `app:gbTextColor`: Change text color.
- `app:gbBackgroundColor`: Change background color.




start player lifecycle in code
```kotlin
class MainActivity : AppCompatActivity() {

    lateinit var  gbVisionPlayer : GbVisionPlayer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gbVisionPlayer = findViewById(R.id.player)
        
        gbVisionPlayer.titleOn = resources.getString(R.string.stop_audio_commentary)
        gbVisionPlayer.titleOff = resources.getString(R.string.play_audio_commentary)
        gbVisionPlayer.backgroundColor = ContextCompat.getColor(this, R.color.main)
        gbVisionPlayer.textColor = ContextCompat.getColor(this,R.color.white)
        gbVisionPlayer.url = resources.getString(R.string.url)
    }

    override fun onResume() {
        super.onResume()
        gbVisionPlayer.onResume()
    }
    

    override fun onDestroy() {
        super.onDestroy()
        gbVisionPlayer.onDestroy()
    }
}
```
