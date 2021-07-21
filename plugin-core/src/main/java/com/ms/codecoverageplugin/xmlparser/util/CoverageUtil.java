package com.ms.codecoverageplugin.xmlparser.util;

import com.ms.codecoverageplugin.svn.LineModificationData;
import com.ms.codecoverageplugin.xmlparser.objects.*;
import com.ms.codecoverageplugin.xmlparser.objects.Package;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by CMahesh on 10/17/2017.
 */
public class CoverageUtil {

    private  PrintStream printStream;
    private List<Coverage> coverages;
    private Map<String, Line[]> classLinesMap;

    private static final String COVERAGE_FILE_PREFIX = "coverage";
    private static final String COVERAGE_FILE_SUFFIX = "xml";

    public CoverageUtil(String dirNameList, PrintStream printStream) throws Exception {
        this.printStream=printStream;
        String[] dirNames = dirNameList.split(",");
        this.coverages = new ArrayList<>();
        for (String dirName : dirNames) {
            File dir = new File(dirName.trim());
            Collection<File> files = FileUtils.listFiles(dir, new RegexFileFilter("^" + COVERAGE_FILE_PREFIX + "(.*?)" + COVERAGE_FILE_SUFFIX), DirectoryFileFilter.DIRECTORY);
            for (File file : files) {
                printStream.println(file.getAbsolutePath());
                this.coverages.add(parseCoverageFile(new FileInputStream(file)));
            }
        }
        prepareClassLinesMap();
    }

    public CoverageUtil(InputStream is) throws Exception {
        this.printStream=System.out;
        this.coverages = new ArrayList<>();
        this.coverages.add(parseCoverageFile(is));
        prepareClassLinesMap();
    }

    public List<Coverage> getCoverages() {
        return coverages;
    }

    private Coverage parseCoverageFile(InputStream is) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        spf.setFeature("http://xml.org/sax/features/validation", false);

        JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] {Coverage.class, Package.class, Clazz.class, Line.class}, null);
        return (Coverage) jaxbContext.createUnmarshaller().unmarshal(new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(is)));
    }

    private void prepareClassLinesMap() {
        classLinesMap = new HashMap<>();
        for(Coverage coverage: coverages) {
            for(Package pkg: coverage.getPackages()) {
                for(Clazz c: pkg.getClazzs())
                {
                    Line[] linesOldCoverage = classLinesMap.get(c.getFileName());
                    Line[] linesNewCoverage = c.getLines();
                    if(linesOldCoverage == null)
                    {
                        classLinesMap.put(c.getFileName(), linesNewCoverage);
                    }
                    else
                    {
                        Line[] linesConcat = new Line[linesOldCoverage.length + linesNewCoverage.length];
                        System.arraycopy(linesOldCoverage, 0, linesConcat, 0, linesOldCoverage.length);
                        System.arraycopy(linesNewCoverage, 0, linesConcat, linesOldCoverage.length, linesNewCoverage.length);
                        classLinesMap.put(c.getFileName(), linesConcat);
                    }

                }
            }
        }
    }

    public Map<String,FileCoverageData> calculateCoveragePercentages(Map<String,Map<Integer,LineModificationData>> svnClassToLinesMap) {
        Map<String, FileCoverageData> coveragePercentageMap = new HashMap<>();
        int covered, notCovered, temp;
        String storyIds="";
        String fileName;
        for(Map.Entry<String,Map<Integer,LineModificationData>> entry: svnClassToLinesMap.entrySet())
        {
            fileName = getFileName(entry.getKey());
            printStream.println("Calculating Coverage for File: " + fileName);
            final Map<Integer,LineModificationData> lineModificationDataMap = entry.getValue();
            if(!lineModificationDataMap.isEmpty() && fileName != null && !fileName.isEmpty())
            {
                if(classLinesMap.get(fileName) == null)
                {
                    if(!fileName.toLowerCase().endsWith("Test"))
                    {
                        coveragePercentageMap.put(fileName, new FileCoverageData(fileName,0,0,-99,storyIds));
                    }
                    printStream.println("Skipping file for calculation of coverage, Test class or cobertura report not present for this class:" + fileName);

                    continue;
                }
                covered = 0;
                notCovered = 0;
                storyIds="";
                for(Integer lineNumber: lineModificationDataMap.keySet()) {
                    for (Line line : classLinesMap.get(fileName)) {
                        if (line.getNumber().equals(lineNumber)) {
                            temp = line.getHits() > 0 ? covered++ : notCovered++;
                            final LineModificationData lineModificationData = lineModificationDataMap.get(lineNumber);
                            if(lineModificationData!=null)
                            {
                                final String storyId = lineModificationData.getStoryId();
                                if(storyId != null && !storyId.isEmpty()&&!storyIds.contains(storyId))
                                {
                                    storyIds += storyId+", ";
                                }
                                break;
                            }
                        }
                    }
                }
                if(covered + notCovered != 0)
                {
                    coveragePercentageMap.put(fileName, new FileCoverageData(fileName,covered,notCovered,(covered * 100.0)/(covered + notCovered),storyIds));
                }
                else
                {
                    coveragePercentageMap.put(fileName, new FileCoverageData(fileName,0,0,-100,""));
                    //printStream.println("Skipping file: " + entry.getKey());
                }
            }
            else
            {
                printStream.println("Skipping file: File name is empty " + entry.getKey());
            }
        }
        printStream.println("\nSuccessfully Calculated Coverage for  "+coveragePercentageMap.size()+" files- \n" + coveragePercentageMap);
        return coveragePercentageMap;
    }

    private String getFileName(String fullyQualifiedFileName) {
        String removeFrom = "/com/";
        int index = fullyQualifiedFileName.indexOf(removeFrom);
        if(index != -1)
            return fullyQualifiedFileName.substring(index + 1);
        else return null;
    }
}
