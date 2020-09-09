package com.lk.taskmanager.services.generic;

public class ResponseBuilder {

    public static <T> GenericResponseDTO<?> buildSuccessResponse(T response){
        if(response == null) {
            response = (T) MessageCodeUtil.getCodeValue(MessageCodeUtil.SUCCESS);
        }
        return buildResponse(MessageCodeUtil.SUCCESS, response);
    }

    public static <T> GenericResponseDTO<?> buildFailureResponse(String messageCode){
        return buildResponse(messageCode, null);
    }

    public static <T> GenericResponseDTO<?> buildResponse(String messageCode, T response){
        GenericResponseDTO<Object> genericResponse = new GenericResponseDTO<>();
        genericResponse.setMessageCode(messageCode);
        genericResponse.setResponse(response);
        if(response == null){
            genericResponse.setResponse(MessageCodeUtil.getCodeValue(messageCode));
        }
        genericResponse.setHttpStatus(MessageCodeUtil.getHttpStatus(messageCode));
        return genericResponse;
    }
}
