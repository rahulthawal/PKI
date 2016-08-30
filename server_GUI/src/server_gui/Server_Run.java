
package server_gui;

/**
 *
 * @author rahulthawal
 */
import java.io.File;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFrame;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
public class Server_Run extends JFrame
{	
	
    
    /**
     * This code is used to fetch the Public key in order to decrypt the message which was sent from client.
     * Using the right key the message is found and certain operations are performed
     * @return
     * @throws Exception 
     */
     public static PublicKey getKey()throws Exception{
	ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream("/home/sriteja/Desktop/public_c.txt"));
	PublicKey pubkey = (PublicKey) keyIn.readObject();
	keyIn.close();
	return pubkey;
}  

    public static void main(String a[]) throws Exception
            
    {
       
        ServerSocket server = new ServerSocket(1111); // only port number 
        
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair generatedKeyPair = keyGen.genKeyPair();
        PublicKey public_server = generatedKeyPair.getPublic();
        PrivateKey private_server = generatedKeyPair.getPrivate();
        SaveKeyPair("/home/sriteja", generatedKeyPair);
        
        
        
        
        
        System.out.println(" SERVER STARTED");
			while(true)
                        {
                                Socket socket = server.accept();
				socket.setKeepAlive(true);
				InetAddress address=socket.getInetAddress();
				String ipadd=address.toString();                                
                                // code to write the data to client.
                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                // code to read the data what has been send by client side.
                                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                                Object input[]=(Object[])in.readObject();    
                                // converting to string because client sents messages in string.
                                String type=(String)input[0];    
                                //code for registering newuser. 
                                if(type.equals("newuser"))
                                {
                                        String user = (String)input[1]; // client sends username.                
                                        String pass = (String)input[2]; // client sends password.                
                                        String confirm = (String)input[3]; // client sends reconfirmarion of password.		
                                        
                                        System.out.println("New User Request Accepted");	
                                        String inputs[]={user.trim(),pass.trim(),confirm.trim()};                                        //Registering in Database.
                                        String dbres = DBConn.register(inputs);                                        // Registeration of newuser.
                                        Object response[] = {dbres};		
                                        out.writeObject(response);
                                        out.flush();
                                }
                                //code to handle login operations.
                                if(type.equals("login"))
                                {
                                        String user = (String)input[1];  // recieving username from client.
                                        String pass = (String)input[2];  // recieving password from client.                
                                        
                                        String inputs[]={user.trim(),pass.trim()};                
                                        //calling login function in DBConn to validate.
                                        System.out.println("Login Successful");
                                        String details=DBConn.login(inputs);		
                                        System.out.println(details);
                                        Object response[]={details};
                                        out.writeObject(response);
                                        out.flush();
                                }
        
                                 if(type.equals("deposit"))
                                 {
                                            String acc = (String)input[1];
                                            byte receive_sign[] = (byte[])input[2];
                                            
                                            KeyPair loadedKeyPair = LoadKeyPair("/home/sriteja","RSA");
                                            PublicKey key = loadedKeyPair.getPublic(); //get the public key.
                                            PrivateKey pvtkey = loadedKeyPair.getPrivate(); //get the public key.
                                            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
                                            cipher.init(Cipher.DECRYPT_MODE, private_server);
                                            byte[] cipherData = cipher.doFinal(receive_sign);
                                            //PublicKey key = getKey();
                                            boolean verify = CA.Validate(acc.getBytes(),cipherData,key);
                                            
                                      
                                        if(verify)
                                        {
                                            
                                            System.out.println("Client Certification successfuly verified");
                                                System.out.println("Received certification request from client : "+new String(receive_sign)+"\n");
                                                String details = DBConn.balance(acc.trim());
                                                byte enc[] = KeyGen.encrypt(details.getBytes());
                                                Object response[]={"response",enc};
                                                out.writeObject(response);
                                                out.flush();
                                        }
                                        else
                                        {
                                            
                                            System.out.println("Client Certification successfuly failed");
                                            System.out.println("Received certification request from client : " +new String(receive_sign)+"\n");
                                            byte enc[] = KeyGen.encrypt("Signature verification failed".getBytes());
                                            Object response[]={"response",enc};
                                            out.writeObject(response);
                                            out.flush();
                                        }
                                        
                                 }       
                                        
                                        
                                        if(type.equals("balance"))
                                        {
                                            String acc = (String)input[1];
                                            byte receive_sign[] = (byte[])input[2];
                                            
                                            KeyPair loadedKeyPair = LoadKeyPair("/home/sriteja","RSA");
                                            PublicKey key = loadedKeyPair.getPublic(); //get the public key.
                                            PrivateKey pvtkey = loadedKeyPair.getPrivate(); //get the public key.
                                            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
                                            cipher.init(Cipher.DECRYPT_MODE, private_server);
                                            byte[] cipherData = cipher.doFinal(receive_sign);
                                            //PublicKey key = getKey();
                                            boolean verify = CA.Validate(acc.getBytes(),cipherData,key);
                                            if(verify)  
                                            {
                                              
                                                System.out.println("Client Certification successfuly verified");
                                                System.out.println("Received certification request from client : "+new String(receive_sign)+"\n");
                                                String details = DBConn.balance(acc.trim());
                                                byte enc[] = KeyGen.encrypt(details.getBytes());
                                                Object response[]={"response",enc};
                                                out.writeObject(response);
                                                out.flush();
                                            }
                                            else
                                            {
                                                
                                                System.out.println("Client Certification successfuly failed");
                                                System.out.println("Received certification request from client : " +new String(receive_sign)+"\n");
                                                byte enc[] = KeyGen.encrypt("Signature verification failed".getBytes());
                                                Object response[]={"response",enc};
                                                out.writeObject(response);
                                                out.flush();
                                            }
                                            
                                        }  
                                            
                                            if(type.equals("withdraw"))
                                            {
                                                String acc = (String)input[1];
                                            byte receive_sign[] = (byte[])input[2];
                                            
                                            KeyPair loadedKeyPair = LoadKeyPair("/home/sriteja","RSA");
                                            PublicKey key = loadedKeyPair.getPublic(); //get the public key.
                                            PrivateKey pvtkey = loadedKeyPair.getPrivate(); //get the public key.
                                            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
                                            cipher.init(Cipher.DECRYPT_MODE, private_server);
                                            byte[] cipherData = cipher.doFinal(receive_sign);
                                            //PublicKey key = getKey();
                                            boolean verify = CA.Validate(acc.getBytes(),cipherData,key);
                                                if(verify)
                                                {
                                                   
                                                    System.out.println("Client Certification successfuly verified");
                                                    System.out.println("Received certification request from client : "+new String(receive_sign)+"\n");
                                                    String details = DBConn.balance(acc.trim());
                                                    byte enc[] = KeyGen.encrypt(details.getBytes());
                                                    Object response[]={"response",enc};
                                                    out.writeObject(response);
                                                    out.flush();

                                                }
                                                else
                                                {
                                                    System.out.println("Client Certification successfuly failed");
                                                    System.out.println("Received certification request from client : " +new String(receive_sign)+"\n");
                                                    byte enc[] = KeyGen.encrypt("Signature verification failed".getBytes());
                                                    Object response[]={"response",enc};
                                                    out.writeObject(response);
                                                    out.flush();
                                                }
                                            }
                                            
	}
                                        
	}
       
private static KeyPair LoadKeyPair(String path, String algorithm)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		// Read Public Key.
		File filePublicKey = new File(path + "/public_c.key");
		FileInputStream fis = new FileInputStream(path + "/public_c.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
 
		// Read Private Key.
		File filePrivateKey = new File(path + "/private_c.key");
		fis = new FileInputStream(path + "/private_c.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
 
		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
 
		return new KeyPair(publicKey, privateKey);
	}

private static void SaveKeyPair(String path, KeyPair keyPair) throws IOException {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
 
		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream(path + "/public_s.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream(path + "/private_s.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
}
    

			
}




    
    


