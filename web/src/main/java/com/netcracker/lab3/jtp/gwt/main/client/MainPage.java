package com.netcracker.lab3.jtp.gwt.main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

/**
 * Created by Клиент on 14.06.2017.
 */
public class MainPage implements EntryPoint {
    public void onModuleLoad() {
        TextArea text = new TextArea();
        text.setName("Hello world");
        RootPanel.get().add(text);
    }
}
