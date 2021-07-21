package com.ms.codecoverageplugin.svn;

import com.ms.codecoverageplugin.svn.terminal.TerminalExecutionException;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileModification
{

    //sample change
    private String lastRevision;
    private String latestRevision;
    private Map<String,String> revisionStoryList = new HashMap<>();
    private SvnUtility svnUtility;

    public FileModification(PrintStream logger)
    {
        svnUtility = new SvnUtility(logger);
    }

    public Map<Integer,LineModificationData> getFileModificationMap(String fromDate, String toDate, String fileSource,
                                                                    String userIds, String userStories) throws TerminalExecutionException
    {

        getCompareRevisions(fromDate, toDate, fileSource, userIds, userStories);

        if(revisionStoryList.size() > 0)
        {
            Map<Integer,LineModificationData> lineModificationMap = getLineModificationMap(fileSource, userIds);

            return lineModificationMap;
        }

        return null;
    }


    private Map<Integer,LineModificationData> getLineModificationMap(String fileSource, String userIds)
            throws TerminalExecutionException
    {

        Map<Integer,LineModificationData> modifiedLineMap = null;
        /*
         * if(latestRevision.equalsIgnoreCase(lastRevision)) { //file is added
		 * in between //all lines are modified String
		 * fileContain=SvnUtility.getAllFileContain(fileSource,latestRevision);
		 * 
		 * 
		 * modifiedLineMap=addAllLineAsModified(fileContain); } else { String
		 * diff=getDiffString(fileSource); modifiedLineMap=new
		 * UnidiffDecoder(diff).getModifiedLineMap();
		 * 
		 * }
		 */

        String annotatedFileStr = svnUtility.getSvnBlameDiff(lastRevision, latestRevision, fileSource);
        modifiedLineMap = getModifiedLineMap(annotatedFileStr, userIds);
        return modifiedLineMap;
    }

    private Map<Integer,LineModificationData> getModifiedLineMap(String annotatedFileStr, String userIds)
    {
        Map<Integer,LineModificationData> modifiedLineMap = new LinkedHashMap<>();
        Scanner scanner = new Scanner(annotatedFileStr);
        userIds = userIds.toLowerCase();
        int count = 0;
        while(scanner.hasNext())
        {
            String line = (String)scanner.nextLine();

            count++;

            {
                Scanner scanner2 = new Scanner(line);

                String revisionNo = scanner2.next();
                // System.out.println(revisionNo);
                String modifiedBy = scanner2.next();
                if(modifiedBy.startsWith("(<"))
                {
                    modifiedBy=modifiedBy.substring(2,modifiedBy.length()-1);
                }
                String modifiedLine = scanner2.nextLine();
                if((userIds == null || userIds.isEmpty() || userIds.contains(modifiedBy.toLowerCase())) && revisionStoryList.containsKey(revisionNo))
                {
                    modifiedLineMap.put(count, new LineModificationData(modifiedLine + "\n", modifiedBy, count, revisionNo, revisionStoryList.get(revisionNo)));
                }

            }

        }
        return modifiedLineMap;
    }

    private Map<Integer,LineModificationData> addAllLineAsModified(String fileContain)
    {

        Scanner scanner = new Scanner(fileContain);
        Map<Integer,LineModificationData> modifiedLineMap = new LinkedHashMap<>();
        Integer startLineNo = 0;
        while(scanner.hasNextLine())
        {
            String lineString = scanner.nextLine();

            modifiedLineMap.put(++startLineNo, new LineModificationData(lineString + "\n", "", startLineNo++, "", ""));

        }
        return modifiedLineMap;
    }

    private void processBlameDiff(String annotate)
    {

    }


    private boolean getCompareRevisions(String fromDateStr, String toDateStr, String fileSource, String userIds, String userStories)
            throws TerminalExecutionException
    {

        String historyLog = svnUtility.getHistoryLog(fileSource);
        userIds = userIds.toLowerCase();
        boolean isUpdatedByUser = false;
        if(historyLog != null && !historyLog.isEmpty())
        {
            Date dateFrom = getDate(fromDateStr, "yyyy-MM-dd");
            Date dateTo = getDate(toDateStr, "yyyy-MM-dd");
            Scanner scanner = new Scanner(historyLog);

            while(scanner.hasNext())
            {
                String line = scanner.nextLine();

                if(line.startsWith("---"))
                {
                    continue;
                }
                String[] revisionAttibutes = line.split("\\|");

                if(revisionAttibutes != null && revisionAttibutes.length >= 3)
                {
                    Date revisionDate = getDate(revisionAttibutes[2], "yyyy-MM-dd HH:mm:ss zzz");
                    if(latestRevision == null && (dateTo.compareTo(revisionDate) >= 0))
                    {
                        latestRevision = revisionAttibutes[0].trim();
                        lastRevision = revisionAttibutes[0].trim();

                    }

                    if(!isUpdatedByUser)
                    {
                        isUpdatedByUser = userIds == null || userIds.isEmpty() ? true : userIds.contains(revisionAttibutes[1].trim().toLowerCase());
                    }
                    String storyId = "";
                    while(!line.startsWith("---"))
                    {
                        if(scanner.hasNextLine())
                        {
                            line = scanner.nextLine();


                            if(line.contains("[Story]:"))
                            {
                                if(line.contains("[Review]:"))
                                {
                                    storyId = line.substring(line.indexOf("[Story]:") + "[Story]:".length(), line.indexOf("[Review]:")).trim();
                                }
                                else
                                {
                                    storyId = line.substring(line.indexOf("[Story]:") + "[Story]:".length()).trim();
                                }
                            }
                        }

                    }
                    if(userStories == null || userStories.isEmpty() || (!storyId.isEmpty() && (userStories.contains(storyId) || isStoryIdContains(storyId, userStories))))
                    {
                        revisionStoryList.put(revisionAttibutes[0].trim(), storyId);
                    }
                    if(dateFrom.compareTo(revisionDate) > 0)
                    {
                        lastRevision = revisionAttibutes[0].trim();
                        break;
                    }

                }

            }

        }
        return isUpdatedByUser;

    }

    private boolean isStoryIdContains(String storyId, String userStories)
    {

        boolean isContains = false;

        String[] userStoryIds = userStories.split(",");

        for(int i = 0; i < userStoryIds.length; i++)
        {

            if(storyId.contains(userStoryIds[i]))
            {
                isContains = true;
                break;
            }
        }
        return isContains;
    }


    private Date getDate(String dateStr, String format) throws TerminalExecutionException
    {
        Date date = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try
        {
            date = dateFormat.parse(dateStr);
        }
        catch(ParseException e)
        {
            throw new TerminalExecutionException("Invalid date format-" + dateStr);
        }
        return date;
    }

}
