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

package com.io7m.xstructural.vanilla.internal.xslt_extensions;

import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SequenceType;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.sf.saxon.s9api.ItemType.STRING;
import static net.sf.saxon.s9api.OccurrenceIndicator.ONE;

/**
 * The {@code mimeOf} extension function.
 */

public final class XSMIMEExtensionFunction
  implements ExtensionFunction
{
  private static final Logger LOG =
    LoggerFactory.getLogger(XSMIMEExtensionFunction.class);

  private final Tika tika;

  /**
   * The {@code mimeOf} extension function.
   */

  public XSMIMEExtensionFunction()
  {
    this.tika = new Tika();
  }

  @Override
  public QName getName()
  {
    return new QName("urn:com.io7m.xstructural.mime", "mimeOf");
  }

  @Override
  public SequenceType getResultType()
  {
    return SequenceType.makeSequenceType(STRING, ONE);
  }

  @Override
  public SequenceType[] getArgumentTypes()
  {
    return new SequenceType[]{SequenceType.makeSequenceType(STRING, ONE)};
  }

  @Override
  public XdmValue call(
    final XdmValue[] arguments)
  {
    final var arg = arguments[0].itemAt(0).getStringValue();
    final var detected = this.tika.detect(arg);
    LOG.trace("mimeOf: {} -> {}", arg, detected);
    return new XdmAtomicValue(detected);
  }
}
