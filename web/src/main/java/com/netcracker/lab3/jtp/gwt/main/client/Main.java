package com.netcracker.lab3.jtp.gwt.main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

public class Main implements EntryPoint {
    public void onModuleLoad() {
        TextArea text = new TextArea();
        text.setName("Main page");
        RootPanel.get().add(text);
    }
}
