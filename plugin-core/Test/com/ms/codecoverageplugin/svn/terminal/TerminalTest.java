package com.ms.codecoverageplugin.svn.terminal;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by RMaurya on 11/16/2017.
 */
public class TerminalTest
{
    @Test
    public void waitSomeTimeShouldWaitMaximumTwoMinute() throws Exception
    {

        final Terminal terminalClass =(Terminal) Mockito.spy(new Terminal());
        final Process mock = Mockito.mock(Process.class);

        Mockito.doReturn(true).when(terminalClass).isRunning(mock);

        final long startTime = System.currentTimeMillis();
        terminalClass.waitSomeTime(mock);

        final long endTime = System.currentTimeMillis();

        System.out.println(endTime-startTime);
    }

    @Test
    public void waitSomeTimeShouldReturnOnceItGetResponse() throws Exception
    {

        final Terminal terminalClass =(Terminal) Mockito.spy(new Terminal());
        final Process mock = Mockito.mock(Process.class);

        Mockito.doReturn(false).when(terminalClass).isRunning(mock);

        final long startTime = System.currentTimeMillis();
        terminalClass.waitSomeTime(mock);

        final long endTime = System.currentTimeMillis();

        System.out.println(endTime-startTime);
    }

}