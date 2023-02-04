package br.com.mariah.restapi.exception;

public class InvalidQueryConditionPassed extends RuntimeException {
    public InvalidQueryConditionPassed(String msg) {
        super(msg);
    }
}
