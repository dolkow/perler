/*
	Copyright 2010 Snild Dolkow
	
	This file is part of Perler.
	
	Perler is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	Perler is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with Perler.  If not, see <http://www.gnu.org/licenses/>.
*/
package se.dolkow.imagefiltering.app.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import se.dolkow.imagefiltering.internationalization.Messages;

public class Version implements Comparable<Version> {
	int year = 0;
	int month = 0;
	int day = 0;
	int hours = 0;
	int minutes = 0;
	
	public Version(InputStream v) {
		this(new Scanner(v));
	}
	
	public Version(String v) {
		this(new Scanner(v));
	}
	
	private Version(Scanner sc) {
		sc = sc.useDelimiter("-|\\s"); //$NON-NLS-1$
		RuntimeException e = null;
		try {
			year = sc.nextInt();
			month = sc.nextInt();
			day = sc.nextInt();
			int time = sc.nextInt();
			hours = time / 100;
			minutes = time % 100;
		} catch (InputMismatchException ime) {
			e = ime;
		} catch (NoSuchElementException nsee) {
			e = nsee;
		}
		while(sc.hasNextLine()) {
			System.out.println("Extra version data: " + sc.nextLine()); //$NON-NLS-1$
		}
		if (e != null) {
			throw e;
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (year < 1000) sb.append('0');
		if (year < 100) sb.append('0');
		if (year < 10) sb.append('0');
		sb.append(year);
		sb.append('-');
		
		if (month < 10) sb.append('0');
		sb.append(month);
		sb.append('-');
		
		if (day < 10) sb.append('0');
		sb.append(day);
		sb.append('-');
		if (hours < 10) sb.append('0');
		sb.append(hours);
		if (minutes < 10) sb.append('0');
		sb.append(minutes);
		
		return sb.toString();
	}
	
	private long toLong() {
		return 
			minutes * 1l + 
			hours	* 100l + 
			day		* 10000l + 
			month 	* 1000000l + 
			year	* 100000000l ;
	}

	public int compareTo(Version v) {
		long vl = v.toLong();
		long tl = toLong();
		if (tl > vl) {
			return 1;
		} else if (tl < vl) {
			return -1;
		}
		return 0;
	}
	
	public boolean equals(Object rhs) {
		if (rhs != null && rhs instanceof Version) {
			Version v = (Version)rhs;
			return compareTo(v) == 0;
		}
		return false;
	}

	public static Version getCurrent() throws IOException {
		URL localurl = VersionCheck.class.getResource("/resource/version"); //$NON-NLS-1$
		if (localurl == null) {
			throw new RuntimeException(Messages.get("VersionCheck.version_fail")); //$NON-NLS-1$
		}
		return new Version(localurl.openStream());
	}
}
