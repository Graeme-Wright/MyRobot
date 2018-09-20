package gw;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.*;

public class WaveBullet
{
    private double startX, startY, startBearing, power;
    private long fireTime;
    private int direction;
    private int[] returnSegment;
}

public class Megazord extends AdvancedRobot
{
    public void run()
    {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setTurnRadarRight(Double.POSITIVE_INFINITY);

        while (true)
        {
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e)
    {
        setTurnRadarRight(2.0 * Utils.normalRelativeAngleDegrees(getHeading() + e.getBearing() - getRadarHeading()));
        fire(1);
    }
}
