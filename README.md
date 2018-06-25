AWARE Plugin: Cognitive Experience Sampling
==========================

This plugin can perform Cognitive Tests resembling clinical pencil-and-paper tests of cognition, such as the Mini Mental State Examination (MMSE). Tests are defined using an XML template and can be stored and accessed remotely within the plugin. A digitized version of the MMSE is provided along with the plugin. The plugin is based on a customized version of Aware (https://github.com/jwlay/aware-client) and adds capabilities for several new ESM types:
- Image Naming test, for object recognition and language
- Drawing
- Image copying test, for example for the intesecting pentagons drawing test or the clock drawing test
- One and three stage command tests for testing language, similarly as in the standard MMSE

![developed ESMs](https://github.com/jwlay/Aware-Cognitive-Experience-Sampling/blob/master/Developed%20ESMS.jpeg?raw=true)

# Settings
Parameters adjustable on the dashboard and client:
- **status_plugin_template**: (boolean) activate/deactivate plugin
- **test_names**: Comma separated list of tests to run

# Broadcasts
**ACTION_AWARE_PLUGIN_TEMPLATE**
Broadcast ..., with the following extras:
- **value_1**: (double) amount of time turned off (milliseconds)

# Providers
##  Template Data
> content://mmse.provider.xxx/plugin_template

Field | Type | Description
----- | ---- | -----------
_id | INTEGER | primary key auto-incremented
timestamp | REAL | unix timestamp in milliseconds of sample
device_id | TEXT | AWARE device ID
