package ru.tn.mNet.controller;

import org.primefaces.event.timeline.TimelineAddEvent;
import org.primefaces.model.timeline.TimelineModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Контроллер для мнемосхемы сети
 */
@ManagedBean
@ViewScoped
public class MnemonicNetC implements Serializable {

    private TimelineModel model;

    private Date min;
    private Date max;
    private long zoomMin;
    private long zoomMax;
    private String object;

    /**
     * Инифиализация timeline
     */
    @PostConstruct
    public void init() {
        model = new TimelineModel();

        zoomMin = 1000L * 60 * 2;

        zoomMax = 1000L * 60 * 60 * 24 * 3;

        LocalDate date = LocalDate.now().plusDays(1);
        min = Date.from(date.minusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant());
        max = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public TimelineModel getModel() {
        return model;
    }

    public Date getMin() {
        return min;
    }

    public Date getMax() {
        return max;
    }

    public long getZoomMin() {
        return zoomMin;
    }

    public long getZoomMax() {
        return zoomMax;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    /**
     * Обработчик двойного клика на timeline
     * @param e событие двойного клика
     */
    public void add(TimelineAddEvent e) {
        System.out.println("time to add: " + e.getStartDate());
    }
}
