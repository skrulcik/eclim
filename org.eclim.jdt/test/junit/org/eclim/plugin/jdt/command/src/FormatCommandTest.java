/**
 * Copyright (C) 2005 - 2012  Eric Van Dewoestine
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.eclim.plugin.jdt.command.src;

import java.io.FileWriter;

import org.apache.commons.lang.StringUtils;

import org.eclim.Eclim;

import org.eclim.plugin.jdt.Jdt;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for FormatCommand.
 *
 * @author Eric Van Dewoestine
 */
public class FormatCommandTest
{
  private static final String TEST_FILE =
    "src/org/eclim/test/src/TestFormat.java";

  private static String contents;

  @BeforeClass
  public static void setupClass()
  {
    contents = Eclim.fileToString(Jdt.TEST_PROJECT, TEST_FILE);
  }

  @Before
  public void setupTest()
    throws Exception
  {
    String path = Eclim.resolveFile(Jdt.TEST_PROJECT, TEST_FILE);
    FileWriter writer = new FileWriter(path);
    writer.write(contents);
    writer.close();
  }

  @Test
  public void execute()
  {
    assertTrue("Java project doesn't exist.",
        Eclim.projectExists(Jdt.TEST_PROJECT));

    String contents = Eclim.fileToString(Jdt.TEST_PROJECT, TEST_FILE);
    String[] lines = StringUtils.split(contents, '\n');
    assertEquals("Initial line format incorrect.",
        "System.out.println(\"test formatting\");", lines[6]);

    Eclim.execute(new String[]{
      "java_format", "-p", Jdt.TEST_PROJECT,
      "-f", TEST_FILE,
      "-b", "109", "-e", "146"
    });

    contents = Eclim.fileToString(Jdt.TEST_PROJECT, TEST_FILE);
    lines = StringUtils.split(contents.replace("\t", "  "), '\n');
    assertEquals("Result line format incorrect.",
        "    System.out.println(\"test formatting\");", lines[6]);

    // range of lines
    contents = Eclim.fileToString(Jdt.TEST_PROJECT, TEST_FILE);
    lines = StringUtils.split(contents, '\n');
    assertEquals("Initial line 1 format incorrect.", "if(true){", lines[7]);
    assertEquals("Initial line 2 format incorrect.",
        "System.out.println(\"test format if\");", lines[8]);
    assertEquals("Initial line 3 format incorrect.", "}", lines[9]);

    Eclim.execute(new String[]{
      "java_format", "-p", Jdt.TEST_PROJECT,
      "-f", TEST_FILE,
      "-b", "150", "-e", "200"
    });

    contents = Eclim.fileToString(Jdt.TEST_PROJECT, TEST_FILE);
    lines = StringUtils.split(contents.replace("\t", "  "), '\n');
    assertEquals("Result line 1 format incorrect.", "    if (true) {", lines[7]);
    assertEquals("Result line 2 format incorrect.",
        "      System.out.println(\"test format if\");", lines[8]);
    assertEquals("Result line 3 format incorrect.", "    }", lines[9]);

    // whole file
    assertTrue("Java project doesn't exist.",
        Eclim.projectExists(Jdt.TEST_PROJECT));

    contents = Eclim.fileToString(Jdt.TEST_PROJECT, TEST_FILE);
    lines = StringUtils.split(contents, '\n');
    assertEquals("Initial line 1 format incorrect.", "public", lines[2]);
    assertEquals("Initial line 2 format incorrect.",
        "void main(String[] args)", lines[3]);
    assertEquals("Initial line 3 format incorrect.",
        "throws Exception", lines[4]);

    Eclim.execute(new String[]{
      "java_format", "-p", Jdt.TEST_PROJECT,
      "-f", TEST_FILE,
      "-b", "0", "-e", "211"
    });

    contents = Eclim.fileToString(Jdt.TEST_PROJECT, TEST_FILE);
    lines = StringUtils.split(contents.replace("\t", "  "), '\n');
    assertEquals("Result line 1 format incorrect.",
        "  public void main(String[] args) throws Exception {", lines[2]);
    assertEquals("Result line 1 format incorrect.",
        "    System.out.println(\"test formatting\");", lines[3]);
  }
}