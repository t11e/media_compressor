package com.t11e.mediacompressor;

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class MediaConfigCache
{
  private static final Logger logger =
    Logger.getLogger(MediaConfigCache.class.getName());
  private static final long INVALID_CHECK_INTERVAL_MS = 10 * 1000;
  private final String configPath;
  private final File configFile;
  private int[] lock = {};
  private long lastModified = -1;
  private long nextCheckTime = -1;
  private IMediaConfiguration mediaConfig;

  public MediaConfigCache(final String configPath)
  {
    this.configPath = configPath;
    configFile = new File(configPath);
    try
    {
      autoReload();
    }
    catch (Exception e)
    {
      // Swallow because we'll retry again in autoReload.
    }
  }

  private void autoReload()
  {
    final long currentTime = System.currentTimeMillis();
    final long currentModTime = configFile.lastModified();
    final boolean changed;
    synchronized (lock)
    {
      if (currentTime >= nextCheckTime)
      {
        changed = lastModified != currentModTime && currentModTime != 0;
        if (changed)
        {
          logger.info("Loading media configuration from " + configPath +
            ". New modifcation time " + currentModTime);
          mediaConfig = MediaConfigurationFactory.parseConfig(configFile);
          lastModified = currentModTime;
        }
        final long checkInterval =
          mediaConfig != null
            ? mediaConfig.getCheckIntervalMs()
            : INVALID_CHECK_INTERVAL_MS;
        nextCheckTime = currentTime + checkInterval;
      }
    }
  }

  public String getConfigPath()
  {
    return configPath;
  }

  public IMediaConfiguration getMediaConfig()
  {
    autoReload();
    synchronized (lock)
    {
      return mediaConfig;
    }
  }

  @Override
  public boolean equals(final Object obj)
  {
    boolean isEqual = false;
    if (obj instanceof MediaConfigCache)
    {
      final MediaConfigCache rhs = (MediaConfigCache) obj;
      isEqual = new EqualsBuilder().append(configPath, rhs.configPath).isEquals();

    }
    return isEqual;
  }

  @Override
  public int hashCode()
  {
    return new HashCodeBuilder().append(configPath).toHashCode();
  }
}
