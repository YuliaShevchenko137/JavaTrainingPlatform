package com.netcracker.lab3.jtp.gwt.server;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchCourseAsync {
    void search(String name, AsyncCallback<String> async);
}
