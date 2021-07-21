package com.ms.codecoverageplugin.svn;

import com.ms.codecoverageplugin.svn.terminal.Terminal;
import com.ms.codecoverageplugin.svn.terminal.TerminalExecutionException;

import java.io.PrintStream;
import java.util.*;

public class SvnUtility
{

    PrintStream logger;
    private String baseUrl;

    public SvnUtility(PrintStream logger)
    {
        this.logger = logger == null ? System.out : logger;
    }

    public List<String> getModifiedFileFromMultipleUrlSources(String fromDate, String toDate, String multiSourceUrls, String userids)
            throws TerminalExecutionException
    {

        Set<String> fileList = new HashSet();
        final String[] urlArr = multiSourceUrls.split(",");

        for(String url : urlArr)
        {
            final Set<String> modifiedFiles = getModifiedFiles(fromDate, toDate, url, userids);

            if(modifiedFiles != null && !modifiedFiles.isEmpty())
            {
                fileList.addAll(modifiedFiles);
            }

        }
        return new ArrayList<>(fileList);
    }

    public Set<String> getModifiedFiles(String fromDate, String toDate, String url, String userids)
            throws TerminalExecutionException
    {

        Set<String> fileList = new HashSet<>();

        //baseUrl = getBaseUrl(url);
        String commandoutput = runModifiedFileSvnCommand(fromDate, toDate, url, userids);
        Scanner scanner = new Scanner(commandoutput);

        while(scanner.hasNext())
        {
            String line = scanner.nextLine();
            if((line.startsWith("M") || line.startsWith("A")))
            {
                if(line.toLowerCase().endsWith(".java"))
                {
                    fileList.add(line.substring(1).trim());
                }

            }


        }

        // System.out.println(fileList);
        printList(fileList);
        return fileList;
    }

    private void printList(Set<String> fileList)
    {

        for(Iterator<String> iterator = fileList.iterator(); iterator.hasNext(); )
        {
            String string = iterator.next();

            logger.println(string);
        }
    }


    private String runModifiedFileSvnCommand(String fromDate, String toDate, String url, String userids)
            throws TerminalExecutionException
    {

        Terminal terminal = new Terminal(logger);

        String command = "git --no-pager log --oneline --name-status --after=\"" + fromDate + "\" --before=\"" + toDate + "\" ";
        if(userids != null && !userids.isEmpty())
        {
            final String[] split = userids.split(",");
            command += "--committer=\"";
            for(String user : split)
            {


                command+="\\("+ getGitCommiterUserName(user) +"\\)\\|";

            }
        }
        command+="\"";
        //command +=  " | grep -E '^M|^A'";
        command = terminal.executeCommand(command);

        return command;
    }

    public static String getGitCommiterUserName(String user)
    {
        String committer = "";

        if(user == null || user.isEmpty())
            return committer;
        user = user.split("@")[0];

        try(Scanner s = new Scanner(user))
        {
            s.useDelimiter("\\.");
            while(s.hasNext())
            {

                String str = s.next();
                committer += str.substring(0, 1).toUpperCase() + str.substring(1) + ".";
            }

            if(committer.endsWith("."))
            {
                committer=committer.substring(0,committer.length()-1);
            }
        }


        return committer;
    }

    public String getHistoryLog(String fileSource) throws TerminalExecutionException
    {

        String command = "  git log --pretty=\"\\\"%n----%n%H|%ae|%ci%n%s%n----\"\\\" -- " + fileSource + "";


        Terminal terminal = new Terminal(logger);

        String output = "";

        output = terminal.executeCommand(command);
        return output;
    }

    public String getSvnBlameDiff(String lastRevision, String latestRevision, String fileSource) throws TerminalExecutionException
    {

        String command = "git annotate -e -l " + " " + fileSource + " " + latestRevision;

        Terminal terminal = new Terminal(logger);

        String output = "";

        output = terminal.executeCommand(command);
        return output;
    }

}
