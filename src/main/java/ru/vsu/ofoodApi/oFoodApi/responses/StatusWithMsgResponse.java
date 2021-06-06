package ru.vsu.ofoodApi.oFoodApi.responses;

public class StatusWithMsgResponse extends StatusResponse{
    public String message;

    public StatusWithMsgResponse(String status, String message) {
        super(status);
        this.message = message;
    }
}