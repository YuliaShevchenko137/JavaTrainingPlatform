package com.netcracker.lab3.jtp.gwt.test.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Header;
import com.google.gwt.thirdparty.common.css.compiler.gssfunctions.GssFunctions;
import com.google.gwt.user.client.ui.*;

public class Test implements EntryPoint {
    public void onModuleLoad() {
        HTML text = new HTML("<p>GWT working on test page</p>");
        RootPanel.get().add(text);
    }
}
