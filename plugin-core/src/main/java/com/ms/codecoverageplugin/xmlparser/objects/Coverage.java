package com.ms.codecoverageplugin.xmlparser.objects;

import javax.xml.bind.annotation.*;

/**
 * Created by CMahesh on 10/17/2017.
 */
@XmlRootElement(name="coverage")
@XmlAccessorType(XmlAccessType.FIELD)
public class Coverage {

    @XmlElementWrapper(name = "packages")
    @XmlElement(name = "package")
    private Package[] packages;

    public Package[] getPackages() {
        return packages;
    }

    public void setPackages(Package[] packages) {
        this.packages = packages;
    }
}
