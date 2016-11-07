package org.fife.emu;

import java.io.*;


/**
 * Utility methods for emulators.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class Util {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Util() {
	}

	/**
	 * Returns a 4-characters string representation of the specified
	 * unsigned byte.
	 *
	 * @param ubyte An unsigned byte.
	 * @return A 4-character string representation (e.g. if <code>ubyte</code>
	 *         is <code>15</code>, this method returns <code>0x0f</code>).
	 * @see #getHexStringUWord
	 */
	public static String getHexStringUByte(int ubyte) {
		return (ubyte<0x10 ? "0x0" : "0x") + Integer.toHexString(ubyte);
	}

	/**
	 * Returns a 4-characters string representation of the specified
	 * unsigned word.
	 *
	 * @param uword An unsigned word.
	 * @return A 6-character string representation (e.g. if <code>uword</code>
	 *         is <code>15</code>, this method returns <code>0x000f</code>).
	 * @see #getHexStringUByte
	 */
	public static String getHexStringUWord(int uword) {
		String str = Integer.toHexString(uword);
		while (str.length()<4) {
			str = "0" + str;
		}
		return "0x" + str;
	}

	/**
	 * Returns the location of the specified jar file in the currently-running
	 * application's classpath.  This can be useful if you wish to know the
	 * location of the installation of the currently-running application.<p>
	 * For example, a Java program running from the executable jar
	 * <code>Foo.jar</code> can call this method with <code>Foo.jar</code> as
	 * the parameter, and the location of the jar file would be returned.  With
	 * this knowledge, along with knowledge of the directory layout of the
	 * application, the programmer can access other files in the installation.
	 *
	 * @param jarFileName The name of the jar file for which to search.
	 * @return The directory in which the jar file resides.
	 */
	public static String getLocationOfJar(String jarFileName) {

		String classPath = System.getProperty("java.class.path");
		jarFileName = jarFileName.toLowerCase(); // Can help on Windows
		int index = classPath.indexOf(jarFileName);

		// A jar file on a classpath MUST be explicitly given; a jar file
		// in a directory, for example, will not be picked up by specifying
		// "-classpath /my/directory/".  So, we can simply search for the
		// jar name in the classpath string, and if it isn't there, it must
		// be in the current directory.
		if (index>-1) {
			int pathBeginning = classPath.lastIndexOf(File.pathSeparator,
												index-1) + 1;
			String location = classPath.substring(pathBeginning, index);
			File temp = new File(System.getProperty("user.dir"), location);
			return temp.getAbsolutePath();
		}

		// Otherwise, it must be in the current directory.
		else {
			return System.getProperty("user.dir");
		}

	}
}