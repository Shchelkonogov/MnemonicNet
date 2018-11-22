package ru.tn.mNet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tag {

    private String name;
    private String value;
    private List<Attr> attrs = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();
    private List<String> stringTags = new ArrayList<>();
    private boolean close;

    public Tag(String name, boolean close) {
        this.name = name;
        this.close = close;
    }

    public void addAttr(Attr attr) {
        this.attrs.add(attr);
    }

    public void addAttrs(List<Attr> items) {
        this.attrs.addAll(items);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void addStringTags(String tag) {
        stringTags.add(tag);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("<");
        sb.append(name);
        if (Objects.nonNull(attrs)) {
            for (Attr item: attrs) {
                sb.append(' ').append(item);
            }
        }
        if (close) {
            sb.append("/>");
        } else {
            sb.append('>');
            if (Objects.nonNull(tags)) {
                for (Tag item: tags) {
                    sb.append(item);
                }
            }
            if (Objects.nonNull(stringTags)) {
                for (String item: stringTags) {
                    sb.append(item);
                }
            }
            sb.append(value);
            sb.append("</").append(name).append('>');
        }
        return sb.toString();
    }
}
