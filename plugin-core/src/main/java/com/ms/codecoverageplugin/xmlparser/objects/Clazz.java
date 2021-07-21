package com.ms.codecoverageplugin.xmlparser.objects;

import javax.xml.bind.annotation.*;

/**
 * Created by CMahesh on 10/17/2017.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Clazz {

    @XmlElementWrapper(name = "lines")
    @XmlElement(name = "line")
    private Line[] lines;

    @XmlAttribute(name = "name")
    private String className;

    @XmlAttribute(name = "filename")
    private String fileName;

    public Line[] getLines() {
        return lines;
    }

    public void setLines(Line[] lines) {
        this.lines = lines;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String retrieveSimpleClassName() {
        return className.substring(className.lastIndexOf('.') + 1);
    }
}
