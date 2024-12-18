package com.coms5540.calendarmemo.Utilities;

//This class just a set of String that describe
//the url in use
public class Variable {
    public static String webSocket = "ws://localhost:8081";

    public final static String host = "http://192.168.1.12:5001";
    public final static String register = "/api/auth/register";
    public final static String login = "/api/auth/login";
    public final static String event = "/api/events?startDate=";
    public final static String createEvent = "/api/events";
    public final static String joinGroup = "/api/auth/join-group";
    public final static String requireLock = "/api/events/request-lock";
    public final static String releaseLock = "/api/events/release-lock";
}
