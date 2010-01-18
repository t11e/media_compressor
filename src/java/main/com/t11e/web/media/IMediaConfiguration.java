package com.t11e.web.media;

import java.util.List;

public interface IMediaConfiguration
{
  List<IMediaGroup> getGroups();

  String getMediaRootDir();

  String getOutputDir();

  String getTempDir();

  String getDefaultMediaMode();

  long getCheckIntervalMs();

  List<String> getJslintOptions();
}
