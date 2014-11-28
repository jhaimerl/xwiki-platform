/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.crypto.x509.internal;

import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.util.Store;
import org.xwiki.crypto.internal.Convert;
import org.xwiki.crypto.x509.XWikiX509Certificate;
import org.xwiki.crypto.x509.XWikiX509KeyPair;


/**
 * Implementation of signature that uses PKCS7 encoding. Signatures are stored as a
 * PKCS#7 signed data objects with embedded signer certificate and detached content.
 *
 * @version $Id$
 * @since 2.5M1
 */
public class X509SignatureService
{
    /** Digest Object ID. TODO should the digest used be configurable? */
    private static final String DIGEST_OID = CMSSignedGenerator.DIGEST_SHA1;

    /** Type of the certificate store to use for PKCS7 encoding/decoding. */
    private static final String CERT_STORE_TYPE = "Collection";

    /** The JCA provider to use. */
    private Provider provider;

    /**
     * @return the JCA provider in use.
     */
    public Provider getProvider()
    {
        return provider;
    }

    /**
     * Set the JCA provider to use.
     * @param provider a JCA provider
     * @return this object for easy call chaining.
     */
    public X509SignatureService setProvider(Provider provider)
    {
        this.provider = provider;
        return this;
    }

    /**
     * Produce a pkcs#7 signature for the given text.
     * Text will be signed with the key belonging to the author of the code which calls this.
     *
     * @param textToSign the text which the user wishes to sign.
     * @param toSignWith the certificate and matching private key to sign the text with.
     * @param password to access the private key in the key pair.
     * @return a signature which can be used to validate the signed text.
     * @throws GeneralSecurityException if anything goes wrong during signing.
     */
    public String signText(final String textToSign,
        final XWikiX509KeyPair toSignWith,
        final String password)
        throws GeneralSecurityException
    {
        XWikiX509Certificate certificate = toSignWith.getCertificate();

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        Collection<?> certs = Collections.singleton(certificate);
        CertStore store = CertStore.getInstance(CERT_STORE_TYPE, new CollectionCertStoreParameters(certs));

        try {
            gen.addCertificatesAndCRLs(store);
            gen.addSigner(toSignWith.getPrivateKey(password), certificate, DIGEST_OID);
            byte[] data = textToSign.getBytes();
            CMSSignedData cmsData = gen.generate(new CMSProcessableByteArray(data), false, provider);

            return Convert.toBase64String(cmsData.getEncoded());
        } catch (GeneralSecurityException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new GeneralSecurityException(exception);
        }
    }

    /**
     * Verify a pkcs#7 signature and return the certificate of the user who signed it.
     *
     * @param signedText the text which has been signed.
     * @param base64Signature the signature on the text in Base64 encoded DER format.
     * @return the certificate used to sign the text or null if it's invalid.
     * @throws GeneralSecurityException if anything goes wrong.
     */
    public XWikiX509Certificate verifyText(String signedText, String base64Signature) throws GeneralSecurityException
    {
        try {
            byte[] data = signedText.getBytes();
            byte[] signature = Convert.fromBase64String(base64Signature);
            CMSSignedData cmsData = new CMSSignedData(new CMSProcessableByteArray(data), signature);
            Store certStore = cmsData.getCertificates();
            SignerInformationStore signers = cmsData.getSignerInfos();

            int numSigners = signers.getSigners().size();
            if (numSigners != 1) {
                throw new GeneralSecurityException("Only one signature is supported, found " + numSigners);
            }
            XWikiX509Certificate result = null;
            for (Iterator<?> it = signers.getSigners().iterator(); it.hasNext();) {
                if (result != null) {
                    throw new GeneralSecurityException("Only one certificate is supported");
                }
                SignerInformation signer = (SignerInformation) it.next();
                Collection<X509CertificateHolder> certs = certStore.getMatches(signer.getSID());
                for (Iterator<X509CertificateHolder> cit = certs.iterator(); cit.hasNext();) {
                    X509CertificateHolder certificate = cit.next();
                    if (!signer.verify(
                        new JcaSimpleSignerInfoVerifierBuilder().setProvider(provider).build(certificate))) {
                        return null;
                    }

                    result = new XWikiX509Certificate(
                        new JcaX509CertificateConverter().setProvider(provider).getCertificate(certificate), null);
                }
            }
            return result;
        } catch (CMSException exception) {
            // This means the text is invalid, our contract says we return null.
            // This message is hardcoded in org.bouncycastle.cms.SignerInformation
            if ("message-digest attribute value does not match calculated value".equals(exception.getMessage())) {
                return null;
            }
            throw new GeneralSecurityException(exception);
        } catch (Exception exception) {
            throw new GeneralSecurityException(exception);
        }
    }
}
