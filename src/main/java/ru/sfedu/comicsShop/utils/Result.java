package ru.sfedu.comicsShop.utils;


public class Result<T> {
    private T object;
    private Status status;
    private String message;
    private long newPrice;

    public Result(Status status) {
        this.status = status;
    }

    public Result(T object, Status status, String message) {
        this.object = object;
        this.status = status;
        this.message = message;
    }

    public Result(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Result(T object, Status status) {
        this.object = object;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //!!!
    public Result(Status status, T data, long newPrice){
        this.status = status;
        this.object = data;
        this.newPrice = newPrice;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(long newPrice) {
        this.newPrice = newPrice;
    }
}
