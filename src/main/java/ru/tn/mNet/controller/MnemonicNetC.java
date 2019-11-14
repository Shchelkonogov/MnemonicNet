package ru.tn.mNet.controller;

import org.primefaces.PrimeFaces;
import org.primefaces.event.timeline.TimelineAddEvent;
import org.primefaces.model.timeline.TimelineModel;
import ru.tn.mNet.bean.LoadObjectParamData;
import ru.tn.mNet.model.ObjectParamData;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Контроллер для мнемосхемы сети
 */
@ManagedBean
@ViewScoped
public class MnemonicNetC implements Serializable {

    @EJB
    private LoadObjectParamData bean;

    private TimelineModel model;
    private Date min;
    private Date max;
    private long zoomMin;
    private long zoomMax;
    private String object;
    private int type = 1;

    private static final String PARSE_PATTERN = "dd.MM.yyyy HH:mm:ss";

    /**
     * Инициализация timeline
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * Обработчик двойного клика на timeline
     * @param e событие двойного клика
     */
    public void add(TimelineAddEvent e) {
        if(Objects.isNull(object)) {
            throw new FacesException("no such object");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(PARSE_PATTERN);
        String date = sdf.format(e.getStartDate());

        String script = "var svgObject = document.getElementById('svgDocument');" +
                "if('contentDocument' in svgObject) {" +
                "var svgDom = svgObject.contentDocument;" +
                "}" +
                "jQuery('#timeID', svgDom).text('Время на потребителе " + date + "');" +
                "var graphWidthMax = jQuery('#graphWidthMax', svgDom).text();" +
                "var objects = jQuery('#objects', svgDom).text();" +
                "var lengths = jQuery('#lengths', svgDom).text();" +
                "jsCall([{name: 'graphWidthMax', value: graphWidthMax}, " +
                    "{name: 'date', value: '" + date + "'}, " +
                    "{name: 'objects', value: objects}, " +
                    "{name: 'lengths', value: lengths}]);";
        PrimeFaces.current().executeScript(script);
    }

    /**
     * Метод для отрисовки новых значений на мнемосхеме (вызывается js методом)
     */
    public void sendData() {
        StringBuilder script = new StringBuilder();

        String graphWidthMax = getParameter("graphWidthMax");
        String date = getParameter("date");
        String objects = getParameter("objects");
        String lengths = getParameter("lengths");

        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(PARSE_PATTERN));
        List<String> objectsList = Arrays.asList(objects.split(" "));
        List<String> objectsLength = Arrays.asList(lengths.split(" "));

        //Вывод полученных значений
//        System.out.println("dateTime: " + dateTime);
//        System.out.println("windowWidth: " + graphWidthMax);
//        System.out.println("objectsList: " + objectsList);
//        System.out.println("objectsLength: " + objectsLength);

        //Высчитываем время на ТЭЦ так как в момент запроса время должно быть на цтп
        LocalTime timeOnTecLt = LocalTime.ofSecondOfDay(new BigDecimal(String.valueOf(Double.valueOf(graphWidthMax) / 1.6))
                .setScale(0, RoundingMode.HALF_EVEN).longValueExact());
        LocalDateTime timeOnTec = dateTime
                .minusHours(timeOnTecLt.getHour())
                .minusMinutes(timeOnTecLt.getMinute())
                .minusSeconds(timeOnTecLt.getSecond());

        ObjectParamData paramData;
        for (int i = 0; i < objectsList.size(); i++) {
            System.out.println("MnemonicNetC.sendData load object: " + objectsList.get(i));

            double length = Double.valueOf(objectsLength.get(i));

            //Расчет времени на каждом объекте (подача обратка)
            LocalTime startTime = LocalTime.ofSecondOfDay(new BigDecimal(String.valueOf(length / 1.6))
                    .setScale(0, RoundingMode.HALF_EVEN).longValueExact());
            LocalTime endTime = LocalTime.ofSecondOfDay(new BigDecimal(String.valueOf((2 * Double.valueOf(graphWidthMax) - length) / 1.6))
                    .setScale(0, RoundingMode.HALF_EVEN).longValueExact());

            paramData = bean.load(Integer.valueOf(objectsList.get(i)),
                    timeOnTec.plusHours(startTime.getHour())
                            .plusMinutes(startTime.getMinute())
                            .plusSeconds(startTime.getSecond())
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                    timeOnTec.plusHours(endTime.getHour())
                            .plusMinutes(endTime.getMinute())
                            .plusSeconds(endTime.getSecond())
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

            script.append(changeValue("T1_" + (i + 1), paramData.getT1()));
            script.append(changeValue("T2_" + (i + 1), paramData.getT2()));
            script.append(changeValue("T1п_" + (i + 1), paramData.getT1p()));
            script.append(changeValue("T2п_" + (i + 1), paramData.getT2p()));
        }

        script.append("PF('bui').hide();");
        PrimeFaces.current().executeScript(script.toString());
    }

    /**
     * Метод меняет текстовые значения в svg тегах
     * @param name имя тега
     * @param value новое значение
     */
    private String changeValue(String name, String value) {
        return "jQuery('#" + name + "', svgDom).text('" + value + "');";
    }

    private String getParameter(String name) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }

    public void redirect() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/mNet/?objectId=" + object + "&type=" + (type == 1 ? 0 : 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
