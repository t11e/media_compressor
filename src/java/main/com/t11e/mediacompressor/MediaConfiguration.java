package com.t11e.mediacompressor;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class MediaConfiguration
  implements IMediaConfiguration
{
  private String mediaRootDir;
  private String outputDir;
  private String tempDir;
  private String defaultMediaMode = "minified";
  private long checkIntervalMs = 10 * 1000; // 10 seconds default
  private List<IMediaGroup> groups = Collections.emptyList();
  private List<String> jslintOptions = Collections.emptyList();

  public void setGroups(List<IMediaGroup> config)
  {
    this.groups = Collections.unmodifiableList(config);
  }

  public List<IMediaGroup> getGroups()
  {
    return groups;
  }

  public String getMediaRootDir()
  {
    return mediaRootDir;
  }

  public void setMediaRootDir(String mediaRootDir)
  {
    this.mediaRootDir = mediaRootDir;
  }

  public String getOutputDir()
  {
    return outputDir;
  }

  public void setOutputDir(String outputDir)
  {
    this.outputDir = outputDir;
  }

  public String getTempDir()
  {
    return tempDir;
  }

  public void setTempDir(String tempDir)
  {
    this.tempDir = tempDir;
  }
  public long getCheckIntervalMs()
  {
    return checkIntervalMs;
  }

  public void setCheckIntervalMs(long checkIntervalMs)
  {
    this.checkIntervalMs = checkIntervalMs;
  }

  public List<String> getJslintOptions()
  {
    return jslintOptions;
  }

  public void setJslintOptions(List<String> jslintOptions)
  {
    this.jslintOptions = Collections.unmodifiableList(jslintOptions);
  }

  public String getDefaultMediaMode()
  {
    return defaultMediaMode;
  }

  public void setDefaultMediaMode(String defaultMediaMode)
  {
    this.defaultMediaMode = defaultMediaMode;
  }

  @Override
  public String toString()
  {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("mediaRootDir", mediaRootDir)
      .append("outputDir", outputDir)
      .append("tempDir", tempDir)
      .append("defaultMediaMode", defaultMediaMode)
      .append("checkIntervalMs", checkIntervalMs)
      .append("groups", groups)
      .toString();
  }
}
