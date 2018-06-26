AWARE Plugin: Cognitive Experience Sampling
==========================

This plugin can perform Cognitive Tests resembling clinical pencil-and-paper tests of cognition, such as the Mini Mental State Examination (MMSE). Tests are defined using an XML template and can be stored and accessed remotely within the plugin. A digitized version of the MMSE is provided along with the plugin. The plugin is based on a customized version of Aware (https://github.com/jwlay/aware-client) and adds capabilities for several new ESM types:
- Image Naming test, for object recognition and language
- Drawing
- Image copying test, for example for the intesecting pentagons drawing test or the clock drawing test
- One and three stage command tests for testing language, similarly as in the standard MMSE

![developed ESMs](https://github.com/jwlay/Aware-Cognitive-Experience-Sampling/blob/master/Developed%20ESMS.jpeg?raw=true)

# Publications

For more information please see:

Wohlfahrt-Laymann, Jan (2017) CogniDecline : tracking mobile interaction for cognitive assessment.
http://purl.utwente.nl/essays/73360

Wohlfahrt-Laymann, J., Hermens, H., Villalonga, C., Vollenbroek-Hutten, M., & Banos, O. (2018). Enabling remote assessment of cognitive behavior through mobile experience sampling. Paper presented at 2nd International Workshop on Emotion Awareness for Pervasive Computing with Mobile and Wearable Devices, EmotionAware 2018, Athens, Greece.
https://research.utwente.nl/en/publications/enabling-remote-assessment-of-cognitive-behavior-through-mobile-e

Wohlfahrt-Laymann, J., Hermens, H., Villalonga, C. et al. J Ambient Intell Human Comput (2018). https://doi.org/10.1007/s12652-018-0827-y

# Settings
Parameters adjustable on the dashboard and client:
- **status_plugin_template**: (boolean) activate/deactivate plugin.
- **test_names**: Comma separated list of tests to run.

# Broadcasts

- **ESM.ACTION_AWARE_TRY_ESM**: try the esm
- **ESM.ACTION_AWARE_QUEUE_ESM**: broadcast to add a new ESM to the queue.
- **ESM.ACTION_AWARE_ESM_ANSWERED**: broadcast when the user answers an ESM question.
- **ESM.ACTION_AWARE_ESM_DISMISSED**: broadcast when the user dismisses an ESM question.
- **ESM.ACTION_AWARE_ESM_EXPIRED**: broadcast when the ESM is expired.
- **ESM.ACTION_AWARE_ESM_REPLACED**: broadcast when the ESM is replaced by another ESM.

# Providers
No context providers

# ESM Data

The ESM data is included within the "esms" table in the database.

Field | Type | Description
----- | ---- | -----------
_id | INTEGER | primary key auto-incremented
timestamp | REAL | unix timestamp in milliseconds of sample
device_id | TEXT | AWARE device ID
esm_json | TEXT | The ESM question json string (see below)
esm_status | INTEGER | the status of the ESM (0-new, 1-dismissed, 2-answered, 3-expired, 4-visible, 5-branched)
esm_expiration_threshold | INTEGER | ESM expiration threshold (in seconds)
esm_notification_timeout | INTEGER | ESM notification timeout (in seconds)
double_esm_user_answer_timestamp | REAL | user answer timestamp
esm_user_answer | TEXT | the user’s answer (see below)
esm_trigger | TEXT | the name of the XML test that triggered the ESM

## ESM User Anwer

The "esm_user_answer" field for ESM types with text based or numeric user answers are a string of the user answer. For drawing tasks such as drawing or image copying tasks, the user answer is a base64 encoded string of the user answer, which can be converted into an image. For the one stage and three stage command the user answer is in json form, which includes a base64 encoded string of the screen, and an indication whether the test was passed successfully. In the case the plugin evaluated the test as failed the "evaluation" object includes an explanation on the evaluation. 


Example of an esm_user_answer output for the tree stage command (the Image object is left blank here):
```json
{
  "Image":"",
  "test passed":false,
  "evaluation":
  {
    "x-values":"Only two circles aligned on the x-axis",
    "y-values":"Only two circles aligned on the y-axis",
    "overlap":"The circles are overlapping"
   }
}
```

## ESM Json

This plugin also introduces several new objects to the "esm_json" field: The "esm_speak_instruction" object indicates whether speech-to-text was used, the "esm_solution" object if a correct solution was defined within the XML definition, and the "esm_class" for the one-stage and three-stage command for the class in the plugin that was used. Note that for the the one-stage and three-stage command the "esm_instruction" field is a JSON String with the internally used instructions to create the ESM dialog, such as:
```json
"esm_instructions":"{\"Text\":\"Volg het eerder genoemde commando.\",\"Shapes\":[{\"type\":\"Circle\",\"xPos\":50,\"yPos\":50,\"radius\":100,\"color\":-65536},{\"type\":\"Circle\",\"xPos\":100,\"yPos\":20,\"radius\":100,\"color\":-16776961},{\"type\":\"Circle\",\"xPos\":10,\"yPos\":100,\"radius\":100,\"color\":-16711936}]}"
```

For further explaination on the JSON output for ESMs already defined within the AWARE framework please check the AWARE framework website: http://www.awareframework.com/esm/

