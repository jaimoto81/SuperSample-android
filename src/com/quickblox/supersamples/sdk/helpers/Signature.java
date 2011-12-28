/**
 * 
 */
package com.quickblox.supersamples.sdk.helpers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SignatureException;


/**
 * @author Igor Khomenko
 * 		   injoit.com
 *
 * This class defines common routines for generating
 * authentication signatures for requests.
 */
public class Signature {
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	/**
	* Computes RFC 2104-compliant HMAC signature.
	* * @param data
	* The data to be signed.
	* @param key
	* The signing key.
	* @return
	* The Base64-encoded RFC 2104-compliant HMAC signature.
	* @throws
	* java.security.SignatureException when signature generation fails
	*/
	public static String calculateHMAC_SHA(String data, String key) throws SignatureException{
		String result = null;
		try {

			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			byte[] digest = mac.doFinal(data.getBytes());
	        
	        StringBuilder sb = new StringBuilder(digest.length*2);
	        String s;
	        for (byte b : digest){
	        	s = Integer.toHexString(0xFF & b);
	        	if(s.length() == 1) {
	        		sb.append('0');
	        	}
	        	
	        	sb.append(s);
	        }
	        
	        result = sb.toString();

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		
		return result;
	}
}