package com.ms.codecoverageplugin.xmlparser.util;

import com.ms.codecoverageplugin.svn.LineModificationData;
import com.ms.codecoverageplugin.svn.SvnMain;
import com.ms.codecoverageplugin.xmlparser.objects.FileCoverageData;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;


/**
 * Created by palathingalf on 10/24/2017.
 * This class is used for
 */
public class CoverageUtilTest
{
    @Test
    public void showCoveragefortheInput() throws Exception
    {

        SvnMain svnMain = new SvnMain("2017-01-01","2017-10-01", "https://svnmirror.apac.novell.com/svn/nrm/brimstone/trunk/server/inventory", "palathingalf,kdaddangadi,RMaurya,CMahesh,ksurabhi ");
        Map<String,Map<Integer,LineModificationData>> modifiedFilesToLinesMap = svnMain.getFileModifiedMapWithLineMetaData();
        CoverageUtil coverageUtil = new CoverageUtil("D:\\JENKINS-PLUGIN", System.out);
        Map<String,FileCoverageData> fileToCoveragePercentageMap = coverageUtil.calculateCoveragePercentages(modifiedFilesToLinesMap);

        System.out.println(fileToCoveragePercentageMap);
        int a = 0;

    }

}