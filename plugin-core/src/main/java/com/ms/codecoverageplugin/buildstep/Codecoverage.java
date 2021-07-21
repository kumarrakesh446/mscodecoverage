package com.ms.codecoverageplugin.buildstep;

/**
 * Created by palathingalf on 10/13/2017.
 * This class is used for
 */
import hudson.Extension;
import hudson.views.ViewsTabBar;
import hudson.views.ViewsTabBarDescriptor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;


public class Codecoverage extends ViewsTabBar {

    @DataBoundConstructor
    public Codecoverage() {

        super();
        //sample changes for testing GIT
    }

    @Extension
    public static final class CustomViewsTabBarDescriptor extends ViewsTabBarDescriptor {

        public CustomViewsTabBarDescriptor() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Custom Views TabBar";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return false;
        }
    }
}