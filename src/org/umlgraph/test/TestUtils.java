/*
 * UmlGraph class diagram testing framework
 *
 * Contibuted by Andrea Aime
 * (C) Copyright 2005 Diomidis Spinellis
 *
 * Permission to use, copy, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies and that
 * both that copyright notice and this permission notice appear in
 * supporting documentation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * $Id$
 *
 */

package org.umlgraph.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Collection of utility methods used by the test classes
 * @author wolf
 * 
 */
public class TestUtils {

    /**
         * Simple text file diffing: will tell you if two text files are line by
         * line equals, and will stop at the first difference found.
         * @throws IOException
         */
    public static boolean textFilesEquals(PrintWriter pw, File refTextFile, File outTextFile)
	    throws IOException {
	BufferedReader refReader = null, outReader = null;
	String refLine = null, outFile = null;
	try {
	    pw.println("Performing diff:");
	    pw.println("out: " + outTextFile.getAbsolutePath());
	    pw.println("ref: " + refTextFile.getAbsolutePath());

	    refReader = new BufferedReader(new FileReader(refTextFile));
	    outReader = new BufferedReader(new FileReader(outTextFile));

	    /*
	     * Line by line scan, exit when one file ends or lines are not
	     * equal.
	     * Ignore the "Generated by javadoc ..." comments, and the
	     * "<META NAME="date"" tags
	     */
	    for (;;) {
		refLine = refReader.readLine();
		outFile = outReader.readLine();
		if (refLine == null || outFile == null) {
		    break;
		} else if (refLine.startsWith("<META NAME=\"date\"")) {
		    if (!outFile.startsWith("<META NAME=\"date\""))
			break;
		} else if (refLine.startsWith("<!-- Generated by javadoc ")) {
		    if (!outFile.startsWith("<!-- Generated by javadoc "))
			break;
		} else if (!refLine.equals(outFile)) {
		    break;
		}

	    }
	} finally {
	    if (refReader != null)
		refReader.close();
	    if (outReader != null)
		outReader.close();
	}
	// they were equals if both files ended at the same time
	boolean equal = refLine == null && outFile == null;
	if (equal)
	    pw.print("File contents are equal");
	else
	    pw.println("Differences found\nref: " + refLine + "\nout: " + outFile);
	pw.println();
	pw.println();

	return equal;
    }

    public static boolean dotFilesEqual(PrintWriter pw, String dotPath, String refPath)
	    throws IOException {
	pw.println("Performing diff:\nout:" + dotPath + "\nref:" + refPath);
	DotDiff differ = new DotDiff(new File(dotPath), new File(refPath));
	boolean equal = differ.graphEquals();
	if (equal) {
	    pw.println("File contents are structurally equal");
	} else {
	    pw.println("File contents are structurally not equal");
	    printList(pw, "# Lines in out but not in ref", differ.getExtraLines1());
	    printList(pw, "# Lines in ref but not in out", differ.getExtraLines2());
	    printList(pw, "# Nodes in out but not in ref", differ.getNodes1());
	    printList(pw, "# Nodes in ref but not in out", differ.getNodes2());
	    printList(pw, "# Arcs in out but not in ref", differ.getArcs1());
	    printList(pw, "# Arcs in ref but not in out", differ.getArcs2());
	}
	pw.println("\n\n");
	return equal;
    }

    public static void printList(PrintWriter pw, String message, List extraOut) {
	if (extraOut.size() > 0) {
	    pw.println(message);
	    for (Object o : extraOut) {
		pw.println(o);
	    }
	}
    }

    /**
         * Deletes the content of the folder, eventually in a recursive way (but
         * avoids deleting eventual .cvsignore files and CVS folders)
         */
    public static void cleanFolder(File folder, boolean recurse) {
	for (File f : folder.listFiles()) {
	    if (f.isDirectory() && !f.getName().equals("CVS")) {
		if (recurse) {
		    cleanFolder(f, true);
		    if (f.list().length == 0)
			f.delete();
		}
	    } else if (!f.getName().equals(".cvsignore")) {
		f.delete();
	    }

	}

    }

}
