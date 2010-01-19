package com.t11e.mediacompressor;

import java.util.Collections;
import java.util.List;

public class MediaGroup
  implements IMediaGroup
{
  private String groupName;
  private String mediaType;
  private String outputFileName;
  private String outputFileNameMinified;
  private List<String> sourceFileNames = Collections.emptyList();
  private boolean jslint = true;

  public void setGroupName(String groupName)
  {
    this.groupName = groupName;
  }

  public String getGroupName()
  {
    return groupName;
  }

  public void setMediaType(String mediaType)
  {
    this.mediaType = mediaType;
  }

  public String getMediaType()
  {
    return mediaType;
  }

  public void setOutputFileName(String outputFileName)
  {
    this.outputFileName = outputFileName;
  }

  public String getOutputFileName()
  {
    return outputFileName;
  }

  public void setOutputFileNameMinified(String outputFileNameMinified)
  {
    this.outputFileNameMinified = outputFileNameMinified;
  }

  public String getOutputFileNameMinified()
  {
    return outputFileNameMinified;
  }

  public void setSourceFileNames(List<String> fileNames)
  {
    this.sourceFileNames = Collections.unmodifiableList(fileNames);
  }

  public List<String> getSourceFileNames()
  {
    return sourceFileNames;
  }

  public void setJslint(boolean jslint)
  {
    this.jslint = jslint;
  }

  public boolean isJslint()
  {
    return jslint;
  }
}
