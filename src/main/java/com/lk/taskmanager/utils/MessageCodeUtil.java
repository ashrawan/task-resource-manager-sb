package com.lk.taskmanager.utils;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MessageCodeUtil {

    // Success
    public static String SUCCESS = "S200";

    // Errors
    public static String NOT_FOUND = "404";
    public static String CONFLICT = "409";
    public static String UNAUTHORIZED = "401";
    public static String REQUEST_MISMATCH = "400";

    // Custom Errors
    public static String FILENAME_ERROR = "F80";
    public static String FILESTORAGE_ERROR = "F81";
    public static String IMPLEMENTATION_NOT_AVAILABLE = "F900";
    public static String UNKNOWN_ERROR = "F999";

    private static final Map<String, String> messageCodeMap = Collections.unmodifiableMap(
            new HashMap<>() {{
                put(SUCCESS, "Operation successful");

                put(NOT_FOUND, "Operation failed, Reason: resource Not Found");
                put(CONFLICT, "Operation failed, Reason: data conflict");
                put(UNAUTHORIZED, "Operation failed, Reason: Unauthorized Access");
                put(REQUEST_MISMATCH, "Request Mismatch, Couldn't process your request");

                put(FILENAME_ERROR, "Sorry! Filename contains invalid path sequence ");
                put(FILESTORAGE_ERROR, "Operation failed, Reason: Could not store file");
                put(IMPLEMENTATION_NOT_AVAILABLE, "Operation failed, Reason: Operation not yet implemented");
                put(UNKNOWN_ERROR, "Something went wrong");
            }});

    private static final Map<String, HttpStatus> httpStatusCodeMap = Collections.unmodifiableMap(
            new HashMap<>() {{
                put(SUCCESS, HttpStatus.OK);

                put(CONFLICT, HttpStatus.CONFLICT);
                put(NOT_FOUND, HttpStatus.NOT_FOUND);
                put(REQUEST_MISMATCH, HttpStatus.BAD_REQUEST);

                put(FILENAME_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
                put(FILESTORAGE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
                put(IMPLEMENTATION_NOT_AVAILABLE, HttpStatus.NOT_IMPLEMENTED);
                put(UNKNOWN_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }});

    public static final String getCodeValue(String key) {
        return messageCodeMap.get(key);
    }
    public static final HttpStatus getHttpStatus(String key) {
        return httpStatusCodeMap.get(key);
    }
}
