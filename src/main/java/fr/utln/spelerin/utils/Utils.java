package fr.utln.spelerin.utils;

import java.time.Instant;

public class Utils {
	public static Instant getCreatedAt(long id){
		final long DISCORD_EPOCH = 1420070400000L; 
		long discordTimestamp = id >> 22; 
		long unixTimestamp = discordTimestamp + DISCORD_EPOCH; 
		return Instant.ofEpochMilli(unixTimestamp);
	}
}