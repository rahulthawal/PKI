
package client_gui;

import java.security.PrivateKey; // library for private key
import java.security.Signature; // library for signature i.e for verify and signing
import java.security.PublicKey; // library for public key
public class CA
{
    // code for signing 
    
    /**
     * This is the code for Certifying using Algorithm.
     * @param filedata
     * @param privkey
     * @return 
     */
    public static byte[] Certifying(byte filedata[],PrivateKey privkey)
    {
	byte[] signature = null;
	try
        {
		Signature signalg = Signature.getInstance("SHA1withRSA"); 
                signalg.initSign(privkey);  
                signalg.update(filedata); 
		signature = signalg.sign();
	}
        catch(Exception e) // Handling exception i.e no such algorithm
        {
		e.printStackTrace();
	}
	return signature;
    
     }
    
    
    // code to verify the signature.
    
    public static boolean Validate(byte filedata[],byte[] signature,PublicKey pubkey)throws Exception
    {
	boolean flag = false;
	Signature verifyalg = Signature.getInstance("SHA1withRSA"); // obtaining the signature.
        
        verifyalg.initVerify(pubkey); // initialize the signature with publickey 
        
        verifyalg.update(filedata);
	if(verifyalg.verify(signature)) // flag to check whether true or false
		flag = true;
	return flag;
    }
}