package ru.tn.mNet.model;

public class Attr {

    private String name;
    private String value;

    public Attr(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(name);
        sb.append("=\"").append(value).append('"');
        return sb.toString();
    }
}
