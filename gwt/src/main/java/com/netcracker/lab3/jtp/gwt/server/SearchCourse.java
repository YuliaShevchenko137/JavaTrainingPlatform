package com.netcracker.lab3.jtp.gwt.server;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("search")
public interface SearchCourse extends RemoteService {
    String search(String name);

    public static class App {
        private static final SearchCourseAsync OUR_INSTANCE = (SearchCourseAsync) GWT.create(SearchCourse.class);

        public static SearchCourseAsync getInstance() {
            return OUR_INSTANCE;
        }
    }
}
