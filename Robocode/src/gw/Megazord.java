package gw;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.*;

import java.util.*;

public class Megazord extends AdvancedRobot
{
    List<WaveBullet> waves = new ArrayList<WaveBullet>();

    static int[][] stats = new int[13][31]; // onScannedRobot can scan up to 1200px, so there are only 13.
    int direction = 1;

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

        // Enemy absolute bearing, you can use your one if you already declare it.
        double absBearing = getHeadingRadians() + e.getBearingRadians();

        // find our enemy's location:
        double ex = getX() + Math.sin(absBearing) * e.getDistance();
        double ey = getY() + Math.cos(absBearing) * e.getDistance();

        // Let's process the waves now:
        for (int i=0; i < waves.size(); i++)
        {
            WaveBullet currentWave = (WaveBullet)waves.get(i);
            if (currentWave.checkHit(ex, ey, getTime()))
            {
                waves.remove(currentWave);
                i--;
            }
        }

        double power = 1.9; // Math.min(3, Math.max(.1, /* some function */));a
        // don't try to figure out the direction they're moving
        // they're not moving, just use the direction we had before
        if (e.getVelocity() != 0)
        {
            if (Math.sin(e.getHeadingRadians()-absBearing)*e.getVelocity() < 0)
                direction = -1;
            else
                direction = 1;
        }

        int[] currentStats = stats[(int)(e.getDistance() / 100)]; // It doesn't look silly now!
        WaveBullet newWave = new WaveBullet(getX(), getY(), absBearing, power,
                direction, getTime(), currentStats);

        int bestindex = 15;	// initialize it to be in the middle, guessfactor 0.
        for (int i=0; i<31; i++)
            if (currentStats[bestindex] < currentStats[i])
                bestindex = i;

        // this should do the opposite of the math in the WaveBullet:
        double guessfactor = (double)(bestindex - (stats.length - 1) / 2)
                / ((stats.length - 1) / 2);
        double angleOffset = direction * guessfactor * newWave.maxEscapeAngle();
        double gunAdjust = Utils.normalRelativeAngle(
                absBearing - getGunHeadingRadians() + angleOffset);
        setTurnGunRightRadians(gunAdjust);

        if (getGunHeat() == 0 && gunAdjust < Math.atan2(9, e.getDistance()) && setFireBullet(power) != null)
        {
            waves.add(newWave);
        }
    }
}
