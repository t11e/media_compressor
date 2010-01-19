package com.t11e.mediacompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.Option;
import com.t11e.yuicompressor.mozilla.javascript.ErrorReporter;
import com.t11e.yuicompressor.mozilla.javascript.EvaluatorException;
import com.t11e.yuicompressor.yahoo.platform.yui.compressor.CssCompressor;
import com.t11e.yuicompressor.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class MediaCompressor
{
  private File mediaRootDir;
  private File outputDir;
  private File tempDir;
  private List<String> jslintOptions;

  public static MediaCompressor createFromConfig(IMediaConfiguration config)
  {
    final MediaCompressor result = new MediaCompressor();
    result.setMediaRootDir(new File(config.getMediaRootDir()));
    result.setOutputDir(new File(config.getOutputDir()));
    result.setTempDir(new File(config.getTempDir()));
    result.setJslintOptions(config.getJslintOptions());
    return result;
  }

  public void compressMedia(final IMediaGroup group)
  {
    final List<String> sourceFileNames = group.getSourceFileNames();
    if ("js".equals(group.getMediaType()))
    {
      if (group.isJslint())
      {
        for (final String fileName : sourceFileNames)
        {
          final File inFile = new File(mediaRootDir, fileName);
          final File outFile = new File(tempDir, fileName);
          final boolean passed = lintFile(inFile, outFile, jslintOptions);
          System.out.println((passed ? "Passed " : "Failed ") + fileName);
        }
      }
      else
      {
        System.out.println("Not linting group: " + group.getGroupName());
      }
    }
    for (final String fileName : sourceFileNames)
    {
      final File inFile = new File(mediaRootDir, fileName);
      final File outFile = new File(tempDir, fileName);
      compressFile(inFile, outFile, group.getMediaType());
    }

    final File destDir = new File(mediaRootDir, outputDir.getPath());
    concatenateMedia(new File(destDir, group.getOutputFileName()),
      mediaRootDir, sourceFileNames);
    concatenateMedia(new File(destDir, group.getOutputFileNameMinified()),
      tempDir, sourceFileNames);
  }

  static boolean compressFile(
    final File inFile,
    final File outFile,
    final String media_type)
  {
    boolean output = false;
    try
    {
      if (!FileUtils.isFileNewer(outFile, inFile))
      {
          outFile.getParentFile().mkdirs();
          final Reader reader = new FileReader(inFile);
          try
          {
            final Writer writer = new FileWriter(outFile);
            try
            {
              if ("js".equals(media_type))
              {
                compressJs(writer, reader);
              }
              else if ("css".equals(media_type))
              {
                compressCss(writer, reader);
              }
            }
            finally
            {
              IOUtils.closeQuietly(writer);
            }
          }
          finally
          {
            IOUtils.closeQuietly(reader);
          }
          output = true;
        }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
    return output;
  }

  private static void compressCss(final Writer out, final Reader in)
  {
    try
    {
      final CssCompressor compressor = new CssCompressor(in);
      compressor.compress(out, -1);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  private static void compressJs(final Writer out, final Reader reader)
  {
    final JavaScriptCompressor compressor;
    try
    {
      compressor = new JavaScriptCompressor(reader, new CustomErrorReporter());
    }
    catch (EvaluatorException e)
    {
      throw new RuntimeException(e);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }

    try
    {
      compressor.compress(out, -1, true, false, true, true);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  private static class CustomErrorReporter
    implements ErrorReporter
  {
    public void warning(final String message, final String sourceName,
      final int line, final String lineSource, final int lineOffset)
    {
      if (line < 0)
      {
        System.err.println("\n[WARNING] " + message);
      }
      else
      {
        System.err.println("\n[WARNING] " + line + ':' + lineOffset + ':' + message);
      }
    }

    public void error(final String message, final String sourceName,
      final int line, final String lineSource, final int lineOffset) {
      if (line < 0)
      {
        System.err.println("\n[ERROR] " + message);
      }
      else
      {
        System.err.println("\n[ERROR] " + line + ':' + lineOffset + ':' + message);
      }
    }

    public EvaluatorException runtimeError(final String message, final String sourceName,
      final int line, final String lineSource, final int lineOffset)
    {
      error(message, sourceName, line, lineSource, lineOffset);
      return new EvaluatorException(message);
    }
  }

  private static void concatenateMedia(
    final File outputFile,
    final File parentDir,
    final List<String> sourceFileNames)
  {
    try
    {
      outputFile.getParentFile().mkdirs();
      final OutputStream os = new FileOutputStream(outputFile);
      try
      {
        for (final String fileName : sourceFileNames)
        {
          final InputStream is = new FileInputStream(new File(parentDir, fileName));
          try
          {
            IOUtils.copy(is, os);
          }
          finally
          {
            IOUtils.closeQuietly(is);
          }
        }
      }
      finally
      {
        IOUtils.closeQuietly(os);
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  private static boolean lintFile(
    final File inFile,
    final File outFile,
    final List<String> jslintOptions)
  {
    boolean success = true;
    try
    {
      if (!outFile.exists() || !FileUtils.isFileNewer(outFile, inFile))
      {
        success = false;
        final Reader reader = new FileReader(inFile);
        try
        {
          final JSLintBuilder builder = new JSLintBuilder();
          final JSLint jsLint = builder.fromDefault();
          for (final String opt : jslintOptions)
          {
            final String[] pair = StringUtils.split(opt, '=');
            final Option option = Option.valueOf(StringUtils.trimToEmpty(pair[0]).toUpperCase());
            if (option != null)
            {
              if (pair.length == 2)
              {
                jsLint.addOption(option, StringUtils.trimToEmpty(pair[1]));
              }
              else
              {
                jsLint.addOption(option);
              }
            }
          }

          final List<Issue> issues = jsLint.lint(inFile.getPath(), reader);
          if (issues.isEmpty())
          {
            success = true;
          }
          else
          {
            for (final Issue issue : issues)
            {
              System.err.println(issue.getSystemId() + ":" + issue.getLine() + " " + issue.getReason());
            }
            throw new RuntimeException("JSLint failed.");
          }
        }
        finally
        {
          IOUtils.closeQuietly(reader);
        }
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
    return success;
  }

  public void setMediaRootDir(File mediaRootDir)
  {
    this.mediaRootDir = mediaRootDir;
  }

  public File getMediaRootDir()
  {
    return mediaRootDir;
  }

  public void setOutputDir(File outputDir)
  {
    this.outputDir = outputDir;
  }

  public File getOutputDir()
  {
    return outputDir;
  }

  public void setTempDir(File tempDir)
  {
    this.tempDir = tempDir;
  }

  public File getTempDir()
  {
    return tempDir;
  }

  public void setJslintOptions(List<String> jslintOptions)
  {
    this.jslintOptions = jslintOptions;
  }

  public List<String> getJslintOptions()
  {
    return jslintOptions;
  }
}
