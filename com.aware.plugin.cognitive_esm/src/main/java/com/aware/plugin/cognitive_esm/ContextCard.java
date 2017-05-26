package com.aware.plugin.cognitive_esm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aware.Aware;
import com.aware.ESM;
import com.aware.ui.esms.ESMFactory;
import com.aware.utils.IContextCard;

import java.io.IOException;
import java.util.ArrayList;

public class ContextCard implements IContextCard {

    private ListView tests;

    //Constructor used to instantiate this card
    public ContextCard() {}

    @Override
    public View getContextCard(final Context context) {

        //Load card layout
        View card = LayoutInflater.from(context).inflate(R.layout.card, null);

        //Initialize UI elements from the card
        tests = (ListView) card.findViewById(R.id.tests);

        //Set data on the UI
        ArrayList<String> cognitiveTests = new ArrayList<>();
        try {
            for (String item : context.getAssets().list(""))
                cognitiveTests.add(item);
        } catch (IOException e) {
            e.printStackTrace();
            cognitiveTests.add("Failed to load tests");
        }
        if (cognitiveTests.size() == 0 )
            cognitiveTests.add("No tests found");

        tests.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cognitiveTests));
        tests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Schedule the test on click
                ESMFactory esmFactory = new ESMFactory();
                String test = (String) adapterView.getItemAtPosition(i);
                TestExecuter executer = new TestExecuter();
                executer.createTest(context, esmFactory,test);
                ESM.queueESM(context, esmFactory.build());
                Aware.startAWARE(context);
            }
        });


        //Return the card to AWARE/apps
        return card;
    }
}
