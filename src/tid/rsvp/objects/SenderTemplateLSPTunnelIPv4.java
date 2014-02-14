package tid.rsvp.objects;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 

  <p>RFC 3209  RSVP-TE	LSP_TUNNEL_IPv4 Sender Template Object</p>

  <p> Class = SENDER_TEMPLATE, LSP_TUNNEL_IPv4 C-Type = 7

    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                   IPv4 tunnel sender address                  |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |  MUST be zero                 |            LSP ID             |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

      IPv4 tunnel sender address

         IPv4 address for a sender node

      LSP ID

         A 16-bit identifier used in the SENDER_TEMPLATE and the
         FILTER_SPEC that can be changed to allow a sender to share
         resources with itself. </p>


@author Fernando Mu�oz del Nuevo	fmn@tid.es

 */

public class SenderTemplateLSPTunnelIPv4 extends SenderTemplate{

	/**
	 * <p>IPv4 address of the sender node</p>
	 */
	
	private Inet4Address senderNodeAddress;
	
	
	/**
	 *   <p> A 16-bit identifier used in the SENDER_TEMPLATE and the
         FILTER_SPEC that can be changed to allow a sender to share
         resources with itself. </p>

	 */
	
	private int LSPId;
	
	/**
	 * <p>Log
	 */
	
	private Logger log;
	
	/**
	 * <p>Parameters constructor to encode this kind of object. </p>
	 * @param senderNodeAddress The sender IPv4 Sender node address
	 * @param LSPId	The LSP identifier
	 */
	
	public SenderTemplateLSPTunnelIPv4(Inet4Address senderNodeAddress, int LSPId){
		
		classNum = 11;
		cType = 7;
		
		this.senderNodeAddress = senderNodeAddress;
		this.LSPId = LSPId;
		
		length = RSVPObjectParameters.RSVP_OBJECT_COMMON_HEADER_SIZE + 8;

		bytes = new byte[length];
		
		log=Logger.getLogger("ROADM");

		log.finest("Sender Template LSP Tunnel IPv4 Object Created");
			
	}
	
	/**
	 * <p>Constructor to be used when a new Sender Template LSP Tunnel IPv4 Object wanted 
	 * to be decoded from a received message.</p>
	 * @param bytes
	 * @param offset
	 */
	
	public SenderTemplateLSPTunnelIPv4(byte[] bytes, int offset){
		
		this.decodeHeader(bytes,offset);
		this.bytes = new byte[RSVPObject.getLength(bytes, offset)];
		
		log = Logger.getLogger("ROADM");

		log.finest("Sender Template LSP Tunnel IPv4 Object Created");
		
	}

	
	/**
	<p>
    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                   IPv4 tunnel sender address                  |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |  MUST be zero                 |            LSP ID             |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   </p>
	 */

	public void encode() {
		
		encodeHeader();

		byte[] addr = senderNodeAddress.getAddress();
		
		int currentIndex =  RSVPObjectParameters.RSVP_OBJECT_COMMON_HEADER_SIZE;

		System.arraycopy(addr,0, getBytes(), currentIndex, addr.length);
		currentIndex = currentIndex + addr.length;
		
		bytes[currentIndex] = (byte) 0;
		bytes[currentIndex+1] = (byte) 0;
		bytes[currentIndex+2] = (byte)((LSPId>>8) & 0xFF);
		bytes[currentIndex+3] = (byte)(LSPId & 0xFF);
		log.finest("Sender Template LSP Tunnel IPv4 Object Encoded");
		
	}

	/**
	<p>
    0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |                   IPv4 tunnel sender address                  |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |  MUST be zero                 |            LSP ID             |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   </p>
	 */
	
	public void decode(byte[] bytes, int offset) {

		byte[] receivedAddress = new byte[4];
		
		offset = offset + RSVPObjectParameters.RSVP_OBJECT_COMMON_HEADER_SIZE;
		
		System.arraycopy(bytes,offset,receivedAddress,0,4);
		try{
			senderNodeAddress = (Inet4Address) Inet4Address.getByAddress(receivedAddress);
		}catch(UnknownHostException e){
			log.severe("Unknown Host received on Sender Template LSP IPv4 Object");
		}
		offset = offset + receivedAddress.length;
		LSPId = (int)(bytes[offset+2] | bytes[offset+3]);
		log.finest("Sender Template LSP Tunnel IPv4 Object Decoded");				
		
	}
	// Getters & Setters

	public Inet4Address getSenderNodeAddress() {
		return senderNodeAddress;
	}

	public void setSenderNodeAddress(Inet4Address senderNodeAddress) {
		this.senderNodeAddress = senderNodeAddress;
	}

	public int getLSPId() {
		return LSPId;
	}

	public void setLSPId(int lSPId) {
		LSPId = lSPId;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}
	
	
	
}
