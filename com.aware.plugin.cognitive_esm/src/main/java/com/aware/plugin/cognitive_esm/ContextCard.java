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

    //Constructor used to instantiate this card
    public ContextCard() {}

    @Override
    public View getContextCard(final Context context) {

        //Load card layout
        View card = LayoutInflater.from(context).inflate(R.layout.card, null);


        //Return the card to AWARE/apps
        return card;
    }
}
