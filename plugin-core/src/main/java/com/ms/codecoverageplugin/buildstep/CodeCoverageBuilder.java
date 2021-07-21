package com.ms.codecoverageplugin.buildstep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.codecoverageplugin.svn.LineModificationData;
import com.ms.codecoverageplugin.svn.SvnMain;
import com.ms.codecoverageplugin.xmlparser.objects.FileCoverageData;
import com.ms.codecoverageplugin.xmlparser.util.CoverageUtil;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

/**
 * Created by CMahesh on 10/17/2017.
 */
public class CodeCoverageBuilder extends Builder {

    private String buildPath;

    private static final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    @DataBoundConstructor
    public CodeCoverageBuilder(String buildPath)
    {
        this.buildPath = buildPath;
    }





    public String getBuildPath() {
        return buildPath;
    }

    public void setBuildPath(String buildPath) {
        this.buildPath = buildPath;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Collecting code coverage info");
         listener.getLogger().println("build path: " + buildPath);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SvnMain svnMain = new SvnMain(listener.getLogger());
        try {
            Map<String,Map<Integer,LineModificationData>> modifiedFilesToLinesMap = svnMain.getFileModifiedMapWithLineMetaData();
            Set<String> modifiedFilesKeys = modifiedFilesToLinesMap.keySet();
            listener.getLogger().println("Found "+modifiedFilesKeys.size()+" Modified files from Git by the Team:"+ modifiedFilesKeys);
            CoverageUtil coverageUtil = new CoverageUtil(buildPath, listener.getLogger());

            Map<String,FileCoverageData> fileToCoveragePercentageMap = coverageUtil.calculateCoveragePercentages(modifiedFilesToLinesMap);


            CodeCoverageBuildAction buildAction = new CodeCoverageBuildAction(fileToCoveragePercentageMap, build.getActions(CodeCoverageBuildAction.class).size(), build.getProject().getName(), build.getNumber());
            buildAction.generateReport();
            build.addAction(buildAction);

            //listener.getLogger().println(new ObjectMapper().writeValueAsString(fileToCoveragePercentageMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;    // build succeeds
    }

    @Extension
    public static final class CodeCoverageBuildStep extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Code Coverage Setup";
        }

        public AutoCompletionCandidates doAutoCompleteMembers(@QueryParameter String value) {
            AutoCompletionCandidates candidates = new AutoCompletionCandidates();
            if(value != null && !value.isEmpty()) {
                int index = value.lastIndexOf(',');
                String member;
                String prefix = "";
                if (index == -1) {
                    member = value;
                } else {
                    member = value.substring(index + 1);
                    prefix = value.substring(0, index + 1);
                }
                if (!member.isEmpty()) {
                    member = member.trim();
                    for (User user : User.getAll()) {
                        if (user.getId().toLowerCase().startsWith(member.toLowerCase())) {
                            candidates.add(prefix + user.getId());
                        }
                    }
                }
            }
            return candidates;
        }

        public FormValidation doCheckSvnUrl(@QueryParameter String svnUrl) {
            try {
                if(svnUrl.isEmpty()) {
                    return FormValidation.error("svn url can not be empty");
                } else {
                    new URL(svnUrl);
                }
            } catch (MalformedURLException e) {
                return FormValidation.error("please enter a valid url");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckStartDate(@QueryParameter String startDate) {
            try {
                if(startDate == null || startDate.isEmpty()) {
                    return FormValidation.error("start date can not be empty");
                } else {
                    formatter.parse(startDate);
                }
            } catch (Exception e) {
                return FormValidation.error("please enter a date specified by the format in help!");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckEndDate(@QueryParameter String endDate) {
            if(!(endDate == null || endDate.isEmpty())) {
                try {
                    formatter.parse(endDate);
                } catch (Exception e) {
                    return FormValidation.error("please enter a date specified by the format in help!");
                }
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckBuildPath(@QueryParameter String buildPath) {
            if(buildPath == null || buildPath.isEmpty())
                return FormValidation.error("build path can not be empty");
            else
                return FormValidation.ok();
        }

    }

}
