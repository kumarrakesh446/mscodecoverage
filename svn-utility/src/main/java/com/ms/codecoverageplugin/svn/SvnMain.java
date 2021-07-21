package com.ms.codecoverageplugin.svn;

import com.ms.codecoverageplugin.svn.terminal.Terminal;
import com.ms.codecoverageplugin.svn.terminal.TerminalExecutionException;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class SvnMain
{

    private PrintStream logger;
    private String fromDate;
    private String toDate;
    private String url;
    private Map<String,Map<Integer,LineModificationData>> fileModifiedMapwithLineMetaData = new HashMap<>();
    private Map<String,List<Integer>> fileModifiedMapWithOutLineMetaData = new HashMap<>();
    private String userIds;
    private String userStories;
    private SvnUtility svnUtility;

    public SvnMain(PrintStream logger)
    {
        this.logger = logger;
    }


    public Map<String,Map<Integer,LineModificationData>> getFileModifiedMapWithLineMetaData()
            throws TerminalExecutionException
    {

        Map<String,Map<Integer,LineModificationData>> fileModifiedMapwithLineMetaData=new HashMap<>();
        File file=new File(Terminal.HOME_JENKINS_PLUGINS_msCODECOVERAGE+File.separator+"git_diff_response.txt");
        try(ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream(file)))
        {
            fileModifiedMapwithLineMetaData= (Map<String,Map<Integer,LineModificationData>>)objectInputStream.readObject();
            file.delete();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return fileModifiedMapwithLineMetaData;
    }

    public Map<String,Map<Integer,LineModificationData>> getFileModifiedMapWithLineMetaDataFromCLI()
            throws TerminalExecutionException
    {
        getModifiedFileMap();
        return fileModifiedMapwithLineMetaData;
    }

    public Map<String,List<Integer>> getFileModifiedMapWithOutLineMetaData() throws TerminalExecutionException
    {

        getModifiedFileMap();

        return fileModifiedMapWithOutLineMetaData;
    }

    /**
     * @param fromDate
     * @param toDate
     * @param url
     * @param logger
     */
    public SvnMain(String fromDate, String toDate, String url, String userIds, PrintStream logger)
    {
        this(fromDate, toDate, url, userIds, "",logger);
    }

    /**
     * @param fromDate
     * @param toDate
     * @param url
     * @param userStories
     */
    public SvnMain(String fromDate, String toDate, String url, String userIds, String userStories)
    {
        this(fromDate, toDate, url, userIds, "",null);
    }

    /**
     * @param fromDate
     * @param toDate
     * @param url
     * @param userStories
     */
    public SvnMain(String fromDate, String toDate, String url, String userIds, String userStories, PrintStream logger)
    {
        super();
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.url = url;
        this.userIds = userIds;
        this.userStories = userStories;
        this.logger = logger==null?System.out:logger;
        svnUtility=new SvnUtility(logger);
    }

    /**
     * @param fromDate
     * @param toDate
     * @param url
     */
    public SvnMain(String fromDate, String toDate, String url, String userIds)
    {


        this(fromDate, toDate, url, userIds, "",null);
    }

    public static void main(String[] args) throws TerminalExecutionException, IOException
    {

        if(args == null || args.length < 2)
        {
            System.out.println("parameters- fromdate(yyyy-mm-dd) todate(yyyy-mm-dd)  userids url userStroyids");
            System.out.println(
                    "Example-2017-9-17 2017-10-19 rmaurya,yharsh  https://svnmirror.apac.novell.com/svn/nrm/brimstone/branches/Defender/server/inventory,https://svnmirror.apac.novell.com/svn/nrm/brimstone/branches/Defender/server/mobile-bundles  B-114601");
            return;
        }

        // String url =
        // "https://svnmirror.apac.novell.com/svn/nrm/msworks-ui/trunk/zmm/zapp/platforms/android/";

        SvnMain svnMain = new SvnMain(args[0], args[1], args[3], args[2]);

        final Map<String,Map<Integer,LineModificationData>> fileModifiedMapWithLineMetaData = svnMain.getFileModifiedMapWithLineMetaDataFromCLI();

        saveAsObjectFile(fileModifiedMapWithLineMetaData);

    }

    private static void saveAsObjectFile(Map<String, Map<Integer, LineModificationData>> fileModifiedMapWithLineMetaData)
    {
        try(ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(Terminal.HOME_JENKINS_PLUGINS_msCODECOVERAGE+File.separator+"git_diff_response.txt")))
        {
            objectOutputStream.writeObject(fileModifiedMapWithLineMetaData);
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void saveTofile(Map<String,Map<Integer,LineModificationData>> map)
    {

        try
        {
            FileWriter fileWriter = null;
            try
            {
                fileWriter = new FileWriter(new File("out.txt"));

                for(Iterator<Entry<String,Map<Integer,LineModificationData>>> iterator = map.entrySet()
                        .iterator(); iterator.hasNext(); )
                {
                    Entry<String,Map<Integer,LineModificationData>> type = iterator.next();
                    fileWriter.write("****************************************\n");
                    fileWriter.write(type.getKey() + "\n");

                    fileWriter.write("\n" + type.getValue().toString() + "\n");
                    fileWriter.write("****************************************\n");

                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(fileWriter != null)
                {
                    fileWriter.flush();
                    fileWriter.close();
                }
            }

        }
        catch(Exception e)
        {

            e.printStackTrace();
        }

    }

    private void getModifiedFileMap() throws TerminalExecutionException
    {

        long startTime = System.currentTimeMillis();
        List<String> modifiedFileList = svnUtility.getModifiedFileFromMultipleUrlSources(fromDate, toDate, url, userIds);
        int size = modifiedFileList.size();

        int count = 0;
        for(Iterator<String> iterator = modifiedFileList.iterator(); iterator.hasNext(); )
        {
            String fileSource = iterator.next();

            //logger.println("***************************************************************");
            logger.println("[" + (++count) + "/" + size + "] started processing-" + fileSource);
            //logger.println("***************************************************************");
            try
            {
                Map<Integer,LineModificationData> map = new FileModification(logger).getFileModificationMap(fromDate, toDate,
                        fileSource, userIds, userStories);
                if(map != null)
                {
                    fileModifiedMapwithLineMetaData.put(fileSource, map);
                    fileModifiedMapWithOutLineMetaData.put(fileSource, new ArrayList<Integer>(map.keySet()));
                }
            }
            catch(TerminalExecutionException e)
            {
                e.printStackTrace(logger);
            }

        }
        System.out.println("***************************************************************");
        System.out.println(fileModifiedMapwithLineMetaData);

        long endTime = System.currentTimeMillis();

        logger.println("Time Taken=" + (endTime - startTime)/1000+" sec");
        saveTofile(fileModifiedMapwithLineMetaData);

    }

    private Map<Integer,LineModificationData> getFileModification(String fromDate2, String toDate2,
                                                                  String fileSource)
    {

        return null;
    }

}
