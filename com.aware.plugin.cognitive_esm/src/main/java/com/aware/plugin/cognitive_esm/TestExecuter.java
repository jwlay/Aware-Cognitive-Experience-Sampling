package com.aware.plugin.cognitive_esm;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.aware.plugin.cognitive_esm.definition.Instructions;
import com.aware.plugin.cognitive_esm.definition.Schedule;
import com.aware.plugin.cognitive_esm.definition.Shape;
import com.aware.plugin.cognitive_esm.definition.SpecialInstructions;
import com.aware.plugin.cognitive_esm.definition.TestDefinition;
import com.aware.plugin.cognitive_esm.definition.Task;
import com.aware.plugin.cognitive_esm.definition.Component;
import com.aware.plugin.cognitive_esm.extraESM.MyESM;
import com.aware.plugin.cognitive_esm.extraESM.OneStepCommand;
import com.aware.plugin.cognitive_esm.extraESM.ThreeStepCommand;
import com.aware.ui.esms.ESMFactory;
import com.aware.ui.esms.ESM_Checkbox;
import com.aware.ui.esms.ESM_ImageManipulation;
import com.aware.ui.esms.ESM_IMAGE_Freetext;
import com.aware.ui.esms.ESM_Image_Draw;
import com.aware.ui.esms.ESM_Question;
import com.aware.ui.esms.ESM_QuickAnswer;
import com.aware.ui.esms.ESM_Radio;
import com.aware.utils.Scheduler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Jan Wohlfahrt-Laymann on 2017-05-17.
 */

public class TestExecuter {
    private static String LOG_TAG = "TestExecuter";

    private ESMFactory factory;
    private TestDefinition test;
    private Context context;

    public ESMFactory createTest(Context context, ESMFactory factory, String File) {
        this.context = context;
        this.factory = factory;
        TestDefinition test = null;
        if (File.startsWith("http://") || File.startsWith("https://")) { //the file is a url'
            try {
                Log.v(LOG_TAG,"Trying to connect to url: "+File);
                URL url = new URL(File);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.v(LOG_TAG,"connected to url");
                    InputStream is = connection.getInputStream();
                    test = TestDeserializer.deserializeXml(getStringFromInputStream(is));
                    Log.v(LOG_TAG,"XML deserialized");
                } else {
                    Log.e(LOG_TAG,"Error connection to url: "+connection.getResponseCode()+ " " + connection.getResponseMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                Log.v(LOG_TAG,"Looking for file in local folder");
                InputStream is = context.getApplicationContext().getAssets().open(File);
                test = TestDeserializer.deserializeXml(getStringFromInputStream(is));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (test != null) {
            Log.v(LOG_TAG,"Starting test: "+test.getName());
            this.test = test;
            executeTest();
        } else Log.i(LOG_TAG, "Test is null cannot start");
        return factory;
    }

    public void executeTest() {
        setupTestComponents();
        setupTestSchedule();
    }

    private String buildTestComponents(List<String> components) {
        ESMFactory customFactory = new ESMFactory();
        if (test.getComponents() == null) {
            Log.e(LOG_TAG, "No test components found");
            return null;
        }
        for (String componentName : components) {
            for (Component component : test.getComponents()) {
                if (component.getName().equalsIgnoreCase(componentName))
                    customFactory = setupTestComponent(component, customFactory);
            }
        }
        return customFactory.build();
    }

    private void setupTestComponents() {
        if (test.getComponents() == null) {
            Log.e(LOG_TAG, "No test components found");
            return;
        }
        Log.v(LOG_TAG,Integer.toString(test.getComponents().size()) + " Test components found");
        for (Component component : test.getComponents()) {
            factory = setupTestComponent(component, factory);
        }
    }

    private ESMFactory setupTestComponent(Component component, ESMFactory f) {
        Log.v(LOG_TAG, component.getName());
        for (Task task : component.getTasks()) {
            for (com.aware.plugin.cognitive_esm.definition.Aware aware : task.getAware()) {
                try {
                    ESM_Question question;
                    if (aware.getCommand() != null)
                        question = specialQuestionConstructor(aware.getCommand());
                    else
                        question = constructQuestion(aware.getESM_Type());
                    try {
                        question.setTitle(aware.getTitle())
                                .setTrigger(test.getName())
                                .setExpirationThreshold(0);

                        //Additional options for ESMS
                        Boolean tts = test.getText2speech();
                        if (aware.getText2Speech() != null)
                            tts = aware.getText2Speech();
                        if (tts != null)
                            question.setSpeakInstructions(tts); //enable text-2-speech

                        if (question instanceof ESM_Image_Draw || question instanceof ESM_IMAGE_Freetext) {
                            Instructions instructions = aware.getImageInstructions();
                            JSONObject json = new JSONObject();
                            instructions.setText(instructions.getText().replace("\n","").replace("\r",""));
                            aware.setImageInstructions(instructions);
                            json.put("Text",instructions.getText());
                            if (instructions.getImageUrl() != null)
                                json.put("ImageUrl",instructions.getImageUrl());
                            if (instructions.getEncodedImage() != null)
                                json.put("encodedImage", instructions.getEncodedImage());
                            question.setInstructions(json.toString());
                        } else if (question instanceof ESM_ImageManipulation) {
                            SpecialInstructions instructions = aware.getCommandInstructions();
                            JSONObject json = new JSONObject();
                            JSONArray shapes = new JSONArray();
                            json.put("Text",instructions.getText());
                            for (Shape shape : instructions.getShapeList()) {
                                JSONObject jsonShape = new JSONObject();
                                jsonShape.put("type",shape.getType());
                                jsonShape.put("xPos",shape.getxPos());
                                jsonShape.put("yPos",shape.getyPos());
                                jsonShape.put("radius",shape.getRadius());
                                Field field = Color.class.getField(shape.getColor().toUpperCase());
                                int color = field.getInt(null);
                                jsonShape.put("color",color);
                                Log.v("T",jsonShape.toString());
                                shapes.put(jsonShape);
                            }

                            json.put("Shapes",shapes);
                            question.setInstructions(json.toString());
                        } else {
                            aware.setInstructions(aware.getInstructions().replace("\n","").replace("\r",""));
                            question.setInstructions(aware.getInstructions());
                        }

                        if (question instanceof ESM_Checkbox) {
                            ((ESM_Checkbox) question).setCheckboxes(aware.getOptionsAsJSON());
                        } else if(question instanceof ESM_Radio) {
                            ((ESM_Radio) question).setRadios(aware.getOptionsAsJSON());
                        } else if(question instanceof ESM_QuickAnswer) {
                            ((ESM_QuickAnswer) question).setQuickAnswers(aware.getOptionsAsJSON());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    f.addESM(question);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return f;
    }

    private ESM_Question specialQuestionConstructor(String command) throws JSONException {
        String lc_command = command.toLowerCase();
        switch (lc_command) {
            case "threestagecommand":
                return new ThreeStepCommand().setESM_Class("com.aware.plugin.cognitive_esm.extraESM.ThreeStepCommand");
            case "onestagecommand":
                return new OneStepCommand().setESM_Class("com.aware.plugin.cognitive_esm.extraESM.OneStepCommand");
            default:
                Log.e(LOG_TAG, "Invalid ESM: " + command);
                return null;
        }
    }

    private void setupTestSchedule() {
        if (test.getSchedules() == null) {
            Log.v(LOG_TAG, "No Schedules found");
            return;
        }
        Log.v(LOG_TAG,Integer.toString(test.getSchedules().size()) + " Test schedules found");
        for (Schedule schedule : test.getSchedules()) {
            Scheduler.Schedule s = new Scheduler.Schedule(schedule.getId());
            Log.v(LOG_TAG, "Setting up schedule: "+schedule.getId());
            try {
                for (int hour : schedule.getHour())
                    s.addHour(hour);

                for (int minute : schedule.getMinute())
                    s.addMinute(minute);

                for (String month : schedule.getMonth())
                    s.addMonth(month);

                for (String weekday : schedule.getWeekday())
                    s.addWeekday(weekday);

                s.setActionType(Scheduler.ACTION_TYPE_BROADCAST);
                s.setActionClass(Plugin.ACTION_AWARE_COGNITIVE_ESM);
                s.setActionIntentAction(MyESM.ACTION_AWARE_QUEUE_ESM);

                if (schedule.getComponents().size() == 0)
                    s.addActionExtra(MyESM.EXTRA_ESM, factory.build());
                else
                    s.addActionExtra(MyESM.EXTRA_ESM, buildTestComponents(schedule.getComponents()));
                Scheduler.saveSchedule(context, s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    private ESM_Question constructQuestion(String esm_type) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        esm_type = "com.aware.ui.esms." + esm_type;
        Class<?> c = Class.forName(esm_type);
        ESM_Question question = (ESM_Question) c.newInstance();
        return question;
    }
}
