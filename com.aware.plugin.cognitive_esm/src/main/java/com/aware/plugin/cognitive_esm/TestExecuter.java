package com.aware.plugin.cognitive_esm;

import android.content.Context;
import android.util.Log;

import com.aware.ESM;
import com.aware.plugin.cognitive_esm.definition.Instructions;
import com.aware.plugin.cognitive_esm.definition.Schedule;
import com.aware.plugin.cognitive_esm.definition.TestDefinition;
import com.aware.plugin.cognitive_esm.definition.Task;
import com.aware.plugin.cognitive_esm.definition.Component;
import com.aware.ui.esms.ESMFactory;
import com.aware.ui.esms.ESM_Checkbox;
import com.aware.ui.esms.ESM_IMAGE_Freetext;
import com.aware.ui.esms.ESM_Image_Draw;
import com.aware.ui.esms.ESM_Question;
import com.aware.ui.esms.ESM_QuickAnswer;
import com.aware.ui.esms.ESM_Radio;
import com.aware.utils.Scheduler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

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
        try {
            InputStream is = context.getApplicationContext().getAssets().open(File);
            TestDefinition test = TestDeserializer.deserializeXml(getStringFromInputStream(is));
            Log.v(LOG_TAG,"Starting test: "+test.getName());
            this.test = test;
            executeTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return factory;
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

    public void executeTest() {
        setupTestComponents();
        setupTestSchedule();
    }

    private void setupTestComponents() {
        if (test.getComponents() == null) {
            Log.e(LOG_TAG, "No test components found");
            return;
        }
        Log.v(LOG_TAG,Integer.toString(test.getComponents().size()) + " Test components found");
        for (Component Component : test.getComponents()) {
            Log.v(LOG_TAG, Component.getName());
            for (Task task : Component.getTasks()) {
                for (com.aware.plugin.cognitive_esm.definition.Aware aware : task.getAware()) {
                    try {
                        ESM_Question question = constructQuestion(aware.getESM_Type());
                        try {
                            question.setTitle(aware.getTitle())
                                    .setTrigger(test.getName())
                                    .setExpirationThreshold(0);

                            //Additional options for ESMS

                            if (test.isText2speech())
                                question.setSpeakInstructions(true); //enable text-2-speech

                            if (question instanceof ESM_Image_Draw || question instanceof ESM_IMAGE_Freetext) {
                                Instructions instructions = aware.getImageInstructions();
                                JSONObject json = new JSONObject();
                                json.put("Text",instructions.getText());
                                if (instructions.getImageUrl() != null)
                                    json.put("ImageUrl",instructions.getImageUrl());
                                if (instructions.getEncodedImage() != null)
                                    json.put("encodedImage", instructions.getEncodedImage());
                                question.setInstructions(json.toString());
                            } else {
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
                        factory.addESM(question);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
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
            try {
                if (schedule.getHour() != 999)
                    s.addHour(schedule.getHour());

                if (schedule.getMinute() != 999)
                    s.addMinute(schedule.getMinute());

                if (schedule.getMonth() != null)
                    s.addMonth(schedule.getMonth());

                if (schedule.getWeekday() != null)
                    s.addWeekday(schedule.getWeekday());

                s.setActionType(Scheduler.ACTION_TYPE_BROADCAST);
                s.setActionIntentAction(ESM.ACTION_AWARE_QUEUE_ESM);
                s.addActionExtra(ESM.EXTRA_ESM, factory.build());
                Scheduler.saveSchedule(context, s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private ESM_Question constructQuestion(String esm_type) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        esm_type = "com.aware.ui.esms." + esm_type;
        Class<?> c = Class.forName(esm_type);
        ESM_Question question = (ESM_Question) c.newInstance();
        return question;
    }
}
