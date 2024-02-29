package com.soya.launcher.http.response;

public class WebResponse<T> {
    private Integer code;
    private Boolean success;
    private T result;
    private String msg;

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Integer getCode() {
        if (code == null){
            return -1;
        }
        return code;
    }


    public T getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public Boolean isSuccess() {
        if (success == null){
            return false;
        }
        return success;
    }
}
