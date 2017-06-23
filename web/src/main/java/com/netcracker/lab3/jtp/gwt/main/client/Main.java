package com.netcracker.lab3.jtp.gwt.main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;
import com.netcracker.lab3.jtp.gwt.design.client.Design;

public class Main implements EntryPoint {
    public void onModuleLoad() {
        Panel content = Design.getContent();
        Button button = new Button("Hello");
        content.add(button);
        Design.fillBody(content, RootPanel.get());
    }
}
