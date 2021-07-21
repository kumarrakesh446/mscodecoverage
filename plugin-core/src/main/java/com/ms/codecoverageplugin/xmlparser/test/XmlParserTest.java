package com.ms.codecoverageplugin.xmlparser.test;

import com.ms.codecoverageplugin.xmlparser.objects.Coverage;
import com.ms.codecoverageplugin.xmlparser.util.CoverageUtil;

/**
 * Created by CMahesh on 10/17/2017.
 */
public class XmlParserTest {

    public static void main(String[] args) throws Exception {
        CoverageUtil coverageUtil = new CoverageUtil(Coverage.class.getClassLoader().getResourceAsStream("coverage.xml"));
        Coverage coverage = coverageUtil.getCoverages().get(0);
        System.out.println(coverage.getPackages().length);
    }

}
