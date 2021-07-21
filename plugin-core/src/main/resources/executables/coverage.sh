#!/bin/sh
COUNT=$#
echo $COUNT
if [ "$COUNT" -lt "3" ];
then
echo "The syntax of the command is incorrect. usage:  coverage <startDate> <endDate> <userName (, separated if multiple)>"
else
java -classpath "/home/jenkins/plugins/mscodecoverage-plugin/WEB-INF/lib/svn-utility-1.0.jar" com.ms.codecoverageplugin.svn.SvnMain $1 $2 $3 "" ""
fi