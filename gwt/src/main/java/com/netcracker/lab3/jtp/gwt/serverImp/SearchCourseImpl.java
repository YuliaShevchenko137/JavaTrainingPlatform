package com.netcracker.lab3.jtp.gwt.serverImp;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.netcracker.lab3.jtp.gwt.server.SearchCourse;

public class SearchCourseImpl extends RemoteServiceServlet implements SearchCourse {

    @Override
    public String search(String name) {
        return "Nothing";
    }
}