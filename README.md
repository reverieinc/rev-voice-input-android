# Reverie Voice Input SDK

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/reverieinc/rev-voice-input-android.svg)](https://jitpack.io/#reverieinc/rev-voice-input-android)

This SDK helps in accurately converting speech into text using an API powered by Reverie's AI
technology. The solution will transcribe the speech in real time in various Indian languages and
audio formats.

## Key Features

- **Accurate Speech-to-Text Conversion:** The Voice Input SDK ensures precise and reliable
  conversion of spoken words into text, leveraging the advanced AI technology developed by Reverie.

- **Minimalistic Integration:** Seamlessly integrate the SDK into your application with minimal
  effort, allowing you to focus on enhancing the user experience rather than grappling with complex
  integration processes.

- **Option to Use Bundled UI:** Enjoy the convenience of utilizing the bundled user interface,
  streamlining the integration process and offering a consistent and user-friendly experience within
  your application.


## API Reference

There are two constructors in the **RevVoiceInput** class. The first constructor accepts all parameters, allowing full customization. The second constructor accepts minimal parameters, assuming default values for language and domain.

There are two **startRecognitions()** methods in the **RevVoiceInput** class. The first method allows customization of language and domain for a specific call, while the second method uses default values for language and domain. Users can choose the constructor or method that best fits their needs based on whether they want to customize language and domain for the entire app flow or individual cases.


### Constructors

#### Constructor 1

| Parameter | Type   | Required | Default        | Description                     |
|-----------|--------|----------|----------------|---------------------------------|
| apiKey    | String | true     | -              | Api key given to the client     |
| appId     | String | true     | -              | App Id given to the client      |
| domain    | String | false    | ‘voice_search’ | Domain of the voice Input      |
| Language  | String | false    | ‘en’           | Language of voice Input        |
| Logging   | String | true     | -              | Logging of data required or not |

#### Constructor 2


| Parameter | Type   | Required | Default        | Description                     |
|-----------|--------|----------|----------------|---------------------------------|
| apiKey    | String | true     | -              | Api key given to the client     |
| appId     | String | true     | -              | App Id given to the client      |
| Logging   | String | true     | -              | Logging of data required or not |

### startRecognition()
##### Method 1

| Element    | Type    | Required | Default        | Description                          |
|------------|---------|----------|----------------|--------------------------------------|
| Context    | context | true     | -              | Context of the activity              |
| isUiNeeded | boolean | true     | -              | to use bundled UI element of the SDK |
| Language   | String  | false    | ‘en’           | Language of Input                    |
| Domain     | String  | false    | ‘voice_search’ | Domain of Input                      |


#### Method 2

| Element    | Type    | Required | Default | Description                          |
|------------|---------|----------|---------|--------------------------------------|
| Context    | context | true     | -       | Context of the activity              |
| isUiNeeded | boolean | true     | -       | to use bundled UI element of the SDK |




### Supported Constants
Various constant values are provided in SDK for DOMAIN, LANGUAGES, and LOGGING parameters

#### Domain
1. `Domain.VOICE_SEARCH`
2. `Domain.GENERIC`
3. `Domain.BFSI`

#### Languages
1.  `Languages.ENGLISH`
2.  `Languages.HINDI`

#### Logging
1.  `Logging.TRUE` - stores client’s audio and keeps transcript in logs.
2.  `Logging.NO_AUDIO` -  does not store client’s audio but keeps transcript in logs.
3.  `Logging.NO_TRANSCRIPT` - does not keep transcript in logs but stores client’s audio.
4.  `Logging.FALSE` - does not keep the client’s audio or the transcript in the log.



## Integrate the SDK in Your Application
[![](https://jitpack.io/v/reverieinc/rev-voice-input-android.svg)](https://jitpack.io/#reverieinc/rev-voice-input-android)


To integrate the SDK into your application, follow these steps:
1. Addition of Gradle dependencies as follows:

   - ##### **Groovy:**

       Add the `jitpack.io` repository to `settings.gradle` file:
       ```groovy
       dependencyResolutionManagement {
               repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
               repositories {
                   mavenCentral()
                   maven { url 'https://jitpack.io' }
               }
           }
       ```
       and add the following to the app-level dependencies:
       ```groovy
       dependencies {
             implementation 'com.github.reverieinc:rev-voice-input-android:1.0.3'
       }
       ```

   - ##### **or Kotlin DSL:**
       Add the `jitpack.io` repository to `settings.gradle` file:
       ```groovy
       dependencyResolutionManagement {
           repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
           repositories {
               mavenCentral()
               maven { url = uri("https://jitpack.io") }
           }
       }
       ```
       and add the following to the app-level dependencies:
       ```groovy
       dependencies {
             implementation ("com.github.reverieinc:rev-voice-input-android:1.0.2")
       }
    
       ```
     > Note:
     > - Verify that the latest version is added as a dependency.
     > - Min Supported Android SDK-21


2. Addition of Necessary Manifest Permissions

    Add the following permissions in your manifest:
    
    ```xml
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>
        <uses-permission android:name="android.permission.RECORD_AUDIO" />
    ```
   > Make sure to request the user to grant the RECORD_AUDIO permission at runtime.
   

## SDK usage example:
- [Kotlin based](#kotlin-based-example-implementation-of-the-sdk)
- [Java based](#java-based-example-implementation-of-the-sdk)





<details open>
  <summary>Kotlin</summary>

### Kotlin-based example implementation of the SDK:


1. Prepare the constructor:

   **Type I:**
   
    ```kotlin
    //Preparing the Constructor with  API-key, APP-ID, language, domain, and logging
        val  voiceInput=RevVoiceInput(API_KEY,APP_ID,
        Domain.VOICE_SEARCH, 
        Languages.ENGLISH,
        Logging.TRUE)
    ```
	**Type II:**

     ```kotlin
    //Preparing the constructor with a valid API key and APP-ID
          val voiceInput = RevVoiceInput(API_KEY, APP_ID,Logging.TRUE)
    ```
	> For more details about the types check: [API Reference](#api-reference)


3. Implement the listener:
    ```kotlin
    //Implement the listener to get results and error details
         voiceInput.setListener(object : VoiceInputListener {
          override fun onResult(result: VoiceInputResultData?) {

            }

            override fun onError(error: VoiceInputErrorResponseData?) {

            }

            override fun onRecordingStart(isStart: Boolean) {
                //
            }

            override fun onRecordingEnd(isEnd: Boolean) {
              
            }
        })
    ```

5. Starting the Input process:

   **Type I:**
   
    ```kotlin
        voiceInput.startRecognition(
                    getApplicationContext(),
                    true
                    ,Domain.VOICE_SEARCH,
                     Languages.ENGLISH
            )
    ```
    **Type II:**
    ```kotlin
   //To start the voice search
          voiceInput.startRecognition(
              applicationContext,
              true 
          )
    ```
	> For more details about the types check: [API Reference](#api-reference)

7. (Optional) Abort the search process
    ```kotlin
         //To stop the voice searching forcefully
       voiceInput.cancel()
   ```
8. (Optional) Finish the search process
    ```kotlin
         //To stop the voice searching forcefully and get the result
       voiceInput.finishInput()
   ```
9. (Optional) To Set the No Input Timeout
    ```kotlin
      voiceInput.setNoInputTimeout(5.0)
   ```
10. (Optional) To Set the TimeOut
    ```kotlin
      voiceInput.setTimeout(5.0)
    ```
11. (Optional) To Set the Silence
    ```kotlin
      voiceInput.setSilence(2.0)
    ```   
12. (Optional) To Enable Logging
    ```kotlin
      import com.reverie.voiceinput.LOG;
   
       LOG.DEBUG=true
    ```

</details>

<details>
  <summary>Java</summary>

### Java based example implementation of the SDK:

1. Prepare the constructor:

	**Type I:**
   
   ```java 
   //Preparing the Constructor with a valid API key, APP-ID, language, domain, and Logging
   
    RevVoiceInput voiceInput = = new RevVoiceInput(
                API_KEY,
                APP_ID,
                Domain.VOICE_SEARCH, Languages.ENGLISH, Logging.TRUE);

   
    ```
   **Type II:**
     ```java
        //Preparing the constructor with valid API key and APP-ID
        RevVoiceInput voiceInput = new RevVoiceInput(API_KEY, APP_ID,Logging.TRUE);
              
    ```

   > For more details about the types check: [API Reference](#api-reference)

3. Implement the listeners:
    ```java
          //Implement the listener to get results and error details
       voiceInput.setListener(new VoiceInputListener() {



            @Override
            public void onResult(VoiceInputResultData result) {
                //Log.d(TAG, "onResult: " + result);
            }

            @Override
            public void onError(VoiceInputErrorResponseData error) {
    		//Log.d(TAG, "onError: " + error.toString());
            }

            @Override
            public void onRecordingStart(boolean isStart) {

            }

            @Override
            public void onRecordingEnd(boolean isEnd) {

            }

        });
    ```  

4. Starting the search Process.

   **Type I:**
      ```java

    voiceInput.startRecognition(
    getApplicationContext(),
    true
    Domain.VOICE_SEARCH, 
    Languages.ENGLISH 
    )
    ```
      **Type II:**
   
    ```java
    //To Start the Voice Input
          voiceInput.startRecognition(
              getApplicationContext(),//Context
              true//isUiNeeded
          );
          
    ```

   > For more details about the types check: [API Reference](#api-reference)

6. (Optional) Abort the search process
    ```java
         //To stop the voice searching forcefully
       voiceInput.cancel();
   ```
7. (Optional) Finish the search process
    ```java
         //To stop the voice searching forcefully and get the result
       voiceInput.finishInput();
   ```
8. (Optional) To Set the No Input Timeout
    ```java
      voiceInput.setNoInputTimeout(5.0);
   ```
9. (Optional) To Set the TimeOut    
    ```java
      voiceInput.setTimeout(5.0);
    ```
10. (Optional) To Set the Silence
    ```java
      voiceInput.setSilence(2.0)
    ```
11. (Optional) To Enable Logging(Logcat)
    ```java
        import com.reverie.voiceinput.LOG;
      
        LOG.Companion.setDEBUG(true);
    ```   


</details>

License
-------
All Rights Reserved. Copyright 2023. Reverie Language Technologies Limited.(https://reverieinc.com/)

Reverie Voice Input SDK can be used according to the [Apache License, Version 2.0](LICENSE).
        
