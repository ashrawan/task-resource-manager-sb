package com.lk.taskmanager.utils.exceptions;

public final class AppExceptionConstants {

    // Auth Exception
    public static final String BAD_LOGIN_CREDENTIALS = "Invalid username/password supplied";
    public static final String UNAUTHORIZED_ACCESS = "Insufficient authorization access";

    // User Exception
    public static final String USER_RECORD_NOT_FOUND = "User doesn't exists";
    public static final String USER_NAME_NOT_AVAILABLE = "This username isn't available";
    public static final String OLD_PASSWORD_DOESNT_MATCH = "Old and New Password doesn't match";

    // Task Exception
    public static final String TASK_RECORD_NOT_FOUND = "Task doesn't exists";

    // Raw-Data Exception
    public static final String REQUESTED_RESOURCE_NOT_FOUND = "Couldn't find the requested resource";

}
