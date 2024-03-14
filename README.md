# Reverie Voice Input SDK

This SDK helps in accurately converting speech into text using an API powered by Reverie's AI
technology. The solution will transcribe the speech in real-time in various Indian languages and
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

| Element    | Type    | Required | Default        | Description                 |
|------------|---------|----------|----------------|-----------------------------|
| Context    | context | true     | -              | Context of the activity     |
| isUiNeeded | boolean | true     | -              | Is Ui needed in client side |
| Language   | String  | false    | ‘en’           | Language of Input          |
| Domain     | String  | false    | ‘voice_search’ | Domain of Input            |


#### Method 2

| Element    | Type    | Required | Default        | Description                 |
|------------|---------|----------|----------------|-----------------------------|
| Context    | context | true     | -              | Context of the activity     |
| isUiNeeded | boolean | true     | -              | Is Ui needed in client side |




### Supported Constants
Various constant values are provided in SDK for DOMAIN, LANGUAGES, and LOGGING parameters

#### Domain
1. Domain.VOICE_SEARCH
2. Domain.GENERIC
3. Domain.BFSI

#### Languages
1.  Languages.ENGLISH
2.  Languages.HINDI

#### Logging
1.  Logging.TRUE - stores client’s audio and keeps transcript in logs.
2.  Logging.NO_AUDIO -  does not store client’s audio but keeps transcript in logs.
3.  Logging.NO_TRANSCRIPT - does not keep transcript in logs but stores client’s audio.
4.  Logging.FALSE - does not keep the client’s audio or the transcript in the log.



## Integrate the SDK in Your Application

To integrate the SDK into your application, follow these steps:
Min Supported Android SDK-21
1. Place the library AAR file in the `libs` folder.
2. Add the following dependency in the app-level `build.gradle` file:

```
dependencies {
    implementation files('libs/reverie-voice-input_v1.0.aar')
    implementation 'com.squareup.okhttp3:okhttp:4.9.3'
}
```

> Note: Verify that the correct file name is added as a dependency.

## SDK usage example:
- [Kotlin based](#kotlin-based-example-implementation-of-the-sdk)
- [Java based](#java-based-example-implementation-of-the-sdk)






### Kotlin-based example implementation of the SDK:


1. Prepare the constructor:

     ```sh
    //Preparing the constructor with valid API key and APP-ID
          val voiceInput = RevVoiceInput(API_KEY, APP_ID,Logging.TRUE)
    ```
    ```sh
    //Preparing the Constructor with  API-key , APP-ID , language,domain and logging
        val  voiceInput=RevVoiceInput(API_KEY,APP_ID,
        Domain.VOICE_SEARCH, 
        Languages.ENGLISH,
        Logging.TRUE)
    ```


2. Implement the listener:
    ```sh
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

3. Starting the Input process:
    ```sh
   //To start the voice search
          voiceInput.startRecognition(
              applicationContext,
              true 
          )
    ```
    ```sh
        voiceInput.startRecognition(
                    getApplicationContext(),
                    true
                    ,Domain.VOICE_SEARCH,
                     Languages.ENGLISH
            )
    ```

4. (Optional) Abort the search process
    ```sh
         //To stop the voice searching forcefully
       voiceInput.cancel();
   ```
5. (Optional) Finish the search process
    ```sh
         //To stop the voice searching forcefully and get the result
       voiceInput.finishInput();
   ```

### Java based example implementation of the SDK:

1. Prepare the constructor:
     ```sh
        //Preparing the constructor with valid API key and APP-ID
        RevVoiceSearch voiceInput = new RevVoiceInput(API_KEY, APP_ID,Logging.TRUE);
              
    ```

   ```sh 
   //Preparing the Constructor with valid API-key , APP-ID , language , domain and Logging
   
    RevVoiceInput voiceInput=RevVoiceInput(API_KEY,APP_ID,
    Domain.VOICE_SEARCH,
    Languages.ENGLISH, 
    Logging.TRUE)

   
    ```
2. Implement the listeners:
    ```sh
          //Implement the listener to get results and error details
       voiceInput.setListener(new VoiceInputListener() {



            @Override
            public void onResult(VoiceInputResultData result) {
                Log.d(TAG, "onResult: " + result);
                outputTv.setText(result.toString());
            }

            @Override
            public void onError(VoiceInputErrorResponseData error) {
                outputTv.setText(error.toString());
            }

            @Override
            public void onRecordingStart(boolean isStart) {

            }

            @Override
            public void onRecordingEnd(boolean isEnd) {

            }

        });
    ```  

3. Starting the search Process.
    ```sh
    //To Start the Voice Input
          voiceInput.startRecognition(
              getApplicationContext(),//Context
              true//isUiNeeded
          );
          
    ```
   ```sh

    voiceInput.startRecognition(
    getApplicationContext(),
    true
    Domain.VOICE_SEARCH, 
    Languages.ENGLISH 
    )
    ```
4. (Optional) Abort the search process
    ```sh
         //To stop the voice searching forcefully
       voiceInput.cancel();
   ```
5. (Optional) Finish the search process
    ```sh
         //To stop the voice searching forcefully and get the result
       voiceInput.finishInput();
   ```

### Necessary Permissions

Following permissions are required for the SDK

```sh
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission  android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
```


License
-------
All Rights Reserved. Copyright 2023. Reverie Language Technologies Limited.(https://reverieinc.com/)

Reverie Voice Input SDK can be used according to the [Apache License, Version 2.0](LICENSE).
        
