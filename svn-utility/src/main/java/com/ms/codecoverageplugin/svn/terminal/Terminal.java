package com.ms.codecoverageplugin.svn.terminal;

import java.io.*;
import java.util.Scanner;

public class Terminal
{

    public static  String HOME_JENKINS_PLUGINS_msCODECOVERAGE = "/home/jenkins/plugins/mscodecoverage-plugin";
    private static File gitDir;
    private final PrintStream logger;

    public Terminal(PrintStream logger)
    {
        this.logger = logger == null ? System.out : logger;
    }

    public Terminal()
    {
        this.logger = System.out;
    }

    public static void main(String[] args) throws TerminalExecutionException, IOException, InterruptedException
    {
        new Terminal().executeCommand(
                "svn log https://svnmirror.apac.novell.com/svn/nrm/brimstone/trunk/server/final-spoke/libraries/pre-global-action/resources/system-updates/v_17_2_0/sql-anywhere/mobile_inventory_update.sql -q");
    }

    public String executeCommandSplit(String commands) throws TerminalExecutionException
    {

        String out = "";
        try
        {


            logger.println("commands-" + commands);
            logger.println("GIT Dir=" + gitDir);


            Process p = Runtime.getRuntime().exec(commands.split(" "), new String[] {}, gitDir);


            SyncPipe commandOutput = new SyncPipe(p.getInputStream());
            new Thread(commandOutput).start();
            SyncPipe errOut = new SyncPipe(p.getErrorStream());

            new Thread(errOut).start();

            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            waitSomeTime(p);

            logger.println(errOut.getCommandOutput());
            //waitSomeTime(p);
            //System.out.println("" + commandOutput.getCommandOutput());
            out = commandOutput.getCommandOutput();
            logger.println("commands Output =" + out);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new TerminalExecutionException("can not execute commands-" + commands);
        }

        return out;

    }

    String readOutPut()
    {
        try(Scanner fileReader = new Scanner(new File("res.txt")).useDelimiter("\\A"))
        {
            return fileReader.next();
        }
        catch(FileNotFoundException e)
        {
            logger.println(e);
        }
        return "";
    }

    protected void waitSomeTime(Process p)
    {
        int count = 0;
        while(count != 60*2)
        {
            if(isRunning(p))
            {

                count++;
                try
                {
                    Thread.sleep(1000);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {

                return;
            }
        }

        try
        {
            p.destroy();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    boolean isRunning(Process process)
    {
        try
        {
            process.exitValue();
            return false;
        }
        catch(Exception e)
        {
            return true;
        }
    }

    public static void setGitDir(String dir)
    {
        gitDir = new File(dir);
    }

    public String executeCommand(String command) throws TerminalExecutionException
    {

        String out = "";
        try
        {


            logger.println("commands-" + command);
            logger.println("GIT Dir=" + gitDir);

            String current = new java.io.File(".").getCanonicalPath();
            System.out.println("Current dir:" + current);
            logger.println("Working Directory = " +
                    System.getProperty("user.dir"));

            final String OS = System.getProperty("os.name");
            String batchCommand="";
            if(OS.startsWith("Windows"))
            {
                batchCommand="run-git-command.bat";
            }
            else
            {
                batchCommand="run-git-command.sh";
                //command=command.replaceAll("\"","\"\\\"");
                //command=command.replaceFirst("git","git "+gitDir+"/.git ");
                current= HOME_JENKINS_PLUGINS_msCODECOVERAGE;

            }
            command = current + File.separator +batchCommand+ " " + command;
            logger.println("commands-" + command);
            Process p = Runtime.getRuntime().exec(command);


			SyncPipe commandOutput = new SyncPipe(p.getInputStream());
            new Thread(commandOutput).start();
			SyncPipe errOut = new SyncPipe(p.getErrorStream());

			new Thread(errOut).start();
            logger.println("executing......");
            waitSomeTime(p);



            waitSomeTime(p);
            logger.println("" + commandOutput.getCommandOutput());
            logger.println("" + errOut.getCommandOutput());

            out = getCommandOutputFromFile();
            logger.println("commands Output =" + out);
        }
        catch(Exception e)
        {
            e.printStackTrace(logger);
            e.printStackTrace();
            throw new TerminalExecutionException("can not execute commands-" + command);
        }

        return out;

    }

    private String getCommandOutputFromFile() throws FileNotFoundException
    {
        StringBuilder response = new StringBuilder();
        final File file = new File("/home/jenkins/plugins/mscodecoverage-plugin/git_command_out.txt");
        logger.println("Command input file-"+file.getAbsolutePath());
        try(Scanner fileReader = new Scanner(file))
        {
            while(fileReader.hasNext())
            {
                response.append(fileReader.nextLine());
                response.append("\n");
            }

        }
        file.delete();
        return response.toString();
    }
}

class SyncPipe implements Runnable
{
    public SyncPipe(InputStream istrm)
    {
        istrm_ = istrm;

    }

    public void run()
    {
        try
        {

            scanner = new Scanner(istrm_);
            while(scanner.hasNext())
            {
                builder.append(scanner.nextLine() + "\n");

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getCommandOutput()
    {

        return builder.toString();

    }

    private final InputStream istrm_;
    private StringBuilder builder = new StringBuilder();
    private Scanner scanner;
}
