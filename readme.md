
 ![Police Cases](https://raw.githubusercontent.com/pablisco/police-cases/master/common-android/src/main/res/mipmap-xxhdpi/ic_launcher_round.png)

# Police Cases UK

This project contains an app that using Kotlin and the latest Android Studio (AS) tried to demo how to load data from [https://data.police.uk/docs/] and show it.

## Notes:

 - This project is by far production ready
 - It uses coroutines to communicate between the different parts of the app.
 - It has a feature module, but it's not attached to an instant app module, but should be ready to adapt.
 
 # Screen flow
 
 A persistent state machine for each screen is used to handle the control flow of each screen
 
 Each Screen object (Activities) has two main channels. One, outwards, which provides events to the state machine. And the other one, inwards, consumes states sent by other parts.
 
 These channels are Kotlin coroutines' `Channels`. They provide a simple, yet powerful pub/sub pattern. They suspend a thread until a value is either sent (`send`) or offered (`offer`). The later doesn't suspend the thread at the side of the emitter.
  
  ## Dependency Injection
 
In this instance, instead of using the customary Dagger 2, I've used manual injection benefiting from some of the features that Kotlin gives us, like property delegation.
 
 What's missing:
 
 - More caching. The app does no caching other than in memory; this can be improved in several ways. Ideally, the server should provide eTags or expiration dates, and on OkHttp we can set a 
 - For this time, I have merged the view components (Activities) with the state machines. However, they should be moved to a plain Kotlin module along with the model objects. However, there are a couple of issues running unit tests with tests in AS 3.0 and standard Kotlin modules.
 - More testing. I've done a couple of showcases. However, due to time limits, it's not fully tested yet.
 - Separate details screen. To keep it simple, I've made the list that shows crimes use expandable rows. However, this was a shortcut. Ideally, it should show a separate screen, maybe on a live map.
