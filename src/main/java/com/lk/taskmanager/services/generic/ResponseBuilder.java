package com.lk.taskmanager.services.generic;

import com.lk.taskmanager.services.generic.dtos.GenericResponseDTO;
import com.lk.taskmanager.utils.MessageCodeUtil;
import org.springframework.data.domain.Page;

import java.util.List;

public class ResponseBuilder {

    public static <T> GenericResponseDTO<T> buildSuccessResponse(T response){
        return buildSuccessResponse(response, 0);
    }

    public static <T> GenericResponseDTO<List<T>> buildPagedSuccessResponse(Page<T> page){
        return buildSuccessResponse(page.getContent(), page.getTotalElements());
    }

    public static <T> GenericResponseDTO<T> buildSuccessResponse(T response, long totalElements){
        if(response == null) {
            response = (T) MessageCodeUtil.getCodeValue(MessageCodeUtil.SUCCESS);
        }
        return buildResponse(MessageCodeUtil.SUCCESS, response, totalElements);
    }

    public static <T> GenericResponseDTO<T> buildFailureResponse(String messageCode){
        return (GenericResponseDTO<T>) buildResponse(messageCode, null, 0);
    }

    public static <T> GenericResponseDTO<T> buildResponse(String messageCode, T response){
        return buildResponse(messageCode, response, 0);
    }

    public static <T> GenericResponseDTO<T> buildResponse(String messageCode, T response, long totalElements){
        GenericResponseDTO<T> genericResponse = new GenericResponseDTO<>();
        genericResponse.setMessageCode(messageCode);
        genericResponse.setResponse(response);
        genericResponse.setTotalElements(totalElements);
        if(response == null){
            genericResponse.setResponse((T) MessageCodeUtil.getCodeValue(messageCode));
        }
        genericResponse.setHttpStatus(MessageCodeUtil.getHttpStatus(messageCode));
        return genericResponse;
    }
}
