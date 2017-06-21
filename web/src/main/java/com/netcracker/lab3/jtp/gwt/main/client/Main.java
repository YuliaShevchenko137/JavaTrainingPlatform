package com.netcracker.lab3.jtp.gwt.main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;
import org.apache.http.protocol.HTTP;

public class Main implements EntryPoint {
    public void onModuleLoad() {
        HTML text = new HTML("<p>GWT working</p>");
        RootPanel.get().add(text);
    }
}
