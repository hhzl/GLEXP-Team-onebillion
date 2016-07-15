package org.onebillion.xprz.mainui;

import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.onebillion.xprz.controls.OBControl;
import org.onebillion.xprz.controls.OBGroup;
import org.onebillion.xprz.utils.OBImageManager;

import java.util.List;
import java.util.Map;

/**
 * Created by alan on 06/12/15.
 */
public class XPRZ_Menu extends XPRZ_SectionController
{
    String saveConfig;

    public void prepare()
    {
        super.prepare();
        saveConfig = (String)Config().get(MainActivity.CONFIG_APP_CODE);
        loadEvent("main");
        for (OBControl but : filterControls("but.*"))
            if (but instanceof OBGroup)
                ((OBGroup) but).outdent(applyGraphicScale(8));
        for (OBControl c : attachedControls)
            c.texturise(true,this);

        boolean permission1 = MainActivity.mainActivity.isAllPermissionGranted();
    }

    public int buttonFlags()
    {
        List<OBSectionController> arr = MainActivity.mainViewController.viewControllers;
        if (arr.indexOf(this) > 0)
            return OBMainViewController.SHOW_TOP_LEFT_BUTTON;
        return 0;
    }

    public void setSceneXX()
    {

    }

    /*public void setButtons()
    {
        Drawable d = OBImageManager.sharedImageManager().imageNamed("backNormal");
        MainActivity.mainViewController.topLeftButton.setImageDrawable(d);
        d = OBImageManager.sharedImageManager().imageNamed("replayAudioNormal");
        MainActivity.mainViewController.topRightButton.setImageDrawable(d);
    }*/

    public void viewWillAppear(boolean animated)
    {
        super.viewWillAppear(animated);
        for (OBControl c : filterControls("button.*"))
            c.lowlight();
        if (saveConfig != null)
            MainActivity.mainActivity.updateConfigPaths(saveConfig,false);
        //setButtons();
    }

    public void start()
    {
        super.start();
        setStatus(STATUS_IDLE);
    }

    OBControl findTarget(PointF pt)
    {
        for (OBControl c : sortedFilteredControls("button.*"))
        {
            if (c.containsPoint(pt))
                return c;
        }
        return null;
    }

    @Override
    public void touchDownAtPoint(PointF pt,View v)
    {
        if (status() != STATUS_IDLE)
            return;
        OBControl c = findTarget(pt);
        if (c != null)
        {
            Map<String,Object> attrs = (Map<String,Object>)c.propertyValue("attrs");
            final String target = (String)attrs.get("target");
            if (target != null)
            {
                final String parm = (String)attrs.get("parm");
                setStatus(STATUS_BUSY);
                c.highlight();
                String configName = (String)attrs.get("config");
                if (configName == null)
                {
                    String appDir = (String) Config().get("app_code");
                    String[] comps = appDir.split("/");
                    configName = comps[0];
                }
                else
                    MainActivity.mainActivity.updateConfigPaths(configName,false);
                if (!MainActivity.mainViewController.pushViewControllerWithNameConfig(target,configName,true,true,parm))
                    setStatus(STATUS_IDLE);
                c.lowlight();
            }
        }
    }

}
