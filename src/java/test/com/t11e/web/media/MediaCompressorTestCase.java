package com.t11e.web.media;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.t11e.web.media.MediaCompressor;

public class MediaCompressorTestCase
{
  @Test
  public void testCompressCSS()
    throws Exception
  {
    File inFile = new File(getClass().getResource("media_test.css").getPath());
    File outFile = new File(System.getProperty("java.io.tmpdir"),"t11e_outfile.css");
    outFile.delete();
    assertFalse(outFile.exists());

    boolean created = MediaCompressor.compressFile(inFile, outFile, "css");
    assertTrue(created);
    assertTrue(inFile.length() > outFile.length());

    created = MediaCompressor.compressFile(inFile, outFile, "css");
    assertFalse(created);

    Thread.sleep(1000);
    inFile.setLastModified(System.currentTimeMillis());
    Thread.sleep(1000);
    created = MediaCompressor.compressFile(inFile, outFile, "css");
    assertTrue(created);

    created = MediaCompressor.compressFile(inFile, outFile, "css");
    assertFalse(created);
    outFile.delete();
  }

  @Test
  public void testCompressJS()
    throws Exception
  {
    File inFile = new File(getClass().getResource("media_test.js").getPath());
    File outFile = new File(System.getProperty("java.io.tmpdir"),"t11e_outfile.js");
    outFile.delete();
    boolean created = MediaCompressor.compressFile(inFile, outFile, "js");
    assertTrue(created);
    assertTrue(0 < outFile.length());
    assertTrue(inFile.length() > outFile.length());
    outFile.delete();
  }
}
