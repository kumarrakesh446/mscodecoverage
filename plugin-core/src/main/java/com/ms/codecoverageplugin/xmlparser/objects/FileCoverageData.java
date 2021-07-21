package com.ms.codecoverageplugin.xmlparser.objects;

/**
 * Created by RMaurya on 10/24/2017.
 */
public class FileCoverageData
{
    private final String fileName;
    private int covered;
    private int notCovered;
    private double coveragePercent;
    private String storyIDs;

    public FileCoverageData(String fileName,int covered, int notCovered, double coveragePercent, String storyIDs)
    {
        this.covered = covered;
        this.notCovered = notCovered;
        this.coveragePercent = coveragePercent;
        this.storyIDs = storyIDs;
        this.fileName=fileName;
    }

    public int getCovered()
    {
        return covered;
    }

    public int getNotCovered()
    {
        return notCovered;
    }

    public double getCoveragePercent()
    {
        return coveragePercent;
    }

    public String getStoryIDs()
    {
        return storyIDs;
    }

    @Override
    public String toString()
    {
        return "(covered/notCovered) ["+covered+"/"+notCovered+"="+coveragePercent+"%] " +storyIDs+"\n";
    }
}
