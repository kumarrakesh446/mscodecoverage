package com.ms.codecoverageplugin.xmlparser.objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by CMahesh on 10/17/2017.
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Line {

    @XmlAttribute
    private Integer number;

    @XmlAttribute
    private Integer hits;

    @XmlAttribute
    private Boolean branch;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Boolean getBranch() {
        return branch;
    }

    public void setBranch(Boolean branch) {
        this.branch = branch;
    }
}
