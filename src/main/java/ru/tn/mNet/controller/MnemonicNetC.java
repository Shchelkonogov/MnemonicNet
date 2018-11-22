package ru.tn.mNet.controller;

import org.primefaces.event.timeline.TimelineDragDropEvent;
import org.primefaces.event.timeline.TimelineSelectEvent;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@ManagedBean
@ViewScoped
public class MnemonicNetC implements Serializable {

    private TimelineModel model;

    private Date min;
    private Date max;
    private long zoomMin;
    private long zoomMax;

    @PostConstruct
    public void init() {
        model = new TimelineModel();

        Calendar cal = Calendar.getInstance();
        cal.set(2015, Calendar.MAY, 25, 0, 0, 0);
        model.add(new TimelineEvent("First", cal.getTime()));

        cal.set(2015, Calendar.MAY, 26, 0, 0, 0);
        model.add(new TimelineEvent("Last", cal.getTime()));

        // lower limit of visible range
        cal.set(2015, Calendar.JANUARY, 1, 0, 0, 0);
        min = cal.getTime();

        // upper limit of visible range
        cal.set(2015, Calendar.DECEMBER, 31, 0, 0, 0);
        max = cal.getTime();

        // one day in milliseconds for zoomMin
        zoomMin = 1000L * 60 * 60 * 24;

        // about three months in milliseconds for zoomMax
        zoomMax = 1000L * 60 * 60 * 24 * 31 * 3;
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

    public void onSelect(TimelineSelectEvent e) {
        //TimelineEvent timelineEvent = e.getTimelineEvent();

        // get your Id
        //String roadmapId = timelineEvent.getEndDate().toString();
        System.out.println("fffffffffffffffffffffff");
    }
    public void onDrop(TimelineDragDropEvent e) {
        System.out.println("dddddddddddddddddddddddd");
    }

        public long getZoomMax() {
        return zoomMax;
    }
}
