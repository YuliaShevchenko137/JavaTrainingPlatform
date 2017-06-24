package com.netcracker.lab3.jtp.gwt.test.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

public class Test implements EntryPoint {
    public void onModuleLoad() {
        HTML text = new HTML("<p>GWT working on test page</p>");
        RootPanel.get().add(text);
    }
}
