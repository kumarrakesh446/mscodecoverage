package com.ms.codecoverageplugin.xmlparser.objects;

import javax.xml.bind.annotation.*;

/**
 * Created by CMahesh on 10/17/2017.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Package {

    @XmlElementWrapper(name = "classes")
    @XmlElement(name = "class")
    private Clazz[] clazzs;

    public Clazz[] getClazzs() {
        return clazzs;
    }

    public void setClazzs(Clazz[] clazzs) {
        this.clazzs = clazzs;
    }
}
