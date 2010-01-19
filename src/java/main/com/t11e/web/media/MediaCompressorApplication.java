package com.t11e.web.media;

import java.io.File;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class MediaCompressorApplication
{
  public static void main(final String[] args)
    throws Exception
  {
    final int resultCode = mainImpl(args);
    if (resultCode != 0)
    {
      System.exit(resultCode);
    }
  }

  @SuppressWarnings("unchecked")
  static int mainImpl(final String[] args)
    throws ParseException
  {
    final Options options = new Options();
    options.addOption("c", "config", true, "Path to configuration file, defaults to media.yml");
    options.addOption("h", "help", false, "Print this message");
    final CommandLineParser parser = new PosixParser();
    final CommandLine cmd = parser.parse(options, args);

    final int resultCode;
    if (cmd.hasOption("help"))
    {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("MediaCompressorApplication [options] [group...]", options, false);
      resultCode = 1;
    }
    else
    {
      final String configPath = cmd.getOptionValue("config", "media.yml");
      final Collection<String> groupNames = cmd.getArgList();
      final IMediaConfiguration config =
        MediaConfigurationFactory.parseConfig(new File(configPath));
      final MediaCompressor compressor =
        MediaCompressor.createFromConfig(config);
      for (final IMediaGroup group : config.getGroups())
      {
        if (groupNames.isEmpty() || groupNames.contains(group.getGroupName()))
        {
          compressor.compressMedia(group);
        }
      }
      resultCode = 0;
    }
    return resultCode;
  }
}
