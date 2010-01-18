package com.t11e.web.media;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class MediaConfigurationFactory
{
  public static IMediaConfiguration parseConfig(final File configFile)
  {
    IMediaConfiguration config = null;
    Reader reader = null;
    try
    {
      reader = new FileReader(configFile);
      config = parseConfig(reader);
    }
    catch (FileNotFoundException e)
    {
      throw new RuntimeException(e);
    }
    finally
    {
      IOUtils.closeQuietly(reader);
    }
    return config;
  }

  public static IMediaConfiguration parseConfig(final Reader reader)
  {
    final Constructor constructor = new Constructor(MediaConfiguration.class);
    final TypeDescription mediaConfig = new TypeDescription(MediaConfiguration.class);
    mediaConfig.putListPropertyType("groups", MediaGroup.class);
    constructor.addTypeDescription(mediaConfig);
    final Loader loader = new Loader(constructor);
    final Yaml yaml = new Yaml(loader);
    final IMediaConfiguration configGroup = (IMediaConfiguration) yaml.load(reader);
    return configGroup;
  }
}
