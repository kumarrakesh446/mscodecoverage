package com.ms.codecoverageplugin.buildstep;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.codecoverageplugin.xmlparser.objects.FileCoverageData;
import hudson.model.Action;
import jenkins.model.Jenkins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by CMahesh on 10/18/2017.
 */
public class CodeCoverageBuildAction implements Action{

    private Map<String,FileCoverageData> fileToCoveragePercentageMap;
    private int id;
    private String projectName;
    private int buildNumber;

    private static final String COVERAGE_NOT_AVAILABLE_MESSAGE = "Cobertura report  Not Available for this class";

    public CodeCoverageBuildAction(Map<String,FileCoverageData> fileToCoveragePercentageMap, int id, String projectName, int buildNumber) {
        this.fileToCoveragePercentageMap = fileToCoveragePercentageMap;
        this.id = id;
        this.projectName = projectName;
        this.buildNumber = buildNumber;
    }

    public String getReportUrl() {
        return Jenkins.getInstance().getRootUrl() + "userContent/" + this.projectName + "/builds/" + this.buildNumber + "/coverage-report.csv";
    }

    public String getReportPath() {
        return Jenkins.getInstance().getRootPath() + "/userContent/" + this.projectName + "/builds/" + this.buildNumber + "/coverage-report.csv";
    }

    public void generateReport() {
        try {
            File file = new File(getReportPath());
            if(file.exists()) {
                file.delete();
            }
            file.getParentFile().mkdirs();
            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (Map.Entry<String, FileCoverageData> entry: fileToCoveragePercentageMap.entrySet()) {
                bufferedWriter.write(entry.getKey() + "," + entry.getValue().getStoryIDs().replaceAll(",", " ").trim() + "," + formatCoveragePercentage(entry.getValue()));
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String,FileCoverageData> getFileToCoveragePercentageMap() {
        return fileToCoveragePercentageMap;
    }

    public void setFileToCoveragePercentageMap(Map<String,FileCoverageData> fileToCoveragePercentageMap) {
        this.fileToCoveragePercentageMap = fileToCoveragePercentageMap;
    }

    public Set<Map.Entry<String,FileCoverageData>> getFileCoveragePercentagesSet() {
        return fileToCoveragePercentageMap.entrySet();
    }

    public String formatStoryIds(FileCoverageData coverageData) {
        return coverageData.getStoryIDs();
    }

    public String formatCoveragePercentage(FileCoverageData coverageData) {
        if( coverageData.getCoveragePercent() == -99 )
        {
            return COVERAGE_NOT_AVAILABLE_MESSAGE;
        }
        else if (coverageData.getCoveragePercent() == -100)
        {
            return "Cobertura file present : But in cobertura report no lines found which is covered or not covered";
        }else
        {
            return String.format("%.2f%%", coverageData.getCoveragePercent());
        }
        // return String.format("%.2f%%", coverageData.getCoveragePercent());
    }

    public String formatFilename(String fileName) {
        int index = fileName.lastIndexOf('/');
        if(index != -1) {
            fileName = fileName.substring(index + 1);
        }
        return fileName;
    }

    @Override
    public String toString() {
        try {
            return "CodeCoverageBuildAction{" +
                    "fileToCoveragePercentageMap=" + new ObjectMapper().writeValueAsString(fileToCoveragePercentageMap) +
                    ", id=" + id +
                    '}';
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "Exception occurred";
    }

    @Override
    public String getIconFileName() {
        return "notepad.png";
    }

    @Override
    public String getDisplayName() {
        return "View Code coverage report";
    }

    @Override
    public String getUrlName() {
        return "codecoverage";
    }
}
