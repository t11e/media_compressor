package com.t11e.web.media;

import static org.junit.Assert.assertEquals;

import java.io.InputStreamReader;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.t11e.web.media.IMediaConfiguration;
import com.t11e.web.media.IMediaGroup;
import com.t11e.web.media.MediaConfigurationFactory;

public class MediaConfigTestCase
{
  @SuppressWarnings("unchecked")
  @Test
  public void testLoadYaml()
    throws Exception
  {
    final Yaml yaml = new Yaml();
    final Map data = (Map)
      yaml.load(getClass().getResourceAsStream("media_test.yml"));
    Assert.assertNotNull(data);
  }

  @Test
  public void testCustomClass()
    throws Exception
    {
      final IMediaConfiguration config =
        MediaConfigurationFactory.parseConfig(
          new InputStreamReader(getClass().getResourceAsStream("media_test.yml")));
      IMediaGroup group = config.getGroups().iterator().next();
      String groupName = group.getGroupName();
      assertEquals("example_css", groupName);
    }
}
