package org.gi.gICore.util;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Result {
    private int code;
    private String message;

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Result SUCCESS = new Result(0, "success");
    public static Result ERROR = new Result(-9999, "error");
    public static Result FAILURE = new Result(-1000, "failure");

    public static Result ERROR(String message) {
        return new Result(-9999, message);
    }

    public static Result FAILURE(String message) {
        return new Result(-1000, message);
    }

    public static Result EXCEPTION(Exception e) {
        return new Result(-1001, e.getMessage());
    }

    public boolean isSuccess() {
        return this.code == 0;
    }

    public boolean isError() {
        return this.code != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return code == result.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Result{code=" + code + ", message='" + message + "'}";
    }
}
