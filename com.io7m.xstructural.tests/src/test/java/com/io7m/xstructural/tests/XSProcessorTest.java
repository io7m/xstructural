/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.xstructural.tests;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import com.io7m.xstructural.api.XSProcessorRequest;
import com.io7m.xstructural.api.XSProcessorRequestType;
import com.io7m.xstructural.api.XSValidationException;
import com.io7m.xstructural.vanilla.XSProcessors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public final class XSProcessorTest
{
  private static final Duration TIMEOUT = Duration.ofSeconds(15L);

  private Path directory;
  private XSProcessors processors;
  private Path outputDirectory;
  private Path sourceDirectory;
  private FileSystem windowsFilesystem;

  @BeforeEach
  public void testSetup()
    throws IOException
  {
    this.directory = XSTestDirectories.createTempDirectory();
    this.sourceDirectory = this.directory.resolve("src");
    this.outputDirectory = this.directory.resolve("out");
    this.processors = new XSProcessors();

    Files.createDirectories(this.sourceDirectory);
    Files.createDirectories(this.outputDirectory);

    this.windowsFilesystem =
      MemoryFileSystemBuilder.newWindows()
        .build();
  }

  @AfterEach
  public void tearDown()
    throws IOException
  {
    this.windowsFilesystem.close();
  }

  @Test
  public void testCompileInvalid()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "file0.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertThrows(XSValidationException.class, processor::execute);
  }

  @Test
  public void testCompileSingleExample0_70()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example0_70.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.SINGLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileSingleExample1_70()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example1_70.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.SINGLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileSingleExample2_70()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example2_70.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.SINGLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileSingleExample0_80()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example0_80.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.SINGLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileSingleExample1_80()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example1_80.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.SINGLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileSingleExample2_80()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example2_80.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.SINGLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileMultipleExample0_70()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example0_70.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.MULTIPLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileMultipleExample1_70()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example1_70.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.MULTIPLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileMultipleExample2_70()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example2_70.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.MULTIPLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileMultipleExample0_80()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example0_80.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.MULTIPLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileMultipleExample1_80()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example1_80.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.MULTIPLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileMultipleExample2_80()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example2_80.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.MULTIPLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }

  @Test
  public void testCompileSingleExampleWindows0_70()
    throws Exception
  {
    final var request =
      XSProcessorRequest.builder()
        .setOutputDirectory(this.outputDirectory)
        .setSourceFile(XSTestDirectories.resourceOf(
          XSProcessorTest.class,
          this.sourceDirectory,
          "example0_70.xml"))
        .setTraceFile(this.directory.resolve("trace.xml"))
        .setMessageFile(this.directory.resolve("messages.txt"))
        .setStylesheet(XSProcessorRequestType.Stylesheet.SINGLE_FILE)
        .build();

    final var processor = this.processors.create(request);
    Assertions.assertTimeout(TIMEOUT, processor::execute);
  }
}
