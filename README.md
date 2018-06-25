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
- **status_plugin_template**: (boolean) activate/deactivate plugin
- **test_names**: Comma separated list of tests to run

# Broadcasts
No broadcasts

# Providers
No context providers

Field | Type | Description
----- | ---- | -----------
_id | INTEGER | primary key auto-incremented
timestamp | REAL | unix timestamp in milliseconds of sample
device_id | TEXT | AWARE device ID
