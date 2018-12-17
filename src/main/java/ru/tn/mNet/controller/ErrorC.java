package ru.tn.mNet.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class ErrorC implements Serializable {

    @ManagedProperty("#{param.errorMsg}")
    private String errorMsg;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        if (errorMsg.equals("1")) {
            this.errorMsg = "Объект не выбран";
        } else {
            this.errorMsg = "Срок сессии истек";
        }
    }
}
