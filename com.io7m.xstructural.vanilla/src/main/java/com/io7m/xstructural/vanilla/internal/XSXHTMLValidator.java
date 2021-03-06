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

package com.io7m.xstructural.vanilla.internal;

import com.io7m.xstructural.api.XSProcessorRequest;
import com.io7m.xstructural.api.XSProcessorType;
import com.io7m.xstructural.api.XSValidationException;
import com.io7m.xstructural.xml.SXMLResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import static javax.xml.xpath.XPathConstants.NODESET;

/**
 * An XHTML validator.
 */

public final class XSXHTMLValidator implements XSProcessorType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(XSXHTMLValidator.class);

  private final SXMLResources resources;
  private final XSProcessorRequest request;
  private final SAXParserFactory saxParsers;

  /**
   * A XHTML validator.
   *
   * @param inResources The SXML resources
   * @param inRequest   The processor request
   */

  public XSXHTMLValidator(
    final SXMLResources inResources,
    final XSProcessorRequest inRequest)
  {
    this.resources =
      Objects.requireNonNull(inResources, "resources");
    this.request =
      Objects.requireNonNull(inRequest, "request");
    this.saxParsers =
      SAXParserFactory.newInstance();
  }

  @Override
  public void execute()
    throws XSValidationException
  {
    LOG.debug("validating XHTML output files");

    var failed = false;

    try {
      final var schemaUrl = this.resources.w3cXHTMLSchema();
      try (var schemaStream = schemaUrl.openStream()) {
        final var schemaSource = new InputSource();
        schemaSource.setByteStream(schemaStream);
        schemaSource.setSystemId(schemaUrl.toString());

        LOG.debug("creating schema");
        final var schemaFactory =
          SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        schemaFactory.setErrorHandler(
          new XSErrorHandler(LoggerFactory.getLogger(
            XSXHTMLValidator.class.getCanonicalName() + ".schemaCompilation"))
        );
        schemaFactory.setResourceResolver(
          new XSResourceResolver(this.resources)
        );

        final var schema =
          schemaFactory.newSchema(new SAXSource(schemaSource));

        try (var directoryStream = Files.list(this.request.outputDirectory())) {
          final var files =
            directoryStream
              .filter(path -> path.toString().endsWith(".xhtml"))
              .filter(path -> !path.toString().endsWith("toc.xhtml"))
              .filter(path -> !path.toString().endsWith("tocInternal.xhtml"))
              .map(Path::toAbsolutePath)
              .collect(Collectors.toList());

          final var logger =
            LoggerFactory.getLogger(
              String.format(
                "%s.validation",
                XSXHTMLValidator.class.getCanonicalName())
            );

          for (final var file : files) {
            LOG.info("validate (xhtml 1.1) {}", file);
            failed |= this.validateOneFile(schema, logger, file);
            failed |= checkLinks(file);
          }
        }
      }

      if (failed) {
        LOG.error("one or more validation errors occurred");
        throw new XSValidationException(
          "The transformer produced invalid XHTML output"
        );
      }
    } catch (final Exception e) {
      throw new XSValidationException(e);
    }
  }

  private static boolean checkLinks(
    final Path file)
    throws IOException
  {
    var failed = false;

    try (var stream = Files.newInputStream(file)) {
      final var builderFactory =
        DocumentBuilderFactory.newInstance();

      builderFactory.setFeature(
        XMLConstants.FEATURE_SECURE_PROCESSING,
        true);
      builderFactory.setFeature(
        "http://apache.org/xml/features/nonvalidating/load-external-dtd",
        false);
      builderFactory.setFeature(
        "http://apache.org/xml/features/xinclude",
        false);
      builderFactory.setFeature(
        "http://xml.org/sax/features/namespaces",
        false);
      builderFactory.setFeature(
        "http://xml.org/sax/features/validation",
        false);
      builderFactory.setFeature(
        "http://apache.org/xml/features/validation/schema",
        false);

      builderFactory.setValidating(false);
      builderFactory.setXIncludeAware(false);
      builderFactory.setNamespaceAware(false);

      final var builder =
        builderFactory.newDocumentBuilder();
      final var xmlDocument =
        builder.parse(stream);

      final var xpaths =
        XPathFactory.newInstance();

      final var idPath =
        xpaths.newXPath()
          .compile("//@id");
      final var aPath =
        xpaths.newXPath()
          .compile("//a");

      final var withIds =
        (NodeList) idPath.evaluate(xmlDocument, NODESET);
      final var anchors =
        (NodeList) aPath.evaluate(xmlDocument, NODESET);

      final var anchorsLocal = new ArrayList<Node>();
      for (int index = 0; index < anchors.getLength(); ++index) {
        final var anchor = anchors.item(index);
        final var attributes = anchor.getAttributes();
        
        final var href = attributes.getNamedItem("href");
        final var hrefText = href.getTextContent();

        if (!checkFootnoteLink(file, attributes, hrefText)) {
          failed = true;
        }
        if (hrefText.startsWith("#")) {
          anchorsLocal.add(anchor);
        }
      }

      LOG.info(
        "checking the integrity of {} references",
        Integer.valueOf(anchorsLocal.size())
      );

      for (final var anchor : anchorsLocal) {
        final var href = anchor.getAttributes().getNamedItem("href");
        final var hrefText = href.getTextContent();
        final var hrefWithout = hrefText.substring(1);
        if (!findId(withIds, hrefWithout)) {
          LOG.error("unable to locate an element with id {}", hrefWithout);
          failed = true;
        }
      }

      if (!failed) {
        LOG.info(
          "checked the integrity of {} local references",
          Integer.valueOf(anchorsLocal.size())
        );
      }

      return failed;
    } catch (final SAXException | XPathExpressionException | ParserConfigurationException e) {
      throw new IOException(e);
    }
  }

  private static boolean checkFootnoteLink(
    final Path file,
    final NamedNodeMap attributes,
    final String hrefText)
  {
    final var cssClass = attributes.getNamedItem("class");
    if (cssClass == null) {
      return true;
    }

    final var cssClassText = cssClass.getTextContent();
    if (cssClassText.contains("stLinkFootnote")) {
      if (!hrefText.startsWith("#")) {
        if (!hrefText.contains(file.getFileName().toString())) {
          LOG.error("a footnote link must link to the same file name ({})",
                    hrefText);
          return false;
        }
      }
    }
    return true;
  }

  private static boolean findId(
    final NodeList withIds,
    final String idName)
  {
    for (int k = 0; k < withIds.getLength(); ++k) {
      final var node = withIds.item(k);
      final var idText = node.getTextContent();
      if (idName.equals(idText)) {
        return true;
      }
    }
    return false;
  }

  private boolean validateOneFile(
    final Schema schema,
    final Logger logger,
    final Path file)
    throws IOException, ParserConfigurationException, SAXException
  {
    try (var sourceStream = Files.newInputStream(file)) {
      final var fileSource = new InputSource();
      fileSource.setByteStream(sourceStream);
      fileSource.setSystemId(file.toUri().toString());

      final var errorHandler =
        new XSErrorHandler(logger);

      this.saxParsers.setSchema(schema);
      this.saxParsers.setNamespaceAware(true);
      this.saxParsers.setValidating(false);

      final var parser = this.saxParsers.newSAXParser();
      final var reader = parser.getXMLReader();
      reader.setErrorHandler(errorHandler);
      reader.setProperty(
        XMLConstants.ACCESS_EXTERNAL_SCHEMA,
        "");
      reader.setProperty(
        XMLConstants.ACCESS_EXTERNAL_DTD,
        "");
      reader.setFeature(
        "http://apache.org/xml/features/nonvalidating/load-external-dtd",
        false);
      reader.setEntityResolver((publicId, systemId) -> {
        LOG.debug("resolveEntity: {} {}", publicId, systemId);
        return new InputSource(new ByteArrayInputStream(new byte[0]));
      });

      reader.parse(fileSource);
      return errorHandler.isFailed();
    }
  }
}
