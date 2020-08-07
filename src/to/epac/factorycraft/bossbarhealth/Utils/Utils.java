package to.epac.factorycraft.bossbarhealth.Utils;

import java.text.DecimalFormat;

import to.epac.factorycraft.bossbarhealth.BossBarHealth;

public class Utils {
	
	private static BossBarHealth plugin = BossBarHealth.inst();
	
	/**
	 * Convert yaw to direction string
	 * CARDINAL_FULL: North, East, South, West
	 * ORDINAL_FULL: CARDINAL_FULL and NorthEast, SouthEast, etc...
	 * CARDINAL: N, E, S, W
	 * ORDINAL: CARDINAL and NE, SE, etc...
	 * NUMBER: 0-360 degrees
	 * 
	 * @param yaw Yaw to convert
	 * @param type Output type
	 * @return String of the direction
	 */
	// TODO - Convert directions
	public static String getDirection(double yaw, String type) {
		
		if (type.equals("NUMBER")) {
			String pattern = "#";
			for (int i = 0; i < plugin.getConfigManager().getDecimal(); i++)
				pattern += (i == 0 ? "." : "#");
			DecimalFormat df = new DecimalFormat(pattern);
			
			return df.format(yaw);
		}
		
        String direction = "";

        if (0 <= yaw && yaw < 22.5) {
            direction = "North";
        }
        if (22.5 <= yaw && yaw < 67.5) {
            direction = "North East";
        }
        if (67.5 <= yaw && yaw < 112.5) {
            direction = "East";
        }
        if (112.5 <= yaw && yaw < 157.5) {
            direction = "SouthEast";
        }
        if (157.5 <= yaw && yaw < 202.5) {
            direction = "South";
        }
        if (202.5 <= yaw && yaw < 247.5) {
            direction = "SouthWest";
        }
        if (247.5 <= yaw && yaw < 292.5) {
            direction = "West";
        }
        if (292.5 <= yaw && yaw < 337.5) {
            direction = "NorthWest";
        }
        if (337.5 <= yaw && yaw <= 360) {
            direction = "North";
        }
		
		return direction;
	}
}
