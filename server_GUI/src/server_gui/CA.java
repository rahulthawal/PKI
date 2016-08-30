
package server_gui;

/**
 *
 * @author Sriteja Nallamilli
 */
import java.security.PrivateKey;
import java.security.Signature;
import java.security.PublicKey;
public class CA
{
    
    /**
     * This is the code for Certifying using Algorithm.
     * @param filedata
     * @param privkey
     * @return 
     */
public static byte[] Certifying(byte filedata[],PrivateKey privkey){
	byte[] signature = null;
	try{
		Signature signalg = Signature.getInstance("SHA1withRSA");
        signalg.initSign(privkey);
	    signalg.update(filedata);
		signature = signalg.sign();
	}catch(Exception e){
		e.printStackTrace();
	}
	return signature;
}
public static boolean Validate(byte filedata[],byte[] signature,PublicKey pubkey)throws Exception{
	boolean flag = false;
	Signature verifyalg = Signature.getInstance("SHA1withRSA");
    verifyalg.initVerify(pubkey);
    verifyalg.update(filedata);
	if(verifyalg.verify(signature))
		flag = true;
	return flag;
}
}