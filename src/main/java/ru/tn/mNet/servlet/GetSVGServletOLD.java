package ru.tn.mNet.servlet;

import ru.tn.mNet.bean.LoadNetData;
import ru.tn.mNet.bean.LoadSvgContent;
import ru.tn.mNet.model.*;
import ru.tn.mNet.model.svgCreate.Attr;
import ru.tn.mNet.model.svgCreate.AttrInter;
import ru.tn.mNet.model.svgCreate.Tag;
import ru.tn.mNet.model.svgCreate.TagInter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Сервлет формирующий svg сети
 */
@WebServlet(name = "GetSVGServletOLD", urlPatterns = "/getSvgOLD")
public class GetSVGServletOLD extends HttpServlet {

    private HashMap<String, SVG> svgItemsMap = new HashMap<>();

    @Inject
    private LoadSvgContent bean;

    @Inject
    private LoadNetData netDataBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Load mnemonic scheme net for: " + req.getParameter("objectId"));

        int windowWidthMax = 1280;
        double graphWidthMax = 0;
        int objectsCount = 0;
        SVG svg;

        Tag rootTag = new Tag(TagInter.SVG, false);
        rootTag.addAttrs(Arrays.asList(new Attr(AttrInter.WIDTH, String.valueOf(windowWidthMax)),
                new Attr(AttrInter.HEIGHT, "1024"),
                new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                new Attr("xmlns:svg", "http://www.w3.org/2000/svg"),
                new Attr("xmlns:xlink", "http://www.w3.org/1999/xlink")));

        //Линия на которой размещаются все объекты графа
        Tag svgElement = new Tag(TagInter.LINE, true);
        svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.Y2, "640"),
                new Attr(AttrInter.X2, String.valueOf(windowWidthMax)),
                new Attr(AttrInter.Y1, "640"),
                new Attr(AttrInter.X1, "0"),
                new Attr(AttrInter.STROKE_WIDTH, "5"),
                new Attr(AttrInter.STROKE, "#ff0000"),
                new Attr(AttrInter.FILL, "none")));
        rootTag.addTag(svgElement);

        List<NetModel> netData = netDataBean.loadData(req.getParameter("objectId"));
        System.out.println("Graph data: " + netData);

        for (NetModel item: netData) {
            graphWidthMax += item.getLength();
        }

        double xCurrentPosition = 0;
        double proportion;
        for(NetModel netItem: netData) {
            System.out.println("Draw " + netItem.getSvgName());
            if (!netItem.getSvgName().equals("Труба")) {
                proportion = (xCurrentPosition * windowWidthMax) / graphWidthMax;

                //Добавляем объект на граф
                svg = getSVG(netItem.getSvgName());
                svgElement = new Tag(TagInter.GROUP, false);
                svgElement.addAttr(new Attr(AttrInter.TRANSFORM, "translate(" + (proportion - (svg.getWidth() / 2))
                        + ", " + (640 - (svg.getHeight() / 2)) + ")"));
                svgElement.addStringTags(svg.getValue());
                rootTag.addTag(svgElement);

                if (netData.indexOf(netItem) != 0) {
                    objectsCount++;

                    //Добавляем правые стрелки для размера
                    svg = getSVG("arrow_right");
                    svgElement = new Tag(TagInter.GROUP, false);
                    svgElement.addAttr(new Attr(AttrInter.TRANSFORM, "translate(" + (proportion - svg.getWidth())
                            + ", " + (840 + (5 * objectsCount) + (svg.getHeight() * (objectsCount - 1))) + ")"));
                    svgElement.addStringTags(svg.getValue());
                    rootTag.addTag(svgElement);

                    //Добавляем горизонтальную линию размера
                    double yLinePosition = 840 + (5 * objectsCount) + ((objectsCount - 1) * svg.getHeight()) + (svg.getHeight() / 2);
                    svgElement = new Tag(TagInter.LINE, true);
                    svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.Y2, String.valueOf(yLinePosition)),
                            new Attr(AttrInter.X2, String.valueOf(proportion - svg.getWidth())),
                            new Attr(AttrInter.Y1, String.valueOf(yLinePosition)),
                            new Attr(AttrInter.X1, String.valueOf(svg.getWidth() + 1)),
                            new Attr(AttrInter.STROKE, "#000000"),
                            new Attr(AttrInter.FILL, "none")));
                    rootTag.addTag(svgElement);

                    double yTextPosition = 840 + (5 * objectsCount) + ((objectsCount - 1) * svg.getHeight()) + (svg.getHeight() / 2);
                    double xTextPosition = (proportion - svg.getWidth() + (svg.getWidth() + 1)) / 2;

                    //Добавляем текст с расстоянием к размерам
                    svgElement = new Tag(TagInter.TEXT, false);
                    svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                            new Attr("xml:space", "preserve"),
                            new Attr("text-anchor", "middle"),
                            new Attr("font-family", "serif"),
                            new Attr("font-size", "16"),
                            new Attr("y", String.valueOf(yTextPosition - 3)),
                            new Attr("x", String.valueOf(xTextPosition)),
                            new Attr(AttrInter.STROKE_WIDTH, "0"),
                            new Attr(AttrInter.FILL, "#000000")));
                    svgElement.setValue(String.valueOf(new BigDecimal(String.valueOf(xCurrentPosition)).setScale(2, RoundingMode.HALF_EVEN)) + "м");
                    rootTag.addTag(svgElement);

                    //Добавляем текст со временем к размерам
                    String timeDirect = "tп=" + LocalTime.ofSecondOfDay(new BigDecimal(String.valueOf(xCurrentPosition / 1.6))
                            .setScale(0, RoundingMode.HALF_EVEN)
                            .longValueExact())
                            .format(DateTimeFormatter.ofPattern("HHч mmм ssс"));
                    String timeReverse = " tо=" + LocalTime.ofSecondOfDay(new BigDecimal(String.valueOf((2 * graphWidthMax - xCurrentPosition) / 1.6))
                            .setScale(0, RoundingMode.HALF_EVEN)
                            .longValueExact())
                            .format(DateTimeFormatter.ofPattern("HHч mmм ssс"));

                    svgElement = new Tag(TagInter.TEXT, false);
                    svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.XMLNS, "http://www.w3.org/2000/svg"),
                            new Attr("xml:space", "preserve"),
                            new Attr("text-anchor", "middle"),
                            new Attr("font-family", "serif"),
                            new Attr("font-size", "16"),
                            new Attr("y", String.valueOf(yTextPosition + 13)),
                            new Attr("x", String.valueOf(xTextPosition)),
                            new Attr(AttrInter.STROKE_WIDTH, "0"),
                            new Attr(AttrInter.FILL, "#000000")));
                    svgElement.setValue(timeDirect + timeReverse);
                    rootTag.addTag(svgElement);
                }

                //Добавляем вертикальные пунктирные линии для размера
                svgElement = new Tag(TagInter.LINE, true);
                svgElement.addAttrs(Arrays.asList(new Attr(AttrInter.Y2, "730"),
                        new Attr(AttrInter.X2, String.valueOf(proportion)),
                        new Attr(AttrInter.Y1, String.valueOf(840
                                + ((objectsCount - 1) * ((netData.indexOf(netItem) == 0 ? 0 : getSVG("arrow_right").getHeight()) + 5))
                                + (objectsCount == 0 ? 5 : 0))),
                        new Attr(AttrInter.X1, String.valueOf(proportion)),
                        new Attr("stroke-linecap", "square"),
                        new Attr("stroke-dasharray", "5,5"),
                        new Attr(AttrInter.STROKE, "#000000"),
                        new Attr(AttrInter.FILL, "none")));
                rootTag.addTag(svgElement);
            } else {
                xCurrentPosition += netItem.getLength();
            }
        }

        //Добавляем левые стрелочки для обозначения расстояния
        svg = getSVG("arrow_left");
        for (int i = 0; i < objectsCount; i++) {
            svgElement = new Tag(TagInter.GROUP, false);
            svgElement.addAttr(new Attr(AttrInter.TRANSFORM, "translate(-1, " + (840 + 5 + (i * 5) + (i * svg.getHeight())) + ")"));
            svgElement.addStringTags(svg.getValue());
            rootTag.addTag(svgElement);
        }

        String contentS = "<?xml version=\"1.0\"?>" + rootTag;
//        System.out.println(contentS);

        byte[] context = contentS.getBytes("UTF-8");

        resp.setContentType("image/svg+xml");
        resp.setContentLength(context.length);
        resp.getOutputStream().write(context);
    }

    private SVG getSVG(String name) {
        if (!svgItemsMap.containsKey(name)) {
            System.out.println("Add to map item: " + name);
            svgItemsMap.put(name, new SVG(bean.getSvg(name)));
        }
        return svgItemsMap.get(name);
    }
}
